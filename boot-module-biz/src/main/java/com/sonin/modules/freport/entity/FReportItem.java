package com.sonin.modules.freport.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 报表数据项管理
 * </p>
 *
 * @author sonin
 * @since 2023-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FReportItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    /**
     * 报表id
     */
    private String reportId;

    /**
     * 数据项id
     */
    private String itemId;

    /**
     * 数据项别名
     */
    private String itemAlias;

    /**
     * 数据项名称
     */
    private String itemCode;

    /**
     * 数据项单位
     */
    private String unit;

    /**
     * 数据有效性最大
     */
    private Double trendMax;

    /**
     * 数据有效性最小
     */
    private Double trendMin;

    /**
     * 报警上限
     */
    private Double alarmMax;

    /**
     * 超出上限背景色
     */
    private String alarmMaxColor;

    /**
     * 报警下限
     */
    private Double alarmMin;

    /**
     * 低于下限背景色
     */
    private String alarmMinColor;

    /**
     * 文本框类型
     */
    private String textType;

    /**
     * 字典CODE/时间格式/正则
     */
    private String typeInit;

    /**
     * 1是0否
     */
    private Integer required;

    /**
     * 是否必填项
     */
    private String isMust;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序序号
     */
    private Integer sortNum;

    private String departId;

    /**
     * 创建人Id
     */
    private String createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改人Id
     */
    private String updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 所属部门
     */
    private String createDept;

    /**
     * 所属公司
     */
    private String createCmpy;

    /**
     * 删除标识
     */
    private Integer delFlag;

    /**
     * 是否能效分析指标
     */
    private String isUsePowerAnalyse;

    /**
     * 组织编码
     */
    private String orgCode;

    /**
     * 计算类型
     */
    private String calculationType;


}
