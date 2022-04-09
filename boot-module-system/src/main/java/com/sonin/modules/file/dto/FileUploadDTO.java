package com.sonin.modules.file.dto;

import lombok.Data;

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

    private String uploadPath;

    private String md5;

    private String fileName;

}
