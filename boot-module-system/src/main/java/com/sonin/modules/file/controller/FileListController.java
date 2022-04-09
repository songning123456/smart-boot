package com.sonin.modules.file.controller;

import cn.hutool.core.util.StrUtil;
import com.sonin.api.vo.Result;
import com.sonin.encryption.util.CommonUtils;
import com.sonin.encryption.util.SM2Utils;
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

    @Value(value = "${boot.encryption.publicKey}")
    private String publicKey;

    @Value(value = "${boot.encryption.privateKey}")
    private String privateKey;

    @GetMapping("/list")
    public Result<List<FileListVO>> listCtrl(FileListDTO fileListDTO) {
        Result<List<FileListVO>> result = new Result<>();
        String fileType = fileListDTO.getFileType();
        // 上传文件所存放的目录(每个文件都有一个唯一的目录)
        File uploadDir;
        if (StringUtils.isNotEmpty(fileType)) {
            uploadDir = new File(fileUploadPath + File.separator + fileType);
        } else {
            uploadDir = new File(fileUploadPath);
        }
        List<FileList> fileLists = new ArrayList<>();
        buildList(uploadDir, fileLists, "");
        List<FileListVO> fileListVOList = buildTree(fileLists);
        result.setResult(fileListVOList);
        return result;
    }

    private void buildList(File file, List<FileList> fileLists, String parentId) {
        FileList fileList = new FileList();
        fileList.setId(file.getAbsolutePath());
        fileList.setEncryptionId(SM2Utils.encrypt(CommonUtils.hexToByte(publicKey), file.getAbsolutePath().getBytes()));
        fileList.setFileName(file.getName());
        fileList.setParentId(parentId);
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
            buildList(fileEntity, fileLists, file.getAbsolutePath());
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
        String path = new String(SM2Utils.decrypt(CommonUtils.hexToByte(privateKey), CommonUtils.hexToByte(encryptionId)));
        boolean successFlag = FileUtils.delFile(new File(path));
        if (successFlag) {
            return Result.ok();
        } else {
            return Result.error("删除失败");
        }
    }

}
