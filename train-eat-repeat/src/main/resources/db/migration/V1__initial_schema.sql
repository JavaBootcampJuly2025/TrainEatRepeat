CREATE TABLE user
(
    uuid     Varchar(30) PRIMARY KEY,
    username VARCHAR(255),
    password            VARCHAR(255),
    age                 INT,
    gender              VARCHAR(255),
    weight              FLOAT,
    height              FLOAT,
    BMI                 FLOAT,
    BMR                 FLOAT,
    role                VARCHAR(255)
);