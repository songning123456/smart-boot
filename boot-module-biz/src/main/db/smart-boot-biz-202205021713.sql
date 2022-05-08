-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: 192.168.2.126    Database: smart-boot-biz
-- ------------------------------------------------------
-- Server version	5.7.29-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_datasource`
--

DROP TABLE IF EXISTS `sys_datasource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_datasource` (
  `id` char(32) NOT NULL COMMENT '主键',
  `datasource` varchar(255) DEFAULT NULL COMMENT '数据源名称',
  `username` varchar(50) DEFAULT NULL COMMENT '数据库用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '数据库密码',
  `driver_class_name` varchar(255) DEFAULT NULL COMMENT '数据库驱动名称',
  `url` varchar(255) DEFAULT NULL COMMENT '数据库地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分库配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_datasource`
--

LOCK TABLES `sys_datasource` WRITE;
/*!40000 ALTER TABLE `sys_datasource` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_datasource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dict` (
  `id` char(32) NOT NULL,
  `dict_name` varchar(100) DEFAULT NULL COMMENT '字典名称',
  `dict_code` varchar(100) DEFAULT NULL COMMENT '字典编码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_dict_code` (`dict_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict` DISABLE KEYS */;
INSERT INTO `sys_dict` VALUES ('bb2be164c943d11689a76209018c4474','文件上传路径','FileUploadPath','文件上传路径','2022-04-10 11:10:49','2022-04-10 11:10:49'),('da510df2b1120f20c6a2cb24273c85c9','Websocket接收用户','WSPushUsers','Websocket接收用户','2022-05-02 08:56:49','2022-05-02 08:56:49');
/*!40000 ALTER TABLE `sys_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_item`
--

DROP TABLE IF EXISTS `sys_dict_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_dict_item` (
  `id` char(32) NOT NULL,
  `sys_dict_id` varchar(32) DEFAULT NULL COMMENT '字典id',
  `item_text` varchar(100) DEFAULT NULL COMMENT '字典项文本',
  `item_value` varchar(100) DEFAULT NULL COMMENT '字典项值',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `order_num` int(10) DEFAULT NULL COMMENT '排序',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_sys_dict_id` (`sys_dict_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_item`
--

LOCK TABLES `sys_dict_item` WRITE;
/*!40000 ALTER TABLE `sys_dict_item` DISABLE KEYS */;
INSERT INTO `sys_dict_item` VALUES ('15c38d72808c7992f23d781c7552c9e5','da510df2b1120f20c6a2cb24273c85c9','指定用户','some','推送给一些指定用户',2,'2022-05-02 08:57:28','2022-05-02 08:57:28'),('2997c3ea417a31b68c3fdcced94772c6','bb2be164c943d11689a76209018c4474','其他','other','其他',4,'2022-04-10 11:11:47','2022-04-10 11:11:47'),('3a2e011a5729e5189048e6764eadf238','bb2be164c943d11689a76209018c4474','视频','video','视频',1,'2022-04-10 11:11:08','2022-04-10 11:11:08'),('5ae6547b0bad3195aae4868d7cac4fce','da510df2b1120f20c6a2cb24273c85c9','我','me','推送给自己',1,'2022-05-02 08:57:11','2022-05-02 08:57:11'),('79ff65ce6261a65da89103ff75097df6','bb2be164c943d11689a76209018c4474','图片','image','图片',3,'2022-04-10 11:11:29','2022-04-10 11:11:29'),('81b3ab7fffbafef0c316145391892acf','bb2be164c943d11689a76209018c4474','音乐','music','音乐',2,'2022-04-10 11:11:19','2022-04-10 11:11:19'),('8685b43df4a3515d5ac0dba916f01909','da510df2b1120f20c6a2cb24273c85c9','所有用户','all','推送给所有用户',3,'2022-05-02 08:57:51','2022-05-02 08:57:51'),('fab1ea2064e48a182df94e406125d9ad','bb2be164c943d11689a76209018c4474','默认','default','默认',5,'2022-04-11 13:59:48','2022-04-11 13:59:48');
/*!40000 ALTER TABLE `sys_dict_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_menu` (
  `id` varchar(32) NOT NULL,
  `parent_id` varchar(32) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `path` varchar(255) DEFAULT NULL COMMENT '菜单路径',
  `meta_icon` varchar(32) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `meta_title` varchar(32) NOT NULL COMMENT '菜单标题',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES ('1a94b1a953bca4ffd9b933d175f84ec0',NULL,'/index','el-icon-s-home',0,'2022-04-05 10:28:17','2022-04-05 10:28:17','首页'),('39fdd5265e35aefed3c264b7925859be','474ef2d7eae9c7b4a7ef586151bf1ccb','/element/FileUpload','el-icon-upload',36,'2022-04-06 14:54:58','2022-05-01 16:30:37','文件上传'),('3d0bcf6b35b04514911fecaa187b6aff','e44ce36651844b94b6b10678673e2e50','/sys/Role','el-icon-rank',1001,'2021-01-15 19:03:45','2021-01-15 19:03:48','角色管理'),('474ef2d7eae9c7b4a7ef586151bf1ccb','efd32f223b538f597002dae4a9a119f5','','el-icon-files',35,'2022-05-01 16:27:18','2022-05-01 16:27:18','文件系列'),('4ddbc7acd2f34a0b96a8700de9c44428','e44ce36651844b94b6b10678673e2e50','/sys/User','el-icon-s-custom',1002,'2021-01-15 19:03:45','2021-01-15 19:03:48','用户管理'),('521c9bd933de0dde8b9c7265561bbfc0','e16758278a537b64a3dc16becac7123d','/information/UserCenter','el-icon-s-custom',401,'2022-04-06 11:00:03','2022-04-06 11:00:03','账户设置'),('71c6f63d358747ada79ac9901e833e4f','99d6478dbd064813afb3473fcd2e2325','/sys/Dict','el-icon-s-order',2001,'2021-01-15 19:07:18','2021-01-18 16:32:13','数字字典'),('911dfe44d178905693788eddb5b330e2','efd32f223b538f597002dae4a9a119f5','/element/SSH','el-icon-camera',34,'2022-05-01 16:22:43','2022-05-01 16:24:29','SSH连接'),('99d6478dbd064813afb3473fcd2e2325','',NULL,'el-icon-s-tools',2000,'2021-01-15 19:06:11','2022-04-02 13:59:01','系统工具'),('9c799cd348125baad107208e9fd87c6d','474ef2d7eae9c7b4a7ef586151bf1ccb','/element/FileList','el-icon-files',37,'2022-04-09 13:55:26','2022-05-01 16:30:33','文件列表'),('be5a2a5f34e85aaecc49c67b9782a270','99d6478dbd064813afb3473fcd2e2325','/sys/Druid','el-icon-setting',2002,'2022-04-30 08:23:36','2022-04-30 08:23:36','Druid监控'),('c534a03d1e5f426ab64d833c62acb3ce','e44ce36651844b94b6b10678673e2e50','/sys/Menu','el-icon-menu',1003,'2021-01-15 19:03:45','2021-01-15 19:03:48','菜单管理'),('e16758278a537b64a3dc16becac7123d',NULL,NULL,'el-icon-info',400,'2022-04-06 10:58:48','2022-04-06 10:58:48','信息资料'),('e44ce36651844b94b6b10678673e2e50','','','el-icon-s-operation',1000,'2021-01-15 18:58:18','2022-03-29 15:35:37','系统管理'),('ed154f50511f7e04de67270004d27ce9','efd32f223b538f597002dae4a9a119f5','/element/Websocket','el-icon-link',33,'2022-04-23 17:06:33','2022-04-23 17:06:33','Websocket'),('efd32f223b538f597002dae4a9a119f5',NULL,NULL,'el-icon-s-tools',30,'2022-04-06 14:53:56','2022-04-06 14:53:56','组件工具'),('cfc6341d6019629ddd130c030d374b9d','99d6478dbd064813afb3473fcd2e2325','/sys/Log','el-icon-document-copy',2003,'2022-05-08 08:40:26','2022-05-08 08:40:26','系统日志');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role` (
  `id` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL COMMENT '名称',
  `code` varchar(64) NOT NULL COMMENT '编码',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`) USING BTREE,
  UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES ('074fb9ddf4f24478bf06da0f5619dc78','超级管理员','admin','系统默认最高权限，不可以编辑和任意修改','2021-01-16 13:29:03','2021-01-17 15:50:45'),('134de5c7260406a9386698c5fef7edf5','普通用户','normal','普通用户','2022-04-05 17:18:20','2022-04-05 17:18:20');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_role_menu` (
  `id` varchar(32) NOT NULL,
  `role_id` varchar(32) NOT NULL,
  `menu_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_menu_index` (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES ('30f2541fc5fd8f9336f0af5f70f31a07','074fb9ddf4f24478bf06da0f5619dc78','1a94b1a953bca4ffd9b933d175f84ec0'),('9ac01989ce952d04fbecab386fe69a55','074fb9ddf4f24478bf06da0f5619dc78','39fdd5265e35aefed3c264b7925859be'),('d55106bf79e033432f41393ef3ec65fa','074fb9ddf4f24478bf06da0f5619dc78','3d0bcf6b35b04514911fecaa187b6aff'),('9159acf26dca532aed73f8b56f343423','074fb9ddf4f24478bf06da0f5619dc78','474ef2d7eae9c7b4a7ef586151bf1ccb'),('a7d8e753ac2b2c1c4e85eeedb7b62b33','074fb9ddf4f24478bf06da0f5619dc78','4ddbc7acd2f34a0b96a8700de9c44428'),('005f88eb7f4ee4d40770411ad5344db1','074fb9ddf4f24478bf06da0f5619dc78','521c9bd933de0dde8b9c7265561bbfc0'),('aa52a7a8875cceb010ccd4519d8b575c','074fb9ddf4f24478bf06da0f5619dc78','71c6f63d358747ada79ac9901e833e4f'),('d5bab79406b469f1e6df4d319ad3fcaf','074fb9ddf4f24478bf06da0f5619dc78','911dfe44d178905693788eddb5b330e2'),('c249bf89818b99c67e70d485d4b9d6ca','074fb9ddf4f24478bf06da0f5619dc78','99d6478dbd064813afb3473fcd2e2325'),('62651a98b100c9f5ea6ae50b2d2b7770','074fb9ddf4f24478bf06da0f5619dc78','9c799cd348125baad107208e9fd87c6d'),('6085121224568bbd5b15d5477da9c383','074fb9ddf4f24478bf06da0f5619dc78','be5a2a5f34e85aaecc49c67b9782a270'),('3b711da4e648d08dfcdd76c9695ccde8','074fb9ddf4f24478bf06da0f5619dc78','c534a03d1e5f426ab64d833c62acb3ce'),('6691c897d2156144870494acd7eac4c6','074fb9ddf4f24478bf06da0f5619dc78','e16758278a537b64a3dc16becac7123d'),('fdab208317521196a4f5263b479eb6b1','074fb9ddf4f24478bf06da0f5619dc78','e44ce36651844b94b6b10678673e2e50'),('9e32ea8eefa2f40a5b1c37be9a1113ea','074fb9ddf4f24478bf06da0f5619dc78','ed154f50511f7e04de67270004d27ce9'),('1003582cb65c33c76167aa1fbd9d3b94','074fb9ddf4f24478bf06da0f5619dc78','efd32f223b538f597002dae4a9a119f5'),('f778a00eb0c68d7dcb01c71b1f51d3d8','134de5c7260406a9386698c5fef7edf5','1a94b1a953bca4ffd9b933d175f84ec0'),('b33f1c490a194e6d3bf11590f2b467f7','134de5c7260406a9386698c5fef7edf5','39fdd5265e35aefed3c264b7925859be'),('881202e765b379b9f7b037813dc71382','134de5c7260406a9386698c5fef7edf5','474ef2d7eae9c7b4a7ef586151bf1ccb'),('0f90ccd9183af41f4388e3ac6b7cad57','134de5c7260406a9386698c5fef7edf5','521c9bd933de0dde8b9c7265561bbfc0'),('a953f3e755e53d88ffdbcfefa4db7ad7','134de5c7260406a9386698c5fef7edf5','71c6f63d358747ada79ac9901e833e4f'),('7e6609be351046d44374bb60bed9ea51','134de5c7260406a9386698c5fef7edf5','911dfe44d178905693788eddb5b330e2'),('94af1d3d2bbb1c79e7c6f419767ded03','134de5c7260406a9386698c5fef7edf5','99d6478dbd064813afb3473fcd2e2325'),('9fbc07cdaf6cdac6bcd1bd71eee2c15f','134de5c7260406a9386698c5fef7edf5','9c799cd348125baad107208e9fd87c6d'),('f1fcdd820940fa05047080ca5006dc91','134de5c7260406a9386698c5fef7edf5','e16758278a537b64a3dc16becac7123d'),('6c76fe20cad46e002cfa82153f29e04d','134de5c7260406a9386698c5fef7edf5','ed154f50511f7e04de67270004d27ce9'),('b2a2bc42955238facf92eb146ee8a527','134de5c7260406a9386698c5fef7edf5','efd32f223b538f597002dae4a9a119f5');
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL,
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `realname` varchar(255) NOT NULL COMMENT '昵称',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `email` varchar(64) DEFAULT NULL COMMENT '电子邮箱',
  `address` varchar(64) DEFAULT NULL COMMENT '地址',
  `telephone` varchar(30) DEFAULT NULL COMMENT '联系方式',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username_index` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES ('4dfdd4628d2b4de8928cb65f229239ec','admin','管理员','$2a$10$aKLb/qV1b4MtXiyBCDbxa.03hWR5etQT2/Yr5YHGhx/thikFJq3pi','https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg','1457065857@qq.com','南京市','15850682191','2021-01-12 22:13:53','2021-01-16 16:57:32'),('8f67e86c4c646af87853909755c6219a','test','测试人员','$2a$10$aKLb/qV1b4MtXiyBCDbxa.03hWR5etQT2/Yr5YHGhx/thikFJq3pi','https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg','1457065857@qq.com','南京市','15850682191','2022-04-05 17:19:14','2022-04-06 14:45:34');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `role_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_index` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES ('42394118b01244ba894da15018678cf6','4dfdd4628d2b4de8928cb65f229239ec','074fb9ddf4f24478bf06da0f5619dc78'),('728fd10cd390f83a8cdb7b762e3e19af','8f67e86c4c646af87853909755c6219a','134de5c7260406a9386698c5fef7edf5');
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'smart-boot-biz'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-02 17:13:34
