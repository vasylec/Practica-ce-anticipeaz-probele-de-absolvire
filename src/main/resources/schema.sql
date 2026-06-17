DROP TABLE IF EXISTS rental;
DROP TABLE IF EXISTS vehicle;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS app_user;
DROP TABLE IF EXISTS customer;

CREATE TABLE vehicle (
     vehicle_id INT AUTO_INCREMENT PRIMARY KEY,
     current_mileage INT NOT NULL CHECK (current_mileage >= 0),
     engine_size DECIMAL(6,1) NOT NULL CHECK (engine_size > 0),
     manufacturer VARCHAR(150) NOT NULL,
     model VARCHAR(150) NOT NULL,
     price_per_day DECIMAL(8,2),
     manufacturer_year INT,
     license_plate VARCHAR(7) NOT NULL CHECK (REGEXP_LIKE(license_plate, '^[A-Z]{3}-[0-9]{3}$')),
     image VARCHAR(255)
);

CREATE TABLE customer(
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    second_name VARCHAR(255),
    phone VARCHAR(255),
    total_rentals INT,
    late_returns INT
);

CREATE TABLE rental(
    rental_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    vehicle_id INT,
    rental_start_date TIMESTAMP,
    rental_end_date TIMESTAMP,
    total_price DECIMAL(10, 2),

    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
            REFERENCES customer(customer_id),

    CONSTRAINT fk_vehicle
        FOREIGN KEY (vehicle_id)
            REFERENCES vehicle(vehicle_id)
);

CREATE TABLE app_user(
    user_id INT AUTO_INCREMENT PRIMARY KEY ,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL,
    password_reset_code VARCHAR(20),
    password_reset_code_expires_at TIMESTAMP,
    balance DECIMAL(10, 2),
    customer_id INT,

    CONSTRAINT fk_user_customer
            FOREIGN KEY (customer_id)
                REFERENCES customer(customer_id)
);

CREATE TABLE payment(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE ,
    payment_id VARCHAR(255) NOT NULL UNIQUE ,
    amount DECIMAL(10, 2),
    user_id INT,
    processed BOOLEAN NOT NULL DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_user
        FOREIGN KEY (user_id)
            REFERENCES  app_user(user_id)
);

CREATE INDEX idx_vehicle_manufacturer ON vehicle(manufacturer);
CREATE INDEX idx_vehicle_model ON vehicle(model);
CREATE INDEX idx_vehicle_license_plate ON vehicle(license_plate);

CREATE INDEX idx_customer_first_name ON customer(first_name);
CREATE INDEX idx_customer_second_name ON customer(second_name);
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_app_user_email ON app_user(email);

CREATE INDEX idx_rental_customer ON rental(customer_id);
CREATE INDEX idx_rental_vehicle ON rental(vehicle_id);
CREATE INDEX idx_rental_dates ON rental(rental_start_date, rental_end_date);
