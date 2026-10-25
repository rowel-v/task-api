-- V4: Add exactly 100 sample tasks; existing rows are preserved.
-- SQL Server / Flyway. Omit identity id so SQL Server generates it.
-- Dates are anchored to migration execution time; today follows Asia/Manila (+08:00).
-- 40 PENDING, 30 IN_PROGRESS, 30 COMPLETED (10 of those archived).
-- 20 overdue active tasks, 20 active tasks due today, 30 upcoming active tasks.
-- JPA lifecycle callbacks do not run for SQL inserts, so timestamps are supplied explicitly.

DECLARE @now DATETIMEOFFSET(7) = TODATETIMEOFFSET(SYSUTCDATETIME(), '+00:00');
DECLARE @today DATETIMEOFFSET(7) = SWITCHOFFSET(
    TODATETIMEOFFSET(
        CONVERT(DATETIME2, CONVERT(DATE, SWITCHOFFSET(@now, '+08:00'))),
        '+08:00'
    ), '+00:00'
);

;WITH seed (n, name, description) AS (
    SELECT n, name, description
    FROM (VALUES
    (1, N'Review weekly priorities', N'Personal seed task: Review weekly priorities.'),
    (2, N'Organize project notes', N'Personal seed task: Organize project notes.'),
    (3, N'Plan dashboard improvements', N'Personal seed task: Plan dashboard improvements.'),
    (4, N'Draft personal learning goals', N'Personal seed task: Draft personal learning goals.'),
    (5, N'Sort pending email replies', N'Personal seed task: Sort pending email replies.'),
    (6, N'Review task search behavior', N'Personal seed task: Review task search behavior.'),
    (7, N'Prepare grocery list', N'Personal seed task: Prepare grocery list.'),
    (8, N'Schedule desk cleanup', N'Personal seed task: Schedule desk cleanup.'),
    (9, N'Plan next reading session', N'Personal seed task: Plan next reading session.'),
    (10, N'Review monthly subscriptions', N'Personal seed task: Review monthly subscriptions.'),
    (11, N'Outline portfolio introduction', N'Personal seed task: Outline portfolio introduction.'),
    (12, N'Organize reusable code snippets', N'Personal seed task: Organize reusable code snippets.'),
    (13, N'Review keyboard shortcuts', N'Personal seed task: Review keyboard shortcuts.'),
    (14, N'Plan weekend errands', N'Personal seed task: Plan weekend errands.'),
    (15, N'Prepare meeting questions', N'Personal seed task: Prepare meeting questions.'),
    (16, N'Review accessibility checklist', N'Personal seed task: Review accessibility checklist.'),
    (17, N'Sort downloaded documents', N'Personal seed task: Sort downloaded documents.'),
    (18, N'Plan exercise sessions', N'Personal seed task: Plan exercise sessions.'),
    (19, N'Write journal prompts', N'Personal seed task: Write journal prompts.'),
    (20, N'Review household supplies', N'Personal seed task: Review household supplies.'),
    (21, N'Update personal task categories', N'Personal seed task: Update personal task categories.'),
    (22, N'Plan tomorrow''s focus session', N'Personal seed task: Plan tomorrow''s focus session.'),
    (23, N'Outline API documentation', N'Personal seed task: Outline API documentation.'),
    (24, N'Review saved articles', N'Personal seed task: Review saved articles.'),
    (25, N'Prepare study materials', N'Personal seed task: Prepare study materials.'),
    (26, N'Organize photo folders', N'Personal seed task: Organize photo folders.'),
    (27, N'Plan backup schedule', N'Personal seed task: Plan backup schedule.'),
    (28, N'Review notification preferences', N'Personal seed task: Review notification preferences.'),
    (29, N'Draft release checklist', N'Personal seed task: Draft release checklist.'),
    (30, N'Sort bookmarked tutorials', N'Personal seed task: Sort bookmarked tutorials.'),
    (31, N'Plan upcoming appointments', N'Personal seed task: Plan upcoming appointments.'),
    (32, N'Review project milestones', N'Personal seed task: Review project milestones.'),
    (33, N'Prepare expense categories', N'Personal seed task: Prepare expense categories.'),
    (34, N'Outline database cleanup', N'Personal seed task: Outline database cleanup.'),
    (35, N'Review mobile layout ideas', N'Personal seed task: Review mobile layout ideas.'),
    (36, N'Plan password manager cleanup', N'Personal seed task: Plan password manager cleanup.'),
    (37, N'Prepare travel packing list', N'Personal seed task: Prepare travel packing list.'),
    (38, N'Organize design references', N'Personal seed task: Organize design references.'),
    (39, N'Plan next skill practice', N'Personal seed task: Plan next skill practice.'),
    (40, N'Review unfinished personal projects', N'Personal seed task: Review unfinished personal projects.'),
    (41, N'Implement task sorting', N'Personal seed task: Implement task sorting.'),
    (42, N'Improve dashboard spacing', N'Personal seed task: Improve dashboard spacing.'),
    (43, N'Write task service unit tests', N'Personal seed task: Write task service unit tests.'),
    (44, N'Refactor task response mapping', N'Personal seed task: Refactor task response mapping.'),
    (45, N'Build priority filter controls', N'Personal seed task: Build priority filter controls.'),
    (46, N'Document archive behavior', N'Personal seed task: Document archive behavior.'),
    (47, N'Polish dialog keyboard navigation', N'Personal seed task: Polish dialog keyboard navigation.'),
    (48, N'Improve form validation messages', N'Personal seed task: Improve form validation messages.'),
    (49, N'Test task status transitions', N'Personal seed task: Test task status transitions.'),
    (50, N'Organize API request examples', N'Personal seed task: Organize API request examples.'),
    (51, N'Update project README', N'Personal seed task: Update project README.'),
    (52, N'Investigate search edge cases', N'Personal seed task: Investigate search edge cases.'),
    (53, N'Improve empty state alignment', N'Personal seed task: Improve empty state alignment.'),
    (54, N'Review database query performance', N'Personal seed task: Review database query performance.'),
    (55, N'Build responsive navigation', N'Personal seed task: Build responsive navigation.'),
    (56, N'Test overdue date handling', N'Personal seed task: Test overdue date handling.'),
    (57, N'Polish task detail timestamps', N'Personal seed task: Polish task detail timestamps.'),
    (58, N'Refactor notification service', N'Personal seed task: Refactor notification service.'),
    (59, N'Review pull request feedback', N'Personal seed task: Review pull request feedback.'),
    (60, N'Implement clearer loading states', N'Personal seed task: Implement clearer loading states.'),
    (61, N'Update component style tokens', N'Personal seed task: Update component style tokens.'),
    (62, N'Prepare demo walkthrough', N'Personal seed task: Prepare demo walkthrough.'),
    (63, N'Test archive selection flow', N'Personal seed task: Test archive selection flow.'),
    (64, N'Improve error message wording', N'Personal seed task: Improve error message wording.'),
    (65, N'Document local setup steps', N'Personal seed task: Document local setup steps.'),
    (66, N'Review upcoming task filtering', N'Personal seed task: Review upcoming task filtering.'),
    (67, N'Polish overview progress card', N'Personal seed task: Polish overview progress card.'),
    (68, N'Add missing input labels', N'Personal seed task: Add missing input labels.'),
    (69, N'Organize integration test fixtures', N'Personal seed task: Organize integration test fixtures.'),
    (70, N'Review date timezone conversions', N'Personal seed task: Review date timezone conversions.'),
    (71, N'Set up project repository', N'Personal seed task: Set up project repository.'),
    (72, N'Create initial task schema', N'Personal seed task: Create initial task schema.'),
    (73, N'Build task creation form', N'Personal seed task: Build task creation form.'),
    (74, N'Add task list view', N'Personal seed task: Add task list view.'),
    (75, N'Implement priority badges', N'Personal seed task: Implement priority badges.'),
    (76, N'Configure development proxy', N'Personal seed task: Configure development proxy.'),
    (77, N'Build basic overview layout', N'Personal seed task: Build basic overview layout.'),
    (78, N'Add task delete confirmation', N'Personal seed task: Add task delete confirmation.'),
    (79, N'Implement task status badge', N'Personal seed task: Implement task status badge.'),
    (80, N'Document initial API endpoints', N'Personal seed task: Document initial API endpoints.'),
    (81, N'Set up code formatting', N'Personal seed task: Set up code formatting.'),
    (82, N'Add application navigation', N'Personal seed task: Add application navigation.'),
    (83, N'Configure database migrations', N'Personal seed task: Configure database migrations.'),
    (84, N'Build task edit dialog', N'Personal seed task: Build task edit dialog.'),
    (85, N'Implement task search input', N'Personal seed task: Implement task search input.'),
    (86, N'Add due date formatting', N'Personal seed task: Add due date formatting.'),
    (87, N'Create task response model', N'Personal seed task: Create task response model.'),
    (88, N'Set up notification banners', N'Personal seed task: Set up notification banners.'),
    (89, N'Review first release notes', N'Personal seed task: Review first release notes.'),
    (90, N'Test initial deployment build', N'Personal seed task: Test initial deployment build.'),
    (91, N'Archive prototype screenshots', N'Personal seed task: Archive prototype screenshots.'),
    (92, N'Archive old setup checklist', N'Personal seed task: Archive old setup checklist.'),
    (93, N'Archive completed onboarding notes', N'Personal seed task: Archive completed onboarding notes.'),
    (94, N'Archive previous sprint summary', N'Personal seed task: Archive previous sprint summary.'),
    (95, N'Archive retired UI mockups', N'Personal seed task: Archive retired UI mockups.'),
    (96, N'Archive finished reading plan', N'Personal seed task: Archive finished reading plan.'),
    (97, N'Archive old test data review', N'Personal seed task: Archive old test data review.'),
    (98, N'Archive completed backup checklist', N'Personal seed task: Archive completed backup checklist.'),
    (99, N'Archive initial design research', N'Personal seed task: Archive initial design research.'),
    (100, N'Archive previous demo preparation', N'Personal seed task: Archive previous demo preparation.')
    ) AS seed_values(n, name, description)
), task_dates AS (
    SELECT *,
        DATEADD(DAY, -60 - (n % 10), @now) AS created_at,
        CASE
            WHEN n <= 20 THEN DATEADD(HOUR, 17, DATEADD(DAY, -(1 + n % 14), @today))
            WHEN n <= 40 THEN DATEADD(HOUR, 9 + n % 10, @today)
            WHEN n <= 70 THEN DATEADD(HOUR, 17, DATEADD(DAY, n - 40, @today))
            ELSE DATEADD(DAY, -(1 + n % 10), @now)
        END AS due_date,
        CASE WHEN n > 70 THEN DATEADD(DAY, -(2 + n % 10), @now) END AS completed_at,
        CASE WHEN n > 90 THEN DATEADD(DAY, -1, @now) END AS archived_at
    FROM seed
)
INSERT INTO dbo.task (
    name, description, status, priority,
    created_at, updated_at, due_date, completed_at, archived, archived_at
)
SELECT
    name,
    description,
    CASE WHEN n <= 40 THEN N'PENDING'
         WHEN n <= 70 THEN N'IN_PROGRESS'
         ELSE N'COMPLETED' END,
    CHOOSE(1 + ((n - 1) % 3), N'LOW', N'MEDIUM', N'HIGH'),
    created_at,
    CASE WHEN n > 90 THEN archived_at
         WHEN n > 70 THEN completed_at
         WHEN n > 40 OR n % 2 = 0 THEN DATEADD(DAY, -1, @now)
         ELSE created_at END,
    due_date,
    completed_at,
    CAST(CASE WHEN n > 90 THEN 1 ELSE 0 END AS BIT),
    archived_at
FROM task_dates;