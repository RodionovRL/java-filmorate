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
-- тесты пока нету функционала с режиссёрами
INSERT INTO DIRECTOR (NAME)
VALUES ('Director Эдвард Печатников');

INSERT INTO film (name, description, RELEASE_DATE, duration, MPA_ID)
VALUES ('Эдвард руки-ножницы', 'Одинокий ученый умер, не успев доделать свое изобретение',
        '1990-03-01', 105, 3),
       ('Волк и теленок', 'Волк решает подрастить теленка, перед тем как его съесть',
        '1984-04-01', 10, 1),
       ('Рекрут', 'Американская разведка вербует талантливого программиста.',
        '2003-07-01', 115, 3),
       ('Пятая печать', 'События происходят во время фашистского террора в Венгрии осенью 1944 года.',
        '1976-11-01', 111, 4);

INSERT INTO FILM_DIRECTOR
VALUES (3, 1);

