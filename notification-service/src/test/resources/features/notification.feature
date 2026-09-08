Feature: Order Notification Processing

  Scenario: Sending a confirmation email after a successful order
    Given a successful order event exists for "user@example.com" with 2 tickets for event "evt-123"
    When the notification service processes the event
    Then a confirmation email should be sent to "user@example.com" for event "evt-123" with 2 tickets