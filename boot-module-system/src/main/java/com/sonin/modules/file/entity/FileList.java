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

    private String parentId;

    private String fileName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
