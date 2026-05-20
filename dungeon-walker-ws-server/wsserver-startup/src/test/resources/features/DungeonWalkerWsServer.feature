Feature: WS Server receives messages

  Scenario: Client connects to server and send its credentials
    Given client "A" sends a connection request to the WebSocket server
    When the WebSocket server establishes a connection with client "A"
    Then client "A" receives the following message from the WebSocket server:
      | server message connected |
    When client "A" sends an "authenticate" "user1" message to the WebSocket server
    Then the WebSocket server sends client "A"'s "enter-dungeon" "user1" request to the Engine
    And client "A" receives the following message from the WebSocket server:
      | server message authenticated |
    When the Engine sends an "entered-the-dungeon" "user1" message to the WebSocket server
    Then client "A" receives the following message from the WebSocket server:
      | dungeon state user1 |
    When client "A" sends a "move-east" "user1" message to the WebSocket server
    Then the WebSocket server sends client "A"'s "movement-east" "user1" request to the Engine
