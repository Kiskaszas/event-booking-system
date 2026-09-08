package hu.viktor.orderservice.bdd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class TestEnvironmentConfig {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Container
    static LocalStackContainer localStack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.0"))
            .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS);

    static {
        postgres.start();
        localStack.start();
        setupAwsResources();
    }

    private static void setupAwsResources() {
        try {
            // Létrehozzuk a topikot a LocalStack konténerben a teszt futása előtt
            localStack.execInContainer("awslocal", "sns", "create-topic", "--name", "order-events-topic");
            localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "test-notification-queue");

            String topicArn = "arn:aws:sns:us-east-1:000000000000:order-events-topic";
            String queueArn = "arn:aws:sqs:us-east-1:000000000000:test-notification-queue";

            localStack.execInContainer("awslocal", "sns", "subscribe",
                    "--topic-arn", topicArn,
                    "--protocol", "sqs",
                    "--notification-endpoint", queueArn,
                    "--attributes", "RawMessageDelivery=true");
        } catch (Exception e) {
            throw new RuntimeException("Hiba az AWS teszt erőforrások létrehozásakor", e);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.cloud.aws.region.static", localStack::getRegion);
        registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.sns.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.SNS).toString());
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());

        registry.add("order.sns.topic-arn", () -> "arn:aws:sns:us-east-1:000000000000:order-events-topic");
    }
}