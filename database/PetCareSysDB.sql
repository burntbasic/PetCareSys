-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 20, 2026 at 01:59 PM
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
-- Database: `PetCareSysDB`
--
CREATE DATABASE IF NOT EXISTS `PetCareSysDB` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `PetCareSysDB`;

-- --------------------------------------------------------

--
-- Table structure for table `ActivityLog`
--

CREATE TABLE `ActivityLog` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `activity` varchar(255) NOT NULL,
  `activity_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Appointments`
--

CREATE TABLE `Appointments` (
  `appointment_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `vet_id` int(11) NOT NULL,
  `appointment_date` datetime NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Scheduled',
  `reason` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Pets`
--

CREATE TABLE `Pets` (
  `pet_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Treatments`
--

CREATE TABLE `Treatments` (
  `treatment_id` int(11) NOT NULL,
  `appointment_id` int(11) NOT NULL,
  `vet_id` int(11) NOT NULL,
  `diagnosis` varchar(255) DEFAULT NULL,
  `treatment_description` text DEFAULT NULL,
  `medication` varchar(255) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Pending',
  `treatment_date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `user_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('PET_OWNER','VET','ADMIN') NOT NULL DEFAULT 'PET_OWNER',
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `ActivityLog`
--
ALTER TABLE `ActivityLog`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `Appointments`
--
ALTER TABLE `Appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `fk_appointment_pet` (`pet_id`),
  ADD KEY `fk_appointment_vet` (`vet_id`);

--
-- Indexes for table `Pets`
--
ALTER TABLE `Pets`
  ADD PRIMARY KEY (`pet_id`),
  ADD KEY `fk_pet_owner` (`owner_id`);

--
-- Indexes for table `Treatments`
--
ALTER TABLE `Treatments`
  ADD PRIMARY KEY (`treatment_id`),
  ADD KEY `fk_treatment_appointment` (`appointment_id`),
  ADD KEY `fk_treatment_vet` (`vet_id`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `unique_username` (`username`),
  ADD UNIQUE KEY `unique_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `ActivityLog`
--
ALTER TABLE `ActivityLog`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Appointments`
--
ALTER TABLE `Appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Pets`
--
ALTER TABLE `Pets`
  MODIFY `pet_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Treatments`
--
ALTER TABLE `Treatments`
  MODIFY `treatment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `ActivityLog`
--
ALTER TABLE `ActivityLog`
  ADD CONSTRAINT `ActivityLog_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`);

--
-- Constraints for table `Appointments`
--
ALTER TABLE `Appointments`
  ADD CONSTRAINT `fk_appointment_pet` FOREIGN KEY (`pet_id`) REFERENCES `Pets` (`pet_id`),
  ADD CONSTRAINT `fk_appointment_vet` FOREIGN KEY (`vet_id`) REFERENCES `Users` (`user_id`);

--
-- Constraints for table `Pets`
--
ALTER TABLE `Pets`
  ADD CONSTRAINT `fk_pet_owner` FOREIGN KEY (`owner_id`) REFERENCES `Users` (`user_id`);

--
-- Constraints for table `Treatments`
--
ALTER TABLE `Treatments`
  ADD CONSTRAINT `fk_treatment_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `Appointments` (`appointment_id`),
  ADD CONSTRAINT `fk_treatment_vet` FOREIGN KEY (`vet_id`) REFERENCES `Users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
