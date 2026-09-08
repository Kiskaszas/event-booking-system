# Cloud-Native Event Booking System (Microservices Demo)

Ez egy eseményvezérelt mikroszerviz architektúrát bemutató demó alkalmazás, amely a Spring Boot 3 és a LocalStack segítségével szimulál egy AWS felhőkörnyezetet (S3, DynamoDB, SNS, SQS) lokálisan[cite: 33]. A rendszer szigorúan követi a **SOLID**, a **Clean Code**, valamint a **Hexagonális Architektúra (Ports and Adapters)** elveit.

## 🏛️ Architektúra

A rendszer egy monorepóban kapott helyet, és három fő mikroszervizből áll[cite: 33]:

1. **Catalog Service (Port 8080)[cite: 33]:**
   - Kezeli az eseményeket és a posztereket[cite: 33].
   - Belső felépítése izolálja a domain logikát az infrastruktúrától.
   - **AWS S3:** Képfeltöltés és tárolás[cite: 33].
   - **AWS DynamoDB:** Esemény entitások gyors NoSQL tárolása[cite: 33].
2. **Order Service (Port 8082)[cite: 33]:**
   - Fogadja a jegyvásárlási tranzakciókat[cite: 33].
   - **PostgreSQL:** Tranzakcionális adatbázis a rendelésekhez[cite: 33]. Beépített **Transactional Outbox** mintát használ a Dual-Write probléma elkerülésére.
   - **AWS SNS:** Sikeres rendelés esetén pub/sub üzenetet küld az eseménybuszra[cite: 33] egy háttérben futó poller segítségével.
3. **Notification Service (Port 8083)[cite: 33]:**
   - Háttérfolyamat (worker), amely a kiküldött értesítésekért felel[cite: 33].
   - **AWS SQS:** Feliratkozik az SNS topikra és aszinkron módon feldolgozza a bejövő üzeneteket[cite: 33]. Robusztus (fail-fast) hibakezelést használ az SQS újrapróbálási és DLQ mechanizmusainak kihasználásához.

## 🚀 Technológiai Stack
- **Java 17**[cite: 33]
- **Spring Boot 3.2.x** (Spring Web, Spring Data JPA, Validation)[cite: 33]
- **Spring Cloud AWS 3.1.x**[cite: 33]
- **Docker & Docker Compose**[cite: 33]
- **LocalStack** (AWS felhő szimuláció)[cite: 33]
- **PostgreSQL**[cite: 33]

## 🛠️ Futtatás Helyben (Local Environment)

### 1. Infrastruktúra indítása
A projekt gyökerében futtasd a Docker Compose-t, amely elindítja a LocalStack-et (és automatikusan létrehozza a felhős erőforrásokat a `setup-aws.sh` szkripttel), valamint a PostgreSQL adatbázist[cite: 33].

```bash
docker compose up -d