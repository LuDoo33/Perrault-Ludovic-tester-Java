/* Setting up PROD DB */
create database prod;
use prod;

create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE bool NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
commit;

/* Setting up TEST DB */
create database test;
use test;

create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE bool NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');

INSERT INTO ticket (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
VALUES
  (1, 'ABC123', 10.00, '2023-09-14 08:00:00', '2023-09-14 16:30:00'),
  (2, 'XYZ456', 15.00, '2023-09-14 09:30:00', '2023-09-14 17:45:00'),
  (3, 'DEF789', 12.50, '2023-09-14 10:15:00', NULL), -- No OUT_TIME for this ticket yet
  (4, 'MNO123', 5.00, '2023-09-14 08:45:00', '2023-09-14 14:30:00'),
  (5, 'PQR789', 7.50, '2023-09-14 11:30:00', '2023-09-14 18:15:00');
/* Setting up PROD DB */
create database prod;
use prod;

create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE bool NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
commit;

/* Setting up TEST DB */
create database test;
use test;

create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE bool NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket(
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
commit;
