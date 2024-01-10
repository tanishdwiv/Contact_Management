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
CREATE TABLE contact_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contact_name VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


SHOW DATABASES  ;
USE contact_manager ;
SHOW TABLES ;
SHOW TABLES FROM contact_manager;

SELECT * FROM contacts;
SELECT * FROM categories;
SELECT * FROM contacts , categories;
SELECT * FROM contact_history;


SELECT name,phone FROM contacts ;

SELECT name,phone, COUNT(*)
FROM contacts
GROUP BY name, phone 
HAVING COUNT(*) > 1;



ALTER TABLE contacts
ADD COLUMN timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
