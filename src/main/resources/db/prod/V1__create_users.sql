CREATE TABLE dbo.[user] (
    id BIGINT IDENTITY(1,1) NOT NULL,
    username NVARCHAR(50) NOT NULL,
    password_hash NVARCHAR(255) NOT NULL,
    display_name NVARCHAR(100) NULL,
    created_at DATETIMEOFFSET(7) NOT NULL
        CONSTRAINT DF_user_created_at
        DEFAULT TODATETIMEOFFSET(SYSUTCDATETIME(), '+00:00'),
    updated_at DATETIMEOFFSET(7) NULL,
    deleted_at DATETIMEOFFSET(7) NULL,

    CONSTRAINT PK_user PRIMARY KEY (id),
    CONSTRAINT UQ_user_username UNIQUE (username)
);
