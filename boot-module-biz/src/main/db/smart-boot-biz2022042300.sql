/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.2.126
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : 192.168.2.126:3306
 Source Schema         : smart-boot-biz

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 23/04/2022 18:14:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `dict_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典编码',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `index_dict_code`(`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('bb2be164c943d11689a76209018c4474', '文件上传路径', 'FileUploadPath', '文件上传路径', '2022-04-10 11:10:49', '2022-04-10 11:10:49');

-- ----------------------------
-- Table structure for sys_dict_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_item`;
CREATE TABLE `sys_dict_item`  (
  `id` char(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `sys_dict_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典id',
  `item_text` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典项文本',
  `item_value` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字典项值',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `order_num` int(10) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_sys_dict_id`(`sys_dict_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict_item
-- ----------------------------
INSERT INTO `sys_dict_item` VALUES ('2997c3ea417a31b68c3fdcced94772c6', 'bb2be164c943d11689a76209018c4474', '其他', 'other', '其他', 4, '2022-04-10 11:11:47', '2022-04-10 11:11:47');
INSERT INTO `sys_dict_item` VALUES ('3a2e011a5729e5189048e6764eadf238', 'bb2be164c943d11689a76209018c4474', '视频', 'video', '视频', 1, '2022-04-10 11:11:08', '2022-04-10 11:11:08');
INSERT INTO `sys_dict_item` VALUES ('79ff65ce6261a65da89103ff75097df6', 'bb2be164c943d11689a76209018c4474', '图片', 'image', '图片', 3, '2022-04-10 11:11:29', '2022-04-10 11:11:29');
INSERT INTO `sys_dict_item` VALUES ('81b3ab7fffbafef0c316145391892acf', 'bb2be164c943d11689a76209018c4474', '音乐', 'music', '音乐', 2, '2022-04-10 11:11:19', '2022-04-10 11:11:19');
INSERT INTO `sys_dict_item` VALUES ('fab1ea2064e48a182df94e406125d9ad', 'bb2be164c943d11689a76209018c4474', '默认', 'default', '默认', 5, '2022-04-11 13:59:48', '2022-04-11 13:59:48');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `parent_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单路径',
  `meta_icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `meta_title` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单标题',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1a94b1a953bca4ffd9b933d175f84ec0', NULL, '/index', 'el-icon-s-home', 0, '2022-04-05 10:28:17', '2022-04-05 10:28:17', '首页');
