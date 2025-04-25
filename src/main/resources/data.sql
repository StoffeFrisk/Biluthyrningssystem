-- Lynsey Fox
INSERT INTO address (street, postal_code, city) VALUES
('Jönköpingsvägen', '55129', 'Huskvarna'),
('Älgstigen', '55339', 'Jönköping'),
('123 Maple Street', '12345', 'Springfield'),
('456 Oak Avenue', '67890', 'Shelbyville'),
('789 Pine Road', '54321', 'Ogdenville');

INSERT INTO customer (personnummer, first_name, last_name, address_id, email, phone) VALUES
('19850101-1234','Anna','Svensson',1,'null','0800 00 1066'),
('19900215-5678','Erik','Johansson',2,'null','0800 00 1066'),
('19751230-9101','Maria','Lindberg',3,'null','0800 00 1066'),
('19881122-3456','Johan','Karlsson',4,'null','0800 00 1066'),
('19950505-7890','Elin','Andersson',5,'null','0800 00 1066');

--Ben Portsmouth

INSERT INTO car(brand, model, registration_number, price_per_day, booked, in_service) VALUES
('Volvo', 'XC90','FBI 123', 90.00, false, false);


INSERT INTO orders(personnummer,car_id,hire_start_date,hire_end_date,total_price,order_cancelled) VALUES
('19850101-1234',1, '2025-04-20','2025-04-25', 700.00, false),
('19751230-9101',1, '2022-04-20','2022-04-30', 7000.00, true),
('19950505-7890',1, '2025-06-10','2025-06-11', 85.00, false);
