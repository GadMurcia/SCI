CREATE DATABASE  IF NOT EXISTS `LibrosInv` /*!40100 DEFAULT CHARACTER SET utf8mb3 */;
USE `LibrosInv`;
-- MariaDB dump 10.19  Distrib 10.6.4-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: 127.0.0.1    Database: LibrosInv
-- ------------------------------------------------------
-- Server version	10.6.4-MariaDB-1:10.6.4+maria~stretch

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
-- Table structure for table `Compras`
--

DROP TABLE IF EXISTS `Compras`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Compras` (
  `idCompras` datetime NOT NULL,
  `usuario` varchar(10) NOT NULL,
  `valor` decimal(11,2) NOT NULL,
  `comentario` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idCompras`,`usuario`),
  KEY `fk_Compras_1_idx` (`usuario`),
  CONSTRAINT `fk_Compras_1` FOREIGN KEY (`usuario`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Compras`
--

LOCK TABLES `Compras` WRITE;
/*!40000 ALTER TABLE `Compras` DISABLE KEYS */;
/*!40000 ALTER TABLE `Compras` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DetalleCompra`
--

DROP TABLE IF EXISTS `DetalleCompra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DetalleCompra` (
  `idCompras` datetime NOT NULL,
  `usuario` varchar(10) NOT NULL,
  `producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `costoUnitario` decimal(11,2) NOT NULL,
  PRIMARY KEY (`idCompras`,`usuario`,`producto`),
  KEY `fk_DetalleCompra_1_idx` (`usuario`),
  KEY `fk_DetalleCompra_2_idx` (`producto`),
  KEY `fk_DetalleCompra_1` (`usuario`,`idCompras`),
  CONSTRAINT `fk_DetalleCompra_1` FOREIGN KEY (`usuario`, `idCompras`) REFERENCES `Compras` (`usuario`, `idCompras`) ON UPDATE CASCADE,
  CONSTRAINT `fk_DetalleCompra_2` FOREIGN KEY (`producto`) REFERENCES `Inventario` (`idInventario`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DetalleCompra`
--

LOCK TABLES `DetalleCompra` WRITE;
/*!40000 ALTER TABLE `DetalleCompra` DISABLE KEYS */;
/*!40000 ALTER TABLE `DetalleCompra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `DetalleVentas`
--

DROP TABLE IF EXISTS `DetalleVentas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DetalleVentas` (
  `idVentas` datetime NOT NULL,
  `giroCaja` int(11) NOT NULL,
  `producto` int(11) NOT NULL,
  `cantidad` double NOT NULL,
  `precioUnitario` decimal(11,2) NOT NULL,
  PRIMARY KEY (`idVentas`,`producto`,`giroCaja`),
  KEY `fk_DetalleVentas_1_idx` (`producto`),
  KEY `fk_DetalleVentas_2_idx` (`giroCaja`),
  KEY `fk_DetalleVentas_2` (`giroCaja`,`idVentas`),
  CONSTRAINT `fk_DetalleVentas_1` FOREIGN KEY (`producto`) REFERENCES `Inventario` (`idInventario`) ON UPDATE CASCADE,
  CONSTRAINT `fk_DetalleVentas_2` FOREIGN KEY (`giroCaja`, `idVentas`) REFERENCES `Ventas` (`giroCaja`, `idVentas`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `DetalleVentas`
--

LOCK TABLES `DetalleVentas` WRITE;
/*!40000 ALTER TABLE `DetalleVentas` DISABLE KEYS */;
/*!40000 ALTER TABLE `DetalleVentas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `GiroDeCaja`
--

DROP TABLE IF EXISTS `GiroDeCaja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `GiroDeCaja` (
  `idGiroDeCaja` int(11) NOT NULL,
  `inicio` datetime NOT NULL,
  `fin` datetime NOT NULL,
  `responsable` varchar(10) NOT NULL,
  `cajaInicial` double NOT NULL DEFAULT 0,
  `faltantes` double NOT NULL DEFAULT 0,
  `excedentes` double NOT NULL DEFAULT 0,
  `cierre` double NOT NULL DEFAULT 0,
  `retiros` double NOT NULL DEFAULT 0,
  `detalleRetiros` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`idGiroDeCaja`),
  KEY `fk_GiroDeCaja_1_idx` (`responsable`),
  CONSTRAINT `fk_GiroDeCaja_1` FOREIGN KEY (`responsable`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `GiroDeCaja`
--

LOCK TABLES `GiroDeCaja` WRITE;
/*!40000 ALTER TABLE `GiroDeCaja` DISABLE KEYS */;
/*!40000 ALTER TABLE `GiroDeCaja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Inventario`
--

DROP TABLE IF EXISTS `Inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Inventario` (
  `idInventario` int(11) NOT NULL,
  `producto` varchar(100) NOT NULL,
  `precioUnitario` decimal(11,2) NOT NULL,
  `descripcion` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idInventario`),
  UNIQUE KEY `producto_UNIQUE` (`producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Inventario`
--

LOCK TABLES `Inventario` WRITE;
/*!40000 ALTER TABLE `Inventario` DISABLE KEYS */;
/*!40000 ALTER TABLE `Inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Misc`
--

DROP TABLE IF EXISTS `Misc`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Misc` (
  `idMisc` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `propietario` varchar(10) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `logo` blob DEFAULT NULL,
  `telefonos` varchar(88) DEFAULT NULL,
  `matriz` int(11) DEFAULT NULL,
  PRIMARY KEY (`idMisc`),
  KEY `fk_Misc_1_idx` (`propietario`),
  KEY `fk_Misc_2_idx` (`matriz`),
  CONSTRAINT `fk_Misc_1` FOREIGN KEY (`propietario`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE,
  CONSTRAINT `fk_Misc_2` FOREIGN KEY (`matriz`) REFERENCES `Misc` (`idMisc`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Misc`
--

LOCK TABLES `Misc` WRITE;
/*!40000 ALTER TABLE `Misc` DISABLE KEYS */;
/*!40000 ALTER TABLE `Misc` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `TipoUsuario`
--

DROP TABLE IF EXISTS `TipoUsuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TipoUsuario` (
  `idTipoUsuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `tpComentario` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`idTipoUsuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `TipoUsuario`
--

LOCK TABLES `TipoUsuario` WRITE;
/*!40000 ALTER TABLE `TipoUsuario` DISABLE KEYS */;
INSERT INTO `TipoUsuario` VALUES (1,'Sistema',NULL),(2,'Propietario',NULL),(3,'Administrador',NULL),(4,'Empleado',NULL);
/*!40000 ALTER TABLE `TipoUsuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Usuario`
--

DROP TABLE IF EXISTS `Usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Usuario` (
  `idUsuario` varchar(10) NOT NULL,
  `nombres` varchar(60) NOT NULL,
  `apellidos` varchar(60) NOT NULL,
  `passwd` varchar(50) NOT NULL,
  `tipoUsuario` int(11) NOT NULL,
  `empresa` int(11) DEFAULT NULL,
  `activo` bit(1) NOT NULL,
  PRIMARY KEY (`idUsuario`),
  KEY `fk_Usuario_2_idx` (`empresa`),
  KEY `fk_Usuario_1_idx` (`tipoUsuario`),
  CONSTRAINT `fk_Usuario_1` FOREIGN KEY (`tipoUsuario`) REFERENCES `TipoUsuario` (`idTipoUsuario`) ON UPDATE CASCADE,
  CONSTRAINT `fk_Usuario_2` FOREIGN KEY (`empresa`) REFERENCES `Misc` (`idMisc`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Usuario`
--

LOCK TABLES `Usuario` WRITE;
/*!40000 ALTER TABLE `Usuario` DISABLE KEYS */;
INSERT INTO `Usuario` VALUES ('00000000-0','Andrés','Andrea','ff34dd4411a7d929576c5828f01cb5e9',2,NULL,''),('04536707-3','Guillermo Aldolfo','Delsas Murcia','02951a6cb9595ef475fed783e59c687e',1,NULL,'');
/*!40000 ALTER TABLE `Usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Ventas`
--

DROP TABLE IF EXISTS `Ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Ventas` (
  `idVentas` datetime NOT NULL,
  `giroCaja` int(11) NOT NULL,
  `valor` decimal(11,2) NOT NULL,
  `comentario` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`idVentas`,`giroCaja`),
  KEY `fk_Ventas_1_idx` (`giroCaja`),
  CONSTRAINT `fk_Ventas_1` FOREIGN KEY (`giroCaja`) REFERENCES `GiroDeCaja` (`idGiroDeCaja`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Ventas`
--

LOCK TABLES `Ventas` WRITE;
/*!40000 ALTER TABLE `Ventas` DISABLE KEYS */;
/*!40000 ALTER TABLE `Ventas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-09-17 11:51:09