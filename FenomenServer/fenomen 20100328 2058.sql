-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.39-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema fenomen
--

CREATE DATABASE IF NOT EXISTS fenomen;
USE fenomen;

--
-- Definition of table `module`
--

DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'уникальный номер в системе',
  `id_module` varchar(45) NOT NULL COMMENT 'уникальная последовательность символов для идентификации',
  `address` varchar(200) NOT NULL COMMENT 'адрес, по которому находится данный модуль',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=cp1251;

--
-- Dumping data for table `module`
--

/*!40000 ALTER TABLE `module` DISABLE KEYS */;
INSERT INTO `module` (`id`,`id_module`,`address`) VALUES 
 (1,'07','temp_address');
/*!40000 ALTER TABLE `module` ENABLE KEYS */;


--
-- Definition of table `module_alarm`
--

DROP TABLE IF EXISTS `module_alarm`;
CREATE TABLE `module_alarm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный идентификатор модуля',
  `id_storage` varchar(255) NOT NULL COMMENT 'уникальный идентификатор данного Alarm в хранилище на модуле',
  `id_description` varchar(1024) NOT NULL COMMENT 'описание тревожного события',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи данного события',
  `id_sensor` int(10) unsigned NOT NULL COMMENT 'уникальный номер датчика, по которому данное событие зарегестрировано',
  `sensor_register_address` int(10) NOT NULL COMMENT 'адрес регистра на датчике',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=cp1251 COMMENT='тревожные события по модулю';

--
-- Dumping data for table `module_alarm`
--

/*!40000 ALTER TABLE `module_alarm` DISABLE KEYS */;
INSERT INTO `module_alarm` (`id`,`id_module`,`id_storage`,`id_description`,`time_write`,`id_sensor`,`sensor_register_address`) VALUES 
 (88,1,'2010_03_10_23_13_54_0FD.alarm','button on register 12','2010-03-10 23:13:54',0,12),
 (89,1,'2010_03_10_23_14_16_43E.alarm','button on register 12','2010-03-10 23:14:16',0,12),
 (90,1,'2010_03_10_23_14_24_227.alarm','button on register 12','2010-03-10 23:14:24',0,12),
 (91,1,'2010_03_10_23_14_32_F6A.alarm','button on register 12','2010-03-10 23:14:32',0,12),
 (92,1,'2010_03_10_23_15_26_708.alarm','button on register 12','2010-03-10 23:15:26',0,12),
 (93,1,'2010_03_10_23_16_04_4F7.alarm','button on register 12','2010-03-10 23:16:04',0,12),
 (94,1,'2010_03_10_23_16_29_C24.alarm','button on register 12','2010-03-10 23:16:29',0,12),
 (95,1,'2010_03_10_23_16_36_19E.alarm','button on register 12','2010-03-10 23:16:36',0,12),
 (96,1,'2010_03_23_19_33_16_387.alarm','button on register 12','2010-03-23 19:33:16',0,12),
 (97,1,'2010_03_27_14_46_01_54F.alarm','button on register 12','2010-03-27 14:46:01',0,12),
 (98,1,'2010_03_27_14_46_07_E44.alarm','button on register 12','2010-03-27 14:46:07',0,12),
 (99,1,'2010_03_27_14_46_17_491.alarm','button on register 12','2010-03-27 14:46:17',0,12),
 (100,1,'2010_03_27_14_46_40_0C9.alarm','button on register 12','2010-03-27 14:46:40',0,12),
 (101,1,'2010_03_27_14_47_51_D54.alarm','button on register 12','2010-03-27 14:47:51',0,12),
 (102,1,'2010_03_27_14_48_04_BCB.alarm','button on register 12','2010-03-27 14:48:04',0,12),
 (103,1,'2010_03_27_14_48_10_4F3.alarm','button on register 12','2010-03-27 14:48:10',0,12),
 (104,1,'2010_03_27_14_48_20_8BA.alarm','button on register 12','2010-03-27 14:48:20',0,12),
 (105,1,'2010_03_28_10_29_00_6A1.alarm','button on register 12','2010-03-28 10:29:00',0,12),
 (106,1,'2010_03_28_10_29_08_1AB.alarm','button on register 12','2010-03-28 10:29:08',0,12),
 (107,1,'2010_03_28_10_29_18_37A.alarm','button on register 12','2010-03-28 10:29:18',0,12),
 (108,1,'2010_03_28_10_29_33_016.alarm','button on register 12','2010-03-28 10:29:33',0,12),
 (109,1,'2010_03_28_10_29_42_1D9.alarm','button on register 12','2010-03-28 10:29:42',0,12),
 (110,1,'2010_03_28_10_32_39_637.alarm','нажата кнопка №11','2010-03-28 10:32:39',0,11),
 (111,1,'2010_03_28_10_32_56_046.alarm','button on register 12','2010-03-28 10:32:56',0,12),
 (112,1,'2010_03_28_10_33_01_74B.alarm','нажата кнопка №11','2010-03-28 10:33:01',0,11),
 (113,1,'2010_03_28_10_33_06_23B.alarm','button on register 12','2010-03-28 10:33:06',0,12),
 (114,1,'2010_03_28_10_33_11_04E.alarm','нажата кнопка №11','2010-03-28 10:33:11',0,11),
 (115,1,'2010_03_28_10_33_16_327.alarm','button on register 12','2010-03-28 10:33:16',0,12),
 (116,1,'2010_03_28_10_33_23_5FA.alarm','нажата кнопка №11','2010-03-28 10:33:23',0,11);
/*!40000 ALTER TABLE `module_alarm` ENABLE KEYS */;


--
-- Definition of table `module_alarm_checker`
--

DROP TABLE IF EXISTS `module_alarm_checker`;
CREATE TABLE `module_alarm_checker` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'таблица для хранения Checker-ов для Alarm событий',
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный идентификатор модуля ',
  `id_storage` varchar(255) NOT NULL COMMENT 'идентификатор хранилища',
  `id_state` int(10) NOT NULL DEFAULT '0' COMMENT 'идентификатор состояния ( 0 - новое задание )     ( 1 - забрано модулем )   ( 2 - успешно внедрено в модуль )  ( -1 - есть на модуле, нет на сервере )',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи\r\n',
  `description` varchar(255) DEFAULT NULL COMMENT 'описание',
  `sensor_register_address` int(10) DEFAULT NULL COMMENT 'адрес регистра на модуле ( модуль в системе Modbus)',
  `sensor_modbus_address` int(10) unsigned DEFAULT NULL COMMENT 'адрес модуля в системе ModBus',
  `sensor_modbus_id_on_device` int(10) DEFAULT NULL COMMENT 'порядковый номер Checker-a на модуле ( в системе Modbus) ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=cp1251 COMMENT='проверки на тревожные состояния';

--
-- Dumping data for table `module_alarm_checker`
--

/*!40000 ALTER TABLE `module_alarm_checker` DISABLE KEYS */;
INSERT INTO `module_alarm_checker` (`id`,`id_module`,`id_storage`,`id_state`,`time_write`,`description`,`sensor_register_address`,`sensor_modbus_address`,`sensor_modbus_id_on_device`) VALUES 
 (6,1,'ca2010_03_10-23_13_15_4EAB.bin',2,'2010-03-10 23:13:15','description: this is control of button',12,1,6),
 (7,1,'ca2010_03_28-10_32_07_4CC7.bin',2,'2010-03-28 10:32:07','описание: реакция на нажатие клавиши №11',11,1,7);
/*!40000 ALTER TABLE `module_alarm_checker` ENABLE KEYS */;


--
-- Definition of table `module_heart_beat`
--

DROP TABLE IF EXISTS `module_heart_beat`;
CREATE TABLE `module_heart_beat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный ID модуля из таблицы MODULE',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'дата записи данного значения',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19922 DEFAULT CHARSET=cp1251;

