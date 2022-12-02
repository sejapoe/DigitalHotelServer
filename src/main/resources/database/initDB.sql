CREATE TABLE IF NOT EXISTS hotels
(
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    rooms json         NOT NULL DEFAULT '[]'
);