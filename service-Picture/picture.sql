/*
SQLyog Ultimate v12.5.1 (64 bit)
MySQL - 5.6.46 : Database - graduate_picture_server
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`graduate_picture_server` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `graduate_picture_server`;

/*Table structure for table `record` */

DROP TABLE IF EXISTS `record`;

CREATE TABLE `record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '绿码申请记录',
  `openid` varchar(255) DEFAULT NULL COMMENT '用户身份',
  `color` int(8) DEFAULT NULL COMMENT '当前建康码颜色（1 2 3 4）',
  `reason` int(8) DEFAULT NULL COMMENT '原因（7种情况）',
  `time` varchar(32) DEFAULT NULL COMMENT '申请的时间',
  `tripurl` text COMMENT '大数据形成码在云服务器上的url',
  `reporturl` text COMMENT '核酸检测报告',
  `ischeck` int(8) DEFAULT NULL COMMENT '是否被审核（未审核 拒绝 通过）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `record` */

insert  into `record`(`id`,`openid`,`color`,`reason`,`time`,`tripurl`,`reporturl`,`ischeck`) values 
(2,'ohxHN5CIqvRdCY3Wui9X_hpJxgSU',2,2,'2022-05-07','wx.ctstudy.xyz/FqJbpcgXWy18XyaFyFN0DJK7RsYG','',2);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
