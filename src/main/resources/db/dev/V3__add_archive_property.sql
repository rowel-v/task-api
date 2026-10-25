ALTER TABLE dbo.task
ADD
    archived    BIT NOT NULL CONSTRAINT DF_task_archived DEFAULT 0,
    archived_at DATETIMEOFFSET NULL;