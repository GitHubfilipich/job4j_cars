CREATE TABLE history_owner
(
    id       SERIAL PRIMARY KEY,
    owner_id INT NOT NULL REFERENCES owner(id),
    car_id   INT NOT NULL REFERENCES car(id),
    startAt  TIMESTAMP,
    endAt    TIMESTAMP
);
