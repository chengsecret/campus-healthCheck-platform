/*
SQLyog Ultimate v12.5.1 (64 bit)
MySQL - 5.6.46 : Database - graduate_daka_server
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`graduate_daka_server` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `graduate_daka_server`;

/*Table structure for table `classroom` */

DROP TABLE IF EXISTS `classroom`;

CREATE TABLE `classroom` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '班级表',
  `mid` int(11) DEFAULT NULL COMMENT '所属专业',
  `num` varchar(16) DEFAULT NULL COMMENT '班级号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `classroom` */

insert  into `classroom`(`id`,`mid`,`num`) values 
(1,1,'18052312'),
(2,1,'18052313'),
(3,2,'18085566'),
(4,3,'180135644'),
(5,4,'180905522');

/*Table structure for table `healthinfo` */

DROP TABLE IF EXISTS `healthinfo`;

CREATE TABLE `healthinfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户健康（码）信息',
  `openid` varchar(255) DEFAULT NULL COMMENT '所属用户',
  `color` int(8) DEFAULT NULL COMMENT '通行码颜色绿紫黄红分别为1 2 3 4',
  `reason` int(8) DEFAULT NULL COMMENT '建康码颜色的原因有7种情况',
  `updatetime` varchar(32) DEFAULT NULL COMMENT '上一次的打卡时间',
  `continuousnum` int(11) DEFAULT NULL COMMENT '截至上次打卡连续打卡的天数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `healthinfo` */

insert  into `healthinfo`(`id`,`openid`,`color`,`reason`,`updatetime`,`continuousnum`) values 
(4,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU',2,2,'2022-05-06',1);

/*Table structure for table `major` */

DROP TABLE IF EXISTS `major`;

CREATE TABLE `major` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '专业表',
  `name` varchar(255) DEFAULT NULL COMMENT '专业名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `major` */

insert  into `major`(`id`,`name`) values 
(1,'计算机科学与技术'),
(2,'软件工程'),
(3,'网络安全'),
(4,'人文与法');

/*Table structure for table `record_daka` */

DROP TABLE IF EXISTS `record_daka`;

CREATE TABLE `record_daka` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '打卡记录',
  `openid` varchar(255) DEFAULT NULL COMMENT '用户身份',
  `state` int(11) DEFAULT NULL COMMENT '健康状况（3种情况）',
  `zjpicture` int(11) DEFAULT NULL COMMENT '浙江建康码颜色（无红黄绿）',
  `time` varchar(32) DEFAULT NULL COMMENT '打卡时间',
  `longitude` double DEFAULT NULL COMMENT '经度',
  `latitude` double DEFAULT NULL COMMENT '纬度',
  `isrisk` int(11) DEFAULT NULL COMMENT '是否为风险地区（1是 0不是）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

/*Data for the table `record_daka` */

insert  into `record_daka`(`id`,`openid`,`state`,`zjpicture`,`time`,`longitude`,`latitude`,`isrisk`) values 
(2,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU',0,3,'2022-05-03',120.15515,30.27415,0),
(3,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU',0,3,'2022-05-04',120.15515,30.27415,0),
(5,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU',0,3,'2022-05-06',120.15515,30.27415,0);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(255) NOT NULL COMMENT '用户身份（微信生成）',
  `name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `num` varchar(16) DEFAULT NULL COMMENT '学号',
  `classnum` varchar(16) DEFAULT NULL COMMENT '班级号',
  `mid` int(11) DEFAULT NULL COMMENT '专业（major表中的id）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`openid`,`name`,`num`,`classnum`,`mid`) values 
(19,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU','cheng','180512','18052313',1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
