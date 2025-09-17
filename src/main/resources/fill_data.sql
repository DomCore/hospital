BEGIN;

insert into patients (id, first_name, last_name)
values (1, 'Alice', 'Smith'),
       (2, 'Bob', 'Johnson'),
       (3, 'Carol', 'Williams'),
       (4, 'David', 'Jones'),
       (5, 'Ella', 'Brown'),
       (6, 'Frank', 'Davis'),
       (7, 'Grace', 'Miller'),
       (8, 'Henry', 'Wilson'),
       (9, 'Ivy', 'Moore'),
       (10, 'Jack', 'Taylor'),
       (11, 'Kara', 'Anderson'),
       (12, 'Liam', 'Thomas'),
       (13, 'Mia', 'Jackson'),
       (14, 'Noah', 'Martin'),
       (15, 'Oscar', 'White');

INSERT INTO doctors (id, first_name, last_name, time_zone)
VALUES (1, 'Ethan', 'Hughes', 'America/New_York'),
       (2, 'Sophia', 'Lewis', 'Europe/London'),
       (3, 'Liam', 'Walker', 'America/Chicago'),
       (4, 'Olivia', 'Hall', 'Asia/Tokyo'),
       (5, 'Noah', 'Allen', 'America/Los_Angeles'),
       (6, 'Isabella', 'Young', 'Australia/Sydney'),
       (7, 'Mason', 'King', 'Europe/Paris'),
       (8, 'Mia', 'Wright', 'America/Denver'),
       (9, 'Logan', 'Scott', 'Asia/Kolkata'),
       (10, 'Ava', 'Green', 'America/Phoenix'),
       (11, 'Lucas', 'Baker', 'Europe/Berlin'),
       (12, 'Emma', 'Adams', 'Pacific/Auckland'),
       (13, 'Elijah', 'Nelson', 'America/Sao_Paulo'),
       (14, 'Charlotte', 'Carter', 'Europe/Madrid'),
       (15, 'James', 'Mitchell', 'Africa/Johannesburg');

insert into visits (doctor_id, `start`, `end`, patient_id)
values
    (1, '2025-09-10 10:00', '2025-09-10 10:30', 1),
    (2, '2025-09-10 11:00', '2025-09-10 11:20', 2),
    (3, '2025-09-11 09:30', '2025-09-11 10:00', 3),
    (1, '2025-09-11 10:15', '2025-09-11 10:45', 4),
    (2, '2025-09-12 14:00', '2025-09-12 14:30', 5),
    (3, '2025-09-12 15:00', '2025-09-12 15:25', 6),
    (4, '2025-09-13 09:00', '2025-09-13 09:30', 7),
    (5, '2025-09-13 10:30', '2025-09-13 11:00', 8),
    (6, '2025-09-14 08:45', '2025-09-14 09:15', 9),
    (7, '2025-09-14 09:30', '2025-09-14 10:00', 10),
    (8, '2025-09-15 10:00', '2025-09-15 10:30', 11),
    (9, '2025-09-15 11:00', '2025-09-15 11:25', 12),
    (10, '2025-09-16 13:00', '2025-09-16 13:30', 13),
    (11, '2025-09-16 14:00', '2025-09-16 14:30', 14),
    (12, '2025-09-17 09:00', '2025-09-17 09:30', 15),
    (13, '2025-09-17 10:00', '2025-09-17 10:30', 1),
    (14, '2025-09-18 11:00', '2025-09-18 11:30', 2),
    (15, '2025-09-18 12:00', '2025-09-18 12:25', 3),
    (1, '2025-09-19 09:00', '2025-09-19 09:30', 5),
    (2, '2025-09-19 10:00', '2025-09-19 10:30', 6);

COMMIT;