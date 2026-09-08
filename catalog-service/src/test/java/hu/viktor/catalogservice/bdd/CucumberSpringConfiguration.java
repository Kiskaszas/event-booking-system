package hu.viktor.catalogservice.bdd;

import hu.viktor.catalogservice.port.EventRepository;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @MockBean
    private EventRepository eventRepository;
}