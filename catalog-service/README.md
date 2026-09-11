# Catalog Service

The catalog microservice of the Event Booking System. Responsible for storing and querying event (concerts, performances) metadata, as well as managing the images (posters) associated with events.

## 🚀 Technology Stack
* **Java 17**
* **Spring Boot 3.2.5** (Web, Validation)
* **Spring Cloud AWS 3.1.1**
* **Lombok**

## ☁️ Cloud (LocalStack) Integrations
* **AWS DynamoDB:** The `event` table provides fast, NoSQL-based storage of event data.
* **AWS S3:** The `event-posters` bucket is responsible for storing and serving uploaded posters (images).

## 📡 API Endpoints

### 1. Creating a new event
**POST** `http://localhost:8080/api/events`

*Request Body:*
```json
{
  "title": "Java Spring Boot Workshop",
  "date": "2026-10-15T18:00:00",
  "availableSeats": 10
}
```