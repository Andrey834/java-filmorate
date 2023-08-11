drop table IF EXISTS users CASCADE;
drop table IF EXISTS user_friends CASCADE;
drop table IF EXISTS film_likes CASCADE;
drop table IF EXISTS film_genre CASCADE;
drop table IF EXISTS genre CASCADE;
drop table IF EXISTS film CASCADE;
drop table IF EXISTS mpa CASCADE;

create TABLE IF NOT EXISTS USERS(
    id       INTEGER PRIMARY KEY,
    name     varchar(30),
    login    varchar(255) NOT NULL,
    email    varchar(255) NOT NULL,
    birthday date    NOT NULL);

create TABLE IF NOT EXISTS user_friends(
    user_id    INTEGER REFERENCES users (id) ON delete CASCADE,
    friends_id INTEGER REFERENCES users (id) ON delete CASCADE,
    status boolean,
    PRIMARY KEY (user_id, friends_id));

create TABLE IF NOT EXISTS mpa(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(30) NOT NULL,
    description varchar(255));

create TABLE IF NOT EXISTS film(
    id           INTEGER PRIMARY KEY,
    name         varchar(100) NOT NULL,
    description  varchar(255) NOT NULL,
    duration     INTEGER,
    release_date date    NOT NULL,
    mpa          INTEGER REFERENCES mpa (id) ON delete CASCADE);

create TABLE IF NOT EXISTS genre(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL);

create TABLE IF NOT EXISTS film_genre(
    film_id  INTEGER REFERENCES film (id) ON delete CASCADE,
    genre_id INTEGER REFERENCES genre (id) ON delete CASCADE,
    PRIMARY KEY (film_id, genre_id));

create TABLE IF NOT EXISTS film_likes(
    user_id INTEGER REFERENCES users (id) ON delete CASCADE,
    film_id INTEGER REFERENCES film (id) ON delete CASCADE,
    PRIMARY KEY (user_id, film_id));