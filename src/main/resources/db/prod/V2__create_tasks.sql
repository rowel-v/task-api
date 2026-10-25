-- Every task belongs to a user. No sample tasks are inserted.
CREATE TABLE dbo.task (
    id BIGINT IDENTITY(1,1) NOT NULL,
    user_id BIGINT NOT NULL,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(2000) NULL,
    status NVARCHAR(50) NOT NULL,
    priority NVARCHAR(50) NOT NULL,
    archived BIT NOT NULL CONSTRAINT DF_task_archived DEFAULT 0,
    created_at DATETIMEOFFSET(7) NOT NULL
        CONSTRAINT DF_task_created_at
        DEFAULT TODATETIMEOFFSET(SYSUTCDATETIME(), '+00:00'),
    updated_at DATETIMEOFFSET(7) NULL,
    due_date DATETIMEOFFSET(7) NULL,
    completed_at DATETIMEOFFSET(7) NULL,
    archived_at DATETIMEOFFSET(7) NULL,
    deleted_at DATETIMEOFFSET(7) NULL,

    CONSTRAINT PK_task PRIMARY KEY (id),
    CONSTRAINT FK_task_user FOREIGN KEY (user_id)
        REFERENCES dbo.[user](id) ON DELETE CASCADE
);

CREATE INDEX IX_task_user_id ON dbo.task(user_id);
