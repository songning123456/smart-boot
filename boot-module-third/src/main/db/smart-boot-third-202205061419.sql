-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: 192.168.2.126    Database: smart-boot-third
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
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(200) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_cron_triggers` VALUES ('DefaultQuartzScheduler','triggerName','triggerGroup','* * * * * ? *','Asia/Shanghai');
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
INSERT INTO `qrtz_job_details` VALUES ('DefaultQuartzScheduler','jobName','jobGroup',NULL,'com.sonin.modules.quartz.job.HelloJob','0','0','0','0','#\r\n#Wed May 04 10:22:38 CST 2022\r\n');
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
INSERT INTO `qrtz_locks` VALUES ('DefaultQuartzScheduler','STATE_ACCESS'),('DefaultQuartzScheduler','TRIGGER_ACCESS');
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
INSERT INTO `qrtz_scheduler_state` VALUES ('DefaultQuartzScheduler','LAPTOP-27GLFF1E1651817487691',1651817932972,7500),('DefaultQuartzScheduler','sit4.com1651729694657',1651817937946,7500);
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_triggers` VALUES ('DefaultQuartzScheduler','triggerName','triggerGroup','jobName','jobGroup',NULL,1651634094000,1651634093000,5,'PAUSED','CRON',1651630958000,0,NULL,0,'');
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_log` (
  `id` char(32) NOT NULL,
  `log_name` varchar(255) DEFAULT NULL COMMENT '类的全路径名称',
  `message` text COMMENT '打印的消息',
  `thread_name` varchar(255) DEFAULT NULL COMMENT '线程名称',
  `time_stamp` varchar(50) DEFAULT NULL COMMENT '日志打印的时间',
  `log_level` varchar(20) DEFAULT NULL COMMENT '日志打印级别',
  `log_type` tinyint(4) DEFAULT NULL COMMENT '日志类型: 1:logback,2:log4j2',
  `log_ip` varchar(20) DEFAULT NULL COMMENT 'IP地址',
  `log_port` int(11) DEFAULT NULL COMMENT '端口号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
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
INSERT INTO `sys_menu` VALUES ('04494adb2368de8815ac4b45083ea4e9',NULL,'','el-icon-help',3000,'2022-05-03 10:37:25','2022-05-03 10:37:56','第三方组件'),('1a94b1a953bca4ffd9b933d175f84ec0',NULL,'/index','el-icon-s-home',0,'2022-04-05 10:28:17','2022-04-05 10:28:17','首页'),('39fdd5265e35aefed3c264b7925859be','474ef2d7eae9c7b4a7ef586151bf1ccb','/element/FileUpload','el-icon-upload',36,'2022-04-06 14:54:58','2022-05-01 16:30:37','文件上传'),('3d0bcf6b35b04514911fecaa187b6aff','e44ce36651844b94b6b10678673e2e50','/sys/Role','el-icon-rank',1001,'2021-01-15 19:03:45','2021-01-15 19:03:48','角色管理'),('474ef2d7eae9c7b4a7ef586151bf1ccb','efd32f223b538f597002dae4a9a119f5','','el-icon-files',35,'2022-05-01 16:27:18','2022-05-01 16:27:18','文件系列'),('4ddbc7acd2f34a0b96a8700de9c44428','e44ce36651844b94b6b10678673e2e50','/sys/User','el-icon-s-custom',1002,'2021-01-15 19:03:45','2021-01-15 19:03:48','用户管理'),('521c9bd933de0dde8b9c7265561bbfc0','e16758278a537b64a3dc16becac7123d','/information/UserCenter','el-icon-s-custom',401,'2022-04-06 11:00:03','2022-04-06 11:00:03','账户设置'),('71c6f63d358747ada79ac9901e833e4f','99d6478dbd064813afb3473fcd2e2325','/sys/Dict','el-icon-s-order',2001,'2021-01-15 19:07:18','2021-01-18 16:32:13','数字字典'),('911dfe44d178905693788eddb5b330e2','efd32f223b538f597002dae4a9a119f5','/element/SSH','el-icon-camera',34,'2022-05-01 16:22:43','2022-05-01 16:24:29','SSH连接'),('99d6478dbd064813afb3473fcd2e2325','',NULL,'el-icon-s-tools',2000,'2021-01-15 19:06:11','2022-04-02 13:59:01','系统工具'),('9c799cd348125baad107208e9fd87c6d','474ef2d7eae9c7b4a7ef586151bf1ccb','/element/FileList','el-icon-files',37,'2022-04-09 13:55:26','2022-05-01 16:30:33','文件列表'),('be5a2a5f34e85aaecc49c67b9782a270','99d6478dbd064813afb3473fcd2e2325','/sys/Druid','el-icon-setting',2002,'2022-04-30 08:23:36','2022-04-30 08:23:36','Druid监控'),('c534a03d1e5f426ab64d833c62acb3ce','e44ce36651844b94b6b10678673e2e50','/sys/Menu','el-icon-menu',1003,'2021-01-15 19:03:45','2021-01-15 19:03:48','菜单管理'),('e16758278a537b64a3dc16becac7123d',NULL,NULL,'el-icon-info',400,'2022-04-06 10:58:48','2022-04-06 10:58:48','信息资料'),('e44ce36651844b94b6b10678673e2e50','','','el-icon-s-operation',1000,'2021-01-15 18:58:18','2022-03-29 15:35:37','系统管理'),('e472f7730957beca7808fea2949465b3','04494adb2368de8815ac4b45083ea4e9','','el-icon-bell',3100,'2022-05-03 10:39:50','2022-05-03 10:40:52','Quartz组件'),('ed154f50511f7e04de67270004d27ce9','efd32f223b538f597002dae4a9a119f5','/element/Websocket','el-icon-link',33,'2022-04-23 17:06:33','2022-04-23 17:06:33','Websocket'),('efd32f223b538f597002dae4a9a119f5',NULL,NULL,'el-icon-s-tools',30,'2022-04-06 14:53:56','2022-04-06 14:53:56','组件工具'),('f9dbba8acace9a85135744332566a0a6','e472f7730957beca7808fea2949465b3','/third/quartz/QuartzConfig','el-icon-s-open',3101,'2022-05-03 10:40:44','2022-05-03 10:40:44','Quartz配置');
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
INSERT INTO `sys_role_menu` VALUES ('a07acfff03be9d801c19ba338982b765','074fb9ddf4f24478bf06da0f5619dc78','04494adb2368de8815ac4b45083ea4e9'),('8ab752e12f0945245402af865281bec5','074fb9ddf4f24478bf06da0f5619dc78','1a94b1a953bca4ffd9b933d175f84ec0'),('2f654d746e165f89f8afa6ecd322e8d1','074fb9ddf4f24478bf06da0f5619dc78','39fdd5265e35aefed3c264b7925859be'),('aa619a9fe34067e0cdd3d9fa7a373694','074fb9ddf4f24478bf06da0f5619dc78','3d0bcf6b35b04514911fecaa187b6aff'),('3f65673a850ad947c1d9ce379562a42a','074fb9ddf4f24478bf06da0f5619dc78','474ef2d7eae9c7b4a7ef586151bf1ccb'),('a95a7e68753fe641f081077275b1d40e','074fb9ddf4f24478bf06da0f5619dc78','4ddbc7acd2f34a0b96a8700de9c44428'),('6544f21b5b526af5a3aeeb0cee06dce1','074fb9ddf4f24478bf06da0f5619dc78','521c9bd933de0dde8b9c7265561bbfc0'),('6514af5619016ca34bef35dc7b82680a','074fb9ddf4f24478bf06da0f5619dc78','71c6f63d358747ada79ac9901e833e4f'),('86a7e1f8a911ea7c35d534745119160e','074fb9ddf4f24478bf06da0f5619dc78','911dfe44d178905693788eddb5b330e2'),('da36a50a49869d5277875d4a69f89dc4','074fb9ddf4f24478bf06da0f5619dc78','99d6478dbd064813afb3473fcd2e2325'),('a0022152d32672d892e074531a85b7a5','074fb9ddf4f24478bf06da0f5619dc78','9c799cd348125baad107208e9fd87c6d'),('9ae6b2a3199fed3da79aac17c80ec7d7','074fb9ddf4f24478bf06da0f5619dc78','be5a2a5f34e85aaecc49c67b9782a270'),('1e8b2ffa1c1e1d2082d7106713dc5891','074fb9ddf4f24478bf06da0f5619dc78','c534a03d1e5f426ab64d833c62acb3ce'),('fbafd360c59ad9fa7ec3cc19eb69e75c','074fb9ddf4f24478bf06da0f5619dc78','e16758278a537b64a3dc16becac7123d'),('a6cefe4545e723f4e315244b45d07a35','074fb9ddf4f24478bf06da0f5619dc78','e44ce36651844b94b6b10678673e2e50'),('3341d8b6fd690fd0f73a7021a54994ba','074fb9ddf4f24478bf06da0f5619dc78','e472f7730957beca7808fea2949465b3'),('bb91c21e0b2aa04678785134681f1d3b','074fb9ddf4f24478bf06da0f5619dc78','ed154f50511f7e04de67270004d27ce9'),('093663814b8c725c486bbbac9907461f','074fb9ddf4f24478bf06da0f5619dc78','efd32f223b538f597002dae4a9a119f5'),('b5c7a11e12f8123329b251da1c138ce3','074fb9ddf4f24478bf06da0f5619dc78','f9dbba8acace9a85135744332566a0a6'),('d0f0c01090bf9d6ab27b1cae750476cd','134de5c7260406a9386698c5fef7edf5','04494adb2368de8815ac4b45083ea4e9'),('29f175fc9bd9b05d1b00b9f89cb8b89e','134de5c7260406a9386698c5fef7edf5','1a94b1a953bca4ffd9b933d175f84ec0'),('223d4b464ec77c75ae12b4356db1a978','134de5c7260406a9386698c5fef7edf5','39fdd5265e35aefed3c264b7925859be'),('ba7f34c8af9b585ee475cf33693f9682','134de5c7260406a9386698c5fef7edf5','474ef2d7eae9c7b4a7ef586151bf1ccb'),('8bd9452083bf4045f5d439a67329fe4f','134de5c7260406a9386698c5fef7edf5','521c9bd933de0dde8b9c7265561bbfc0'),('d3110c33aa6a11dfcce753ce6232ae38','134de5c7260406a9386698c5fef7edf5','71c6f63d358747ada79ac9901e833e4f'),('1ed356e5fec9b6b090f9421d79f77c44','134de5c7260406a9386698c5fef7edf5','911dfe44d178905693788eddb5b330e2'),('3a08f6a3300ae564e01f3b40fda2e3af','134de5c7260406a9386698c5fef7edf5','99d6478dbd064813afb3473fcd2e2325'),('d69ee6ba6ef8e87626d65116c47bf23e','134de5c7260406a9386698c5fef7edf5','9c799cd348125baad107208e9fd87c6d'),('08834bd9f8a43ced17ee3143e5aa16a6','134de5c7260406a9386698c5fef7edf5','e16758278a537b64a3dc16becac7123d'),('3ee2bb02c997f25f443ca83d45f60cad','134de5c7260406a9386698c5fef7edf5','e472f7730957beca7808fea2949465b3'),('d1bb2e7179ad8326c15ad0b28d63210e','134de5c7260406a9386698c5fef7edf5','ed154f50511f7e04de67270004d27ce9'),('61f0018c2464d191e56fa5829264899e','134de5c7260406a9386698c5fef7edf5','efd32f223b538f597002dae4a9a119f5'),('2933f8c41f0e6b009aae985a023558f6','134de5c7260406a9386698c5fef7edf5','f9dbba8acace9a85135744332566a0a6');
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
-- Dumping routines for database 'smart-boot-third'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-06 14:19:00
