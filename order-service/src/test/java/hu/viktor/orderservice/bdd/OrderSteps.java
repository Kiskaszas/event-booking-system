package hu.viktor.orderservice.bdd;

import hu.viktor.orderservice.model.Order;
import hu.viktor.orderservice.repository.OrderRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@CucumberContextConfiguration
public class OrderSteps extends TestEnvironmentConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private OrderRepository orderRepository;

    private Response response;
    private String testCustomerEmail;

    @Given("A rendszer fut és az adatbázis tiszta")
    public void setup() {
        RestAssured.port = port;
        orderRepository.deleteAll();
    }

    @When("Beküldök egy rendelést a {string} eseményre {string} email címmel és {int} darab jegyre")
    public void sendOrder(String eventId, String email, int quantity) {
        this.testCustomerEmail = email;
        String payload = String.format(
                "{\"eventId\": \"%s\", \"customerEmail\": \"%s\", \"quantity\": %d}",
                eventId, email, quantity);

        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/orders");
    }

    @Then("A válasz státuszkódja {int} Created kell legyen")
    public void verifyStatusCode(int expectedStatusCode) {
        response.then().statusCode(expectedStatusCode);
    }

    @And("A rendelés megjelenik a PostgreSQL adatbázisban a {string} státusszal")
    public void verifyDatabase(String expectedStatus) {
        List<Order> orders = orderRepository.findAll();
        Assertions.assertEquals(1, orders.size(), "Nem jött létre a rekord az adatbázisban!");

        Order savedOrder = orders.get(0);
        Assertions.assertEquals(testCustomerEmail, savedOrder.getCustomerEmail());
        Assertions.assertEquals(expectedStatus, savedOrder.getStatus());
    }

    @And("Egy értesítés megjelenik az AWS felhős üzenetsorban")
    public void verifySnsToSqsMessage() {
        // A várakozást 15 másodpercre emeltük a poller 5 másodperces késleltetése miatt
        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            try {
                org.testcontainers.containers.Container.ExecResult result = localStack.execInContainer(
                        "awslocal", "sqs", "receive-message",
                        "--queue-url", "http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/test-notification-queue",
                        "--wait-time-seconds", "3" // SQS Long Polling aktiválása
                );

                String stdout = result.getStdout();
                Assertions.assertTrue(stdout.contains(testCustomerEmail),
                        "Az üzenet nem érkezett meg a sorba! Log: " + stdout);
            } catch (Exception e) {
                Assertions.fail("Hiba az SQS ellenőrzésekor: " + e.getMessage());
            }
        });
    }
}