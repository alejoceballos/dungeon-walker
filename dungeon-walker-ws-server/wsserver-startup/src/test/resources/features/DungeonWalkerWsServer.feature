Feature: WebSocket Server process flow

  Scenario: WebSocket Server interacts with the user and the engine server
    Given the server receives a connection request from user "01"
    And the server establishes this connection with user "01"
    Then the server sends the following message to user "01":
      | server message: connected |
    When the server receives an "authentication: user1" message from user "01"
    Then the server sends an "enter dungeon: user1" request to the engine
    And the server sends the following message to user "01":
      | server message: entering dungeon |
    When the server receives the following messages from the engine:
      | dungeon state: placed user1      |
      | dungeon cell state: placed user1 |
    Then the server sends the following messages to user "01":
      | dungeon state: user1 |
      | cell state: user1    |
    When the server receives a "move east: user1" message from user "01"
    Then the server sends a "move east: user1" request to the engine
    Then the server receives a "heartbeat: user1" message from user "01"
    Then the server sends a "heartbeat: user1" request to the engine
    When the engine sends an "heartbeat: user1" message to the server
    Then the server sends the following message to user "01":
      | heartbeat |
