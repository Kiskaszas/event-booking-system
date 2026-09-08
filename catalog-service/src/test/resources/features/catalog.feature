Feature: Event Catalog Management

  Scenario: Creating a new event in the catalog
    Given the admin wants to create a "Java Spring Boot Workshop" on "2026-10-15T18:00:00" with 10 available seats
    When the event is registered in the catalog
    Then the catalog should contain the event "Java Spring Boot Workshop" with 10 seats
    And the event repository should have saved the entity