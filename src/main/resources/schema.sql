CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     VARCHAR NOT NULL,
    email    VARCHAR NOT NULL,
    login    VARCHAR NOT NULL,
    birthday DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS genre
(
    id   INT PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
    id          INT PRIMARY KEY,
    name        VARCHAR NOT NULL UNIQUE,
    description VARCHAR
);

CREATE TABLE IF NOT EXISTS director
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS friends
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id   BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    friend_id BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    date      TIMESTAMP NOT NULL,
    UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR NOT NULL,
    release_date DATE    NOT NULL,
    description  VARCHAR NOT NULL,
    duration     INT     NOT NULL,
    mpa_id       INT     NOT NULL REFERENCES mpa (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS likes
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    film_id BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    date    TIMESTAMP NOT NULL,
    UNIQUE (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS film_director
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id     BIGINT NOT NULL REFERENCES film (id),
    director_id BIGINT NOT NULL REFERENCES director (id),
    UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_id INT    NOT NULL REFERENCES genre (id) ON DELETE CASCADE,
    film_id  BIGINT NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    UNIQUE (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS review
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    date        TIMESTAMP NOT NULL,
    is_positive boolean   NOT NULL,
    useful      INT       NOT NULL,
    user_id     BIGINT    NOT NULL REFERENCES users (id),
    film_id     BIGINT    NOT NULL REFERENCES film (id),
    UNIQUE (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS event_type
(
    id   INT PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS operation_type
(
    id   INT PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS events
(
    event_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    date       TIMESTAMP NOT NULL,
    user_id    BIGINT    NOT NULL REFERENCES users (id),
    event_type INTEGER   NOT NULL REFERENCES event_type (id),
    operation  INTEGER   NOT NULL REFERENCES operation_type (id),
    entity_id  INTEGER
)