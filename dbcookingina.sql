
-- Create the database
CREATE DATABASE IF NOT EXISTS dbcookingina;
USE dbcookingina;

-- Player table
CREATE TABLE tblplayer (
    playerid INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    displayname VARCHAR(100),
    joindate DATE DEFAULT CURRENT_DATE
);

-- Level table
CREATE TABLE tbllevel (
    levelid INT PRIMARY KEY AUTO_INCREMENT,
    levelnumber INT NOT NULL,
    targetincome DECIMAL(10,2),
    maxcustomers INT,
    timelimit INT, -- in seconds
    patiencelevel INT
);

-- MenuItem table with playerid and levelid foreign keys
CREATE TABLE tblmenuitem (
    menuitemid INT PRIMARY KEY AUTO_INCREMENT,
    menuitemname VARCHAR(100) NOT NULL,
    price DECIMAL(6,2) NOT NULL,
    description TEXT,
    cookingtime INT, -- in seconds
    levelid INT,
    playerid INT,
    FOREIGN KEY (levelid) REFERENCES tbllevel(levelid),
    FOREIGN KEY (playerid) REFERENCES tblplayer(playerid)
);

-- Equipment table with playerid foreign key
CREATE TABLE tblequipment (
    equipmentid INT PRIMARY KEY AUTO_INCREMENT,
    equipmentname VARCHAR(100) NOT NULL,
    equipmenttype VARCHAR(50),
    speedmultiplier DECIMAL(3,2) DEFAULT 1.0,
    description TEXT,
    isunlocked BOOLEAN DEFAULT FALSE,
    playerid INT,
    FOREIGN KEY (playerid) REFERENCES tblplayer(playerid)
);
