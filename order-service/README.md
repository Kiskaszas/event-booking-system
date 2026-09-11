# Order Service

The transactional microservice of the Ticket Booking System. Responsible for recording user ticket purchases in a relational database, and then asynchronously publishing the fact of a successful order to the system's event bus. The service is built on **Hexagonal Architecture**.

## 🏛️ Key Design Patterns
* **Transactional Outbox:** The service avoids the "dual-write" problem. The order and the event to be dispatched are saved within a single atomic ACID transaction. A scheduled process (poller) reads the outbox table and forwards the messages to AWS, guaranteeing at-least-once delivery.
* **Ports and Adapters:** The core business logic (Service) is completely free of infrastructure dependencies (AWS, REST, database).

## 🚀 Technology Stack
* **Java 17**
* **Spring Boot 3.2.5** (Web, Data JPA, Validation)
* **Spring Cloud AWS 3.1.1**
* **PostgreSQL Driver**
* **Lombok**

## ☁️ Cloud and Database Integrations
* **PostgreSQL:** Stores orders in the `ticket_orders` table, and events still awaiting dispatch in the `outbox_events` table.
* **AWS SNS (Simple Notification Service):** Publishes successful transactions to the `order-events-topic` topic as an `OrderCreatedEvent` payload, so the rest of the system can react to it asynchronously.

## 📡 API Endpoint

### Placing an order
**POST** `/api/orders`

*Request Body:*
```json
{
  "eventId": "<uuid-received-from-catalog-service>",
  "customerEmail": "user@example.com",
  "quantity": 2
}
```

*Response*
A strictly filtered DTO (OrderResponse) that hides the internal state of the database entity.

```json
{
  "id": 1,
  "status": "CONFIRMED"
}
```