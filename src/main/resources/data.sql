-- создание рейтингов
INSERT INTO MPA
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');
--создание жанров
INSERT INTO GENRE
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');
--создание типов событий
INSERT INTO EVENT_TYPE
VALUES (1, "LIKE"),
       (2, "REVIEW"),
       (3, "FRIEND");
--создание операций
INSERT INTO OPERATION_TYPE
VALUES (1, "REMOVE"),
       (2, "ADD"),
       (3, "UPDATE");
