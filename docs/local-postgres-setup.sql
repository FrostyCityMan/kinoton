DO
$$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_roles
        WHERE rolname = 'kinoton'
    ) THEN
        CREATE USER kinoton WITH PASSWORD 'kinoton';
    ELSE
        ALTER USER kinoton WITH PASSWORD 'kinoton';
    END IF;
END
$$;

SELECT 'CREATE DATABASE kinoton OWNER kinoton'
WHERE NOT EXISTS (
    SELECT 1
    FROM pg_database
    WHERE datname = 'kinoton'
)\gexec

GRANT ALL PRIVILEGES ON DATABASE kinoton TO kinoton;
