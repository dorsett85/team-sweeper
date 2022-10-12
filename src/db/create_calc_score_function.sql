DROP FUNCTION IF EXISTS CalcScore;

DELIMITER $$

# Calculates the score of a game based on uncovers and duration
CREATE FUNCTION CalcScore(uncovers INT, start_time TIMESTAMP, end_time TIMESTAMP)
    RETURNS FLOAT
    DETERMINISTIC
BEGIN
    DECLARE duration INT;
    SET duration = CalcDurationMS(start_time, end_time);
    IF duration = 0 THEN
        RETURN 0;
    END IF;
    RETURN (uncovers / 10) + (1000 / duration);
END $$

DELIMITER ;