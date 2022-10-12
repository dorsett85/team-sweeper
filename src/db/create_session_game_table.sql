CREATE TABLE IF NOT EXISTS session_game
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    session_id TINYTEXT      NOT NULL,
    game_id    INT           NOT NULL,
    -- number of cells directly uncovered (not cascaded)
    uncovers   INT DEFAULT 0 NOT NULL,
    FOREIGN KEY (game_id) REFERENCES game (id) ON DELETE CASCADE
);
