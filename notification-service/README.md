# Notification Service

The asynchronous event processor (worker) of the Ticket Booking System. It has no public REST API; instead, it runs as a background process, subscribing to the message queue and reacting to incoming orders (for example, by sending a confirmation email).

## 🚀 Technology Stack
* **Java 17**
* **Spring Boot 3.2.5**
* **Spring Cloud AWS 3.1.1**
* **Jackson (ObjectMapper)**

## ☁️ Cloud (LocalStack) Integrations
* **AWS SQS (Simple Queue Service):** The service continuously reads the `notification-queue` queue using `@SqsListener`. The queue is wired to the central SNS topic in "Raw Message Delivery" mode, so the service processes the plain JSON payload directly.

## ⚙️ How It Works
As soon as the Order Service publishes a new order to SNS, the message is delivered to the SQS queue. The Notification Service reads it, converts the raw JSON into a Java record (`OrderCreatedEvent`) using its own `ObjectMapper` logic, and then simulates the processing and notification flow by logging it to the console.

## 🛠️ Running
The service runs on port `8083`. It requires the SNS and SQS network running in LocalStack to function. It has no relational or NoSQL database dependency.