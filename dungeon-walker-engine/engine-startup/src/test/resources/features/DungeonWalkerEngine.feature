Feature: Engine process flow

  Scenario: Walker awakes and starts moving
    Given the engine receives a client request to "enter dungeon: user1"
    Then the engine sends an "entered the dungeon: user1" message to the client
    When the engine receives a client request to "move east: user1"
    Then the engine sends a "dungeon state: moved user1" message to the client
    When the engine receives a client request to "move east: user1" again
    Then the engine sends a "dungeon state: further moved user1" message to the client
#    Then the engine sends a "heartbeat: user1" message to the server