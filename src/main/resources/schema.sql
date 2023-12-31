drop table IF EXISTS users CASCADE;
drop table IF EXISTS user_friends CASCADE;
drop table IF EXISTS film_likes CASCADE;
drop table IF EXISTS film_genres CASCADE;
drop table IF EXISTS genres CASCADE;
drop table IF EXISTS films CASCADE;
drop table IF EXISTS mpa CASCADE;
drop table IF EXISTS directors CASCADE;
drop table IF EXISTS film_directors CASCADE;
drop table IF EXISTS review_films CASCADE;
drop table IF EXISTS review_like CASCADE;
drop table IF EXISTS events CASCADE;

create TABLE IF NOT EXISTS USERS
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar(255),
    login    varchar(255) NOT NULL,
    email    varchar(255) NOT NULL,
    birthday date         NOT NULL
);

create TABLE IF NOT EXISTS user_friends
(
    user_id    INTEGER REFERENCES users (id) ON delete CASCADE,
    friends_id INTEGER REFERENCES users (id) ON delete CASCADE,
    status     boolean,
    UNIQUE (user_id, friends_id)
);

create TABLE IF NOT EXISTS mpa
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        varchar(30) NOT NULL,
    description varchar(255)
);

create TABLE IF NOT EXISTS directors
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL
);

create TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(100) NOT NULL,
    description  varchar(255) NOT NULL,
    duration     INTEGER,
    release_date date         NOT NULL,
    mpa          INTEGER REFERENCES mpa (id) ON delete CASCADE
);

create TABLE IF NOT EXISTS film_directors
(
    film_id     INTEGER REFERENCES films (id) ON delete CASCADE,
    director_id INTEGER REFERENCES directors (id) ON delete CASCADE,
    UNIQUE (film_id, director_id)
);

create TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL
);

create TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER REFERENCES films (id) ON delete CASCADE,
    genre_id INTEGER REFERENCES genres (id) ON delete CASCADE,
    UNIQUE (film_id, genre_id)
);

create TABLE IF NOT EXISTS film_likes
(
    user_id INTEGER REFERENCES users (id) ON delete CASCADE,
    film_id INTEGER REFERENCES films (id) ON delete CASCADE
);

CREATE TABLE IF NOT EXISTS review_films
(
    review_id   INTEGER GENERATED BY DEFAULT AS IDENTITY UNIQUE,
    content     VARCHAR(255),
    is_positive BOOLEAN,
    user_id     INTEGER REFERENCES users (id),
    film_id     INTEGER REFERENCES films (id),
    useful      INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS review_like
(
    user_id     INTEGER REFERENCES users (id),
    review_id   INTEGER REFERENCES review_films (review_id) ON DELETE CASCADE,
    is_positive BOOLEAN,
    UNIQUE (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS events
(
    create_time TIMESTAMP,
    user_id     INTEGER,
    event_type  VARCHAR(10),
    operation   VARCHAR(10),
    event_id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    entity_id   INTEGER
);