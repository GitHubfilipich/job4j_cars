CREATE TABLE auto_post_foto
(
    id      SERIAL PRIMARY KEY,
    post_id INT NOT NULL REFERENCES auto_post(id),
    foto_id INT NOT NULL REFERENCES foto(id),
    UNIQUE (post_id, foto_id)
);
