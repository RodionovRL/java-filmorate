-- Добавление пользователей
INSERT INTO users
    (name, email, login, birthday)
VALUES ('Petr', 'petr@mail.ru', 'PetrYa', '1993-02-01');
INSERT INTO users
    (name, email, login, birthday)
VALUES ('Marfa', 'marfa@mail.ru', 'MarFUsha', '1995-12-01');
INSERT INTO users
    (name, email, login, birthday)
VALUES ('Andr', 'arey@mail.ru', 'AndrUsha', '1990-09-08');
INSERT INTO users
    (name, email, login, birthday)
VALUES ('Yana', 'yana@mail.ru', 'YaNa', '1990-09-18');

-- добавление друзей
INSERT INTO friends
    (user_id, friend_id, status)
VALUES (1, 3, false);

INSERT INTO friends
    (user_id, friend_id, status)
VALUES (1, 2, true);

INSERT INTO friends
    (user_id, friend_id, status)
VALUES (2, 3, false);

INSERT INTO friends
    (user_id, friend_id, status)
VALUES (4, 2, true);

INSERT INTO friends
    (user_id, friend_id, status)
VALUES (3, 4, true);

-- создание рейтингов
INSERT INTO rating (name, description)
VALUES ('G', 'нет возрастных ограничений'),
       ('PG', 'детям рекомендуется смотреть фильм с родителями'),
       ('PG-13', 'детям до 13 лет просмотр не желателен'),
       ('R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого,'),
       ('NC-17', 'лицам до 18 лет просмотр запрещён');

-- добавление фильмов
INSERT INTO film (name, description, realisedate, duration, rating_id)
VALUES ('Эдвард руки-ножницы', 'Одинокий ученый умер, не успев доделать свое изобретение',
        '1990-03-01', 105, 3),
       ('Волк и теленок', 'Волк решает подрастить теленка, перед тем как его съесть',
        '1984-04-01', 10, 1),
       ('Рекрут', 'Американская разведка вербует талантливого программиста.',
        '2003-07-01', 115, 3),
       ('Пятая печать', 'События происходят во время нилашистского террора в Венгрии осенью 1944 года.',
        '1976-11-01', 111, 4);

INSERT INTO genre (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик'),
       ('Фэнтези'),
       ('Военный'),
       ('Детский');

INSERT INTO film_genre (film_id, genre_id)
VALUES (1, 1),
       (1, 7),
       (2, 3),
       (2, 9),
       (3, 4),
       (3, 5),
       (4, 2),
       (4, 8);


INSERT INTO likes (film_id, user_id)
VALUES (1, 4),
       (1, 2),
       (2, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (3, 1),
       (4, 4);





