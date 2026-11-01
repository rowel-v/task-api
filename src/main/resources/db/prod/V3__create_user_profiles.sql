-- Preserve existing display names while separating profiles from account credentials.
CREATE TABLE dbo.user_profile (
    id BIGINT IDENTITY(1,1) NOT NULL,
    user_id BIGINT NOT NULL,
    display_name NVARCHAR(100) NULL,
    avatar_url NVARCHAR(500) NULL,
    bio NVARCHAR(500) NULL,
    time_zone NVARCHAR(100) NULL,
    CONSTRAINT PK_user_profile PRIMARY KEY (id),
    CONSTRAINT UQ_user_profile_user_id UNIQUE (user_id),
    CONSTRAINT FK_user_profile_user FOREIGN KEY (user_id) REFERENCES dbo.[user](id)
);

INSERT INTO dbo.user_profile (user_id, display_name)
SELECT id, display_name FROM dbo.[user];

ALTER TABLE dbo.[user] DROP COLUMN display_name;