--
-- Dumping data for table `module_heart_beat`
--

/*!40000 ALTER TABLE `module_heart_beat` DISABLE KEYS */;
INSERT INTO `module_heart_beat` (`id`,`id_module`,`time_write`) VALUES 
 (19828,1,'2010-03-11 22:08:05'),
 (19829,1,'2010-03-11 22:08:15'),
 (19830,1,'2010-03-11 22:08:25'),
 (19831,1,'2010-03-11 22:08:35'),
 (19832,1,'2010-03-11 22:08:45'),
 (19833,1,'2010-03-11 22:08:55'),
 (19834,1,'2010-03-11 22:09:05'),
 (19835,1,'2010-03-11 22:09:15'),
 (19836,1,'2010-03-11 22:09:25'),
 (19837,1,'2010-03-11 22:09:35'),
 (19838,1,'2010-03-11 22:09:46'),
 (19839,1,'2010-03-11 22:09:56'),
 (19840,1,'2010-03-11 22:10:06'),
 (19841,1,'2010-03-11 22:10:16'),
 (19842,1,'2010-03-11 22:10:26'),
 (19843,1,'2010-03-11 22:10:36'),
 (19844,1,'2010-03-11 22:15:36'),
 (19845,1,'2010-03-11 22:20:36'),
 (19846,1,'2010-03-11 22:21:06'),
 (19847,1,'2010-03-11 22:21:36'),
 (19848,1,'2010-03-11 22:22:06'),
 (19849,1,'2010-03-23 19:32:17'),
 (19850,1,'2010-03-23 19:32:59'),
 (19851,1,'2010-03-23 19:33:29'),
 (19852,1,'2010-03-23 19:33:59'),
 (19853,1,'2010-03-23 19:34:29'),
 (19854,1,'2010-03-23 19:34:59'),
 (19855,1,'2010-03-23 19:35:29'),
 (19856,1,'2010-03-23 19:36:07'),
 (19857,1,'2010-03-23 19:36:37'),
 (19858,1,'2010-03-23 19:37:07'),
 (19859,1,'2010-03-23 19:41:41'),
 (19860,1,'2010-03-23 19:42:11'),
 (19861,1,'2010-03-23 19:42:41'),
 (19862,1,'2010-03-23 19:43:11'),
 (19863,1,'2010-03-23 19:54:40'),
 (19864,1,'2010-03-23 19:55:10'),
 (19865,1,'2010-03-23 19:55:40'),
 (19866,1,'2010-03-23 19:56:10'),
 (19867,1,'2010-03-23 19:56:40'),
 (19868,1,'2010-03-23 19:57:10'),
 (19869,1,'2010-03-23 19:57:40'),
 (19870,1,'2010-03-23 19:58:13'),
 (19871,1,'2010-03-23 20:07:18'),
 (19872,1,'2010-03-23 20:07:48'),
 (19873,1,'2010-03-23 20:08:18'),
 (19874,1,'2010-03-23 20:08:48'),
 (19875,1,'2010-03-23 20:09:18'),
 (19876,1,'2010-03-23 20:09:48'),
 (19877,1,'2010-03-23 20:10:18'),
 (19878,1,'2010-03-23 20:10:48'),
 (19879,1,'2010-03-23 20:11:18'),
 (19880,1,'2010-03-23 20:11:48'),
 (19881,1,'2010-03-23 20:12:18'),
 (19882,1,'2010-03-23 20:12:48'),
 (19883,1,'2010-03-23 20:13:18'),
 (19884,1,'2010-03-23 20:13:48'),
 (19885,1,'2010-03-23 20:14:18'),
 (19886,1,'2010-03-23 20:14:48'),
 (19887,1,'2010-03-23 20:15:18'),
 (19888,1,'2010-03-23 20:15:48'),
 (19889,1,'2010-03-23 20:16:18'),
 (19890,1,'2010-03-23 20:16:48'),
 (19891,1,'2010-03-23 20:17:22'),
 (19892,1,'2010-03-27 14:44:11'),
 (19893,1,'2010-03-27 14:44:41'),
 (19894,1,'2010-03-27 14:45:06'),
 (19895,1,'2010-03-27 14:45:31'),
 (19896,1,'2010-03-27 14:45:56'),
 (19897,1,'2010-03-27 14:46:21'),
 (19898,1,'2010-03-27 14:46:46'),
 (19899,1,'2010-03-27 14:47:11'),
 (19900,1,'2010-03-27 14:47:36'),
 (19901,1,'2010-03-27 14:48:01'),
 (19902,1,'2010-03-27 14:48:26'),
 (19903,1,'2010-03-27 14:48:51'),
 (19904,1,'2010-03-27 14:49:16'),
 (19905,1,'2010-03-27 14:49:41'),
 (19906,1,'2010-03-28 10:28:40'),
 (19907,1,'2010-03-28 10:29:06'),
 (19908,1,'2010-03-28 10:29:31'),
 (19909,1,'2010-03-28 10:29:56'),
 (19910,1,'2010-03-28 10:30:21'),
 (19911,1,'2010-03-28 10:30:46'),
 (19912,1,'2010-03-28 10:31:12'),
 (19913,1,'2010-03-28 10:31:37'),
 (19914,1,'2010-03-28 10:32:02'),
 (19915,1,'2010-03-28 10:32:27'),
 (19916,1,'2010-03-28 10:32:52'),
 (19917,1,'2010-03-28 10:33:17'),
 (19918,1,'2010-03-28 10:33:42'),
 (19919,1,'2010-03-28 10:34:07'),
 (19920,1,'2010-03-28 10:34:32'),
 (19921,1,'2010-03-28 10:34:57');
/*!40000 ALTER TABLE `module_heart_beat` ENABLE KEYS */;


--
-- Definition of table `module_information`
--

DROP TABLE IF EXISTS `module_information`;
CREATE TABLE `module_information` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'таблица для хранения данных об информации, которая поступила от модуля ',
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный номер модуля',
  `id_storage` varchar(255) DEFAULT NULL COMMENT 'уникальный номер в хранилище для данного Checker-a',
  `description` varchar(1024) DEFAULT NULL COMMENT 'описание события',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи события',
  `id_sensor` int(10) unsigned NOT NULL COMMENT 'уникальный номер датчика, по которому данное событие произошло',
  `sensor_register_address` int(10) NOT NULL COMMENT 'адрес регистра на модуле.сенсоре',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=166 DEFAULT CHARSET=cp1251 COMMENT='информационные события от модуля';

--
-- Dumping data for table `module_information`
--

