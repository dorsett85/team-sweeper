CREATE TABLE IF NOT EXISTS game (
    id INT AUTO_INCREMENT PRIMARY KEY,
    difficulty CHAR(1) CHECK ( difficulty IN ('e', 'm', 'h') ) NOT NULL,
    status TINYTEXT CHECK ( status IN ('IN_PROGRESS', 'WON', 'LOST') ) NOT NULL,
    -- 2 dimensional array of rows and columns containing cells
    -- TODO this should probably go in a document database because of the rapid
    --  read/write access from the game.
    board JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

drop table game;