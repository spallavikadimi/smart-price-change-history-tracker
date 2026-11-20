
CREATE DATABASE IF NOT EXISTS smart_price_tracker;

USE smart_price_tracker;

-- 1. Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tracked products table
CREATE TABLE IF NOT EXISTS tracked_products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    product_url VARCHAR(1000) NOT NULL,
    product_name VARCHAR(255),
    current_price DECIMAL(10,2),
    target_price DECIMAL(10,2),
    tracking_status ENUM('ACTIVE', 'PAUSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 3. Price history table
CREATE TABLE IF NOT EXISTS price_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES tracked_products(product_id)
);

-- 4. Price alerts table
CREATE TABLE IF NOT EXISTS price_alerts (
    alert_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    target_price DECIMAL(10,2) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES tracked_products(product_id)
);

-- 5. Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    message VARCHAR(500) NOT NULL,
    notification_type ENUM(
        'PRICE_DROP',
        'PRICE_INCREASE',
        'TARGET_REACHED'
    ) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES tracked_products(product_id)
);
