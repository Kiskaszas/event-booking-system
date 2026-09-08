Feature: Order Management

  Scenario: Helyes adatokkal történő rendelés leadása
    Given A rendszer fut és az adatbázis tiszta
    When Beküldök egy rendelést a "teszt-event-123" eseményre "viktor@test.com" email címmel és 2 darab jegyre
    Then A válasz státuszkódja 201 Created kell legyen
    And A rendelés megjelenik a PostgreSQL adatbázisban a "CONFIRMED" státusszal
    And Egy értesítés megjelenik az AWS felhős üzenetsorban