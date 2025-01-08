-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 30, 2024 at 04:35 PM
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
-- Database: `spendingdb`
--

-- --------------------------------------------------------

--
-- Table structure for table `category_info`
--

CREATE TABLE `category_info` (
  `category` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category_info`
--

INSERT INTO `category_info` (`category`) VALUES
('Bacon'),
('Beef'),
('Chicken'),
('Shane-Test');

-- --------------------------------------------------------
-- Table structure for table `person_info`

CREATE TABLE `person_info` (
  `person` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
--
-- Dumping data for table `person_info`

INSERT INTO `person_info` (`person`) VALUES
('Shane'),
('Sai')
('Kusuma');

-- Table structure for table `person_info`
CREATE TABLE `person_spendings` (
  `pid` int(11) NOT NULL,
  `person` varchar(50) NOT NULL,
  `pdate` date NOT NULL,
  `amount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table `person_spendings`

INSERT INTO `person_spendings` (`pid`, `person`, `pdate`, `amount`) VALUES
(1, 'Shane', '2024-04-29', 200),
(2, 'Sai', '2024-04-28', 350);

-- Table structure for table `spendings`
--

CREATE TABLE `spendings` (
  `sid` int(11) NOT NULL,
  `category` varchar(50) NOT NULL,
  `sdate` date NOT NULL,
  `amount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `spendings`
--

INSERT INTO `spendings` (`sid`, `category`, `sdate`, `amount`) VALUES
(1, 'Sugar', '2020-04-29', 200),
(3, 'CookingOil', '2020-04-28', 350),
(4, 'Coffee', '2019-04-13', 666),
(5, 'Coffee', '2020-04-08', 99),
(6, 'Coffee', '2020-04-09', 88),
(10, 'CookingOil', '2020-04-17', 456),
(11, 'Sugar', '2020-04-16', 785),
(13, 'Tomato', '2020-04-28', 100),
(14, 'Beef', '2024-12-03', 12);



--
-- Indexes for dumped tables
--

--
-- Indexes for table `category_info`
--
ALTER TABLE `category_info`
  ADD PRIMARY KEY (`category`);

-- Indexes for table `person_info`
ALTER TABLE `person_info`
  ADD PRIMARY KEY (`person`);

-- Indexes for table `person_spendings` 
ALTER TABLE `person_spendings`
  ADD PRIMARY KEY (`pid`); 
--
-- Indexes for table `spendings`
--
ALTER TABLE `spendings`
  ADD PRIMARY KEY (`sid`);

--
-- AUTO_INCREMENT for dumped tables
--
-- AUTO_INCREMENT for table `person_spendings`

ALTER TABLE `person_spendings`
  MODIFY `pid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

--
-- AUTO_INCREMENT for table `spendings`
--
ALTER TABLE `spendings`
  MODIFY `sid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
