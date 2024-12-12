-- Create Supplier Table
CREATE TABLE Supplier (
    SupplierID INT AUTO_INCREMENT PRIMARY KEY,
    SupplierName VARCHAR(50) NOT NULL,
    PhoneContact VARCHAR(15) CHECK (LENGTH(PhoneContact) BETWEEN 10 AND 15)
);

-- Create Product Table
CREATE TABLE Product (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ProductName VARCHAR(50) NOT NULL,
    Category ENUM('Snack', 'Drink') NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL CHECK (UnitPrice > 0),
    SupplierID INT,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID)
);

-- Create Customer Table
CREATE TABLE Customer (
    CustomerID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerName VARCHAR(50) NOT NULL,
    PhoneNumber VARCHAR(15) CHECK (PhoneNumber IS NULL OR LENGTH(PhoneNumber) BETWEEN 10 AND 15),
    Email VARCHAR(255)
);

-- Create CustomerOrder Table
CREATE TABLE CustomerOrder (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    CustomerID INT,
    OrderDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    TotalOrderPrice DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

-- Create ItemOrder Table
CREATE TABLE ItemOrder (
    ItemOrderID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT,
    ProductID INT,
    Quantity INT CHECK (Quantity BETWEEN 1 AND 999),
    UnitPrice DECIMAL(10,2) NOT NULL,
    TotalPrice DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES CustomerOrder(OrderID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

-- Insert Supplier Data
INSERT INTO Supplier (SupplierName, PhoneContact) VALUES
('Snack Master Inc.', '1234567890'),
('Beverage Bliss LLC', '9876543210'),
('Healthy Treats Co.', '5555555555');

-- Insert Product Data
INSERT INTO Product (ProductName, Category, UnitPrice, SupplierID) VALUES
('Chocolate Chip Cookie', 'Snack', 1.50, 1),
('Potato Chips', 'Snack', 2.00, 1),
('Pretzels', 'Snack', 1.75, 1),
('Cola', 'Drink', 2.50, 2),
('Bottled Water', 'Drink', 1.25, 2),
('Fruit Punch', 'Drink', 2.25, 2),
('Granola Bar', 'Snack', 1.25, 3),
('Energy Drink', 'Drink', 3.00, 2);

-- Insert Customer Data
INSERT INTO Customer (CustomerName, PhoneNumber, Email) VALUES
('John Smith', '7778889999', 'john.smith@email.com'),
('Emily Johnson', '4445556666', 'emily.j@gmail.com'),
('Michael Brown', '1112223333', 'mbrown@yahoo.com'),
('Sarah Davis', '9990001111', 'sarah.davis@hotmail.com');

-- Insert CustomerOrder Data
INSERT INTO CustomerOrder (CustomerID, OrderDate, TotalOrderPrice) VALUES
(1, '2024-01-15', 6.75),
(2, '2024-01-16', 4.25),
(3, '2024-01-17', 9.50),
(4, '2024-01-18', 5.00);

-- Insert ItemOrder Data
INSERT INTO ItemOrder (OrderID, ProductID, Quantity, UnitPrice, TotalPrice) VALUES
(1, 1, 2, 1.50, 3.00),  -- 2 Chocolate Chip Cookies
(1, 4, 1, 2.50, 2.50),  -- 1 Cola
(1, 5, 1, 1.25, 1.25),  -- 1 Bottled Water
(2, 2, 1, 2.00, 2.00),  -- 1 Potato Chips
(2, 6, 1, 2.25, 2.25),  -- 1 Fruit Punch
(3, 3, 2, 1.75, 3.50),  -- 2 Pretzels
(3, 8, 2, 3.00, 6.00),  -- 2 Energy Drinks
(4, 7, 3, 1.25, 3.75),  -- 3 Granola Bars
(4, 5, 1, 1.25, 1.25);  -- 1 Bottled Water

ALTER TABLE Customer
ADD PaymentType VARCHAR(15) CHECK (LENGTH(PaymentType) BETWEEN 10 AND 15),
ADD CardNumber VARCHAR(16) CHECK (LENGTH(CardNumber) = 16),
ADD ExpirationDate VARCHAR(5) CHECK (ExpirationDate LIKE '__/__');

UPDATE Customer
SET PaymentType = 'Credit Card',
    CardNumber = '1234567812345678',
    ExpirationDate = '05/25'
WHERE CustomerName = 'John Smith';

UPDATE Customer
SET PaymentType = 'Debit Card',
    CardNumber = '8765432187654321',
    ExpirationDate = '12/24'
WHERE CustomerName = 'Emily Johnson';

UPDATE Customer
SET PaymentType = 'Credit Card',
    CardNumber = '1122334455667788',
    ExpirationDate = '07/27'
WHERE CustomerName = 'Michael Brown';

UPDATE Customer
SET PaymentType = 'Credit Card',
    CardNumber = '5555666677778888',
    ExpirationDate = '08/26'
WHERE CustomerName = 'Sarah Davis';

ALTER TABLE Product
ADD COLUMN Quantity INT;

UPDATE Product
SET Quantity = 100
WHERE ProductName = 'Chocolate Chip Cookie';

UPDATE Product
SET Quantity = 150
WHERE ProductName = 'Potato Chips';

UPDATE Product
SET Quantity = 200
WHERE ProductName = 'Pretzels';

UPDATE Product
SET Quantity = 300
WHERE ProductName = 'Cola';

UPDATE Product
SET Quantity = 250
WHERE ProductName = 'Bottled Water';

UPDATE Product
SET Quantity = 150
WHERE ProductName = 'Fruit Punch';

UPDATE Product
SET Quantity = 100
WHERE ProductName = 'Granola Bar';

UPDATE Product
SET Quantity = 50
WHERE ProductName = 'Energy Drink';

SELECT * FROM product;