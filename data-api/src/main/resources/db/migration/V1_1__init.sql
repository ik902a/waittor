CREATE TABLE IF NOT EXISTS torrent
(
    id           bigserial PRIMARY KEY,
    name         varchar(255),
    release      date,
    series       int,
    torrent_type varchar(50) CHECK (torrent_type IN ('OUR', 'FOREIGN', 'SERIES'))
);

CREATE TABLE IF NOT EXISTS users
(
    id bigserial PRIMARY KEY,
    username varchar(255) UNIQUE,
    password varchar(255)
);
