Feature: WS Server receives messages

  Background:
    Given a user "user1" with password "password1" as client "01"
    And a user "user2" with password "password2" as client "02"
    When the client "01" sends a connection request to the WebSocket server
    And the client "02" sends a connection request to the WebSocket server
    When the WebSocket server process the messages
    Then the WebSocket server establishes 2 connections with clients
    And the WebSocket server sends 2 requests to the Engine
    And the WebSocket server request, started by client "01", to the Engine, should be a "connection" request
    And the WebSocket server request, started by client "02", to the Engine, should be a "connection" request

  Scenario: Client disconnects from the server
    Given the client "01" sends a disconnection request to the WebSocket server
    Then the client "01" should disconnect from the WebSocket server

  Scenario: Client sends a message to the server
    Given the following client JSON message:
      """
      {
        "type": "movement",
          "data": {
            "direction": "E"
          }
      }
      """
    And the client "01" sends the message to the WebSocket server
    When the WebSocket server process the messages
    Then the WebSocket server request, started by client "01", to the Engine, should be a "movement" request

  Scenario: Client receives a broadcast message from the server
    Given the following Engine Protobuf message as JSON:
      """
      {
        "walkerPositions" : {
          "coordinatesByWalkerId" : {
            "user2" : {
              "x" : 3,
              "y" : 4
            },
            "user1" : {
              "x" : 1,
              "y" : 2
            }
          }
        }
      }
      """
    When the Engine sends the Protobuf message to the WebSocket server
    Then after 50 "milliseconds"
    Then the client "01" should receive 1 "walkers-coordinates" message from the Engine
    Then the client "02" should receive 1 "walkers-coordinates" message from the Engine