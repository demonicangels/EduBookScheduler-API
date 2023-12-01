CREATE EVENT missed_booking_event
ON SCHEDULE EVERY 1 MINUTE
DO
BEGIN
    UPDATE booking
    SET state=3
    WHERE state=1
    AND NOW()>STR_TO_DATE(CONCAT(DATE_FORMAT(date,'%Y-%m-%d'),' ',FLOOR(end_time/60),':',end_time%60),'%Y-%m-%d %H:%i');
END