package com.sonin.modules.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/6 17:26
 */
@Data
public class FileUploadDTO {

    private MultipartFile file;

    private String fileType;

    private String md5;

    private String fileName;

    private Integer currentChunk = -1;

}
