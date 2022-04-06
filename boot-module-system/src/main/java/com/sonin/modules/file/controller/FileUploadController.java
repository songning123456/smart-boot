package com.sonin.modules.file.controller;

import com.sonin.api.vo.Result;
import com.sonin.modules.file.dto.FileUploadDTO;
import com.sonin.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * 文件上传
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/6 17:22
 */
@RestController
@RequestMapping("/file/fileUpload")
public class FileUploadController {

    @Value(value = "${boot.path.fileUpload}")
    private String fileUploadPath;

    @GetMapping("/isExist")
    public Result<Object> isExistCtrl(FileUploadDTO fileUploadDTO) {
        Result<Object> result = new Result<>();
        String fileType = fileUploadDTO.getFileType();
        String md5 = fileUploadDTO.getMd5();
        String fileName = fileUploadDTO.getFileName();
        File dirFile = new File(fileUploadPath + File.separator + fileType + File.separator + md5);
        List<Integer> list = new ArrayList<>();
        if (dirFile.exists()) {
            File[] folders = dirFile.listFiles();
            if (folders != null && folders.length != 0) {
                for (File folder : folders) {
                    // 判断是否存在文件夹，即已经上传的部分
                    if (folder.isDirectory()) {
                        File[] chunkFiles = folder.listFiles();
                        if (chunkFiles != null && chunkFiles.length != 0) {
                            for (File file : chunkFiles) {
                                list.add(Integer.parseInt(file.getName()));
                            }
                        }
                    }
                }
            }
            if (list.isEmpty()) {
                result.setMessage("文件已上传");
                result.setResult(File.separator + fileType + File.separator + md5 + File.separator + fileName);
            } else {
                result.setResult(list);
                result.setMessage("文件仅上传部分");
            }
        } else {
            result.setCode(201);
            result.setMessage("文件未上传");
        }
        return result;
    }

    @PostMapping("/shardUpload")
    public Result<?> shardUploads(@RequestBody FileUploadDTO fileUploadDTO) throws Exception {
        Result<?> result = new Result<>();
        MultipartFile file = fileUploadDTO.getFile();
        String fileType = fileUploadDTO.getFileType();
        String md5 = fileUploadDTO.getMd5();
        String fileName = fileUploadDTO.getFileName();
        int currentChunk = fileUploadDTO.getCurrentChunk();
        String dirPath = fileUploadPath + File.separator + fileType + File.separator + md5;
        String fileName0 = fileName.split("\\.")[0];
        if (FileUtils.mkDir(dirPath)) {
            File tmpFolder = new File(dirPath + File.separator + fileName0 + ".chunk");
            if (!tmpFolder.exists()) {
                tmpFolder.mkdirs();
            }
            File chunkFile = new File(dirPath + File.separator + fileName0 + ".chunk" + File.separator + currentChunk);
            if (!chunkFile.exists()) {
                file.transferTo(chunkFile);
            }
        }
        return result;
    }

    @GetMapping("/shardMerge")
    public Result<String> shardMergeCtrl(FileUploadDTO fileUploadDTO) {
        Result<String> result = new Result<>();
        String fileType = fileUploadDTO.getFileType();
        String md5 = fileUploadDTO.getMd5();
        String fileName = fileUploadDTO.getFileName();
        String dirPath = fileUploadPath + File.separator + fileType + File.separator + md5;
        String fileName0 = fileName.split("\\.")[0];
        File chunkFolderFile = new File(dirPath + File.separator + fileName0 + ".chunk");
        File[] files = chunkFolderFile.listFiles();
        File mergeFile = new File(dirPath + File.separator + fileName);
        if (files != null) {
            List<File> fileList = Arrays.asList(files);
            FileUtils.shardMerge(fileList, mergeFile);
            FileUtils.delFile(chunkFolderFile);
            result.success("分片合并成功");
        } else {
            result.error500("分片合并失败");
        }
        result.setResult(File.separator + fileType + File.separator + md5 + File.separator + fileName);
        return result;
    }

}
