CREATE
DATABASE AulaSync;
GO

USE AulaSync;
GO

-- Table User
CREATE TABLE UserTable
(
    UserId   INT PRIMARY KEY IDENTITY(1,1),
    UserName VARCHAR(100)        NOT NULL,
    Email    VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(255)        NOT NULL
);
GO

-- Table Classroom
CREATE TABLE Classroom
(
    ClassroomId INT PRIMARY KEY IDENTITY(1,1),
    Capacity    INT         NOT NULL,
    Status      VARCHAR(50) NOT NULL
);
GO

-- Table Reservation
CREATE TABLE Reservation
(
    ReservationId   INT PRIMARY KEY IDENTITY(1,1),
    UserId          INT  NOT NULL,
    ClassroomId     INT  NOT NULL,
    ReservationDate DATE NOT NULL,
    StartTime       TIME NOT NULL,
    EndTime         TIME NOT NULL,
    CONSTRAINT FK_Reservation_User FOREIGN KEY (UserId) REFERENCES UserTable (UserId),
    CONSTRAINT FK_Reservation_Classroom FOREIGN KEY (ClassroomId) REFERENCES Classroom (ClassroomId),
    CONSTRAINT CHK_Schedule CHECK (StartTime < EndTime)
);
GO