/*!40000 ALTER TABLE `module_information` DISABLE KEYS */;
INSERT INTO `module_information` (`id`,`id_module`,`id_storage`,`description`,`time_write`,`id_sensor`,`sensor_register_address`) VALUES 
 (140,1,'2010_03_10_21_55_28_370.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>129</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.10 21:55:28</date_write></register><register><number>','2010-03-10 21:55:28',0,0),
 (141,1,'2010_03_10_21_57_49_B97.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>130</id><value>OK</value><alarm_checker><id_modbus>1</id_modbus><add_confirm><register>1</register><id_file>4</id_file><id_on_module>4.bin</id_on_module></add_confirm></alarm_checker></task></task_response>','2010-03-10 21:57:49',0,0),
 (142,1,'2010_03_10_21_59_29_8BA.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>131</id><value>OK</value></task></task_response>','2010-03-10 21:59:29',0,0),
 (143,1,'2010_03_10_22_00_29_3AB.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>132</id><value>OK</value><alarm_checker><id_modbus>1</id_modbus><add_confirm><register>1</register><id_file>5</id_file><id_on_module>5.bin</id_on_module></add_confirm></alarm_checker></task></task_response>','2010-03-10 22:00:29',0,0),
 (144,1,'2010_03_10_22_21_23_491.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>133</id><value>OK</value><alarm_checker><id_modbus>1</id_modbus><add_confirm><register>12</register><id_file>5</id_file><id_on_module>5.bin</id_on_module></add_confirm></alarm_checker></task></task_response>','2010-03-10 22:21:23',0,0),
 (145,1,'2010_03_10_23_12_31_804.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>134</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.10 23:12:31</date_write></register><register><number>','2010-03-10 23:12:31',0,0),
 (146,1,'2010_03_10_23_12_41_D06.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>135</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.10 23:12:40</date_write></register><register><number>','2010-03-10 23:12:41',0,0),
 (147,1,'2010_03_10_23_13_21_60C.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>136</id><value>OK</value><alarm_checker><id_modbus>1</id_modbus><add_confirm><register>1</register><id_file>6</id_file><id_on_module>6.bin</id_on_module></add_confirm></alarm_checker></task></task_response>','2010-03-10 23:13:21',0,0),
 (148,1,'2010_03_11_22_10_26_3AD.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>137</id><value>OK</value></task></task_response>','2010-03-11 22:10:26',0,0),
 (149,1,'2010_03_11_22_15_36_8F3.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>138</id><value>OK</value></task></task_response>','2010-03-11 22:15:36',0,0),
 (150,1,'2010_03_11_22_21_36_AC2.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>139</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.11 22:21:36</date_write></register><register><number>','2010-03-11 22:21:36',0,0),
 (151,1,'2010_03_11_22_21_41_899.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>140</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.11 22:21:41</date_write></register><register><number>','2010-03-11 22:21:41',0,0),
 (152,1,'2010_03_11_22_22_06_A7E.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>141</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>','2010-03-11 22:22:06',0,0),
 (153,1,'2010_03_11_22_22_11_D61.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>142</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>','2010-03-11 22:22:11',0,0),
 (154,1,'2010_03_11_22_22_17_ACD.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>143</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.11 22:22:06</date_write></register><register><number>','2010-03-11 22:22:17',0,0),
 (155,1,'2010_03_27_14_44_21_90F.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>144</id><value>OK</value></task></task_response>','2010-03-27 14:44:21',0,0),
 (156,1,'2010_03_27_14_44_27_0C4.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>147</id><value>OK</value></task></task_response>','2010-03-27 14:44:27',0,0),
 (157,1,'2010_03_27_14_44_32_D08.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>148</id><value>OK</value></task></task_response>','2010-03-27 14:44:32',0,0),
 (158,1,'2010_03_27_14_44_37_CC0.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>149</id><value>OK</value></task></task_response>','2010-03-27 14:44:37',0,0),
 (159,1,'2010_03_27_14_44_42_7B7.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>150</id><value>OK</value></task></task_response>','2010-03-27 14:44:42',0,0),
 (160,1,'2010_03_27_14_44_47_2B4.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>151</id><value>OK</value></task></task_response>','2010-03-27 14:44:47',0,0),
 (161,1,'2010_03_28_10_28_51_8A4.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>145</id><value>OK</value></task></task_response>','2010-03-28 10:28:51',0,0),
 (162,1,'2010_03_28_10_28_56_DBC.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>146</id><value>OK</value></task></task_response>','2010-03-28 10:28:56',0,0),
 (163,1,'2010_03_28_10_32_27_145.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>152</id><value>OK</value><alarm_checker><id_modbus>1</id_modbus><add_confirm><register>1</register><id_file>7</id_file><id_on_module>7.bin</id_on_module></add_confirm></alarm_checker></task></task_response>','2010-03-28 10:32:27',0,0),
 (164,1,'2010_03_28_10_32_32_235.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>153</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.28 10:32:27</date_write></register><register><number>','2010-03-28 10:32:32',0,0),
 (165,1,'2010_03_28_10_32_52_FBE.info','<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><task_response><task><id>154</id><value>OK</value><sensor><id_modbus>1</id_modbus><type>1235812592</type><enabled>true</enabled><register_list><register><number>0</number><value>1</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>1</number><value>6</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>2</number><value>12358</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>3</number><value>12592</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>4</number><value>12630</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>5</number><value>12590</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>6</number><value>1</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>7</number><value>1</value><date_write>2010.03.28 10:32:52</date_write></register><register><number>','2010-03-28 10:32:52',0,0);
/*!40000 ALTER TABLE `module_information` ENABLE KEYS */;


--
-- Definition of table `module_information_checker`
--

DROP TABLE IF EXISTS `module_information_checker`;
CREATE TABLE `module_information_checker` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'таблица, которая содержит задания для модулей ( ссылки на них, и флаг того, забрано задание или нет )',
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный идентификатор модуля ',
  `id_storage` varchar(255) NOT NULL COMMENT 'уникальный идентификатор в хранилище (хранилище находится на модуле)',
  `id_state` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '0 - создано\r\n1 - модуль забрал\r\n2 - модуль подтвердил то что забрал (-1 - есть на модуле, но нет на сервере )',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(255) DEFAULT NULL COMMENT 'описание датчика',
  `sensor_register_address` int(10) NOT NULL COMMENT 'адрес регистра на модуле.сенсоре',
  `sensor_modbus_address` int(10) unsigned NOT NULL COMMENT 'адрес модуля/сенсора в сети ModBus ',
  `sensor_modbus_id_on_device` int(10) unsigned NOT NULL COMMENT 'порядковый номер данного Checker-а на модуле/сенсоре',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=cp1251 COMMENT='объекты по проверке на информационные события';

--
-- Dumping data for table `module_information_checker`
--

/*!40000 ALTER TABLE `module_information_checker` DISABLE KEYS */;
/*!40000 ALTER TABLE `module_information_checker` ENABLE KEYS */;


--
-- Definition of table `module_restart`
--

DROP TABLE IF EXISTS `module_restart`;
CREATE TABLE `module_restart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module` int(10) unsigned NOT NULL,
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=cp1251 COMMENT='время старта/рестарта модуля';

--
-- Dumping data for table `module_restart`
--

