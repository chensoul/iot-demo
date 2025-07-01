CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40) NOT NULL,
    value VARCHAR(40) NOT NULL,
    UNIQUE KEY uk_tag_name_value (name, value)
);
