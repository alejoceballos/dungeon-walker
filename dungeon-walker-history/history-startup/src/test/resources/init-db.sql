-- File used to create the schema for the History Schema. This is a copy of
-- "dungeon-walker-docker/ddl-scripts/history/create_history_tables_postgres.sql" and must be kept updated with the
-- original.

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

CREATE SCHEMA IF NOT EXISTS DUNGEON_WALKER_HISTORY;

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

CREATE TABLE IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER
(
    WALKER_ID INT          NOT NULL,
    SYSTEM_ID VARCHAR(255) NOT NULL,

    PRIMARY KEY (WALKER_ID),
    UNIQUE (SYSTEM_ID)
    );

CREATE SEQUENCE IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_ID_SEQ
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE UNIQUE INDEX IF NOT EXISTS DUNGEON_WALKER_HISTORY_WALKER_SYSTEM_ID
    ON DUNGEON_WALKER_HISTORY.WALKER(SYSTEM_ID);

-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

CREATE TABLE IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_HISTORY
(
    WALKER_HISTORY_ID INT                      NOT NULL,
    WALKER_ID         INT                      NOT NULL,
    HISTORY_TIMESTAMP TIMESTAMP WITH TIME ZONE NOT NULL,
    X_COORD           SMALLINT,
    Y_COORD           SMALLINT,

    PRIMARY KEY (WALKER_HISTORY_ID),
    FOREIGN KEY (WALKER_ID) REFERENCES DUNGEON_WALKER_HISTORY.WALKER(WALKER_ID)
);

CREATE SEQUENCE  IF NOT EXISTS DUNGEON_WALKER_HISTORY.WALKER_HISTORY_ID_SEQ
    START WITH 1
    INCREMENT BY 5
    MINVALUE 1
    NO MAXVALUE
    CACHE 10;

CREATE INDEX IF NOT EXISTS DUNGEON_WALKER_HISTORY_WALKER_HISTORY_WALKER_ID
    ON DUNGEON_WALKER_HISTORY.WALKER_HISTORY(WALKER_ID);
