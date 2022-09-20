package com.sonin.modules.demo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 水表档案信息
 */
@Data
public class BaseMeterInfo {

    /**
     * 主键
     */
    private String id;
    /**
     * 户号
     */
    private String meterCode;
    /**
     * 区域编码
     */
    private String areaCode;
    /**
     * 用户名称
     */
    private String meterName;
    /**
     * 移动电话
     */
    private String mobilePhone;
    /**
     * 固定电话
     */
    private String telephone;
    /**
     * 水表状态
     */
    private String meterState;
    /**
     * 立户日期
     */
    private String openDate;
    /**
     * 用水性质
     */
    private String useWaterType;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 用户状态
     */
    private String userState;
    /**
     * 站点号
     */
    private String stationCode;
    /**
     * 安装人
     */
    private String installUser;
    /**
     * 安装日期
     */
    private String installDate;
    /**
     * 地址
     */
    private String installAddr;
    /**
     * 表身号
     */
    private String meterBodyCode;
    /**
     * 表册号
     */
    private String sheetCode;
    /**
     * 铅封号
     */
    private String leadSealingCode;
    /**
     * 口径
     */
    private java.math.BigDecimal meterCaliber;
    /**
     * 表编号（iot平台标识）
     */
    private String oldMeterIotCode;
    /**
     * 水表厂家
     */
    private String brandCode;
    /**
     * 水表型号
     */
    private String modelCode;
    /**
     * 水表类型（1远传大表/2远传小表/3手工大表/4手工小表）
     */
    private String meterType;
    /**
     * 抄表周期
     */
    private String copyCycle;
    /**
     * 抄表日期
     */
    private String copyDate;
    /**
     * 老表止码
     */
    private String oldCode;
    /**
     * 新表起码
     */
    private String newCode;
    /**
     * 是否关阀(0开/1关)
     */
    private String closeTag;
    /**
     * 是否换表(0未换表/1已换表)
     */
    private String changeTag;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 机构id
     */
    private String departId;
    /**
     * 水表用途编号
     */
    private String meterUsageId;
    /**
     * 抄表顺序号
     */
    private String sheetCopyNo;
    /**
     * 表位号
     */
    private String meterLocNo;
    /**
     * 营收分公司编码
     */
    private String ltdNo;
    /**
     * 表编号（iot平台标识）
     */
    private String meterIotCode;
    /**
     * 数据来源，1：小表；2：大表
     */
    private Integer meterIotCodeType;
    /**
     * 水表位置
     */
    private String meterLocation;
    /**
     * 位置分类
     */
    private String locationCategory;

    /**
     * 水表类型(营销系统)
     */
    private String categoryId;
}
