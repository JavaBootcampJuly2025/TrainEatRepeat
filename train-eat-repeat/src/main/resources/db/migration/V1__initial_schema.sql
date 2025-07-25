CREATE TABLE USERS
(
    uuid     VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255),
    email    VARCHAR(255),
    password VARCHAR(255),
    age      INT,
    gender   VARCHAR(255),
    weight   FLOAT,
    height   FLOAT,
    BMI      FLOAT,
    BMR      FLOAT,
    role     VARCHAR(255),
    verification_code VARCHAR(36),
    verification_expires_at TIMESTAMP,
    is_email_verified BOOLEAN DEFAULT FALSE,
    reset_token VARCHAR(36),
    reset_token_expires_at TIMESTAMP
);

CREATE TABLE MEALRECORDS
(
    id               VARCHAR(36) PRIMARY KEY,
    uuid             VARCHAR(36),
    food_name        VARCHAR(255),
    calories         FLOAT,
    carbs            FLOAT,
    protein          FLOAT,
    fat              FLOAT,
    weight_in_grams  FLOAT NOT NULL DEFAULT 100.0,
    date             DATE,
    CONSTRAINT fk_user FOREIGN KEY (uuid) REFERENCES USERS (uuid) ON DELETE CASCADE
);

CREATE TABLE TRAININGRECORDS
(
    id            VARCHAR(36) PRIMARY KEY,
    uuid          VARCHAR(36),
    exercise      VARCHAR(255),
    duration      FLOAT,
    calories_lost FLOAT,
    date          DATE,
    CONSTRAINT fk_training_user FOREIGN KEY (uuid) REFERENCES USERS (uuid) ON DELETE CASCADE
);

CREATE TABLE EXERCISES
(
    id   VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    met  FLOAT
);
