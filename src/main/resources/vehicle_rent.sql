use vehicle_rent


SELECT * FROM Customer
SELECT * FROM Rental
SELECT * FROM Vehicle
SELECT * FROM app_user


DROP TABLE Rental
DROP TABLE Vehicle
DROP TABLE app_user
DROP TABLE Customer

CREATE TABLE vehicle (
    vehicle_id INT IDENTITY(1,1) PRIMARY KEY,
    current_mileage INT NOT NULL CHECK (current_mileage >= 0),
    engine_size DECIMAL(6,1) NOT NULL CHECK (engine_size > 0),
    manufacturer VARCHAR(150) NOT NULL,
    model VARCHAR(150) NOT NULL,
    price_per_day DECIMAL(8,2),
    manufacturer_year INT,
    license_plate VARCHAR(7) NOT NULL CHECK (license_plate LIKE '[A-Z][A-Z][A-Z]-[0-9][0-9][0-9]'),
    image VARCHAR(255)
);

CREATE TABLE customer (  
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    first_name VARCHAR(255),
    second_name VARCHAR(255),
    phone VARCHAR(255),
    total_rentals INT,
    late_returns INT
);

CREATE TABLE rental (
    rental_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT,
    vehicle_id INT,
    rental_start_date DATETIME2,
    rental_end_date DATETIME2,
    total_price DECIMAL(10, 2),

    CONSTRAINT fk_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id),

    CONSTRAINT fk_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicle(vehicle_id)
);

CREATE TABLE app_user (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(255) NOT NULL,
    password_reset_code VARCHAR(20),
    password_reset_code_expires_at DATETIME2,
    balance DECIMAL(10, 2),
    customer_id INT,

    CONSTRAINT fk_user_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id)
);

CREATE TABLE payment(
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    payment_id VARCHAR(255) NOT NULL UNIQUE,
    amount DECIMAL(10, 2),
    user_id INT FOREIGN KEY REFERENCES app_user(user_id),
    processed BIT NOT NULL DEFAULT 0,
    created_date DATETIME DEFAULT GETDATE()
)

SELECT * FROM payment

CREATE UNIQUE INDEX idx_vehicle_license_plate ON vehicle(license_plate);
CREATE INDEX idx_customer_phone ON customer(phone);
CREATE INDEX idx_app_user_email ON app_user(email);
CREATE INDEX idx_vehicle_manufacturer_model ON vehicle(manufacturer, model);
CREATE INDEX idx_customer_name ON customer(first_name, second_name);



