CREATE TABLE foto
(
    id        SERIAL PRIMARY KEY,
    name      VARCHAR NOT NULL,
    file_path VARCHAR UNIQUE NOT NULL
);
