#!/usr/bin/env bash
#
# test-e2e.sh — Végigviszi a teljes event-booking-system folyamatot:
#   1) esemény létrehozása (catalog-service, 8080)
#   2) esemény visszaolvasása (ellenőrzés)
#   3) rendelés leadása (order-service, 8082)
#   4) várakozás az Outbox Relay + SNS/SQS láncra
#
# Előfeltétel:
#   - docker compose up -d  (LocalStack + Postgres fut)
#   - catalog-service, order-service, notification-service el van indítva
#     (pl. külön terminálokban: mvn spring-boot:run)
#
# Használat:
#   chmod +x test-e2e.sh
#   ./test-e2e.sh

set -euo pipefail

CATALOG_URL="${CATALOG_URL:-http://localhost:8080}"
ORDER_URL="${ORDER_URL:-http://localhost:8082}"
WAIT_SECONDS="${WAIT_SECONDS:-8}"

# --- színek a jobb olvashatóságért -----------------------------------------
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

step() { echo -e "\n${BLUE}==> $1${NC}"; }
ok()   { echo -e "${GREEN}✔ $1${NC}"; }
warn() { echo -e "${YELLOW}⚠ $1${NC}"; }
fail() { echo -e "${RED}✘ $1${NC}"; exit 1; }

# --- jq elérhetőség ellenőrzése (opcionális, de kényelmesebb) --------------
HAS_JQ=true
if ! command -v jq >/dev/null 2>&1; then
    HAS_JQ=false
    warn "jq nincs telepítve — nyers grep/sed-del dolgozunk. (brew install jq / apt install jq ajánlott)"
fi

extract_json_field() {
    # $1 = json string, $2 = mező neve
    local json="$1"
    local field="$2"
    if $HAS_JQ; then
        echo "$json" | jq -r ".$field"
    else
        echo "$json" | sed -n "s/.*\"$field\":\"\{0,1\}\([^\",}]*\)\"\{0,1\}.*/\1/p" | head -1
    fi
}

# --- 0) elérhetőség-ellenőrzés ----------------------------------------------
step "Szolgáltatások elérhetőségének ellenőrzése"
if ! curl -sf -o /dev/null "$CATALOG_URL/api/events/00000000-0000-0000-0000-000000000000" \
        -w "%{http_code}" 2>/dev/null; then
    :
fi
curl -s -o /dev/null -w "" "$CATALOG_URL" 2>/dev/null || warn "catalog-service ($CATALOG_URL) esetleg még nem fut."
curl -s -o /dev/null -w "" "$ORDER_URL" 2>/dev/null || warn "order-service ($ORDER_URL) esetleg még nem fut."

# --- 1) esemény létrehozása --------------------------------------------------
step "1) Esemény létrehozása (catalog-service)"
EVENT_PAYLOAD='{"title":"Teszt Koncert","date":"2026-12-01T20:00:00","availableSeats":100}'
echo "POST $CATALOG_URL/api/events"
echo "  body: $EVENT_PAYLOAD"

CREATE_EVENT_RESPONSE=$(curl -sS -X POST "$CATALOG_URL/api/events" \
    -H "Content-Type: application/json" \
    -d "$EVENT_PAYLOAD") || fail "Nem sikerült elérni a catalog-service-t. Fut az alkalmazás a $CATALOG_URL címen?"

echo "  válasz: $CREATE_EVENT_RESPONSE"

EVENT_ID=$(extract_json_field "$CREATE_EVENT_RESPONSE" "eventId")

if [ -z "${EVENT_ID:-}" ] || [ "$EVENT_ID" = "null" ]; then
    fail "Nem sikerült kiolvasni az eventId-t a válaszból. Nézd meg a fenti nyers választ."
fi
ok "Esemény létrehozva, eventId = $EVENT_ID"

# --- 2) esemény visszaolvasása (ellenőrzés) ---------------------------------
step "2) Esemény visszaolvasása (ellenőrzés)"
echo "GET $CATALOG_URL/api/events/$EVENT_ID"
GET_EVENT_RESPONSE=$(curl -sS "$CATALOG_URL/api/events/$EVENT_ID") \
    || fail "Nem sikerült visszaolvasni az eseményt."
echo "  válasz: $GET_EVENT_RESPONSE"
ok "Esemény sikeresen visszaolvasva DynamoDB-ből."

# --- 3) rendelés leadása -----------------------------------------------------
step "3) Rendelés (jegyvásárlás) leadása (order-service)"
ORDER_PAYLOAD=$(cat <<EOF
{"eventId":"$EVENT_ID","customerEmail":"teszt@pelda.hu","quantity":2}
EOF
)
echo "POST $ORDER_URL/api/orders"
echo "  body: $ORDER_PAYLOAD"

CREATE_ORDER_RESPONSE=$(curl -sS -X POST "$ORDER_URL/api/orders" \
    -H "Content-Type: application/json" \
    -d "$ORDER_PAYLOAD") || fail "Nem sikerült elérni az order-service-t. Fut az alkalmazás a $ORDER_URL címen?"

echo "  válasz: $CREATE_ORDER_RESPONSE"

ORDER_STATUS=$(extract_json_field "$CREATE_ORDER_RESPONSE" "status")
if [ "$ORDER_STATUS" = "CONFIRMED" ]; then
    ok "Rendelés létrehozva és megerősítve (status=CONFIRMED)."
else
    warn "A rendelés státusza nem CONFIRMED (kapott érték: '$ORDER_STATUS'). Nézd meg a nyers választ."
fi

# --- 4) várakozás az Outbox Relay + SNS/SQS láncra --------------------------
step "4) Várakozás az Outbox Relay -> SNS -> SQS -> notification-service láncra (${WAIT_SECONDS}s)"
for i in $(seq "$WAIT_SECONDS" -1 1); do
    printf "\r  hátralévő idő: %2ds" "$i"
    sleep 1
done
echo ""

echo ""
echo "-----------------------------------------------------------------------"
ok "Folyamat vége."
echo "Ellenőrizd manuálisan a notification-service konzol/log kimenetét —"
echo "pár másodperccel a rendelés után meg kell jelennie egy log sornak"
echo "(ConsoleEmailSender), ami az e-mail-küldést szimulálja az eventId=$EVENT_ID"
echo "és a customerEmail=teszt@pelda.hu adatokkal."
echo "-----------------------------------------------------------------------"
