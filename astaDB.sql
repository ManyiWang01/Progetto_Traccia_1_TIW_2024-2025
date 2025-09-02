CREATE DATABASE  IF NOT EXISTS `asta` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `asta`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: asta
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `articolo`
--

DROP TABLE IF EXISTS `articolo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `articolo` (
  `id_articolo` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(64) NOT NULL,
  `descrizione` mediumtext NOT NULL,
  `immagine` varchar(255) NOT NULL,
  `prezzo` decimal(10,2) NOT NULL,
  `username` varchar(255) NOT NULL,
  `id_asta` int DEFAULT NULL,
  PRIMARY KEY (`id_articolo`),
  KEY `username_idx` (`username`),
  KEY `id_asta_idx` (`id_asta`),
  CONSTRAINT `id_asta_articolo` FOREIGN KEY (`id_asta`) REFERENCES `asta` (`id_asta`) ON UPDATE CASCADE,
  CONSTRAINT `user_articolo` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articolo`
--

LOCK TABLES `articolo` WRITE;
/*!40000 ALTER TABLE `articolo` DISABLE KEYS */;
INSERT INTO `articolo` VALUES (2,'tavolo1','tavolo di legno','imgtavolo1.jpg',20.00,'user1',NULL),(3,'tavolo2','tavolo di legno','imgtavolo2.jpg',15.00,'user2',5),(4,'sedia1','sedia di legno','imgsedia1.jpg',5.50,'user1',1),(5,'sedia2','sedia di plastica','imgsedia2.jpg',3.00,'user1',1),(6,'sedia3','sedia da gaming','imgsedia3',35.00,'user3',NULL),(7,'mouse','mouse gaming','imgmouse.jpg',6.00,'user3',NULL),(8,'tastiera1','tastiera gaming','imgtastiera1.jpg',12.50,'user3',NULL),(9,'tastiera2','tastiera wireless','imgtastiera2.jpg',25.00,'user2',NULL),(10,'tastiera3','tastiera meccanica','imgtastiera3.jpg',18.50,'user4',4),(11,'tastiera4','tastiera meccanica gaming','imgtastiera4.jpg',20.50,'user5',NULL),(12,'cuffia1','cuffia wireless','imgcuffia1.jpg',25.00,'user5',NULL),(13,'cuffia2','cuffiada gaming','imgcuffia2.jpg',18.00,'user4',4),(14,'anello1','gioiello in argento ','imganello1.jpg',50.00,'user2',2),(15,'anello2','gioiello in oro','imganello2.jpg',100.00,'user2',2),(16,'anello3','gioiello in oro con perla','imganello3.jpg',60.00,'user1',NULL),(17,'anello4','gioiello in oro con perla','imganello4.jpg',45.50,'user1',NULL),(18,'bracciale','bracciale in argento','imgbracciale.jpg',50.00,'user3',3),(19,'ciondolo1','ciondolo in oro','imgciondolo1.jpg',75.00,'user2',NULL),(20,'ciondolo2','ciondolo in argento','imgciondolo2.jpg',105.00,'user2',NULL),(21,'ciondolo3','ciondolo con perla','imgciondolo3.jpg',64.50,'user1',NULL),(22,'penna','a sfera','imgpenna',2.00,'user1',NULL),(40,'smartphone','nuovo','imgsmartphone',500.00,'user5',6),(41,'ciotola','legno','imgciotola',5.00,'user2',7),(42,'borsa1','pelle vera','imgborsa1',200.00,'user2',NULL),(43,'padella','nuovo antiaderente','imgpadella',20.00,'user2',7),(44,'borsa2','di lusso','imgborsa2',3500.00,'user2',NULL),(45,'chitarra1','nuovo','imgchitarra1',300.00,'user3',8),(46,'chitarra2','acustica','imgchitarra2',150.00,'user3',NULL),(47,'basso','nuovo','imgbasso',200.00,'user3',NULL),(48,'abito','lungo con etichetta','imgabito',50.00,'user4',9),(49,'maglia','nuovo con etichetta','imgmaglia',20.00,'user4',9),(50,'pantalone di lusso','nero nuovo','imgpantalone',125.00,'user4',NULL),(51,'scarpe','di colore nero','imgstampante',60.00,'user4',9),(52,'giacca di lusso','nuovo con tasca ','imggiacca',150.00,'user5',NULL),(53,'stampante','nuovo','imgstampante',85.50,'user5',10),(54,'giacca da vento','nuovo','imggiacca',45.00,'user5',NULL),(55,'scaffale','di legno','imgscaffale',80.00,'user5',11);
/*!40000 ALTER TABLE `articolo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asta`
--

DROP TABLE IF EXISTS `asta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asta` (
  `id_asta` int NOT NULL AUTO_INCREMENT,
  `p_iniziale` decimal(10,2) NOT NULL,
  `min_rialzo` int NOT NULL,
  `data_scadenza` timestamp NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `winner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id_asta`),
  KEY `username_idx` (`username`) /*!80000 INVISIBLE */,
  KEY `winner_idx` (`winner`),
  KEY `user_idx` (`username`,`winner`),
  CONSTRAINT `user_asta` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE,
  CONSTRAINT `winner_asta` FOREIGN KEY (`winner`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asta`
--

LOCK TABLES `asta` WRITE;
/*!40000 ALTER TABLE `asta` DISABLE KEYS */;
INSERT INTO `asta` VALUES (1,8.50,1,'2025-09-04 22:00:00','user1',0,NULL),(2,150.00,1,'2025-09-03 22:00:00','user2',0,NULL),(3,50.00,2,'2025-02-01 23:00:00','user3',1,'user2'),(4,38.50,1,'2025-03-02 23:00:00','user4',1,'user1'),(5,15.00,2,'2025-04-03 22:00:00','user2',1,'user3'),(6,500.00,50,'2025-07-07 20:00:00','user5',1,'user4'),(7,25.00,2,'2025-07-01 08:05:00','user2',1,'user5'),(8,300.00,10,'2025-09-05 10:35:00','user3',0,NULL),(9,130.00,2,'2025-09-06 08:00:00','user4',0,NULL),(10,85.50,5,'2025-06-14 10:00:00','user5',1,'user1'),(11,80.00,1,'2025-08-30 10:00:00','user5',0,NULL);
/*!40000 ALTER TABLE `asta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offerta`
--

DROP TABLE IF EXISTS `offerta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `offerta` (
  `id_asta` int NOT NULL,
  `username` varchar(255) NOT NULL,
  `data_offerta` timestamp NOT NULL,
  `p_offerta` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_asta`,`username`,`data_offerta`),
  KEY `username_idx` (`username`),
  CONSTRAINT `id_asta_offerta` FOREIGN KEY (`id_asta`) REFERENCES `asta` (`id_asta`) ON UPDATE CASCADE,
  CONSTRAINT `user_offerta` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offerta`
--

LOCK TABLES `offerta` WRITE;
/*!40000 ALTER TABLE `offerta` DISABLE KEYS */;
INSERT INTO `offerta` VALUES (1,'user2','2025-09-03 15:00:00',10.00),(1,'user3','2025-09-03 15:01:00',20.00),(3,'user1','2025-02-01 06:00:00',52.00),(3,'user1','2025-02-01 06:07:00',58.00),(3,'user2','2025-02-01 06:05:00',55.00),(3,'user2','2025-02-01 06:10:00',60.00),(4,'user1','2025-03-01 11:00:00',40.00),(4,'user1','2025-03-01 11:06:00',50.00),(4,'user1','2025-03-02 11:00:00',70.00),(4,'user2','2025-03-01 11:05:00',41.00),(4,'user3','2025-03-01 11:05:00',45.00),(4,'user5','2025-03-01 11:10:00',60.00),(5,'user3','2025-04-03 15:00:00',35.00),(5,'user5','2025-04-03 13:00:00',17.00),(6,'user1','2025-07-04 20:00:00',600.00),(6,'user2','2025-07-05 06:10:00',650.00),(6,'user2','2025-07-07 09:00:00',750.00),(6,'user4','2025-07-04 10:00:00',550.00),(6,'user4','2025-07-06 07:00:00',700.00),(6,'user4','2025-07-07 18:00:00',800.00),(7,'user3','2025-06-30 08:05:00',27.00),(7,'user3','2025-07-01 07:30:00',37.00),(7,'user4','2025-07-01 07:05:00',29.00),(7,'user4','2025-07-01 07:45:00',39.00),(7,'user5','2025-07-01 07:25:00',35.00),(7,'user5','2025-07-01 08:00:00',42.00),(8,'user1','2025-09-01 13:35:00',350.00),(8,'user1','2025-09-02 08:48:00',375.00),(8,'user2','2025-09-01 13:35:00',340.00),(8,'user2','2025-09-02 18:35:00',405.00),(8,'user4','2025-09-01 10:35:00',310.00),(8,'user4','2025-09-01 11:51:00',330.00),(8,'user4','2025-09-01 13:35:00',360.00),(8,'user4','2025-09-02 13:55:00',385.00),(8,'user5','2025-09-01 11:45:00',320.00),(8,'user5','2025-09-02 17:00:00',395.00),(9,'user1','2025-09-02 05:35:00',132.00),(9,'user1','2025-09-02 18:35:00',150.00),(9,'user3','2025-09-02 06:05:00',134.00),(10,'user1','2025-06-11 10:00:00',100.00),(10,'user1','2025-06-13 10:00:00',120.00),(10,'user1','2025-06-14 09:00:00',130.00),(10,'user3','2025-06-10 10:00:00',95.00),(10,'user3','2025-06-12 10:00:00',105.00),(10,'user3','2025-06-14 08:00:00',125.00),(11,'user1','2025-08-28 10:00:00',81.00),(11,'user1','2025-08-29 10:00:00',83.00),(11,'user3','2025-08-30 09:00:00',95.00),(11,'user4','2025-08-28 13:00:00',82.00),(11,'user4','2025-08-29 13:00:00',85.00);
/*!40000 ALTER TABLE `offerta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nome` varchar(32) NOT NULL,
  `cognome` varchar(32) NOT NULL,
  `indirizzo` varchar(64) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('user1','password1','Giulio','Rossi','via user 1'),('user2','password2','Anna','Blu','via user 2'),('user3','password3','Manuel','Verdi','via user 3'),('user4','password3','Paolo','Gialli','via user 4'),('user5','password4','Rachele','Bianchi','via user 5');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'asta'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-02 21:14:17
