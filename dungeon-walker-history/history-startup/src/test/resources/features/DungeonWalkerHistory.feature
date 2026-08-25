Feature: WebSocket Server process flow

  Scenario: History Server receives history logs from the Engine
    Given the engine sends the following history logs:
      | history log: user1 entered dungeon  |
      | history log: user1 moved east       |
      | history log: user1 moved north      |
      | history log: user1 left the dungeon |
    When the history service receives the history logs
    Then after 1 "second"
    Then the following records are saved in the database table "DUNGEON_WALKER_HISTORY.WALKER":
      # @header: row
      | WALKER_ID -> Long  | SYSTEM_ID -> String |
      | {{PUT->walker-id}} | user1               |
    And the following records are saved in the database table "DUNGEON_WALKER_HISTORY.WALKER_HISTORY":
      # @header: row
      | HISTORY_TIMESTAMP -> Timestamp | WALKER_ID -> Long  | X_COORD -> Integer | Y_COORD -> Integer |
      | 2026-08-18 23:20:30            | {{GET->walker-id}} | 5                  | 5                  |
      | 2026-08-18 23:20:31            | {{GET->walker-id}} | 6                  | 5                  |
      | 2026-08-18 23:20:32            | {{GET->walker-id}} | 6                  | 4                  |
      | 2026-08-18 23:20:33            | {{GET->walker-id}} | {{NULL}}           | {{NULL}}           |