/*!40000 ALTER TABLE `module_restart` DISABLE KEYS */;
INSERT INTO `module_restart` (`id`,`id_module`,`time_write`) VALUES 
 (1,1,'2010-01-30 22:30:52'),
 (2,1,'2010-01-30 22:39:37'),
 (3,1,'2010-01-30 22:54:06'),
 (4,1,'2010-01-31 00:03:48'),
 (5,1,'2010-01-31 00:05:24'),
 (6,1,'2010-01-31 00:11:52'),
 (7,1,'2010-01-31 00:17:01'),
 (8,1,'2010-01-31 07:26:50'),
 (9,1,'2010-01-31 07:41:26'),
 (10,1,'2010-01-31 07:56:50'),
 (11,1,'2010-01-31 07:59:54'),
 (12,1,'2010-01-31 08:01:51'),
 (13,1,'2010-01-31 08:10:48'),
 (14,1,'2010-01-31 08:11:35'),
 (15,1,'2010-01-31 08:21:24'),
 (16,1,'2010-01-31 16:09:36'),
 (17,1,'2010-01-31 16:10:27'),
 (18,1,'2010-01-31 16:11:43'),
 (19,1,'2010-01-31 16:14:06'),
 (20,1,'2010-01-31 16:29:21'),
 (21,1,'2010-01-31 17:44:41'),
 (22,1,'2010-01-31 18:04:41'),
 (23,1,'2010-01-31 21:21:02'),
 (24,1,'2010-01-31 22:20:52'),
 (25,1,'2010-01-31 23:19:06'),
 (26,1,'2010-01-31 23:22:54'),
 (27,1,'2010-02-01 09:12:36'),
 (28,1,'2010-02-01 09:15:53'),
 (29,1,'2010-02-01 09:37:32'),
 (30,1,'2010-02-01 09:50:09'),
 (31,1,'2010-02-01 11:52:16'),
 (32,1,'2010-02-01 12:19:46'),
 (33,1,'2010-02-02 13:41:59'),
 (34,1,'2010-02-02 14:04:36'),
 (35,1,'2010-02-02 14:06:54'),
 (36,1,'2010-02-02 14:14:50'),
 (37,1,'2010-02-02 14:32:34'),
 (38,1,'2010-02-02 14:33:45'),
 (39,1,'2010-02-02 15:00:29'),
 (40,1,'2010-02-02 15:22:32'),
 (41,1,'2010-02-02 15:40:29'),
 (42,1,'2010-02-02 15:48:06'),
 (43,1,'2010-02-02 15:48:59'),
 (44,1,'2010-02-02 15:50:32'),
 (45,1,'2010-02-02 15:54:32'),
 (46,1,'2010-02-02 16:01:43'),
 (47,1,'2010-02-02 16:06:44'),
 (48,1,'2010-02-02 16:09:17'),
 (49,1,'2010-02-02 16:21:07'),
 (50,1,'2010-02-02 16:27:13'),
 (51,1,'2010-02-08 18:01:17'),
 (52,1,'2010-02-08 18:14:53'),
 (53,1,'2010-02-09 10:05:55'),
 (54,1,'2010-02-09 10:36:21'),
 (55,1,'2010-02-09 10:53:02'),
 (56,1,'2010-02-09 10:58:09'),
 (57,1,'2010-02-09 11:17:09'),
 (58,1,'2010-02-09 11:42:50'),
 (59,1,'2010-02-09 11:56:14'),
 (60,1,'2010-02-09 12:29:05'),
 (61,1,'2010-02-09 12:40:59'),
 (62,1,'2010-02-09 12:43:06'),
 (63,1,'2010-02-09 12:57:08'),
 (64,1,'2010-02-09 13:09:12'),
 (65,1,'2010-02-09 13:19:20'),
 (66,1,'2010-03-08 18:28:35'),
 (67,1,'2010-03-08 18:33:34'),
 (68,1,'2010-03-08 18:37:11'),
 (69,1,'2010-03-08 20:16:24'),
 (70,1,'2010-03-08 22:00:39'),
 (71,1,'2010-03-08 22:09:26'),
 (72,1,'2010-03-08 22:39:41'),
 (73,1,'2010-03-08 23:06:20'),
 (74,1,'2010-03-08 23:10:27'),
 (75,1,'2010-03-10 11:14:16'),
 (76,1,'2010-03-10 16:54:20'),
 (77,1,'2010-03-10 17:18:59'),
 (78,1,'2010-03-10 21:54:30'),
 (79,1,'2010-03-10 23:11:23'),
 (80,1,'2010-03-10 23:15:24'),
 (81,1,'2010-03-11 22:07:41'),
 (82,1,'2010-03-11 22:08:09'),
 (83,1,'2010-03-23 19:32:22'),
 (84,1,'2010-03-23 19:33:04'),
 (85,1,'2010-03-27 14:44:16'),
 (86,1,'2010-03-28 10:28:40');
/*!40000 ALTER TABLE `module_restart` ENABLE KEYS */;


--
-- Definition of table `module_sensor`
--

DROP TABLE IF EXISTS `module_sensor`;
CREATE TABLE `module_sensor` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module` int(10) unsigned NOT NULL COMMENT 'принадлежность к модулю',
  `id_modbus` varchar(45) DEFAULT NULL COMMENT 'уникальный идентификатор MODBUS',
  `id_sensor_type` int(10) unsigned NOT NULL COMMENT 'тип сенсора/датчика',
  `is_enabled` smallint(5) unsigned DEFAULT NULL COMMENT '0 - Disabled; 1 - Enabled',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=cp1251 COMMENT='Таблица, которая содержит сенсоры, привязанные к модулям';

--
-- Dumping data for table `module_sensor`
--

/*!40000 ALTER TABLE `module_sensor` DISABLE KEYS */;
INSERT INTO `module_sensor` (`id`,`id_module`,`id_modbus`,`id_sensor_type`,`is_enabled`,`time_write`) VALUES 
 (5,1,'1',2,1,'2010-03-28 10:32:52');
/*!40000 ALTER TABLE `module_sensor` ENABLE KEYS */;


--
-- Definition of table `module_sensor_register`
--

DROP TABLE IF EXISTS `module_sensor_register`;
CREATE TABLE `module_sensor_register` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_sensor` int(10) unsigned NOT NULL COMMENT 'уникальный идентификатор из таблицы  module_sensor.id',
  `register_address` int(10) NOT NULL COMMENT 'адрес регитра на удаленном модуле.устройстве',
  `register_value` int(10) NOT NULL COMMENT 'значение регистра на удаленном модуле.устройстве',
  `description` varchar(100) DEFAULT NULL COMMENT 'описание регистра на удаленном модуле.устройстве',
  `register_value_date_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи значения ( в базу данных, либо же от устройства )',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=576 DEFAULT CHARSET=cp1251 COMMENT='регистры по сенсору/датчику';

--
-- Dumping data for table `module_sensor_register`
--

/*!40000 ALTER TABLE `module_sensor_register` DISABLE KEYS */;
INSERT INTO `module_sensor_register` (`id`,`id_sensor`,`register_address`,`register_value`,`description`,`register_value_date_write`) VALUES 
 (556,5,0,1,NULL,'2010-03-28 10:32:52'),
 (557,5,1,6,NULL,'2010-03-28 10:32:52'),
 (558,5,2,12358,NULL,'2010-03-28 10:32:52'),
 (559,5,3,12592,NULL,'2010-03-28 10:32:52'),
 (560,5,4,12630,NULL,'2010-03-28 10:32:52'),
 (561,5,5,12590,NULL,'2010-03-28 10:32:52'),
 (562,5,6,1,NULL,'2010-03-28 10:32:52'),
 (563,5,7,1,NULL,'2010-03-28 10:32:52'),
 (564,5,8,1,NULL,'2010-03-28 10:32:52'),
 (565,5,9,0,NULL,'2010-03-28 10:32:52'),
 (566,5,10,0,NULL,'2010-03-28 10:32:52'),
 (567,5,11,0,NULL,'2010-03-28 10:32:52'),
 (568,5,12,0,NULL,'2010-03-28 10:32:52'),
 (569,5,13,0,NULL,'2010-03-28 10:32:52'),
 (570,5,14,1,NULL,'2010-03-28 10:32:52'),
 (571,5,15,1,NULL,'2010-03-28 10:32:52'),
 (572,5,16,1,NULL,'2010-03-28 10:32:52'),
 (573,5,17,1,NULL,'2010-03-28 10:32:52'),
 (574,5,18,0,NULL,'2010-03-28 10:32:52'),
 (575,5,19,0,NULL,'2010-03-28 10:32:52');
/*!40000 ALTER TABLE `module_sensor_register` ENABLE KEYS */;


--
-- Definition of table `module_settings`
--

DROP TABLE IF EXISTS `module_settings`;
CREATE TABLE `module_settings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '(settings of Thread: ThreadHeartBeat, ThreadTaskProcessor, ThreadInformation, ThreadAlarm, ThreadSensor) ',
  `id_module` int(10) unsigned NOT NULL COMMENT 'идентификатор модуля',
  `id_section` int(10) unsigned NOT NULL COMMENT 'идентификатор секции к которой относится настройка',
  `id_parameter` int(10) unsigned NOT NULL COMMENT 'идентификатор параметра',
  `settings_value` varchar(255) NOT NULL COMMENT 'строковое значение параметра',
  `module_recieve` smallint(5) unsigned zerofill NOT NULL DEFAULT '00000' COMMENT '0 - записано в базу, 1 - принято модулем',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=cp1251 COMMENT='Таблица настроек для модуля';

