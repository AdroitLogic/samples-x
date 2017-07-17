#!/bin/bash
mysql -u root -p <<EOF
CREATE DATABASE dbConnection;
USE dbConnection;
CREATE TABLE PurchaseType (
    OrderID int,	
    ShipTo varchar(255),
    BillTo varchar(255),
    Item varchar(255),
    OrderDate DATE 
);
SHOW tables;
INSERT INTO PurchaseType (
    OrderID,	
    ShipTo,
    BillTo,
    Item,
    OrderDate)
VALUES (1,"testAddr1", "testAddr2", "TestItem", now());
INSERT INTO PurchaseType (
    OrderID,	
    ShipTo,
    BillTo,
    Item,
    OrderDate)
VALUES (2,"testAddr3", "testAddr4", "TestItem2", now());
EOF
