-- Crear la tabla UserTable
CREATE TABLE UserTable
(
    UserId   INT PRIMARY KEY IDENTITY(1,1),
    UserName VARCHAR(100) NOT NULL,
    Email    VARCHAR(100) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL
);
GO

-- Crear la tabla ClassroomTable
CREATE TABLE ClassroomTable
(
    Id          INT PRIMARY KEY IDENTITY(1,1),   -- Nueva columna Id con identidad
    ClassroomId INT NOT NULL,                      -- Campo ClassroomId, sin identidad
    Capacity    INT NOT NULL,                      -- Capacidad del aula
    Status      VARCHAR(50) NOT NULL,              -- Estado del aula
    CONSTRAINT UC_ClassroomId UNIQUE (ClassroomId) -- Constraint único para ClassroomId
);
GO

-- Crear la tabla ReservationTable
CREATE TABLE ReservationTable
(
    ReservationId   INT PRIMARY KEY IDENTITY(1,1),  -- ID de la reserva
    UserId          INT NOT NULL,                     -- ID del usuario
    ClassroomId     INT NOT NULL,                     -- ID del aula reservado
    ReservationDate DATE NOT NULL,                    -- Fecha de la reserva
    StartTime       TIME NOT NULL,                    -- Hora de inicio de la reserva
    EndTime         TIME NOT NULL,                    -- Hora de fin de la reserva
    CONSTRAINT FK_Reservation_User FOREIGN KEY (UserId) REFERENCES UserTable (UserId),  -- FK para la tabla UserTable
    CONSTRAINT FK_Reservation_Classroom FOREIGN KEY (ClassroomId) REFERENCES ClassroomTable (ClassroomId), -- FK para la tabla ClassroomTable
    CONSTRAINT CHK_Schedule CHECK (StartTime < EndTime) -- Restricción de horario (hora de inicio debe ser antes que la de fin)
);
GO