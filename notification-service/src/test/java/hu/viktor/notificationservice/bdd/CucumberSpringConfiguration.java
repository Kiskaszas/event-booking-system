package hu.viktor.notificationservice.bdd;

import hu.viktor.notificationservice.adapter.in.SqsOrderEventListener;
import hu.viktor.notificationservice.port.NotificationSender;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@CucumberContextConfiguration
@SpringBootTest
public class CucumberSpringConfiguration {

    @MockBean
    private NotificationSender notificationSender;

    @MockBean
    private SqsOrderEventListener sqsOrderEventListener;
}