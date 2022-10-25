CREATE TABLE IF NOT EXISTS game
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    difficulty     CHAR(1) CHECK ( difficulty IN ('e', 'm', 'h') )             NOT NULL,
    status         TINYTEXT CHECK ( status IN ('IN_PROGRESS', 'WON', 'LOST') ) NOT NULL,
    -- 2 dimensional array of rows and columns containing cells
    board          JSON                                                        NOT NULL,
    -- Percent of the game that was completed after it began. This is calculated
    -- by the number of uncovered cells divided by total non-mine cells.
    completion_pct FLOAT     DEFAULT 0                                         NOT NULL,
    -- When the first click occurred
    started_at     TIMESTAMP(3),
    -- When a game is won or lost
    ended_at       TIMESTAMP(3),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP                         NOT NULL
);
