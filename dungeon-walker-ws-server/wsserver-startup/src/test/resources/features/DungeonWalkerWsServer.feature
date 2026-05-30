Feature: WS Server receives messages

  Scenario: User connects, authenticates and interact with the server
    Given user "01" sends a connection request to the server
    When the server establishes a connection with user "01"
    Then user "01" receives the following message from the server:
      | server message: connected |
    When user "01" sends an "authentication: user1" message to the server
    Then the server sends a "enter dungeon: user1" request to the engine
    And user "01" receives the following message from the server:
      | server message: entering dungeon |
    When the engine sends an "entered the dungeon: user1" message to the server
    Then user "01" receives the following message from the server:
      | dungeon state: user1 |
    When user "01" sends a "move east: user1" message to the server
    Then the server sends a "move east: user1" request to the engine
    Then user "01" sends a "heartbeat: user1" message to the server
    Then the server sends a "heartbeat: user1" request to the engine
    When the engine sends an "heartbeat: user1" message to the server
    Then user "01" receives the following message from the server:
      | heartbeat |

