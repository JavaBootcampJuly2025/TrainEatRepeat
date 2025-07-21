INSERT INTO USERS (uuid, username, email, password, age, gender, weight, height, BMI, BMR, role)
VALUES  ('550e8400-e29b-41d4-a716-446655440000', 'john_doe', 'john@example.com', 'password123', 28, 'Male', 75.0, 180.0, 23.15, 1755.0, 'admin'),
        ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'jane_smith', 'jane@example.com', 'securepass', 25, 'Female', 60.0, 165.0, 22.04, 1400.0, 'admin'),
        ('123e4567-e89b-12d3-a456-426614174000', 'alex_fitness', 'alex@example.com', 'abc123xyz', 32, 'Male', 82.0, 178.0, 25.88, 1850.0, 'user'),
        ('9c858901-8a57-4791-81fe-4c455b099bc9', 'emma_runner', 'emma@example.com', 'runFast!', 29, 'Female', 55.0, 160.0, 21.48, 1350.0, 'user'),
        ('16fd2706-8baf-433b-82eb-8c7fada847da', 'mike_builder', 'mike@example.com', 'buildIt!', 35, 'Male', 90.0, 185.0, 26.29, 1950.0, 'user');


INSERT INTO MEALRECORDS (id, uuid, food_name, calories, carbs, protein, fat, weight_in_grams, date)
VALUES  ('a1b2c3d4-e5f6-7890-abcd-1234567890ab', '550e8400-e29b-41d4-a716-446655440000', 'Chicken Salad', 350, 10, 30, 15, 250.0, '2025-07-15'),
        ('b2c3d4e5-f6a7-8901-bcde-2345678901bc', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Pasta', 500, 60, 15, 10, 180.0, '2025-07-15'),
        ('c3d4e5f6-a7b8-9012-cdef-3456789012cd', '123e4567-e89b-12d3-a456-426614174000', 'Protein Shake', 200, 5, 25, 5, 300.0, '2025-07-15'),
        ('d4e5f6a7-b8c9-0123-def0-4567890123de', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Fruit Bowl', 150, 30, 2, 1, 200.0, '2025-07-15'),
        ('e5f6a7b8-c9d0-1234-ef01-5678901234ef', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Steak', 700, 0, 50, 50, 225.0, '2025-07-15');

INSERT INTO TRAININGRECORDS (id, uuid, exercise, duration, calories_lost, date)
VALUES
    ('a1b2c3d4-5678-9101-1121-314151617181', '550e8400-e29b-41d4-a716-446655440000', 'Running', 45, 600, '2025-07-15'),
    ('b2c3d4e5-6789-1011-2131-415161718192', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Cycling', 60, 500, '2025-07-15'),
    ('c3d4e5f6-7890-1121-3141-516171819203', '123e4567-e89b-12d3-a456-426614174000', 'Swimming', 30, 400, '2025-07-15'),
    ('d4e5f6a7-8901-1223-4151-617181920314', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Yoga', 60, 200, '2025-07-15'),
    ('e5f6a7b8-9012-2324-5161-718192031425', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Weightlifting', 45, 350, '2025-07-15');


INSERT INTO EXERCISES (id, name, met)
VALUES
    ('1e2d3c4b-5a6f-7b8d-9c0e-123456789abc', 'Running', 9.8),
    ('2a3b4c5d-6e7f-8d9c-0e1f-abcdefabcdef', 'Cycling', 7.5),
    ('3c4d5e6f-7a8b-9c0d-1e2f-fedcba987654', 'Swimming', 8.0),
    ('4d5e6f7a-8b9c-0d1e-2f3g-112233445566', 'Weightlifting', 5.0),
    ('5e6f7a8b-9c0d-1e2f-3g4h-998877665544', 'Yoga', 3.0);


--For statistics

INSERT INTO MEALRECORDS (id, uuid, food_name, calories, carbs, protein, fat, weight_in_grams, date)
VALUES
    ('f1e2d3c4-b5a6-7890-1234-567890abcdef', '550e8400-e29b-41d4-a716-446655440000', 'Oatmeal', 250, 40, 8, 5, 150, '2025-07-14'),
    ('a2b3c4d5-e6f7-8901-2345-678901bcdef0', '550e8400-e29b-41d4-a716-446655440000', 'Eggs', 150, 1, 12, 10, 100, '2025-07-03'),
    ('b3c4d5e6-f7a8-9012-3456-789012cdef01', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Salad', 120, 8, 3, 6, 200, '2025-07-14'),
    ('c4d5e6f7-a8b9-0123-4567-890123def012', '123e4567-e89b-12d3-a456-426614174000', 'Rice', 180, 38, 4, 1, 120, '2025-07-13'),
    ('d5e6f7a8-b9c0-1234-5678-901234ef0123', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Banana', 90, 23, 1, 0, 118, '2025-07-12'),
    ('e6f7a8b9-c0d1-2345-6789-012345f01234', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Fish', 220, 0, 25, 12, 100, '2025-07-14');

INSERT INTO TRAININGRECORDS (id, uuid, exercise, duration, calories_lost, date) VALUES
    ('f1e2d3c4-5678-9101-1121-314151617182', '550e8400-e29b-41d4-a716-446655440000', 'Cycling', 30, 300, '2025-07-14'),
    ('a2b3c4d5-6789-1011-2131-415161718193', '550e8400-e29b-41d4-a716-446655440000', 'Yoga', 60, 180, '2025-07-03'),
    ('b3c4d5e6-7890-1121-3141-516171819204', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Running', 40, 450, '2025-07-14'),
    ('c4d5e6f7-8901-1223-4151-617181920315', '123e4567-e89b-12d3-a456-426614174000', 'Weightlifting', 50, 320, '2025-07-13'),
    ('d5e6f7a8-9012-2324-5161-718192031426', '9c858901-8a57-4791-81fe-4c455b099bc9', 'Swimming', 25, 220, '2025-07-12'),
    ('e6f7a8b9-0123-3425-6171-819203142537', '16fd2706-8baf-433b-82eb-8c7fada847da', 'Running', 35, 330, '2025-07-14');