--
-- Dumping data for table `module_settings`
--

/*!40000 ALTER TABLE `module_settings` DISABLE KEYS */;
INSERT INTO `module_settings` (`id`,`id_module`,`id_section`,`id_parameter`,`settings_value`,`module_recieve`,`time_write`) VALUES 
 (10,1,1,1,'25000',00000,'2010-03-27 09:36:13'),
 (11,1,1,2,'1250',00000,'2010-03-27 09:36:13'),
 (12,1,2,3,'5007',00000,'2010-02-01 09:16:24'),
 (13,1,2,4,'12008',00000,'2010-02-01 09:16:24'),
 (14,1,2,5,'21',00000,'2010-02-01 09:16:24'),
 (15,1,3,6,'7003',00000,'2010-02-01 09:11:41'),
 (16,1,3,7,'12004',00000,'2010-02-01 09:11:41'),
 (17,1,3,8,'8',00000,'2010-02-01 09:11:41'),
 (18,1,4,9,'1005',00000,'2010-02-01 09:11:56');
/*!40000 ALTER TABLE `module_settings` ENABLE KEYS */;


--
-- Definition of table `module_settings_parameter`
--

DROP TABLE IF EXISTS `module_settings_parameter`;
CREATE TABLE `module_settings_parameter` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_section` int(10) unsigned NOT NULL COMMENT 'идентификатор секции параметров',
  `parameter_name` varchar(45) NOT NULL COMMENT 'имя параметра',
  `parameter_description` varchar(255) DEFAULT '' COMMENT 'текстовое описание параметра',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=cp1251 COMMENT='имена всех параметров';

--
-- Dumping data for table `module_settings_parameter`
--

/*!40000 ALTER TABLE `module_settings_parameter` DISABLE KEYS */;
INSERT INTO `module_settings_parameter` (`id`,`id_section`,`parameter_name`,`parameter_description`) VALUES 
 (1,1,'time_wait',''),
 (2,1,'time_error',''),
 (3,2,'time_wait',''),
 (4,2,'time_error',''),
 (5,2,'max_count',''),
 (6,3,'time_wait',''),
 (7,3,'time_error',''),
 (8,3,'max_count',''),
 (9,4,'time_wait','');
/*!40000 ALTER TABLE `module_settings_parameter` ENABLE KEYS */;


--
-- Definition of table `module_settings_section`
--

DROP TABLE IF EXISTS `module_settings_section`;
CREATE TABLE `module_settings_section` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT 'имя секции настройки',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=cp1251 COMMENT='секции настроек для модуля (HeartBeat, Information, ...)';

--
-- Dumping data for table `module_settings_section`
--

/*!40000 ALTER TABLE `module_settings_section` DISABLE KEYS */;
INSERT INTO `module_settings_section` (`id`,`name`) VALUES 
 (1,'HeartBeat'),
 (2,'Information'),
 (3,'Alarm'),
 (4,'Sensor');
/*!40000 ALTER TABLE `module_settings_section` ENABLE KEYS */;


--
-- Definition of table `module_storage`
--

DROP TABLE IF EXISTS `module_storage`;
CREATE TABLE `module_storage` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT 'зарегестрированные имена ( Task, Alarm, Information )',
  `xml_directory` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=cp1251 COMMENT='место хранения файлов XML, для Task, Alarm, Information';

--
-- Dumping data for table `module_storage`
--

/*!40000 ALTER TABLE `module_storage` DISABLE KEYS */;
INSERT INTO `module_storage` (`id`,`name`,`xml_directory`) VALUES 
 (1,'Task','c:\\temp\\Object_Task\\'),
 (2,'Alarm','c:\\temp\\Module_Alarm\\'),
 (4,'Information','c:\\temp\\Module_Information\\'),
 (5,'AlarmChecker','c:\\temp\\Object_Checker_Alarm\\'),
 (6,'InformationChecker','c:\\temp\\Object_Checker_Information\\');
/*!40000 ALTER TABLE `module_storage` ENABLE KEYS */;


--
-- Definition of table `module_task`
--

DROP TABLE IF EXISTS `module_task`;
CREATE TABLE `module_task` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'Таблица для выдачи Task каждому модулю',
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный идентификатор модуля ',
  `id_storage` varchar(255) NOT NULL COMMENT 'уникальный идентификатор в хранилище задач для модуля',
  `id_state` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'состояние задачи:\r\n0 - новая задача\r\n1 - задача была выдана модулю\r\n2 - задача была отработана модулем',
  `time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи/создания',
  `id_result` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'результат выполнения задачи:\r\n0 - задача в неопределенном состоянии\r\n1 - задача выполнена\r\n2 - задача не выполнена - задача не найдена\r\n3 - задача не выполнена - ошибка выполнения',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=cp1251;

--
-- Dumping data for table `module_task`
--

/*!40000 ALTER TABLE `module_task` DISABLE KEYS */;
INSERT INTO `module_task` (`id`,`id_module`,`id_storage`,`id_state`,`time_write`,`id_result`) VALUES 
 (134,1,'t_2010_03_10-23_12_13_2DD5.txml',2,'2010-03-10 23:12:13',1),
 (135,1,'t_2010_03_10-23_12_36_B6B9.txml',2,'2010-03-10 23:12:36',1),
 (136,1,'t_2010_03_10-23_13_15_0E14.txml',2,'2010-03-10 23:13:15',1),
 (137,1,'t_2010_03_11-22_10_10_A986.txml',2,'2010-03-11 22:10:10',1),
 (138,1,'t_2010_03_11-22_12_43_F11F.txml',2,'2010-03-11 22:12:43',1),
 (139,1,'t_2010_03_11-22_21_23_DE3B.txml',2,'2010-03-11 22:21:23',1),
 (140,1,'t_2010_03_11-22_21_40_E7C5.txml',2,'2010-03-11 22:21:40',1),
 (141,1,'t_2010_03_11-22_21_47_F001.txml',2,'2010-03-11 22:21:47',1),
 (142,1,'t_2010_03_11-22_21_51_4548.txml',2,'2010-03-11 22:21:51',1),
 (143,1,'t_2010_03_11-22_21_53_5A82.txml',2,'2010-03-11 22:21:53',1),
 (144,1,'t_2010_03_27-09_17_52_34AB.txml',2,'2010-03-27 09:17:52',1),
 (145,1,'t_2010_03_27-09_18_12_79F0.txml',2,'2010-03-27 09:18:12',1),
 (146,1,'t_2010_03_27-09_30_00_6711.txml',2,'2010-03-27 09:30:00',1),
 (147,1,'t_2010_03_27-09_32_31_0EEF.txml',2,'2010-03-27 09:32:31',1),
 (148,1,'t_2010_03_27-09_33_20_B2AE.txml',2,'2010-03-27 09:33:20',1),
 (149,1,'t_2010_03_27-09_35_43_DCA3.txml',2,'2010-03-27 09:35:43',1),
 (150,1,'t_2010_03_27-09_36_01_CF4E.txml',2,'2010-03-27 09:36:01',1),
 (151,1,'t_2010_03_27-09_36_13_36C0.txml',2,'2010-03-27 09:36:13',1),
 (152,1,'t_2010_03_28-10_32_07_42AB.txml',2,'2010-03-28 10:32:07',1),
 (153,1,'t_2010_03_28-10_32_15_C06F.txml',2,'2010-03-28 10:32:15',1),
 (154,1,'t_2010_03_28-10_32_39_79EE.txml',2,'2010-03-28 10:32:39',1);
