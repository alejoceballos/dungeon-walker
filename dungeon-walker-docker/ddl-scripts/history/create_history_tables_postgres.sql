-- File used to create the schema for the History Schema. It should run automatically when Docker starts because the
-- volume is mounted at the container's "/docker-entrypoint-initdb.d". In case a manual execution is needed, run:
-- docker exec -i postgres-history-db psql -U Postgres -t < ddl-scripts/history/create_history_tables_postgres.sql

CREATE SCHEMA IF NOT EXISTS DUNGEON_WALKER_HISTORY;

CREATE TABLE IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER
(
    WALKER_ID INT          NOT NULL,
    SYSTEM_ID VARCHAR(255) NOT NULL,

    PRIMARY KEY (WALKER_ID),
    UNIQUE (SYSTEM_ID)
);

CREATE INDEX IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_SYSTEM_ID
    ON DUNGEON_WALKER_HISTORY.WALKER(SYSTEM_ID);

CREATE TABLE IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_HISTORY
(
    WALKER_ID         INT                      NOT NULL,
    HISTORY_TIMESTAMP TIMESTAMP WITH TIME ZONE NOT NULL,
    X_COORD           SMALLINT                 NOT NULL,
    Y_COORD           SMALLINT                 NOT NULL,

    PRIMARY KEY (WALKER_ID),
    FOREIGN KEY (WALKER_ID) REFERENCES DUNGEON_WALKER_HISTORY.WALKER(WALKER_ID)
);

CREATE INDEX IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_HISTORY_WALKER_ID
    ON DUNGEON_WALKER_HISTORY.WALKER_HISTORY(WALKER_ID);
