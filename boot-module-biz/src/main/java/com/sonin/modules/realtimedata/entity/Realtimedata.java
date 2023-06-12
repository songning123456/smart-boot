package com.sonin.modules.realtimedata.entity;

import lombok.Data;

/**
 * <p>
 * 实时表
 * </p>
 *
 * @author sonin
 * @since 2023-06-12
 */
@Data
public class Realtimedata {

    private String id;

    private String nm;

    private String v;

    private String ts;

    private String createtime;

    private String factoryname;

    private String devicename;

    private String type;

    private String gatewaycode;

}
