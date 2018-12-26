-- MySQL Script generated by MySQL Workbench
-- Mon Dec 24 23:43:01 2018
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema jwc
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema jwc
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `jwc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
USE `jwc` ;

-- -----------------------------------------------------
-- Table `jwc`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`user` (
  `uid` INT NOT NULL AUTO_INCREMENT,
  `role` INT NULL DEFAULT 1 COMMENT '1代表学生，2代表老师，0代表管理员',
  `username` VARCHAR(20) NULL,
  `nickname` VARCHAR(20) NULL,
  `password` VARCHAR(20) NULL,
  `unread` INT NULL DEFAULT 0,
  PRIMARY KEY (`uid`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `nickname_UNIQUE` (`nickname` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`course` (
  `course_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `institution_id` INT NOT NULL,
  `abstract` VARCHAR(200) NULL,
  PRIMARY KEY (`course_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`teacher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`teacher` (
  `tid` INT NOT NULL COMMENT '教师id等于userid',
  `name` VARCHAR(20) NULL,
  `title` VARCHAR(10) NULL,
  `abstract` VARCHAR(200) NULL,
  `institution_id` INT NULL,
  `school` VARCHAR(45) NULL,
  PRIMARY KEY (`tid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`institution`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`institution` (
  `institution_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NULL,
  PRIMARY KEY (`institution_id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`course_teacher_takes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`course_teacher_takes` (
  `taken_id` INT NOT NULL AUTO_INCREMENT,
  `course_id` INT NOT NULL,
  `teacher_id` INT NOT NULL,
  `year` INT NULL,
  `section` INT NULL,
  `activated` INT NULL DEFAULT 1,
  PRIMARY KEY (`taken_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`question`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`question` (
  `question_id` INT NOT NULL DEFAULT 0,
  `title` VARCHAR(20) NULL,
  `content` VARCHAR(200) NULL,
  `uid` INT NULL,
  `last_update` DATETIME NULL,
  `taken_id` INT NULL,
  `date` DATETIME NULL,
  `read` INT NULL DEFAULT 0,
  PRIMARY KEY (`question_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`answer` (
  `answer_id` INT NOT NULL DEFAULT 0,
  `question_id` INT NOT NULL DEFAULT 0,
  `content` VARCHAR(200) NULL,
  `from_id` INT NULL,
  `date` DATETIME NULL,
  `read` INT NULL DEFAULT 0,
  `ans_order` INT NULL,
  PRIMARY KEY (`answer_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`sub_answer`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`sub_answer` (
  `answer_id` INT NOT NULL DEFAULT 0,
  `to_id` INT NULL,
  `from_id` INT NULL,
  `date` DATETIME NULL,
  `read` INT NULL DEFAULT 0,
  `sub_answer_order` INT NULL,
  `content` VARCHAR(200) NULL,
  `sub_ans_id` INT NOT NULL AUTO_INCREMENT,
  `to_sub_ans_id` INT NULL,
  PRIMARY KEY (`sub_ans_id`),
  INDEX `index_date` (`date` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`files`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`files` (
  `ans_id` INT NULL,
  `files` MEDIUMBLOB NULL,
  `file_name` VARCHAR(45) NULL,
  `type` VARCHAR(20) NULL,
  `question_id` INT NULL,
  `order` INT NULL,
  INDEX `answer` (`ans_id` ASC) INVISIBLE,
  INDEX `question` (`question_id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`student`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`student` (
  `uid` INT NOT NULL,
  `name` VARCHAR(20) NULL,
  `school` VARCHAR(45) NULL,
  `inst_id` INT NULL,
  PRIMARY KEY (`uid`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`bannedRead`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`bannedRead` (
  `takes_id` INT NOT NULL,
  `stu_id` INT NULL,
  PRIMARY KEY (`takes_id`),
  INDEX `su` (`stu_id` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `jwc`.`bannedPost`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `jwc`.`bannedPost` (
  `takes_id` INT NOT NULL,
  `stu_id` INT NULL,
  PRIMARY KEY (`takes_id`),
  INDEX `stu` (`stu_id` ASC) VISIBLE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
