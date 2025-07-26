CREATE TABLE auto_post (
    id              SERIAL PRIMARY KEY,
    description     VARCHAR      NOT NULL,
    created         TIMESTAMP    NOT NULL,
    auto_user_id    INT          NOT NULL REFERENCES auto_user(id)
);