INSERT INTO `sys_menu` VALUES ('39fdd5265e35aefed3c264b7925859be', 'efd32f223b538f597002dae4a9a119f5', '/element/FileUpload', 'el-icon-upload', 31, '2022-04-06 14:54:58', '2022-04-06 14:54:58', '文件上传');
INSERT INTO `sys_menu` VALUES ('3d0bcf6b35b04514911fecaa187b6aff', 'e44ce36651844b94b6b10678673e2e50', '/sys/Role', 'el-icon-rank', 1001, '2021-01-15 19:03:45', '2021-01-15 19:03:48', '角色管理');
INSERT INTO `sys_menu` VALUES ('4ddbc7acd2f34a0b96a8700de9c44428', 'e44ce36651844b94b6b10678673e2e50', '/sys/User', 'el-icon-s-custom', 1002, '2021-01-15 19:03:45', '2021-01-15 19:03:48', '用户管理');
INSERT INTO `sys_menu` VALUES ('521c9bd933de0dde8b9c7265561bbfc0', 'e16758278a537b64a3dc16becac7123d', '/information/UserCenter', 'el-icon-s-custom', 401, '2022-04-06 11:00:03', '2022-04-06 11:00:03', '账户设置');
INSERT INTO `sys_menu` VALUES ('71c6f63d358747ada79ac9901e833e4f', '99d6478dbd064813afb3473fcd2e2325', '/sys/Dict', 'el-icon-s-order', 2001, '2021-01-15 19:07:18', '2021-01-18 16:32:13', '数字字典');
INSERT INTO `sys_menu` VALUES ('99d6478dbd064813afb3473fcd2e2325', '', NULL, 'el-icon-s-tools', 2000, '2021-01-15 19:06:11', '2022-04-02 13:59:01', '系统工具');
INSERT INTO `sys_menu` VALUES ('9c799cd348125baad107208e9fd87c6d', 'efd32f223b538f597002dae4a9a119f5', '/element/FileList', 'el-icon-files', 32, '2022-04-09 13:55:26', '2022-04-09 13:55:26', '文件列表');
INSERT INTO `sys_menu` VALUES ('c534a03d1e5f426ab64d833c62acb3ce', 'e44ce36651844b94b6b10678673e2e50', '/sys/Menu', 'el-icon-menu', 1003, '2021-01-15 19:03:45', '2021-01-15 19:03:48', '菜单管理');
INSERT INTO `sys_menu` VALUES ('e16758278a537b64a3dc16becac7123d', NULL, NULL, 'el-icon-info', 400, '2022-04-06 10:58:48', '2022-04-06 10:58:48', '信息资料');
INSERT INTO `sys_menu` VALUES ('e44ce36651844b94b6b10678673e2e50', '', '', 'el-icon-s-operation', 1000, '2021-01-15 18:58:18', '2022-03-29 15:35:37', '系统管理');
INSERT INTO `sys_menu` VALUES ('ed154f50511f7e04de67270004d27ce9', 'efd32f223b538f597002dae4a9a119f5', '/element/Websocket', 'el-icon-link', 33, '2022-04-23 17:06:33', '2022-04-23 17:06:33', 'Websocket');
INSERT INTO `sys_menu` VALUES ('efd32f223b538f597002dae4a9a119f5', NULL, NULL, 'el-icon-s-tools', 30, '2022-04-06 14:53:56', '2022-04-06 14:53:56', '组件工具');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名称',
  `code` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编码',
  `remark` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE,
  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('074fb9ddf4f24478bf06da0f5619dc78', '超级管理员', 'admin', '系统默认最高权限，不可以编辑和任意修改', '2021-01-16 13:29:03', '2021-01-17 15:50:45');
