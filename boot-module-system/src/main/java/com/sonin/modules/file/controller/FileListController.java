package com.sonin.modules.file.controller;

import cn.hutool.core.util.StrUtil;
import com.sonin.api.vo.Result;
import com.sonin.encryption.sm2.utils.ByteUtils;
import com.sonin.encryption.sm2.utils.SM2Utils;
import com.sonin.modules.file.dto.FileListDTO;
import com.sonin.modules.file.entity.FileList;
import com.sonin.modules.file.vo.FileListVO;
import com.sonin.utils.BeanExtUtils;
import com.sonin.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <pre>
 * 文件列表
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/9 14:36
 */
@RestController
@RequestMapping("/file/fileList")
public class FileListController {

    @Value(value = "${boot.path.fileUpload}")
    private String fileUploadPath;

    @Value(value = "${boot.encryption.sm2.publicKey}")
    private String publicKey;

    @Value(value = "${boot.encryption.sm2.privateKey}")
    private String privateKey;

    @GetMapping("/list")
    public Result<List<FileListVO>> listCtrl(FileListDTO fileListDTO) {
        Result<List<FileListVO>> result = new Result<>();
        String uploadPath = fileListDTO.getUploadPath();
        // 上传文件所存放的目录(每个文件都有一个唯一的目录)
        File uploadDir;
        if (StringUtils.isEmpty(uploadPath)) {
            uploadPath = "default";
        }
        uploadDir = new File(fileUploadPath + File.separator + uploadPath);
        if (uploadDir.exists()) {
            List<FileList> fileLists = new ArrayList<>();
            buildList(uploadDir, fileLists, "", uploadPath);
            List<FileListVO> fileListVOList = buildTree(fileLists);
            result.setResult(fileListVOList);
        } else {
            result.setResult(new ArrayList<>());
        }
        return result;
    }

    private void buildList(File file, List<FileList> fileLists, String parentId, String uploadPath) {
        FileList fileList = new FileList();
        fileList.setId(file.getAbsolutePath());
        fileList.setEncryptionId(SM2Utils.encrypt(ByteUtils.hexToByte(publicKey), file.getAbsolutePath().getBytes()));
        fileList.setFileName(file.getName());
        fileList.setParentId(parentId);
        if (file.isFile()) {
            fileList.setFileType("file");
            String[] params;
            if (System.getProperties().getProperty("os.name").contains("Windows")) {
                params = fileList.getId().split("\\\\");
            } else  {
                params = fileList.getId().split("/");
            }
            boolean flag = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (String param : params) {
                if (uploadPath.equals(param)) {
                    flag = true;
                }
                if (flag) {
                    stringBuilder.append(File.separator).append(param);
                }
            }
            fileList.setSuffixUrl(stringBuilder.toString());
        } else if (file.isDirectory()) {
            fileList.setFileType("directory");
        } else if (file.isHidden()) {
            fileList.setFileType("hidden");
        } else if (file.isAbsolute()) {
            fileList.setFileType("absolute");
        }
        fileList.setUpdateTime(new Date(file.lastModified()));
        fileLists.add(fileList);
        if (file.isFile()) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File fileEntity : files) {
            buildList(fileEntity, fileLists, file.getAbsolutePath(), uploadPath);
        }
    }

    private List<FileListVO> buildTree(List<FileList> fileLists) {
        List<FileListVO> fileListVOS = new ArrayList<>();
        List<FileListVO> fileListVOList = new ArrayList<>();
        try {
            fileListVOList = BeanExtUtils.beans2Beans(fileLists, FileListVO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 先各自寻找到各自的孩子
        for (FileListVO k : fileListVOList) {
            for (FileListVO v : fileListVOList) {
                if (k.getId().equals(v.getParentId())) {
                    k.getChildren().add(v);
                }
            }
            // 提取出父节点
            if (StrUtil.isBlank(k.getParentId())) {
                fileListVOS.add(k);
            }
        }
        return fileListVOS;
    }

    @DeleteMapping("/delete/{encryptionId}")
    public Result deleteCtrl(@PathVariable("encryptionId") String encryptionId) {
        String path = new String(SM2Utils.decrypt(ByteUtils.hexToByte(privateKey), ByteUtils.hexToByte(encryptionId)));
        boolean successFlag = FileUtils.delFile(new File(path));
        if (successFlag) {
            return Result.ok();
        } else {
            return Result.error("删除失败");
        }
    }

}
