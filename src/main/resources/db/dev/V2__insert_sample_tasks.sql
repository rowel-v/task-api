WITH numbers AS (
  SELECT TOP (250) CAST(ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS INT) AS n
  FROM sys.all_objects a
  CROSS JOIN sys.all_objects b
)
INSERT INTO task (name, description, priority, status, due_date)
SELECT
  CHOOSE(1 + (n % 10), 'Fix','Build','Review','Update','Write','Test','Deploy','Refactor','Plan','Document')
    + ' ' +
  CHOOSE(1 + ((n / 10) % 10), 'login page','task API','database schema','unit tests','dashboard',
         'search filter','user profile','CI pipeline','docs','dark mode')
    + ' #' + CAST(n AS VARCHAR(10)),
  'Sample description for task ' + CAST(n AS VARCHAR(10)) + '.',
  CHOOSE(1 + ((n / 3) % 3), 'LOW','MEDIUM','HIGH'),
  CHOOSE(1 + (n % 3), 'PENDING','IN_PROGRESS','COMPLETED'),
  DATEADD(HOUR, n % 24, DATEADD(DAY, (n % 60) - 20, SYSDATETIME()))
FROM numbers;