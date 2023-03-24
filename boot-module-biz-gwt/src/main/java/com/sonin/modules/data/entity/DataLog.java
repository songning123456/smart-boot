package com.sonin.modules.data.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 数据日志
 * </p>
 *
 * @author sonin
 * @since 2023-03-24
 */
@Data
public class DataLog {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 数据内容
     */
    private String dataContent;

    /**
     * 数据来源IP
     */
    private String dataSourceIp;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除状态(0正常;1已删除)
     */
    private String delFlag;

}
