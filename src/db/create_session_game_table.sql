CREATE TABLE IF NOT EXISTS session_game
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    -- Sessions can get deleted so we won't reference the id in that table
    session_id TINYTEXT NOT NULL,
    game_id    INT      NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game (id) ON DELETE CASCADE
);
