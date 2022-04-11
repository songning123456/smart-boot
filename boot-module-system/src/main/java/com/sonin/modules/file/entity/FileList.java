package com.sonin.modules.file.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * <pre>
 * 文件列表对象
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/9 14:56
 */
@Data
public class FileList {

    private String id;

    /**
    * 加密ID
    */
    private String encryptionId;

    private String parentId;

    /**
     * 文件/目录 名称
     */
    private String fileName;

    /**
     * 文件类型: 文件/目录
     */
    private String fileType;

    /**
     * 后缀URL: e.g /image/md5值/test.jpeg
     */
    private String suffixUrl;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
