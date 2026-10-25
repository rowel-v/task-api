-- One user owns many tasks; every task must have one owner.
CREATE TABLE dbo.[user] (
    id BIGINT IDENTITY(1,1) NOT NULL,
    display_name NVARCHAR(100) NOT NULL,
    username NVARCHAR(50) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    created_at DATETIMEOFFSET(7) NOT NULL
        CONSTRAINT DF_user_created_at
        DEFAULT TODATETIMEOFFSET(SYSUTCDATETIME(), '+00:00'),
    updated_at DATETIMEOFFSET(7) NULL,

    CONSTRAINT PK_user PRIMARY KEY (id),
    CONSTRAINT UQ_user_username UNIQUE (username)
);

-- Requires an empty task table.
ALTER TABLE dbo.task
ADD user_id BIGINT NOT NULL;

ALTER TABLE dbo.task WITH CHECK
ADD CONSTRAINT FK_task_user
    FOREIGN KEY (user_id) REFERENCES dbo.[user](id)
    ON DELETE NO ACTION;

-- Allows multiple tasks per user.
CREATE INDEX IX_task_user_id ON dbo.task(user_id);