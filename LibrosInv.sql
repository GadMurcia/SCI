-- phpMyAdmin SQL Dump
-- version 5.1.1deb4
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost:3306
-- Tiempo de generación: 28-09-2021 a las 01:37:32
-- Versión del servidor: 10.6.4-MariaDB-1:10.6.4+maria~sid
-- Versión de PHP: 7.4.21

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `LibrosInv`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Compras`
--

CREATE TABLE `Compras` (
  `idCompras` datetime NOT NULL,
  `usuario` varchar(10) NOT NULL,
  `valor` decimal(11,2) NOT NULL,
  `comentario` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `Compras`
--

INSERT INTO `Compras` (`idCompras`, `usuario`, `valor`, `comentario`) VALUES
('2021-09-25 10:47:06', '04536707-3', '8.46', NULL),
('2021-09-25 11:45:34', '04536707-3', '1.20', NULL),
('2021-09-25 11:52:57', '04536707-3', '11.25', NULL),
('2021-09-27 19:16:46', '04536707-3', '8.40', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `DetalleCompra`
--

CREATE TABLE `DetalleCompra` (
  `idCompras` datetime NOT NULL,
  `usuario` varchar(10) NOT NULL,
  `producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `costoUnitario` decimal(11,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `DetalleCompra`
--

INSERT INTO `DetalleCompra` (`idCompras`, `usuario`, `producto`, `cantidad`, `costoUnitario`) VALUES
('2021-09-25 10:47:06', '04536707-3', 6, 10, '0.75'),
('2021-09-25 10:47:06', '04536707-3', 18, 12, '0.08'),
('2021-09-25 11:45:34', '04536707-3', 18, 15, '0.08'),
('2021-09-25 11:52:57', '04536707-3', 4, 25, '0.45'),
('2021-09-27 19:16:46', '04536707-3', 19, 12, '0.70');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `DetalleVentas`
--

CREATE TABLE `DetalleVentas` (
  `idVentas` datetime NOT NULL,
  `giroCaja` int(11) NOT NULL,
  `producto` int(11) NOT NULL,
  `cantidad` double NOT NULL,
  `precioUnitario` decimal(11,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `GiroDeCaja`
--

CREATE TABLE `GiroDeCaja` (
  `idGiroDeCaja` int(11) NOT NULL,
  `inicio` datetime NOT NULL,
  `fin` datetime DEFAULT NULL,
  `responsable` varchar(10) NOT NULL,
  `cajaInicial` double NOT NULL DEFAULT 0,
  `faltantes` double NOT NULL DEFAULT 0,
  `excedentes` double NOT NULL DEFAULT 0,
  `cierre` double NOT NULL DEFAULT 0,
  `retiros` double NOT NULL DEFAULT 0,
  `detalleRetiros` varchar(300) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Inventario`
--

CREATE TABLE `Inventario` (
  `idInventario` int(11) NOT NULL,
  `producto` varchar(100) NOT NULL,
  `precioUnitario` decimal(11,2) NOT NULL,
  `tienda` int(11) NOT NULL,
  `activo` bit(1) NOT NULL DEFAULT b'1',
  `descripcion` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `Inventario`
--

INSERT INTO `Inventario` (`idInventario`, `producto`, `precioUnitario`, `tienda`, `activo`, `descripcion`) VALUES
(1, 'COSCAFE 1.5gr', '0.10', 1, b'1', 'café instantáneo'),
(2, 'Azúcar 900gr', '1.00', 1, b'1', 'azucar '),
(3, 'café atlacat', '0.15', 1, b'1', 'café para cafetera'),
(4, 'azucar 1lb', '0.55', 1, b'1', 'azucar'),
(5, 'azucar 2.5kg', '2.25', 1, b'1', 'azucar'),
(6, 'Jabón PROTEX variedad0', '0.90', 1, b'1', ''),
(7, 'consomé continental 10 gr', '0.15', 1, b'1', 'continental'),
(8, 'fabuloso 50 ml', '0.25', 1, b'1', ''),
(17, 'azucar 2 lb', '0.95', 1, b'1', ''),
(18, 'Nachos Diana 20gr', '0.10', 1, b'1', ''),
(19, 'Nacho Diana 100gr', '0.85', 1, b'1', ''),
(20, 'Tocinitos Diana', '0.10', 1, b'1', ''),
(21, 'Alborotos Diana', '0.10', 1, b'1', '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Misc`
--

CREATE TABLE `Misc` (
  `idMisc` int(11) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `propietario` varchar(10) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `logo` blob DEFAULT NULL,
  `telefonos` varchar(88) DEFAULT NULL,
  `matriz` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `Misc`
--

INSERT INTO `Misc` (`idMisc`, `nombre`, `propietario`, `direccion`, `logo`, `telefonos`, `matriz`) VALUES
(1, 'Santa Bárbara', '00000000-0', 'Colonia santa bárbara', NULL, '78965423;78569874', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `TipoUsuario`
--

CREATE TABLE `TipoUsuario` (
  `idTipoUsuario` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `tpComentario` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `TipoUsuario`
--

INSERT INTO `TipoUsuario` (`idTipoUsuario`, `nombre`, `tpComentario`) VALUES
(1, 'Sistema', NULL),
(2, 'Propietario', NULL),
(3, 'Administrador', NULL),
(4, 'Empleado', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Usuario`
--

CREATE TABLE `Usuario` (
  `idUsuario` varchar(10) NOT NULL,
  `nombres` varchar(60) NOT NULL,
  `apellidos` varchar(60) NOT NULL,
  `passwd` varchar(50) NOT NULL,
  `tipoUsuario` int(11) NOT NULL,
  `empresa` int(11) DEFAULT NULL,
  `activo` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Volcado de datos para la tabla `Usuario`
--

INSERT INTO `Usuario` (`idUsuario`, `nombres`, `apellidos`, `passwd`, `tipoUsuario`, `empresa`, `activo`) VALUES
('00000000-0', 'Andrés', 'Andrea', 'ff34dd4411a7d929576c5828f01cb5e9', 2, NULL, b'1'),
('04536707-3', 'Guillermo Aldolfo', 'Delsas Murcia', '02951a6cb9595ef475fed783e59c687e', 1, NULL, b'1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Ventas`
--

CREATE TABLE `Ventas` (
  `idVentas` datetime NOT NULL,
  `giroCaja` int(11) NOT NULL,
  `valor` decimal(11,2) NOT NULL,
  `comentario` varchar(250) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `Compras`
--
ALTER TABLE `Compras`
  ADD PRIMARY KEY (`idCompras`,`usuario`),
  ADD KEY `fk_Compras_1_idx` (`usuario`);

--
-- Indices de la tabla `DetalleCompra`
--
ALTER TABLE `DetalleCompra`
  ADD PRIMARY KEY (`idCompras`,`usuario`,`producto`),
  ADD KEY `fk_DetalleCompra_1_idx` (`usuario`),
  ADD KEY `fk_DetalleCompra_1` (`usuario`,`idCompras`),
  ADD KEY `fk_DetalleCompra_2_idx` (`producto`);

--
-- Indices de la tabla `DetalleVentas`
--
ALTER TABLE `DetalleVentas`
  ADD PRIMARY KEY (`idVentas`,`producto`,`giroCaja`),
  ADD KEY `fk_DetalleVentas_2_idx` (`giroCaja`),
  ADD KEY `fk_DetalleVentas_2` (`giroCaja`,`idVentas`),
  ADD KEY `fk_DetalleVentas_1_idx` (`producto`);

--
-- Indices de la tabla `GiroDeCaja`
--
ALTER TABLE `GiroDeCaja`
  ADD PRIMARY KEY (`idGiroDeCaja`),
  ADD KEY `fk_GiroDeCaja_1_idx` (`responsable`);

--
-- Indices de la tabla `Inventario`
--
ALTER TABLE `Inventario`
  ADD PRIMARY KEY (`idInventario`),
  ADD UNIQUE KEY `producto_UNIQUE` (`producto`),
  ADD KEY `fk_Inventario_1_idx` (`tienda`);

--
-- Indices de la tabla `Misc`
--
ALTER TABLE `Misc`
  ADD PRIMARY KEY (`idMisc`),
  ADD KEY `fk_Misc_1_idx` (`propietario`),
  ADD KEY `fk_Misc_2_idx` (`matriz`);

--
-- Indices de la tabla `TipoUsuario`
--
ALTER TABLE `TipoUsuario`
  ADD PRIMARY KEY (`idTipoUsuario`);

--
-- Indices de la tabla `Usuario`
--
ALTER TABLE `Usuario`
  ADD PRIMARY KEY (`idUsuario`),
  ADD KEY `fk_Usuario_1_idx` (`tipoUsuario`),
  ADD KEY `fk_Usuario_2_idx` (`empresa`);

--
-- Indices de la tabla `Ventas`
--
ALTER TABLE `Ventas`
  ADD PRIMARY KEY (`idVentas`,`giroCaja`),
  ADD KEY `fk_Ventas_1_idx` (`giroCaja`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `GiroDeCaja`
--
ALTER TABLE `GiroDeCaja`
  MODIFY `idGiroDeCaja` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `Inventario`
--
ALTER TABLE `Inventario`
  MODIFY `idInventario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT de la tabla `Misc`
--
ALTER TABLE `Misc`
  MODIFY `idMisc` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `TipoUsuario`
--
ALTER TABLE `TipoUsuario`
  MODIFY `idTipoUsuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `Compras`
--
ALTER TABLE `Compras`
  ADD CONSTRAINT `fk_Compras_1` FOREIGN KEY (`usuario`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `DetalleCompra`
--
ALTER TABLE `DetalleCompra`
  ADD CONSTRAINT `fk_DetalleCompra_1` FOREIGN KEY (`usuario`,`idCompras`) REFERENCES `Compras` (`usuario`, `idCompras`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_DetalleCompra_2` FOREIGN KEY (`producto`) REFERENCES `Inventario` (`idInventario`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `DetalleVentas`
--
ALTER TABLE `DetalleVentas`
  ADD CONSTRAINT `fk_DetalleVentas_1` FOREIGN KEY (`producto`) REFERENCES `Inventario` (`idInventario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_DetalleVentas_2` FOREIGN KEY (`giroCaja`,`idVentas`) REFERENCES `Ventas` (`giroCaja`, `idVentas`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `GiroDeCaja`
--
ALTER TABLE `GiroDeCaja`
  ADD CONSTRAINT `fk_GiroDeCaja_1` FOREIGN KEY (`responsable`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `Inventario`
--
ALTER TABLE `Inventario`
  ADD CONSTRAINT `fk_Inventario_1` FOREIGN KEY (`tienda`) REFERENCES `Misc` (`idMisc`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `Misc`
--
ALTER TABLE `Misc`
  ADD CONSTRAINT `fk_Misc_1` FOREIGN KEY (`propietario`) REFERENCES `Usuario` (`idUsuario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_Misc_2` FOREIGN KEY (`matriz`) REFERENCES `Misc` (`idMisc`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `Usuario`
--
ALTER TABLE `Usuario`
  ADD CONSTRAINT `fk_Usuario_1` FOREIGN KEY (`tipoUsuario`) REFERENCES `TipoUsuario` (`idTipoUsuario`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_Usuario_2` FOREIGN KEY (`empresa`) REFERENCES `Misc` (`idMisc`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `Ventas`
--
ALTER TABLE `Ventas`
  ADD CONSTRAINT `fk_Ventas_1` FOREIGN KEY (`giroCaja`) REFERENCES `GiroDeCaja` (`idGiroDeCaja`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