INSERT INTO vehicle
(model, manufacturer, manufacturer_year, license_plate, engine_size, current_mileage, price_per_day, image) 
VALUES
('Logan', 'Dacia', 2011,'BGC-683', 1600.0, 12000, 30.0, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0gXNrhhDE-Pe940YIwZyKmlRthBGTYW-PpQ&s'),
('Sandero', 'Dacia', 2022,'XNM-565', 1400.0, 25000, 28.0, 'https://car-images.bauersecure.com/wp-images/4720/dacia_sandero_longterm_78.jpg'),
('Passat', 'Volkswagen', 2018,'KWI-622', 2000.0, 40000, 55.0, 'https://s2-autoesporte.glbimg.com/M-flf8O5YZYOFobC964Xf6TRw7c=/0x0:620x413/984x0/smart/filters:strip_icc()/i.s3.glbimg.com/v1/AUTH_cf9d035bf26b4646b105bd958f32089d/internal_photos/bs/2020/x/i/Vswk0zQFK2Lb5tLW7hUA/2018-03-12-volkswagen-passat-8.jpg'),
('Golf', 'Volkswagen', 2014,'MBD-861', 1800.0, 18000, 45.0, 'https://i0.wp.com/practicalmotoring.com.au/wp-content/uploads/2014/03/image106377_b.jpg?fit=1024%2C682&ssl=1'),
('320d', 'BMW', 2014,'AOQ-511', 2200.0, 30000, 70.0, 'https://media.autoexpress.co.uk/image/private/s--X-WVjvBW--/f_auto,t_content-image-full-desktop@1/v1563183881/autoexpress/0/39/dsc_3024.jpg'),
('X3', 'BMW', 2023,'NNX-371', 2000.0, 22000, 90.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/BMW_X3_G01_xDrive_20D_2023_(1).jpg'),
('A3', 'Audi', 2017,'IYV-732', 1600.0, 15000, 60.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Audi_A3_SportBack_2017_(front).jpg'),
('A4', 'Audi', 2013,'TFS-204', 2000.0, 34000, 75.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/A4(B8)_MY2013_US_version_front.jpg'),
('A6', 'Audi', 2008,'IOE-603', 2500.0, 50000, 95.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Audi_A6_C6_front_20080108.jpg'),
('Corolla', 'Toyota', 2008,'QFG-065', 1500.0, 8000, 40.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2005-2008_Toyota_Corolla.jpg'),
('RAV4', 'Toyota', 2007,'JFX-670', 1800.0, 12000, 65.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Toyota_RAV4_front_20071007.jpg'),
('Mondeo', 'Ford', 2000,'SXL-570', 2000.0, 27000, 50.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2000_Ford_Mondeo_LX_2.0_Front.jpg'),
('Focus', 'Ford', 2001,'GRP-702', 1600.0, 36000, 42.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2001_Ford_Focus_ZX3_front_--_06-09-2010.jpg'),
('Octavia', 'Skoda', 2016,'KIQ-015', 2000.0, 15000, 48.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2016_Skoda_Octavia_SE_L_TSi_Estate_1.4_Front.jpg'),
('Fabia', 'Skoda', 2019,'GVT-120', 1400.0, 22000, 35.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2019_Skoda_Fabia_S_1.0_Front.jpg'),
('i30', 'Hyundai', 2000,'KRE-631', 1500.0, 10000, 38.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Hyundai_i30_front_20070928.jpg'),
('Tucson', 'Hyundai', 2013,'CLQ-486', 1600.0, 18000, 68.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Hyundai_Tucson_front.jpg'),
('Sportage', 'Kia', 2008,'GTJ-765', 2000.0, 25000, 66.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Kia_Sportage_Facelift_front.jpg'),
('Ceed', 'Kia', 2006,'FTX-000', 1400.0, 12000, 37.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Kia_ceed_front_04-03-2009.jpg'),
('CX-5', 'Mazda', 2010,'YTC-720', 2000.0, 9000, 70.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Mazda_CX-5_(front).jpg'),
('C220', 'Mercedes', 2024,'FOF-018', 2200.0, 40000, 85.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Mercedes-Benz_C_220d_AVANTGARDE_(W206)_front.jpg'),
('E-Class', 'Mercedes', 2010,'QDS-181', 3000.0, 30000, 110.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Mercedes_E_Class_(4488315697).jpg'),
('308', 'Peugeot', 2019,'FHC-572', 1600.0, 14000, 36.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_308_(front).jpg'),
('508', 'Peugeot', 2002,'HVO-141', 2000.0, 35000, 49.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Peugeot_508_Sedan_front.JPG'),
('Megane', 'Renault', 2015,'VRC-418', 1800.0, 17000, 34.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Renault_M%C3%A9gane_front.jpg'),
('Talisman', 'Renault', 2004,'VVT-026', 2000.0, 26000, 52.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Renault_Talisman_(2015)_1X7A1604.jpg'),
('Leon', 'Seat', 2017,'VWL-777', 1600.0, 19000, 39.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2017_SEAT_Leon_FR_Technology_TSi_facelift_1.4_Front.jpg'),
('Ateca', 'Seat', 2023,'RKH-120', 2000.0, 22500, 67.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2021_SEAT_Ateca_SE_Technology_TSi_Evo_facelift_1.5_Front.jpg'),
('Corsa', 'Opel', 2023,'UIJ-663', 1400.0, 8000, 29.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Opel_Corsa_1.2_Edition_2023_(52863303749).jpg'),
('Insignia', 'Opel', 2022,'MWS-835', 2000.0, 42000, 51.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Opel_Insignia_B_GSi_Classic-Days_2022_DSC_0287.jpg'),
('Qashqai', 'Nissan', 2011,'BDW-545', 1800.0, 11000, 64.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Nissan_Qashqai_2.0_CVT_2011.jpg'),
('Juke', 'Nissan', 2020,'IIS-076', 1600.0, 20000, 41.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Nissan_Juke_(50092924268).jpg'),
('S60', 'Volvo', 2012,'EBY-785', 2000.0, 15500, 78.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Volvo_S60_T4_2012_(13998980992).jpg'),
('XC60', 'Volvo', 2010,'NMO-283', 2400.0, 34000, 92.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2018_Volvo_XC60_R-Design_D5_P-Pulse_2.0_Front.jpg'),
('Tipo', 'Fiat', 2019,'FKN-472', 1600.0, 13000, 33.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2019_FIAT_Tipo_Easy_-_1368cc_1.4_(95PS)_Petrol_-_White_-_03-2024,_Front.jpg'),
('500X', 'Fiat', 2005,'PQI-661', 2000.0, 48000, 40.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/2014_Fiat_500X.jpg'),
('Civic', 'Honda', 2012,'TUT-545', 1800.0, 9000, 44.0, 'https://commons.wikimedia.org/wiki/Special:FilePath/Honda_Civic_e-HEV_Sport_(XI)_%E2%80%93_f_30062024.jpg');


INSERT INTO customer (
    first_name,
    second_name,
    total_rentals,
    late_returns,
    phone
) VALUES
      ( 'Ion', 'Popescu', 15, 2, '0700000001'),
      ( 'Andrei', 'Ionescu', 8, 1, '0700000002'),
      ( 'Maria', 'Georgescu', 12, 0, '0700000003'),
      ( 'Elena', 'Dumitrescu', 20, 3, '0700000004'),
      ( 'Mihai', 'Stan', 5, 0, '0700000005'),
      ( 'Radu', 'Marinescu', 9, 2, '0700000006'),
      ( 'Ana', 'Popa', 11, 1, '0700000007'),
      ( 'Paul', 'Nistor', 7, 0, '0700000008'),
      ( 'Cristina', 'Munteanu', 18, 4, '0700000009'),
      ( 'Alex', 'Ilie', 6, 1, '0700000010'),
      ( 'Vasile', 'Cozma', 14, 1, '0700000011'),
      ( 'George', 'Petrescu', 22, 5, '0700000012'),
      ( 'Bogdan', 'Enache', 10, 2, '0700000013'),
      ( 'Daniel', 'Rusu', 4, 0, '0700000014'),
      ('Ioana', 'Preda', 16, 2, '0700000015'),
      ( 'Larisa', 'Tudor', 9, 1, '0700000016'),
      ( 'Florin', 'Barbu', 13, 3, '0700000017'),
      ( 'Oana', 'Gheorghe', 17, 0, '0700000018'),
      ( 'Catalin', 'Voicu', 8, 1, '0700000019'),
      ( 'Sorin', 'Dragan', 19, 4, '0700000020'),
      ( 'Iulia', 'Matei', 6, 0, '0700000021'),
      ( 'Adrian', 'Lazar', 12, 2, '0700000022'),
      ( 'Stefan', 'Neagu', 11, 1, '0700000023'),
      ( 'Alina', 'Constantin', 7, 0, '0700000024'),
      ( 'Robert', 'Pavel', 14, 3, '0700000025'),
      ( 'Teodora', 'Sandu', 10, 1, '0700000026'),
      ( 'Cosmin', 'Oprea', 5, 0, '0700000027'),
      ( 'Bianca', 'Florea', 9, 2, '0700000028'),
      ( 'Valentin', 'Chirila', 16, 3, '0700000029'),
      ( 'Laura', 'Mocanu', 13, 1, '0700000030'),
      ( 'Claudiu', 'Serban', 8, 0, '0700000031'),
      ( 'Monica', 'Dobre', 12, 1, '0700000032'),
      ( 'Sebastian', 'Paun', 20, 4, '0700000033'),
      ( 'Denisa', 'Roman', 6, 0, '0700000034'),
      ( 'Cristian', 'Avram', 11, 2, '0700000035'),
      ( 'Irina', 'Filip', 15, 3, '0700000036'),
      ( 'Eduard', 'Grigore', 9, 1, '0700000037'),
      ( 'Carmen', 'Balan', 7, 0, '0700000038'),
      ( 'Alexandra', 'Sava', 10, 1, '0700000039'),
      ( 'Liviu', 'Anghel', 18, 2, '0700000040'),
      ( 'Patricia', 'Nica', 5, 0, '0700000041'),
      ( 'Razvan', 'Coman', 14, 3, '0700000042'),
      ( 'Diana', 'Ungureanu', 6, 0, '0700000043'),
      ( 'Marian', 'Cristea', 12, 1, '0700000044'),
      ( 'Silvia', 'Iacob', 8, 0, '0700000045'),
      ( 'Lucian', 'Moraru', 16, 2, '0700000046'),
      ( 'Raluca', 'Pintilie', 9, 1, '0700000047'),
      ( 'Tudor', 'Zamfir', 13, 2, '0700000048'),
      ( 'Anca', 'Ralea', 11, 0, '0700000049'),
      ( 'Ion', 'Zaharadji', 4, 1, '070000000050'),
      ( 'Jomir', 'Alexandru', 0, 0, '19283691');


INSERT INTO Rental (
    customer_id,
    vehicle_id,
    rental_start_date,
    rental_end_date,
    total_price
) VALUES
      (1, 11, '2024-02-02 10:00:00', '2024-02-06 10:00:00', 160.00),
      (2, 12, '2024-02-03 09:00:00', '2024-02-08 09:00:00', 250.00),
      (3, 13, '2024-02-04 12:00:00', '2024-02-09 12:00:00', 210.00),
      (4, 14, '2024-03-02 08:00:00', '2024-03-06 08:00:00', 192.00),
      (5, 15, '2024-03-05 10:00:00', '2024-03-10 10:00:00', 175.00),
      (6, 16, '2024-03-06 11:00:00', '2024-03-12 11:00:00', 228.00),
      (7, 17, '2024-03-08 09:30:00', '2024-03-13 09:30:00', 340.00),
      (8, 18, '2024-04-01 10:00:00', '2024-04-05 10:00:00', 264.00),
      (9, 19, '2024-04-03 10:00:00', '2024-04-07 10:00:00', 148.00),
      (10, 20, '2024-04-06 10:00:00', '2024-04-11 10:00:00', 350.00),

      (11, 21, '2024-05-02 14:00:00', '2024-05-07 14:00:00', 425.00),
      (12, 22, '2024-05-04 09:00:00', '2024-05-08 09:00:00', 440.00),
      (13, 23, '2024-05-06 11:00:00', '2024-05-10 11:00:00', 144.00),
      (14, 24, '2024-05-07 13:00:00', '2024-05-12 13:00:00', 245.00),
      (15, 25, '2024-05-09 10:00:00', '2024-05-14 10:00:00', 170.00),
      (16, 26, '2024-05-10 12:00:00', '2024-05-15 12:00:00', 260.00),
      (17, 27, '2024-05-12 09:00:00', '2024-05-16 09:00:00', 156.00),
      (18, 28, '2024-05-13 10:00:00', '2024-05-18 10:00:00', 335.00),
      (19, 29, '2024-05-14 10:00:00', '2024-05-19 10:00:00', 145.00),
      (20, 30, '2024-05-15 10:00:00', '2024-05-20 10:00:00', 255.00),

      (21, 31, '2024-06-01 09:00:00', '2024-06-06 09:00:00', 320.00),
      (22, 32, '2024-06-03 10:00:00', '2024-06-08 10:00:00', 205.00),
      (23, 33, '2024-06-04 08:00:00', '2024-06-09 08:00:00', 390.00),
      (24, 34, '2024-06-05 09:00:00', '2024-06-10 09:00:00', 460.00),
      (25, 35, '2024-06-06 12:00:00', '2024-06-11 12:00:00', 165.00),
      (26, 36, '2024-06-07 10:00:00', '2024-06-12 10:00:00', 200.00),
      (27, 37, '2024-06-08 10:00:00', '2024-06-13 10:00:00', 220.00),

      (28, 5, '2026-04-22 09:00:00', '2026-04-25 09:00:00', 210.00),
      (29, 6, '2026-04-23 10:00:00', '2026-04-27 10:00:00', 360.00),
      (30, 7, '2026-04-23 11:00:00', '2026-04-29 11:00:00', 420.00),
      (31, 8, '2026-04-24 09:00:00', '2026-04-28 09:00:00', 300.00),
      (32, 9, '2026-04-24 12:00:00', '2026-04-30 12:00:00', 570.00),
      (33, 10, '2026-04-25 09:00:00', '2026-04-28 09:00:00', 200.00),

      (34, 1, '2026-04-26 10:00:00', '2026-04-30 10:00:00', 120.00),
      (35, 2, '2026-04-26 10:30:00', '2026-05-01 10:30:00', 140.00),
      (36, 3, '2026-04-27 11:00:00', '2026-05-02 11:00:00', 275.00),
      (37, 4, '2026-04-27 09:00:00', '2026-05-03 09:00:00', 180.00),
      (38, 11, '2026-04-28 08:00:00', '2026-05-03 08:00:00', 200.00),
      (39, 12, '2026-04-28 12:00:00', '2026-05-04 12:00:00', 260.00),

      (40, 13, '2026-04-29 10:00:00', '2026-05-05 10:00:00', 210.00),
      (41, 14, '2026-04-29 14:00:00', '2026-05-06 14:00:00', 320.00),
      (42, 15, '2026-04-30 09:00:00', '2026-05-04 09:00:00', 140.00),
      (43, 16, '2026-04-30 10:00:00', '2026-05-05 10:00:00', 190.00),
      (44, 17, '2026-04-30 11:00:00', '2026-05-06 11:00:00', 350.00),
      (45, 18, '2026-05-01 10:00:00', '2026-05-06 10:00:00', 300.00),
      (46, 19, '2026-05-01 09:00:00', '2026-05-05 09:00:00', 150.00),
      (47, 20, '2026-05-02 08:00:00', '2026-05-06 08:00:00', 280.00),
      (48, 21, '2026-05-02 10:00:00', '2026-05-07 10:00:00', 425.00),
      (49, 22, '2026-05-03 10:00:00', '2026-05-08 10:00:00', 500.00),
      (50, 23, '2026-05-03 11:00:00', '2026-05-09 11:00:00', 216.00);


INSERT INTO Rental (
    customer_id,
    vehicle_id,
    rental_start_date,
    rental_end_date,
    total_price
) VALUES
      (1, 1, '2024-01-05 00:00:00', '2024-01-10 00:00:00', 150.00),
      (2, 2, '2024-01-08 00:00:00', '2024-01-12 00:00:00', 112.00),
      (3, 3, '2024-01-15 00:00:00', '2024-01-20 00:00:00', 275.00),
      (4, 4, '2024-02-01 00:00:00', '2024-02-04 00:00:00', 135.00),
      (5, 5, '2024-02-05 00:00:00', '2024-02-10 00:00:00', 350.00),
      (6, 6, '2024-02-12 00:00:00', '2024-02-18 00:00:00', 540.00),
      (7, 7, '2024-02-20 00:00:00', '2024-02-25 00:00:00', 300.00),
      (8, 8, '2024-03-01 00:00:00', '2024-03-06 00:00:00', 375.00),
      (9, 9, '2024-03-10 00:00:00', '2024-03-15 00:00:00', 475.00),
      (10, 10, '2024-03-18 00:00:00', '2024-03-22 00:00:00', 160.00),
      (11, 1, '2024-04-01 00:00:00', '2024-04-06 00:00:00', 150.00),
      (12, 2, '2024-04-05 00:00:00', '2024-04-09 00:00:00', 112.00),
      (13, 3, '2024-04-10 00:00:00', '2024-04-15 00:00:00', 275.00),
      (14, 4, '2024-04-18 00:00:00', '2024-04-22 00:00:00', 180.00),
      (15, 5, '2024-05-01 00:00:00', '2024-05-07 00:00:00', 420.00),
      (16, 6, '2024-05-10 00:00:00', '2024-05-15 00:00:00', 450.00),
      (17, 7, '2024-05-18 00:00:00', '2024-05-22 00:00:00', 240.00),
      (18, 8, '2024-06-01 00:00:00', '2024-06-06 00:00:00', 375.00),
      (19, 9, '2024-06-10 00:00:00', '2024-06-14 00:00:00', 380.00),
      (20, 10, '2024-06-20 00:00:00', '2024-06-25 00:00:00', 200.00),
      (21, 1, '2024-07-01 00:00:00', '2024-07-05 00:00:00', 120.00),
      (22, 2, '2024-07-08 00:00:00', '2024-07-12 00:00:00', 112.00),
      (23, 3, '2024-07-15 00:00:00', '2024-07-20 00:00:00', 275.00),
      (24, 4, '2024-08-01 00:00:00', '2024-08-05 00:00:00', 180.00),
      (25, 5, '2024-08-10 00:00:00', '2024-08-15 00:00:00', 350.00),
      (26, 6, '2024-08-20 00:00:00', '2024-08-26 00:00:00', 540.00),
      (27, 7, '2024-09-01 00:00:00', '2024-09-06 00:00:00', 300.00),
      (28, 8, '2024-09-10 00:00:00', '2024-09-15 00:00:00', 375.00),
      (29, 9, '2024-09-20 00:00:00', '2024-09-25 00:00:00', 475.00),
      (30, 10, '2024-10-01 00:00:00', '2024-10-05 00:00:00', 160.00),
      (31, 1, '2024-10-10 00:00:00', '2024-10-15 00:00:00', 150.00),
      (32, 2, '2024-10-20 00:00:00', '2024-10-24 00:00:00', 112.00),
      (33, 3, '2024-11-01 00:00:00', '2024-11-06 00:00:00', 275.00),
      (34, 4, '2024-11-10 00:00:00', '2024-11-14 00:00:00', 180.00),
      (35, 5, '2024-11-20 00:00:00', '2024-11-25 00:00:00', 350.00),
      (50, 4, '2026-04-23 17:23:00', '2026-04-28 12:00:00', 150.00),
      (5, 10, '2026-04-24 16:40:00', '2026-04-27 09:53:49.353', 2500.00);


INSERT INTO app_user (username, email, password, roles, balance, customer_id) VALUES
     ('vasile', 'vasile@example.com', '$2a$10$9Dlyqhacii9kXIjfWAGKC.tuKdBfV7hIw8XfIZWDwGuAc0/PCVMwy', 'USER', 123.33, 11),
     ( 'admin', 'admin@example.com', '$2a$10$4Vez/W1VBeYiBfGIWb8EDui99mM28HWjkRUmhJTzzLiPP1yR6RNfW', 'ADMIN', null, null),
     ('user2', 'user2@example.com', '$2a$12$sk.bk/m7l67dE6VKvPPW8OKdJIPW0kpxFaHWhiJcdflYdzntftxI2','USER', 233.2, 3),
     ('user', 'user@example.com', '$2a$10$XBpYEFx/6MwynJfACpR4VOKlXJBXcVSM.owmgWsUilaJvG7dTjvIy', 'USER', 4023.21, 23);


DECLARE @i INT = 1;

WHILE @i <= 100000
BEGIN
    DECLARE @start DATETIME =
        DATEADD(DAY,
            ABS(CHECKSUM(NEWID())) % (DATEDIFF(DAY, '2020-01-01', '2026-12-31')),
            '2020-01-01'
        );

    DECLARE @days INT = (ABS(CHECKSUM(NEWID())) % 10) + 1;

    INSERT INTO Rental (
        customer_id,
        vehicle_id,
        rental_start_date,
        rental_end_date,
        total_price
    )
    VALUES (
        (ABS(CHECKSUM(NEWID())) % 50) + 1,
        (ABS(CHECKSUM(NEWID())) % 37) + 1,
        @start,
        DATEADD(DAY, @days, @start),
        CAST(@days * ((ABS(CHECKSUM(NEWID())) % 80) + 20) AS DECIMAL(10,2))
    );

    SET @i = @i + 1;
END;


SELECT * FROM rental



SELECT * FROM rental
