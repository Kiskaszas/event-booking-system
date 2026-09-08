package hu.viktor.notificationservice.bdd;

import hu.viktor.notificationservice.dto.OrderCreatedEvent;
import hu.viktor.notificationservice.port.NotificationSender;
import hu.viktor.notificationservice.service.OrderNotificationService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class NotificationSteps {

    @Autowired
    private OrderNotificationService notificationService;

    @Autowired
    private NotificationSender mockSender;

    private OrderCreatedEvent event;

    @Given("a successful order event exists for {string} with {int} tickets for event {string}")
    public void a_successful_order_event_exists(String email, int quantity, String eventId) {
        event = new OrderCreatedEvent(100L, eventId, email, quantity);
    }

    @When("the notification service processes the event")
    public void the_notification_service_processes_the_event() {
        notificationService.processOrderNotification(event);
    }

    @Then("a confirmation email should be sent to {string} for event {string} with {int} tickets")
    public void a_confirmation_email_should_be_sent(String email, String eventId, int quantity) {
        verify(mockSender, times(1)).sendOrderConfirmation(email, eventId, quantity);
    }
}