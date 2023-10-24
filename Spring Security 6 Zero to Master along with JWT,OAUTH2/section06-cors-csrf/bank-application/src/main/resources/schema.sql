create database if not exists bank_security;
use bank_security;


CREATE TABLE `users` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `username` VARCHAR(45) NOT NULL,
   `password` VARCHAR(45) NOT NULL,
   `enabled` INT NOT NULL,
   PRIMARY KEY (`id`));

CREATE TABLE `authorities` (
   `id` int NOT NULL AUTO_INCREMENT,
   `username` varchar(45) NOT NULL,
   `authority` varchar(45) NOT NULL,
   PRIMARY KEY (`id`));

INSERT IGNORE INTO `users` VALUES (NULL, 'fox', '12345', '1');
INSERT IGNORE INTO `authorities` VALUES (NULL, 'fox', 'write');


# let's use a custom table for users management
CREATE TABLE `customer` (
    `id` int NOT NULL AUTO_INCREMENT,
    `email` varchar(45) NOT NULL,
    `pwd` varchar(200) NOT NULL,
    `role` varchar(45) NOT NULL,
    PRIMARY KEY (`id`)
);

INSERT INTO `customer` (`email`, `pwd`, `role`)
VALUES ('oops@me.com', '54321', 'admin');
