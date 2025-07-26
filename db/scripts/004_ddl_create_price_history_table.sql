CREATE TABLE price_history (
    id SERIAL PRIMARY KEY,
    before BIGINT NOT NULL,
    after BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    auto_post_id INT REFERENCES auto_post(id)
);