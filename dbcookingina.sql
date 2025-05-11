-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 10, 2025 at 05:17 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dbcookingina`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbllevel`
--

CREATE TABLE `tbllevel` (
  `levelid` int(11) NOT NULL,
  `targetincome` int(11) NOT NULL,
  `maxcustomers` int(11) NOT NULL,
  `timelimit` int(11) NOT NULL,
  `patiencelevel` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tbllevel`
--

INSERT INTO `tbllevel` (`levelid`, `targetincome`, `maxcustomers`, `timelimit`, `patiencelevel`) VALUES
(1, 50, 8, 120, 60),
(2, 100, 10, 120, 60),
(3, 150, 12, 135, 60),
(4, 200, 14, 150, 45),
(5, 250, 16, 165, 45),
(6, 300, 18, 180, 45),
(7, 350, 20, 195, 30),
(8, 400, 22, 210, 30),
(9, 450, 24, 225, 30),
(10, 500, 26, 240, 30);

-- --------------------------------------------------------

--
-- Table structure for table `tblplayer`
--

CREATE TABLE `tblplayer` (
  `playerid` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `displayname` varchar(255) NOT NULL,
  `joindate` datetime NOT NULL DEFAULT current_timestamp(),
  `levelid` int(11) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tblplayer`
--

INSERT INTO `tblplayer` (`playerid`, `username`, `password`, `displayname`, `joindate`, `levelid`) VALUES
(1, 'luis', '123', 'luis', '2025-05-10 23:00:22', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbllevel`
--
ALTER TABLE `tbllevel`
  ADD PRIMARY KEY (`levelid`);

--
-- Indexes for table `tblplayer`
--
ALTER TABLE `tblplayer`
  ADD PRIMARY KEY (`playerid`),
  ADD KEY `levelid` (`levelid`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbllevel`
--
ALTER TABLE `tbllevel`
  MODIFY `levelid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `tblplayer`
--
ALTER TABLE `tblplayer`
  MODIFY `playerid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `tblplayer`
--
ALTER TABLE `tblplayer`
  ADD CONSTRAINT `tblplayer_ibfk_1` FOREIGN KEY (`levelid`) REFERENCES `tbllevel` (`levelid`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