/*!40000 ALTER TABLE `module_task` ENABLE KEYS */;


--
-- Definition of table `monitor`
--

DROP TABLE IF EXISTS `monitor`;
CREATE TABLE `monitor` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `monitor_login` varchar(45) NOT NULL COMMENT 'логин монитора',
  `monitor_password` varchar(45) NOT NULL COMMENT 'пароль монитора',
  `monitor_description` varchar(255) NOT NULL COMMENT 'описание монитора',
  `jabber_url` varchar(150) NOT NULL COMMENT 'URL к Jabber-серверу ( talk.google.com )',
  `jabber_port` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'Port к Jabber серверу ( 5222 )',
  `jabber_proxy` varchar(150) NOT NULL COMMENT 'Proxy к Jabber серверу ( gmail.com )',
  `jabber_login` varchar(100) NOT NULL COMMENT 'login к Jabber серверу  (technik7jobrobot@gmail.com)',
  `jabber_password` varchar(100) NOT NULL COMMENT 'Пароль к Jabber Серверу (robottechnik7)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=cp1251 COMMENT='список доступных мониторов ( для просмотра событий )';

--
-- Dumping data for table `monitor`
--

/*!40000 ALTER TABLE `monitor` DISABLE KEYS */;
INSERT INTO `monitor` (`id`,`monitor_login`,`monitor_password`,`monitor_description`,`jabber_url`,`jabber_port`,`jabber_proxy`,`jabber_login`,`jabber_password`) VALUES 
 (3,'temp','temp','monitor for test','127.0.0.1',5222,'technik','monitor_one@127.0.0.1','monitor_one'),
 (4,'2','2','temp monitor 2','127.0.0.1',5222,'technik','monitor_two@127.0.0.1','monitor_two');
/*!40000 ALTER TABLE `monitor` ENABLE KEYS */;


--
-- Definition of table `monitor_event_alarm`
--

DROP TABLE IF EXISTS `monitor_event_alarm`;
CREATE TABLE `monitor_event_alarm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module_alarm` int(10) unsigned NOT NULL COMMENT 'событие из таблицы module_alarm',
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'ключ монитора monitor.id',
  `id_monitor_event_state` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'состояние данного оповещения',
  `state_time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время установки состояния оповещения',
  `id_monitor_event_resolve` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'код решения проблемы',
  `resolve_time_write` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'время установки ответа от монитора',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=cp1251 COMMENT='таблица событий для оповещения мониторы';

--
-- Dumping data for table `monitor_event_alarm`
--

/*!40000 ALTER TABLE `monitor_event_alarm` DISABLE KEYS */;
INSERT INTO `monitor_event_alarm` (`id`,`id_module_alarm`,`id_monitor`,`id_monitor_event_state`,`state_time_write`,`id_monitor_event_resolve`,`resolve_time_write`) VALUES 
 (1,96,3,4,'2010-03-23 19:33:16',2,'2010-03-26 19:56:28'),
 (2,96,4,0,'2010-03-23 19:33:16',0,'2010-03-23 19:33:16'),
 (3,97,3,3,'2010-03-27 14:46:01',0,'2010-03-27 14:46:01'),
 (4,97,4,1,'2010-03-27 14:46:01',0,'2010-03-27 14:46:01'),
 (5,98,3,3,'2010-03-27 14:46:07',0,'2010-03-27 14:46:07'),
 (6,98,4,1,'2010-03-27 14:46:07',0,'2010-03-27 14:46:07'),
 (7,99,3,3,'2010-03-27 14:46:17',0,'2010-03-27 14:46:17'),
 (8,99,4,1,'2010-03-27 14:46:17',0,'2010-03-27 14:46:17'),
 (9,100,3,3,'2010-03-27 14:46:40',0,'2010-03-27 14:46:40'),
 (10,100,4,1,'2010-03-27 14:46:40',0,'2010-03-27 14:46:40'),
 (11,101,3,3,'2010-03-27 14:47:51',0,'2010-03-27 14:47:51'),
 (12,101,4,1,'2010-03-27 14:47:51',0,'2010-03-27 14:47:51'),
 (13,102,3,3,'2010-03-27 14:48:04',0,'2010-03-27 14:48:04'),
 (14,102,4,1,'2010-03-27 14:48:04',0,'2010-03-27 14:48:04'),
 (15,103,3,3,'2010-03-27 14:48:10',0,'2010-03-27 14:48:10'),
 (16,103,4,1,'2010-03-27 14:48:10',0,'2010-03-27 14:48:10'),
 (17,104,3,3,'2010-03-27 14:48:20',0,'2010-03-27 14:48:20'),
 (18,104,4,1,'2010-03-27 14:48:20',0,'2010-03-27 14:48:20'),
 (19,105,3,3,'2010-03-28 10:29:00',0,'2010-03-28 10:29:00'),
 (20,105,4,1,'2010-03-28 10:29:00',0,'2010-03-28 10:29:00'),
 (21,106,3,3,'2010-03-28 10:29:08',0,'2010-03-28 10:29:08'),
 (22,106,4,1,'2010-03-28 10:29:08',0,'2010-03-28 10:29:08'),
 (23,107,3,3,'2010-03-28 10:29:18',0,'2010-03-28 10:29:18'),
 (24,107,4,1,'2010-03-28 10:29:18',0,'2010-03-28 10:29:18'),
 (25,108,3,3,'2010-03-28 10:29:33',0,'2010-03-28 10:29:33'),
 (26,108,4,1,'2010-03-28 10:29:33',0,'2010-03-28 10:29:33'),
 (27,109,3,3,'2010-03-28 10:29:42',0,'2010-03-28 10:29:42'),
 (28,109,4,1,'2010-03-28 10:29:42',0,'2010-03-28 10:29:42'),
 (29,110,3,3,'2010-03-28 10:32:39',0,'2010-03-28 10:32:39'),
 (30,110,4,1,'2010-03-28 10:32:39',0,'2010-03-28 10:32:39'),
 (31,111,3,3,'2010-03-28 10:32:56',0,'2010-03-28 10:32:56'),
 (32,111,4,1,'2010-03-28 10:32:56',0,'2010-03-28 10:32:56'),
 (33,112,3,3,'2010-03-28 10:33:01',0,'2010-03-28 10:33:01'),
 (34,112,4,1,'2010-03-28 10:33:01',0,'2010-03-28 10:33:01'),
 (35,113,3,3,'2010-03-28 10:33:06',0,'2010-03-28 10:33:06'),
 (36,113,4,1,'2010-03-28 10:33:06',0,'2010-03-28 10:33:06'),
 (37,114,3,3,'2010-03-28 10:33:11',0,'2010-03-28 10:33:11'),
 (38,114,4,1,'2010-03-28 10:33:11',0,'2010-03-28 10:33:11'),
 (39,115,3,3,'2010-03-28 10:33:16',0,'2010-03-28 10:33:16'),
 (40,115,4,1,'2010-03-28 10:33:16',0,'2010-03-28 10:33:16'),
 (41,116,3,3,'2010-03-28 10:33:23',0,'2010-03-28 10:33:23'),
 (42,116,4,1,'2010-03-28 10:33:23',0,'2010-03-28 10:33:23');
/*!40000 ALTER TABLE `monitor_event_alarm` ENABLE KEYS */;


--
-- Definition of table `monitor_event_heart_beat`
--

DROP TABLE IF EXISTS `monitor_event_heart_beat`;
CREATE TABLE `monitor_event_heart_beat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module_heart_beat` int(10) unsigned NOT NULL COMMENT 'ключ module_heart_beat.id ( последний полученный )',
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'ключ monitor.id',
  `id_monitor_event_state` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'соостояние оповщения',
  `state_time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи состояния оповещения',
  `id_monitor_event_resolve` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'код решения проблемы',
  `resolve_time_write` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'время устновки ответа от монитора',
  `id_module` int(10) unsigned NOT NULL COMMENT 'модуль, от которого должен был прийти сигнал',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=cp1251 COMMENT='оповещение мониторов о событии heart_beat';

