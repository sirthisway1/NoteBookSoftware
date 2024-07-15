/*
 Navicat MySQL Dump SQL

 Source Server         : 111
 Source Server Type    : MySQL
 Source Server Version : 80019 (8.0.19)
 Source Host           : localhost:3306
 Source Schema         : notebook

 Target Server Type    : MySQL
 Target Server Version : 80019 (8.0.19)
 File Encoding         : 65001

 Date: 13/07/2024 10:13:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for m_comments
-- ----------------------------
DROP TABLE IF EXISTS `m_comments`;
CREATE TABLE `m_comments`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `note_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_note_id`(`note_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `m_comments_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `m_notes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `m_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `m_users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for m_note_likes
-- ----------------------------
DROP TABLE IF EXISTS `m_note_likes`;
CREATE TABLE `m_note_likes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `note_id` int NOT NULL,
  `user_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_note_user`(`note_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `m_note_likes_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `m_notes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `m_note_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `m_users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for m_notebooks
-- ----------------------------
DROP TABLE IF EXISTS `m_notebooks`;
CREATE TABLE `m_notebooks`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `m_notebooks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `m_users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for m_notes
-- ----------------------------
DROP TABLE IF EXISTS `m_notes`;
CREATE TABLE `m_notes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `notebook_id` int NOT NULL,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `is_private` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `notebook_id`(`notebook_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `m_notes_ibfk_1` FOREIGN KEY (`notebook_id`) REFERENCES `m_notebooks` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `m_notes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `m_users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for m_thought_notes
-- ----------------------------
DROP TABLE IF EXISTS `m_thought_notes`;
CREATE TABLE `m_thought_notes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `note_id` int NOT NULL,
  `type` int NOT NULL COMMENT '0-4代表5种类型的笔记',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `note_id`(`note_id` ASC) USING BTREE,
  CONSTRAINT `m_thought_notes_ibfk_1` FOREIGN KEY (`note_id`) REFERENCES `m_notes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for m_users
-- ----------------------------
DROP TABLE IF EXISTS `m_users`;
CREATE TABLE `m_users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
