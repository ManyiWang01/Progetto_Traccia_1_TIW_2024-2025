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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articolo`
--

LOCK TABLES `articolo` WRITE;
/*!40000 ALTER TABLE `articolo` DISABLE KEYS */;
INSERT INTO `articolo` VALUES (22,'tavolo','questo e\' un tavolo','tavolo.jpg',45.00,'user2',14),(23,'tavolo','questo e\' un tavolo','tavolo.png',30.55,'user2',14),(24,'anello','anello di argento','anello.jpg',70.00,'user2',15),(25,'anello','anello di oro','anello.jpg',900.00,'user2',15),(26,'tavolo','tavolo plastica','tavolo.jpg',15.00,'user2',14),(27,'tavolo','tavolo','tavolo.jpg',45.00,'user2',NULL),(28,'sedia','sedia di legno','sedia.jpg',7.00,'user3',NULL),(29,'sedia','sedia di legno','sedia.jpg',10.00,'user3',16),(30,'sedia','sedia di plastica','sedia.jpg',5.00,'user3',16),(31,'sedia','10 sedie di legno','sedia.jpg',40.00,'user3',16);
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asta`
--

LOCK TABLES `asta` WRITE;
/*!40000 ALTER TABLE `asta` DISABLE KEYS */;
INSERT INTO `asta` VALUES (14,90.55,8,'2025-09-25 21:59:00','user2',0,NULL),(15,970.00,80,'2025-08-03 18:00:00','user2',0,NULL),(16,55.00,20,'2025-09-15 22:00:00','user3',0,NULL);
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
  PRIMARY KEY (`id_asta`,`username`,`data_offerta`,`p_offerta`),
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
INSERT INTO `offerta` VALUES (14,'user1','2025-07-31 15:47:00',100.55),(15,'user1','2025-07-31 15:45:00',1050.00),(15,'user1','2025-07-31 15:50:00',1210.00),(16,'user2','2025-08-31 16:03:00',20.00),(16,'user2','2025-08-31 16:04:00',40.00),(16,'user2','2025-08-31 16:04:00',60.00),(15,'user3','2025-07-31 15:50:00',1130.00),(15,'user4','2025-07-31 15:51:00',1290.00);
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
INSERT INTO `user` VALUES ('user1','password1','Giulio','Rossi','via user 1'),('user2','password2','Anna','Blu','via user 2'),('user3','password3','Manuel','Verdi','via user 3'),('user4','password4','Paolo','Gialli','via user 4'),('user5','password5','Rachele','Bianchi','via user 5');
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

-- Dump completed on 2025-08-31 18:07:22
