Feature: Engine process flow

  Scenario: Walker awakes and starts moving
    Given the engine receives a client request to "enter dungeon: user1"
    Then the engine sends exactly the following messages to the client:
      | dungeon state: [to user1] placed user1      |
      | dungeon cell state: [to user1] placed user1 |
    When the engine receives a client request to "move: user1 east"
    Then the engine sends a "dungeon cell state: [to user1] user1 moved east" message to the client
    When the engine receives a client request to "move: user1 north"
    Then the engine sends a "dungeon cell state: [to user1] user1 moved north" message to the client
    When the engine receives a client request to "enter dungeon: user2"
    Then the engine sends at least the following messages to the client:
      | dungeon state: [to user2] placed user2      |
      | dungeon cell state: [to user1] placed user2 |
    When the engine receives a client request to "leave dungeon: user1"
    Then the engine sends a "dungeon cell state: [to user2] coord 4-2 left" message to the client
    When the engine receives a client request to "enter dungeon: user1" again
    Then the engine sends at least the following messages to the client:
      | dungeon state: [to user1] re placed user1      |
      | dungeon cell state: [to user2] re placed user1 |
    When the engine receives a client request to "move: user1 east"
    And the engine receives a client request to "move: user2 west"
    Then the engine sends exactly the following messages to the client:
      | dungeon cell state: [to user1] user1 further moved east |
      | dungeon cell state: [to user2] user1 further moved east |
      | dungeon cell state: [to user1] user2 moved west         |
      | dungeon cell state: [to user2] user2 moved west         |
    And It ends after 1 "second"
