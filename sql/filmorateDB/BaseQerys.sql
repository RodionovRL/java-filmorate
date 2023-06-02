--Получение списка всех пользователей
SELECT *
FROM users;

-- Всё из таблицы соответствия друзей
SELECT *
FROM friends;

-- Выборка общих друзей без учёта статуса дружбы
SELECT u.id,
       u.name
FROM users u
WHERE u.id = (SELECT fr.id
              FROM (SELECT user_id   AS id,
                           friend_id AS friend
                    FROM friends
                    WHERE friend_id IN (1, 3)
                    UNION
                    SELECT friend_id as id,
                           user_id   as friend
                    FROM friends
                    WHERE user_id IN (1, 3)) fr
              GROUP BY fr.id
              HAVING count(fr.id) = 2)
;

-- Выбрать все фильмы
SELECT *
FROM film;

-- Все фильмы с рейтингами
SELECT f.name,
       f.description,
       f.duration,
       r.name raiting
FROM film f
         LEFT JOIN rating r on f.rating_id = r.id;


-- Посмотреть все жанры
SELECT *
FROM genre;

-- Все фильмы с рейтингами и жанрами (в одну ячейку)
SELECT f.name,
       f.description,
       f.duration,
       r.name raiting,
       string_agg(g.name, ', ') AS genres
FROM film f
         LEFT JOIN rating r on f.rating_id = r.id
         LEFT JOIN film_genre fg on f.id = fg.film_id
         LEFT JOIN genre g on fg.genre_id = g.id
GROUP BY f.name, f.description, f.duration, r.name;


-- Получение ТОП-2 фильмов
SELECT f.name,
        count(l.film_id) rate
FROM film f
JOIN likes l on f.id = l.film_id
GROUP BY f.name
HAVING count(l.film_id) >= 2
ORDER BY rate desc








