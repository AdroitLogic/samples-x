CREATE DATABASE zenythzdb;
USE zenythzdb;
CREATE TABLE machine (
    factory_id SMALLINT,
    machine_id SMALLINT,
    hours SMALLINT,
    restarts SMALLINT,
    minor_repairs SMALLINT,
    major_repairs SMALLINT,
    extra_cost INT,
    PRIMARY KEY (machine_id, factory_id)
);
GRANT ALL PRIVILEGES ON zenythzdb.* TO 'zenythz'@'localhost' IDENTIFIED BY 'zenythz';

INSERT INTO machine VALUES (1, 1, 0, 0, 0, 0, 0), (1, 2, 0, 0, 0, 0, 0), (1, 3, 0, 0, 0, 0, 0), (1, 4, 0, 0, 0, 0, 0),
(2, 1, 0, 0, 0, 0, 0), (2, 2, 0, 0, 0, 0, 0), (2, 5, 0, 0, 0, 0, 0);