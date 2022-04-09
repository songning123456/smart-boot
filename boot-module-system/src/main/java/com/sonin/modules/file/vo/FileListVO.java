package com.sonin.modules.file.vo;

import com.sonin.modules.file.entity.FileList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * <请输入描述信息>
 * </pre>
 *
 * @author sonin
 * @version 1.0 2022/4/9 14:39
 */
@Data
public class FileListVO extends FileList {

    private List<FileListVO> children = new ArrayList<>();

}
