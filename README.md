# Cloud-Native Event Booking System (Microservices Demo)

Ez egy eseményvezérelt mikroszerviz architektúrát bemutató demó alkalmazás, amely a Spring Boot 3 és a LocalStack segítségével szimulál egy AWS felhőkörnyezetet (S3, DynamoDB, SNS, SQS) lokálisan. A rendszer szigorúan követi a **SOLID**, a **Clean Code**, valamint a **Hexagonális Architektúra (Ports and Adapters)** elveit.

## 🏛️ Architektúra

A rendszer egy monorepóban kapott helyet, és három fő mikroszervizből áll:

1. **Catalog Service (Port 8080):**
   - Kezeli az eseményeket és a posztereket.
   - Belső felépítése izolálja a domain logikát az infrastruktúrától.
   - **AWS S3:** Képfeltöltés és tárolás.
   - **AWS DynamoDB:** Esemény entitások gyors NoSQL tárolása.
2. **Order Service (Port 8082):**
   - Fogadja a jegyvásárlási tranzakciókat.
   - **PostgreSQL:** Tranzakcionális adatbázis a rendelésekhez. Beépített **Transactional Outbox** mintát használ a Dual-Write probléma elkerülésére.
   - **AWS SNS:** Sikeres rendelés esetén pub/sub üzenetet küld az eseménybuszra egy háttérben futó poller segítségével.
3. **Notification Service (Port 8083):**
   - Háttérfolyamat (worker), amely a kiküldött értesítésekért felel.
   - **AWS SQS:** Feliratkozik az SNS topikra és aszinkron módon feldolgozza a bejövő üzeneteket. Robusztus (fail-fast) hibakezelést használ az SQS újrapróbálási és DLQ mechanizmusainak kihasználásához.

## 🚀 Technológiai Stack
- **Java 17**
- **Spring Boot 3.2.x** (Spring Web, Spring Data JPA, Validation)
- **Spring Cloud AWS 3.1.x**
- **Docker & Docker Compose**
- **LocalStack** (AWS felhő szimuláció)
- **PostgreSQL**

## 🛠️ Futtatás Helyben (Local Environment)

### 1. Infrastruktúra indítása
A projekt gyökerében futtasd a Docker Compose-t, amely elindítja a LocalStack-et (és automatikusan létrehozza a felhős erőforrásokat a `setup-aws.sh` szkripttel), valamint a PostgreSQL adatbázist.

```bash
docker compose up -d
````

### Futó konténerek leállítása
```bash
docker-compose down
```

### Konténerek logjainak folyamatos követése (pl. LocalStack hibakereséshez)
```bash
docker-compose logs -f
```