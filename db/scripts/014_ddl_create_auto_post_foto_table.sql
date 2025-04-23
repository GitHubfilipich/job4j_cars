create table auto_post_foto
(
    id      serial primary key,
    post_id int not null REFERENCES auto_post(id),
    foto_id int not null REFERENCES foto(id),
    UNIQUE (post_id, foto_id)
);
