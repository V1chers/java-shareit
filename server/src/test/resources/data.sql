INSERT INTO users (name, email)
VALUES
('name1', 'name1@mail.ru'),
('name2', 'name2@mail.ru'),
('name3', 'name3@mail.ru'),
('name4', 'name4@mail.ru'),
('name5', 'name5@mail.ru'),
('name6', 'name6@mail.ru'),
('name7', 'name7@mail.ru');

INSERT INTO requests (created, description, user_id)
VALUES
('2025-06-15 20:00:00+00', 'desc1', 1),
('2025-06-16 20:00:00+00', 'desc2', 2),
('2025-06-17 20:00:00+00', 'desc3', 2);

INSERT INTO items (name, description, available, user_id, request_id)
VALUES
('item1', 'desc1', true, 3, 1),
('item2', 'desc2', true, 3, 2),
('item3', 'desc3', true, 3, 2);

INSERT INTO items (name, description, available, user_id)
VALUES
('item4', 'desc4', true, 4),
('item5', 'desc5', false, 5),
('item6', 'desc6', true, 5),
('item7', 'desc7', true, 7);

INSERT INTO bookings (start, ending, approved, item_id, booker_id)
VALUES
('2025-06-16 20:00:00+00', '2025-06-16 21:00:00+00', true, 1, 1),
('2025-06-16 20:00:00+00', '2025-06-16 21:00:00+00', false, 1, 2),
('2025-06-17 23:00:00+00', '2025-06-17 23:59:00+00', true, 2, 2),
('2025-06-17 22:00:00+00', '2025-06-17 23:00:00+00', true, 2, 1),
('2025-06-17 22:00:00+00', '2025-06-17 23:00:00+00', true, 5, 4),
('2025-06-17 22:00:00+00', '2025-06-17 23:00:00', false, 6, 6),
('2025-06-17 20:00:00', '2025-06-17 21:00:00+00', true, 6, 6);

INSERT INTO comments (text, created, author_id, item_id)
VALUES
('comment1', '2025-06-18 10:00:00+00', 1, 1),
('comment2', '2025-06-18 10:00:00+00', 1, 2),
('comment3', '2025-06-18 10:00:00+00', 2, 2);