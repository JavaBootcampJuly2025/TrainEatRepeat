INSERT INTO users (uuid, username, password, age, gender, weight, height, BMI, BMR, role)
VALUES ('550e8400-e29b-41d4-a716-446655440000', 'john_doe', 'password123', 28, 'Male', 75.0, 180.0, 23.15, 1755.0, 'admin'),
       ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'jane_smith', 'securepass', 25, 'Female', 60.0, 165.0, 22.04, 1400.0, 'admin'),
       ('123e4567-e89b-12d3-a456-426614174000', 'alex_fitness', 'abc123xyz', 32, 'Male', 82.0, 178.0, 25.88, 1850.0, 'user'),
       ('9c858901-8a57-4791-81fe-4c455b099bc9', 'emma_runner', 'runFast!', 29, 'Female', 55.0, 160.0, 21.48, 1350.0, 'user'),
       ('16fd2706-8baf-433b-82eb-8c7fada847da', 'mike_builder', 'buildIt!', 35, 'Male', 90.0, 185.0, 26.29, 1950.0, 'user');
       ('16fd2706-8baf-433b-82eb-8c7fada847da', 'mike_builder', 'buildIt!', 35, 'Male', 90.0, 185.0, 26.29, 1950.0, 'admin');


INSERT INTO MEALRECORDS (id, uuid, foodName, calories, carbs, protein, fat, date)
VALUES
    ('a1b2c3d4-e5f6-7890-abcd-1234567890ab', '550e8400-e29b-41d4-a716-446655440000', 'Chicken Salad', 350, 10, 30, 15, '2025-07-15'),
    ('b2c3d4e5-f6a7-8901-bcde-2345678901bc', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Pasta', 500, 60, 15, 10, '2025-07-15'),
    ('c3d4e5f6-a7b8-9012-cdef-3456789012cd', '123e4567-e89b-12d3-a456-426614174000', 'Protein Shake', 200, 5, 25, 5, '2025-07-15'),
    ('d4e5f6a7-b8c9-0123-def0-4567890123de', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Fruit Bowl', 150, 30, 2, 1, '2025-07-15'),
    ('e5f6a7b8-c9d0-1234-ef01-5678901234ef', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Steak', 700, 0, 50, 50, '2025-07-15');


INSERT INTO trainingrecords (id, user_uuid, exercise, duration, calories_lost, date)
VALUES
    ('t1-a1b2c3d4-5678-9101-1121-314151617181', '550e8400-e29b-41d4-a716-446655440000', 'Running', 45, 600, '2025-07-15'),
    ('t2-b2c3d4e5-6789-1011-2131-415161718192', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Cycling', 60, 500, '2025-07-15'),
    ('t3-c3d4e5f6-7890-1121-3141-516171819203', '123e4567-e89b-12d3-a456-426614174000', 'Swimming', 30, 400, '2025-07-15'),
    ('t4-d4e5f6a7-8901-1223-4151-617181920314', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Yoga', 60, 200, '2025-07-15'),
    ('t5-e5f6a7b8-9012-2324-5161-718192031425', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Weightlifting', 45, 350, '2025-07-15');


INSERT INTO exercises (uuid, name, met)
VALUES
    ('1e2d3c4b-5a6f-7b8d-9c0e-123456789abc', 'Running', 9.8),
    ('2a3b4c5d-6e7f-8d9c-0e1f-abcdefabcdef', 'Cycling', 7.5),
    ('3c4d5e6f-7a8b-9c0d-1e2f-fedcba987654', 'Swimming', 8.0),
    ('4d5e6f7a-8b9c-0d1e-2f3g-112233445566', 'Weightlifting', 5.0),
    ('5e6f7a8b-9c0d-1e2f-3g4h-998877665544', 'Yoga', 3.0);
