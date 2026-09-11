# Cloud-Native Event Booking System (Microservices Demo)

This is a demo application showcasing an event-driven microservice architecture, using Spring Boot 3 and LocalStack to simulate an AWS cloud environment (S3, DynamoDB, SNS, SQS) locally. The system strictly follows the principles of **SOLID**, **Clean Code**, and **Hexagonal Architecture (Ports and Adapters)**.

## 🏛️ Architecture

The system lives in a single monorepo and consists of three main microservices:

1. **Catalog Service (Port 8080):**
   - Manages events and posters.
   - Its internal structure isolates domain logic from infrastructure.
   - **AWS S3:** Image upload and storage.
   - **AWS DynamoDB:** Fast NoSQL storage for event entities.
2. **Order Service (Port 8082):**
   - Handles ticket purchase transactions.
   - **PostgreSQL:** Transactional database for orders. Uses a built-in **Transactional Outbox** pattern to avoid the Dual-Write problem.
   - **AWS SNS:** On a successful order, publishes a pub/sub message to the event bus via a background poller.
3. **Notification Service (Port 8083):**
   - A background worker responsible for dispatched notifications.
   - **AWS SQS:** Subscribes to the SNS topic and processes incoming messages asynchronously. Uses robust (fail-fast) error handling to take advantage of SQS's retry and DLQ mechanisms.

## 🚀 Technology Stack
- **Java 17**
- **Spring Boot 3.2.x** (Spring Web, Spring Data JPA, Validation)
- **Spring Cloud AWS 3.1.x**
- **Docker & Docker Compose**
- **LocalStack** (AWS cloud simulation)
- **PostgreSQL**

## 🛠️ Running Locally (Local Environment)

### 1. Starting the infrastructure
Run Docker Compose from the project root, which starts LocalStack (and automatically provisions the cloud resources via the `setup-aws.sh` script), as well as the PostgreSQL database.

```bash
docker compose up -d
```

### Stopping the running containers
```bash
docker-compose down
```

### Continuously following the container logs (e.g. for LocalStack debugging)
```bash
docker-compose logs -f
```