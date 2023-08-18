## 建表

在创建业务表的时候，请默认包含下列字段，请用id字段作为表主键，下列字段数据框架会自动填充。

CREATE TABLE `table_name` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `create_by` varchar(40) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(40) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `order_num` int DEFAULT NULL COMMENT '排序字段',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除状态(0正常;1已删除)',
   PRIMARY KEY (`id`) USING BTREE
)  ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='table_name';

## 登录信息
username: admin
password: 123456