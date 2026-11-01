-- Retain the legacy avatar_url column to preserve any existing values.
ALTER TABLE dbo.user_profile ADD photo_data VARBINARY(MAX) NULL, photo_content_type VARCHAR(20) NULL;
