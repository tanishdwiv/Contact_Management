CREATE DATABASE IF NOT EXISTS contact_manager;
USE contact_manager;

CREATE TABLE IF NOT EXISTS categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS contacts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

SHOW DATABASES  ;
USE contact_manager ;
SHOW TABLES ;
SHOW TABLES FROM contact_manager;
SELECT * FROM contacts;
SELECT * FROM categories;
SELECT * FROM contacts , categories;
SELECT name,phone FROM contacts ;