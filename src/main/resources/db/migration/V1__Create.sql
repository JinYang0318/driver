CREATE TABLE DRIVER
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    driver_name    VARCHAR(50)         NOT NULL,
    license_number VARCHAR(50) UNIQUE  NOT NULL,
    vehicle_model  VARCHAR(50)         NOT NULL,
    vehicle_number VARCHAR(20) UNIQUE  NOT NULL,
    email          VARCHAR(100) UNIQUE NOT NULL
);

INSERT INTO DRIVER(driver_name, license_number, vehicle_model, vehicle_number, email)
VALUES ('John Doe', 'D1234567', 'Toyota Corolla', 'ABC1234', 'john.doe@example.com'),
       ('Jane Smith', 'D7654321', 'Honda Civic', 'XYZ5678', 'jane.smith@example.com'),
       ('Robert Brown', 'D1122334', 'Ford Focus', 'LMN9123', 'robert.brown@example.com');