--
-- Dumping data for table `monitor_event_heart_beat`
--

/*!40000 ALTER TABLE `monitor_event_heart_beat` DISABLE KEYS */;
INSERT INTO `monitor_event_heart_beat` (`id`,`id_module_heart_beat`,`id_monitor`,`id_monitor_event_state`,`state_time_write`,`id_monitor_event_resolve`,`resolve_time_write`,`id_module`) VALUES 
 (91,0,3,3,'2010-03-28 12:48:20',0,'2010-03-28 12:48:20',1),
 (92,0,3,3,'2010-03-28 12:48:50',0,'2010-03-28 12:48:50',1),
 (93,0,4,1,'2010-03-28 12:49:20',0,'2010-03-28 12:49:20',1),
 (94,0,3,3,'2010-03-28 12:49:20',0,'2010-03-28 12:49:20',1),
 (95,0,3,3,'2010-03-28 12:57:07',0,'2010-03-28 12:57:07',1),
 (96,0,3,3,'2010-03-28 12:57:37',0,'2010-03-28 12:57:37',1),
 (97,0,4,1,'2010-03-28 12:58:07',0,'2010-03-28 12:58:07',1),
 (98,0,3,3,'2010-03-28 12:58:07',0,'2010-03-28 12:58:07',1),
 (99,0,3,3,'2010-03-28 12:58:37',0,'2010-03-28 12:58:37',1),
 (100,0,3,3,'2010-03-28 12:59:07',0,'2010-03-28 12:59:07',1),
 (101,0,4,1,'2010-03-28 12:59:37',0,'2010-03-28 12:59:37',1),
 (102,0,3,3,'2010-03-28 12:59:37',0,'2010-03-28 12:59:37',1),
 (103,0,3,3,'2010-03-28 13:00:08',0,'2010-03-28 13:00:08',1),
 (104,0,3,3,'2010-03-28 13:00:38',0,'2010-03-28 13:00:38',1);
/*!40000 ALTER TABLE `monitor_event_heart_beat` ENABLE KEYS */;


--
-- Definition of table `monitor_event_information`
--

DROP TABLE IF EXISTS `monitor_event_information`;
CREATE TABLE `monitor_event_information` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module_information` int(10) unsigned NOT NULL COMMENT 'ключ module_information.id',
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'ключ monitor.id',
  `id_monitor_event_state` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'состояние сообщения monitor_event_state',
  `state_time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время записи',
  `id_monitor_event_resolve` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'код решения проблемы',
  `resolve_time_write` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'время устновки ответа от монитора',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=cp1251 COMMENT='оповещение мониторов о событии information';

--
-- Dumping data for table `monitor_event_information`
--

/*!40000 ALTER TABLE `monitor_event_information` DISABLE KEYS */;
/*!40000 ALTER TABLE `monitor_event_information` ENABLE KEYS */;


--
-- Definition of table `monitor_event_resolve`
--

DROP TABLE IF EXISTS `monitor_event_resolve`;
CREATE TABLE `monitor_event_resolve` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '( разрешение проблемы event )',
  `name` varchar(255) NOT NULL COMMENT 'состояние, которое вернул монитор для события',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=cp1251 COMMENT='словарь состояний для обратной связи монитора ';

--
-- Dumping data for table `monitor_event_resolve`
--

/*!40000 ALTER TABLE `monitor_event_resolve` DISABLE KEYS */;
INSERT INTO `monitor_event_resolve` (`id`,`name`) VALUES 
 (1,'НЕ АКТУАЛЬНА'),
 (2,'РЕШЕНА ПО МЕСТУ РАСПОЛОЖЕНИЯ');
/*!40000 ALTER TABLE `monitor_event_resolve` ENABLE KEYS */;


--
-- Definition of table `monitor_event_restart`
--

DROP TABLE IF EXISTS `monitor_event_restart`;
CREATE TABLE `monitor_event_restart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_module_restart` int(10) unsigned NOT NULL COMMENT 'ключ module_restart.id',
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'ключ monitor.id',
  `id_monitor_event_state` int(10) unsigned NOT NULL DEFAULT '1' COMMENT 'состояние оповещения',
  `state_time_write` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'время установки состояния',
  `id_monitor_event_resolve` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'код решения проблемы',
  `resolve_time_write` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT 'время устновки ответа от монитора',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=cp1251 COMMENT='оповещение мониторов о событии restart';

--
-- Dumping data for table `monitor_event_restart`
--

/*!40000 ALTER TABLE `monitor_event_restart` DISABLE KEYS */;
INSERT INTO `monitor_event_restart` (`id`,`id_module_restart`,`id_monitor`,`id_monitor_event_state`,`state_time_write`,`id_monitor_event_resolve`,`resolve_time_write`) VALUES 
 (1,83,3,4,'2010-03-23 19:32:22',1,'2010-03-26 19:58:13'),
 (2,83,4,0,'2010-03-23 19:32:22',0,'2010-03-23 19:32:22'),
 (3,84,3,4,'2010-03-23 19:33:04',1,'2010-03-26 19:45:25'),
 (4,84,4,0,'2010-03-23 19:33:04',0,'2010-03-23 19:33:04'),
 (5,85,3,4,'2010-03-27 14:44:16',2,'2010-03-28 10:30:21'),
 (6,85,4,1,'2010-03-27 14:44:16',0,'2010-03-27 14:44:16'),
 (7,86,3,3,'2010-03-28 10:28:41',0,'2010-03-28 10:28:41'),
 (8,86,4,1,'2010-03-28 10:28:41',0,'2010-03-28 10:28:41');
/*!40000 ALTER TABLE `monitor_event_restart` ENABLE KEYS */;


--
-- Definition of table `monitor_event_state`
--

DROP TABLE IF EXISTS `monitor_event_state`;
CREATE TABLE `monitor_event_state` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '1 - новое, 2 - взято для отправки на монитор, 3 - подтвержденние от монитора получено',
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=cp1251 COMMENT='словарь monitor_event_alarm.state';

--
-- Dumping data for table `monitor_event_state`
--

/*!40000 ALTER TABLE `monitor_event_state` DISABLE KEYS */;
INSERT INTO `monitor_event_state` (`id`,`name`) VALUES 
 (1,'new'),
 (2,'taken for send'),
 (3,'confirmed'),
 (4,'resolved');
