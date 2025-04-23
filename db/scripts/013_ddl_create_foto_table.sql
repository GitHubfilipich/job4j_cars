create table foto
(
    id          serial primary key,
    name        varchar not null,
    file_path   varchar unique not null
);
