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

    private static final String SUFFIX_NAME = ".chunk";

    @GetMapping("/isExist")
    public Result<Object> isExistCtrl(FileUploadDTO fileUploadDTO) {
        Result<Object> result = new Result<>();
        String fileType = fileUploadDTO.getFileType();
        String md5 = fileUploadDTO.getMd5();
        // e.g: 测试视频.mp4
        String fileName = fileUploadDTO.getFileName();
        String prefixName = fileName.split("\\.")[0];
        // 上传文件所存放的目录(每个文件都有一个唯一的目录)
        File uploadDir = new File(fileUploadPath + File.separator + fileType + File.separator + md5);
        // e.g: [0, 1, 2, 3......]
        List<Integer> shardNameList = new ArrayList<>();
        if (uploadDir.exists()) {
            // 在文件目录下寻找分片目录: 测试视频.chunk
            File[] tmpShardDirs = uploadDir.listFiles();
            if (tmpShardDirs != null && tmpShardDirs.length != 0) {
                for (File tmpShardDir : tmpShardDirs) {
                    // 判断是否存在文件夹，即已经上传的部分
                    if (tmpShardDir.isDirectory() && prefixName.equals(tmpShardDir.getName().split("\\.")[0])) {
                        File[] shardFiles = tmpShardDir.listFiles();
                        if (shardFiles != null && shardFiles.length != 0) {
                            for (File shardFile : shardFiles) {
                                shardNameList.add(Integer.parseInt(shardFile.getName()));
                            }
                        }
                        break;
                    }
                }
            }
            if (shardNameList.isEmpty()) {
                result.setMessage("文件已上传");
                result.setResult(File.separator + fileType + File.separator + md5 + File.separator + fileName);
            } else {
                result.setResult(shardNameList);
                result.setMessage("文件仅上传部分");
            }
        } else {
            result.error500("文件未上传");
        }
        return result;
    }

    @PostMapping("/shardUpload")
    public Result<?> shardUploadCtrl(@RequestParam("file") MultipartFile file,
                                     @RequestParam("fileType") String fileType,
                                     @RequestParam("md5") String md5,
                                     @RequestParam("fileName") String fileName,
                                     @RequestParam(name = "currentShard", defaultValue = "-1") Integer currentShard) throws Exception {
        Result<?> result = new Result<>();
        String uploadDir = fileUploadPath + File.separator + fileType + File.separator + md5;
        // e.g: 测试视频.mp4
        String prefixName = fileName.split("\\.")[0];
        if (FileUtils.mkDir(uploadDir)) {
            // e.g: /.../测试视频.chunk
            File tmpShardDir = new File(uploadDir + File.separator + prefixName + SUFFIX_NAME);
            if (!tmpShardDir.exists()) {
                tmpShardDir.mkdirs();
            }
            // e.g: /.../测试视频.chunk/0, /.../测试视频.chunk/1 ...
            File shardFile = new File(uploadDir + File.separator + prefixName + SUFFIX_NAME + File.separator + currentShard);
            if (!shardFile.exists()) {
                file.transferTo(shardFile);
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
        String uploadDir = fileUploadPath + File.separator + fileType + File.separator + md5;
        String prefixName = fileName.split("\\.")[0];
        // 临时的分片目录: /.../测试视频.chunk
        File tmpShardDir = new File(uploadDir + File.separator + prefixName + SUFFIX_NAME);
        // e.g: 0、1、2、3......类似这样的文件
        File[] shardFiles = tmpShardDir.listFiles();
        File mergeFile = new File(uploadDir + File.separator + fileName);
        if (shardFiles != null) {
            FileUtils.shardMerge(Arrays.asList(shardFiles), mergeFile);
            FileUtils.delFile(tmpShardDir);
            result.success("分片合并成功");
        } else {
            result.error500("分片合并失败");
        }
        // 返回结果路径
        result.setResult(File.separator + fileType + File.separator + md5 + File.separator + fileName);
        return result;
    }

}