INSERT INTO `sys_role` VALUES ('134de5c7260406a9386698c5fef7edf5', '普通用户', 'normal', '普通用户', '2022-04-05 17:18:20', '2022-04-05 17:18:20');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_menu_index`(`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('da69ad773a4744ce8773af93270cebd6', '074fb9ddf4f24478bf06da0f5619dc78', '1a94b1a953bca4ffd9b933d175f84ec0');
INSERT INTO `sys_role_menu` VALUES ('e0450a2a5f8e25eb6113fd3d5feed627', '074fb9ddf4f24478bf06da0f5619dc78', '39fdd5265e35aefed3c264b7925859be');
INSERT INTO `sys_role_menu` VALUES ('a6006834e2214ef1a7a2d185962fe79c', '074fb9ddf4f24478bf06da0f5619dc78', '3d0bcf6b35b04514911fecaa187b6aff');
INSERT INTO `sys_role_menu` VALUES ('2d47330a53f804fb2b65ff3182987569', '074fb9ddf4f24478bf06da0f5619dc78', '4ddbc7acd2f34a0b96a8700de9c44428');
INSERT INTO `sys_role_menu` VALUES ('efe5ca83c60e228c8a3b039ff5f67665', '074fb9ddf4f24478bf06da0f5619dc78', '521c9bd933de0dde8b9c7265561bbfc0');
INSERT INTO `sys_role_menu` VALUES ('4baa8f02649ef5a6b146b8446e6794c4', '074fb9ddf4f24478bf06da0f5619dc78', '71c6f63d358747ada79ac9901e833e4f');
INSERT INTO `sys_role_menu` VALUES ('65fc8b0725bf88e8f98817da250fca7d', '074fb9ddf4f24478bf06da0f5619dc78', '99d6478dbd064813afb3473fcd2e2325');
INSERT INTO `sys_role_menu` VALUES ('9eec75815547a1e98a33055474a4c15d', '074fb9ddf4f24478bf06da0f5619dc78', '9c799cd348125baad107208e9fd87c6d');
INSERT INTO `sys_role_menu` VALUES ('8748297d671061f880d96c5ed84c6992', '074fb9ddf4f24478bf06da0f5619dc78', 'c534a03d1e5f426ab64d833c62acb3ce');
INSERT INTO `sys_role_menu` VALUES ('cbbe0d607224b0329220e0499bdf9caf', '074fb9ddf4f24478bf06da0f5619dc78', 'e16758278a537b64a3dc16becac7123d');
INSERT INTO `sys_role_menu` VALUES ('a52d2bdb33b23f7e3227a7533dadfabc', '074fb9ddf4f24478bf06da0f5619dc78', 'e44ce36651844b94b6b10678673e2e50');
INSERT INTO `sys_role_menu` VALUES ('8f55d24423540b4ca9837734f3a8f03e', '074fb9ddf4f24478bf06da0f5619dc78', 'ed154f50511f7e04de67270004d27ce9');
INSERT INTO `sys_role_menu` VALUES ('4aa9b4c4e9e46e3e8830bee1ec4da0e9', '074fb9ddf4f24478bf06da0f5619dc78', 'efd32f223b538f597002dae4a9a119f5');
INSERT INTO `sys_role_menu` VALUES ('d82281ec89ed2956c117d2a60ea9e039', '134de5c7260406a9386698c5fef7edf5', '1a94b1a953bca4ffd9b933d175f84ec0');
INSERT INTO `sys_role_menu` VALUES ('3c7aca68b4498099d0ac8e7dad2938f6', '134de5c7260406a9386698c5fef7edf5', '39fdd5265e35aefed3c264b7925859be');
INSERT INTO `sys_role_menu` VALUES ('53a8c1d6001016928dcfba07db7cffab', '134de5c7260406a9386698c5fef7edf5', '521c9bd933de0dde8b9c7265561bbfc0');
INSERT INTO `sys_role_menu` VALUES ('b7020ba3c4647f78b5eef06e4e0cf99d', '134de5c7260406a9386698c5fef7edf5', '71c6f63d358747ada79ac9901e833e4f');
INSERT INTO `sys_role_menu` VALUES ('090bd3634c02949566cc6cb8059fa74a', '134de5c7260406a9386698c5fef7edf5', '99d6478dbd064813afb3473fcd2e2325');
INSERT INTO `sys_role_menu` VALUES ('660248694d6cb78d83e8d97e9d70fb24', '134de5c7260406a9386698c5fef7edf5', '9c799cd348125baad107208e9fd87c6d');
INSERT INTO `sys_role_menu` VALUES ('c88fc3087cdc4ff36424fc0e5a7175c0', '134de5c7260406a9386698c5fef7edf5', 'e16758278a537b64a3dc16becac7123d');
INSERT INTO `sys_role_menu` VALUES ('88945799586438a8cb50a5a5878dbc9e', '134de5c7260406a9386698c5fef7edf5', 'ed154f50511f7e04de67270004d27ce9');
INSERT INTO `sys_role_menu` VALUES ('83eb3ee05af7073858444acc17a75740', '134de5c7260406a9386698c5fef7edf5', 'efd32f223b538f597002dae4a9a119f5');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `realname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电子邮箱',
  `address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `telephone` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username_index`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('4dfdd4628d2b4de8928cb65f229239ec', 'admin', '管理员', '$2a$10$aKLb/qV1b4MtXiyBCDbxa.03hWR5etQT2/Yr5YHGhx/thikFJq3pi', 'https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg', '1457065857@qq.com', '南京市', '15850682191', '2021-01-12 22:13:53', '2021-01-16 16:57:32');
INSERT INTO `sys_user` VALUES ('8f67e86c4c646af87853909755c6219a', 'test', '测试人员', '$2a$10$aKLb/qV1b4MtXiyBCDbxa.03hWR5etQT2/Yr5YHGhx/thikFJq3pi', 'https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg', '1457065857@qq.com', '南京市', '15850682191', '2022-04-05 17:19:14', '2022-04-06 14:45:34');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `role_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_role_index`(`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('42394118b01244ba894da15018678cf6', '4dfdd4628d2b4de8928cb65f229239ec', '074fb9ddf4f24478bf06da0f5619dc78');
INSERT INTO `sys_user_role` VALUES ('728fd10cd390f83a8cdb7b762e3e19af', '8f67e86c4c646af87853909755c6219a', '134de5c7260406a9386698c5fef7edf5');

SET FOREIGN_KEY_CHECKS = 1;
