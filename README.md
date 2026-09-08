# Event Booking System

Felhőnatív, eseményvezérelt mikroszolgáltatás-architektúra (microservices), amely Hexagonális (Ports and Adapters) tervezési mintára épül. A rendszer célja események (koncertek, előadások) böngészése, jegyek foglalása és aszinkron értesítések küldése.

## Architektúra és Szolgáltatások

A projekt egy monorepó, amely az alábbi független szolgáltatásokból áll:

*   **`api-gateway`**: Egységes belépési pont (Spring Cloud Gateway) a kliensek számára, amely a megfelelő szolgáltatásokhoz irányítja a REST kéréseket.
*   **`catalog-service`**: Felelős az események (Event) nyilvántartásáért, a szabad helyek kezeléséért, valamint a rendezvényekhez tartozó poszterképek S3-ban történő tárolásáért. (Adatbázis: Amazon DynamoDB).
*   **`order-service`**: Kezeli a jegyfoglalási folyamatokat, a tranzakciókat és a versenyhelyzeteket (race conditions). (Adatbázis: PostgreSQL).
*   **`notification-service`**: Eseményvezérelt szolgáltatás, amely az SQS/SNS üzenetsorokon keresztül beérkező sikeres foglalási eseményekre reagálva aszinkron értesítéseket küld a felhasználóknak.

## Technológiai Stack

*   **Nyelv & Keretrendszer**: Java 21, Spring Boot 3.2.5
*   **Architektúra minta**: Hexagonal Architecture (Ports and Adapters)
*   **Adatbázisok**: PostgreSQL, Amazon DynamoDB
*   **Cloud & Üzenetküldés**: Amazon S3, SQS, SNS (AWS LocalStack konténerrel emulálva)
*   **Tesztelés**: BDD (Cucumber), JUnit 5, Testcontainers, Awaitility
*   **Build eszköz & Egyéb**: Maven, Lombok, Docker Compose

## Lokális Fejlesztői Környezet Beállítása

### Előfeltételek
*   Java 21 JDK
*   Apache Maven
*   Docker és Docker Compose (a LocalStack és az adatbázisok futtatásához)

### Infrastruktúra indítása
A mikroszolgáltatások elindítása vagy a tesztek futtatása előtt fel kell húzni a háttérszolgáltatásokat (PostgreSQL, LocalStack) tartalmazó konténereket a projekt gyökérmappájából:

```bash
docker-compose up -d
```

### Futó konténerek leállítása
```bash
docker-compose down
```

### Konténerek logjainak folyamatos követése (pl. LocalStack hibakereséshez)
```bash
docker-compose logs -f
```