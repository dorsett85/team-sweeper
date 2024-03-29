DROP FUNCTION IF EXISTS CalcDurationMS;

DELIMITER $$

# Calculates the duration between two timestamps in milliseconds
CREATE FUNCTION CalcDurationMS(start_time TIMESTAMP(3), end_time TIMESTAMP(3))
    RETURNS INT
    DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(MICROSECOND, start_time, end_time) / 1000;
END $$

DELIMITER ;