/*!40000 ALTER TABLE `monitor_event_state` ENABLE KEYS */;


--
-- Definition of table `monitor_settings_alarm`
--

DROP TABLE IF EXISTS `monitor_settings_alarm`;
CREATE TABLE `monitor_settings_alarm` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'монитор',
  `id_module` int(10) unsigned NOT NULL COMMENT 'модуль по данному монитору',
  `is_enabled` int(10) unsigned NOT NULL COMMENT '0 - disabled; 1 - enabled',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=cp1251 COMMENT='настройки для мониторов по alarm оповещениям';

--
-- Dumping data for table `monitor_settings_alarm`
--

/*!40000 ALTER TABLE `monitor_settings_alarm` DISABLE KEYS */;
INSERT INTO `monitor_settings_alarm` (`id`,`id_monitor`,`id_module`,`is_enabled`) VALUES 
 (1,3,1,1),
 (2,4,1,1);
/*!40000 ALTER TABLE `monitor_settings_alarm` ENABLE KEYS */;


--
-- Definition of table `monitor_settings_heart_beat`
--

DROP TABLE IF EXISTS `monitor_settings_heart_beat`;
CREATE TABLE `monitor_settings_heart_beat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'монитор',
  `id_module` int(10) unsigned NOT NULL COMMENT 'уникальный код модуля',
  `is_enabled` int(10) unsigned NOT NULL COMMENT '0 - disabled; 1 - enabled',
  `time_wait` int(11) NOT NULL COMMENT 'время ожидания очередного сигнала в секундах ( предел ожидания )',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=cp1251 COMMENT='настройки для мониторов по сердцебиению модулей';

--
-- Dumping data for table `monitor_settings_heart_beat`
--

/*!40000 ALTER TABLE `monitor_settings_heart_beat` DISABLE KEYS */;
INSERT INTO `monitor_settings_heart_beat` (`id`,`id_monitor`,`id_module`,`is_enabled`,`time_wait`) VALUES 
 (1,3,1,1,30),
 (2,4,1,1,90);
/*!40000 ALTER TABLE `monitor_settings_heart_beat` ENABLE KEYS */;


--
-- Definition of table `monitor_settings_information`
--

DROP TABLE IF EXISTS `monitor_settings_information`;
CREATE TABLE `monitor_settings_information` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'номер монитора',
  `id_module` int(10) unsigned NOT NULL COMMENT 'номер модуля',
  `is_enabled` int(10) unsigned NOT NULL COMMENT 'идентификатор активации',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=cp1251 COMMENT='настройки для мониторов по информационным сообщениям';

--
-- Dumping data for table `monitor_settings_information`
--

/*!40000 ALTER TABLE `monitor_settings_information` DISABLE KEYS */;
INSERT INTO `monitor_settings_information` (`id`,`id_monitor`,`id_module`,`is_enabled`) VALUES 
 (1,3,1,0);
/*!40000 ALTER TABLE `monitor_settings_information` ENABLE KEYS */;


--
-- Definition of table `monitor_settings_restart`
--

DROP TABLE IF EXISTS `monitor_settings_restart`;
CREATE TABLE `monitor_settings_restart` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_monitor` int(10) unsigned NOT NULL COMMENT 'module.id идентификатор монитора',
  `id_module` int(10) unsigned NOT NULL COMMENT 'monitor.id идентификатор модуля',
  `is_enabled` smallint(5) unsigned NOT NULL COMMENT '0 - disabled, 1 - enabled',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=cp1251 COMMENT='оповещение модулей по событию restart';

--
-- Dumping data for table `monitor_settings_restart`
--

/*!40000 ALTER TABLE `monitor_settings_restart` DISABLE KEYS */;
INSERT INTO `monitor_settings_restart` (`id`,`id_monitor`,`id_module`,`is_enabled`) VALUES 
 (1,0,1,0),
 (2,3,1,1),
 (3,4,1,1);
/*!40000 ALTER TABLE `monitor_settings_restart` ENABLE KEYS */;


--
-- Definition of table `sensor_register_type`
--

DROP TABLE IF EXISTS `sensor_register_type`;
CREATE TABLE `sensor_register_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'порядковый номер',
  `id_sensor_type` int(10) unsigned NOT NULL COMMENT 'родительский тип устройства (sensor_type.id)',
  `address_register` int(10) NOT NULL COMMENT 'номер регистра на модуле',
  `description` varchar(2555) NOT NULL COMMENT 'описание регистра ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=cp1251 COMMENT='регистры на модуле, текстовые описания адресов регистров';

--
-- Dumping data for table `sensor_register_type`
--

/*!40000 ALTER TABLE `sensor_register_type` DISABLE KEYS */;
INSERT INTO `sensor_register_type` (`id`,`id_sensor_type`,`address_register`,`description`) VALUES 
 (1,2,0,'#0'),
 (2,2,1,'#1'),
 (3,2,2,'#2'),
 (4,2,3,'#3'),
 (5,2,4,'#4'),
 (6,2,5,'#5'),
 (7,2,6,'#6'),
 (8,2,7,'#7'),
 (9,2,8,'#8'),
 (10,2,9,'#9'),
 (11,2,10,'#10'),
 (12,2,11,'#11'),
 (13,2,12,'#12'),
 (14,2,13,'#13'),
 (15,2,14,'#14'),
 (16,2,15,'#15'),
 (17,2,16,'#16'),
 (18,2,17,'#17'),
 (19,2,18,'#18'),
 (20,2,19,'#19');
/*!40000 ALTER TABLE `sensor_register_type` ENABLE KEYS */;


--
-- Definition of table `sensor_type`
--

DROP TABLE IF EXISTS `sensor_type`;
CREATE TABLE `sensor_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL COMMENT 'тип сенсора',
  `description` varchar(255) DEFAULT NULL COMMENT 'описание сенсора',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=cp1251 COMMENT='типы сенсоров в системе';

--
-- Dumping data for table `sensor_type`
--

/*!40000 ALTER TABLE `sensor_type` DISABLE KEYS */;
INSERT INTO `sensor_type` (`id`,`name`,`description`) VALUES 
 (1,'T1','temp sensor'),
 (2,'1235812592','test stub');
/*!40000 ALTER TABLE `sensor_type` ENABLE KEYS */;


--
-- Definition of table `system_jabber_monitor_settings`
--

DROP TABLE IF EXISTS `system_jabber_monitor_settings`;
CREATE TABLE `system_jabber_monitor_settings` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT ' (для контакт-листа из мониторов )',
  `jabber_server` varchar(150) NOT NULL COMMENT 'сервер ( talk.google.com )',
  `jabber_server_port` int(10) unsigned NOT NULL COMMENT 'порт (5222)',
  `jabber_server_proxy` varchar(150) NOT NULL COMMENT 'прокси ( gmail.com )',
  `jabber_login` varchar(45) NOT NULL COMMENT 'логин ( fenomen.technik@gmail.com)',
  `jabber_password` varchar(45) NOT NULL COMMENT 'пароль ( fenomer.server )',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=cp1251 COMMENT='настройки для клиента Jabber, на стороне сервера';

--
-- Dumping data for table `system_jabber_monitor_settings`
--

/*!40000 ALTER TABLE `system_jabber_monitor_settings` DISABLE KEYS */;
INSERT INTO `system_jabber_monitor_settings` (`id`,`jabber_server`,`jabber_server_port`,`jabber_server_proxy`,`jabber_login`,`jabber_password`) VALUES 
 (1,'127.0.0.1',5222,'technik','server@127.0.0.1','server');
/*!40000 ALTER TABLE `system_jabber_monitor_settings` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
