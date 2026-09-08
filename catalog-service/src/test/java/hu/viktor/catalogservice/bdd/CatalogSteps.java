package hu.viktor.catalogservice.bdd;

import hu.viktor.catalogservice.dto.CreateEventRequest;
import hu.viktor.catalogservice.model.Event;
import hu.viktor.catalogservice.port.EventRepository;
import hu.viktor.catalogservice.service.EventService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CatalogSteps {

    private final EventRepository mockRepository = mock(EventRepository.class);
    private final EventService eventService = new EventService(mockRepository);

    private CreateEventRequest request;
    private Event savedEvent;

    @Given("the admin wants to create a {string} on {string} with {int} available seats")
    public void the_admin_wants_to_create_an_event(String title, String date, int seats) {
        request = new CreateEventRequest(title, LocalDateTime.parse(date).toString(), seats);

        when(mockRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event eventToSave = invocation.getArgument(0);
            return eventToSave;
        });
    }

    @When("the event is registered in the catalog")
    public void the_event_is_registered_in_the_catalog() {
        savedEvent = eventService.createEvent(request);
    }

    @Then("the catalog should contain the event {string} with {int} seats")
    public void the_catalog_should_contain_the_event(String expectedTitle, int expectedSeats) {
        assertNotNull(savedEvent);
        assertEquals(expectedTitle, savedEvent.getTitle());
        assertEquals(expectedSeats, savedEvent.getAvailableSeats());
    }

    @Then("the event repository should have saved the entity")
    public void the_event_repository_should_have_saved_the_entity() {
        verify(mockRepository, times(1)).save(any(Event.class));
    }
}