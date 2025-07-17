CREATE TABLE USERS
(
    uuid     Varchar(36) PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    age      INT,
    gender   VARCHAR(255),
    weight   FLOAT,
    height   FLOAT,
    BMI      FLOAT,
    BMR      FLOAT,
    role     VARCHAR(255)
);

CREATE TABLE mealrecords
(
    id        VARCHAR(36) PRIMARY KEY,
    user_uuid VARCHAR(36),
    food_name VARCHAR(255),
    calories  FLOAT,
    carbs     FLOAT,
    protein   FLOAT,
    fat       FLOAT,
    date      DATE,
    CONSTRAINT fk_user FOREIGN KEY (user_uuid) REFERENCES users(uuid) ON DELETE CASCADE
);


CREATE TABLE trainingrecords
(
    id   VARCHAR(36) PRIMARY KEY,
    user_uuid     VARCHAR(36),
    exercise      VARCHAR(255),
    duration      FLOAT,
    calories_lost FLOAT,
    date          DATE,
    CONSTRAINT fk_training_user FOREIGN KEY (user_uuid) REFERENCES users(uuid) ON DELETE CASCADE
);

CREATE TABLE exercises
(
    uuid VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    met  FLOAT
);