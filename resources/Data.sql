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
 DISCOUNT bool DEFAULT false,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(6,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(7,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(8,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(9,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(10,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(11,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(12,true,'BIKE');
commit;

/* Setting up TEST DB */
create database test;
use test;

create table parking(
PARKING_NUMBER int PRIMARY KEY,
AVAILABLE bool NOT NULL,
TYPE varchar(10) NOT NULL
);

create table ticket
 ID int PRIMARY KEY AUTO_INCREMENT,
 PARKING_NUMBER int NOT NULL,
 VEHICLE_REG_NUMBER varchar(10) NOT NULL,
 PRICE double,
 IN_TIME DATETIME NOT NULL,
 OUT_TIME DATETIME,
 DISCOUNT bool DEFAULT false,
 FOREIGN KEY (PARKING_NUMBER)
 REFERENCES parking(PARKING_NUMBER));

insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(1,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(2,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(3,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(4,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(5,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(6,true,'CAR');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(7,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(8,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(9,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(10,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(11,true,'BIKE');
insert into parking(PARKING_NUMBER,AVAILABLE,TYPE) values(12,true,'BIKE');

insert into ticket (PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, DISCOUNT) VALUES (1, 'ABCDEF', 1.5, '2023-08-22 14:00:00', NULL, false);

commit;



