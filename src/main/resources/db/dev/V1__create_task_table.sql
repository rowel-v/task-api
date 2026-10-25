CREATE TABLE dbo.task (
    id            BIGINT IDENTITY(1,1) NOT NULL,
    name          NVARCHAR(255)  NOT NULL,
    description   NVARCHAR(2000) NULL,
    status        NVARCHAR(50)   NULL,
    priority      NVARCHAR(50)   NULL,
    created_at    DATETIMEOFFSET NULL,
    updated_at    DATETIMEOFFSET NULL,
    due_date      DATETIMEOFFSET NULL,
    completed_at  DATETIMEOFFSET NULL,
    CONSTRAINT PK_task PRIMARY KEY (id)
);