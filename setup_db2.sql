 CREATE DATABASE product_db;

USE product_db;

CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL
);

INSERT INTO products (product_name, price, quantity) VALUES
('Laptop', 1200.50, 50),
('Keyboard', 75.00, 200),
('Mouse', 25.00, 350);