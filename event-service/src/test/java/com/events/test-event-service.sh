#!/bin/bash
# Teste le flux complet de event-service.
# Lancez EventServiceApplication (profil dev/H2, port 8082) avant d'exécuter ce script.
set -e
BASE_URL="http://localhost:8082"

# Date future (dans 7 jours), format attendu par LocalDateTime : yyyy-MM-ddTHH:mm:ss
EVENT_DATE=$(date -v+7d +"%Y-%m-%dT18:30:00")

echo "1) Création d'un événement de dédicace (capacité 2)"
EVENT_ID=$(curl -s -X POST "$BASE_URL/events" \
  -H "Content-Type: application/json" \
  -d "{\"title\": \"Dédicace Victor Hugo\", \"description\": \"Rencontre avec l'auteur\", \"eventDate\": \"$EVENT_DATE\", \"type\": \"DEDICATION\", \"authorId\": 1, \"capacity\": 2}" | tee /dev/stderr | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo -e "\n-> event id = $EVENT_ID\n"

echo "2) Vérification de la liste (GET /events)"
curl -s "$BASE_URL/events" | python3 -m json.tool
echo ""

echo "3) Détail de l'événement (GET /events/{id})"
curl -s "$BASE_URL/events/$EVENT_ID" | python3 -m json.tool
echo ""

echo "4) Inscription de l'utilisateur 100 (doit réussir, 1/2 places prises)"
curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" \
  -H "Content-Type: application/json" \
  -d '{"userId": 100}' | python3 -m json.tool
echo ""

echo "5) Cas d'erreur volontaire : l'utilisateur 100 s'inscrit une deuxième fois (doit renvoyer 409)"
curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" \
  -H "Content-Type: application/json" \
  -d '{"userId": 100}' | python3 -m json.tool
echo ""

echo "6) Inscription de l'utilisateur 200 (doit réussir, 2/2 places prises, événement complet)"
curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" \
  -H "Content-Type: application/json" \
  -d '{"userId": 200}' | python3 -m json.tool
echo ""

echo "7) Cas d'erreur volontaire : l'utilisateur 300 tente de s'inscrire alors que l'événement est complet (doit renvoyer 409)"
curl -s -X POST "$BASE_URL/events/$EVENT_ID/register" \
  -H "Content-Type: application/json" \
  -d '{"userId": 300}' | python3 -m json.tool
echo ""

echo "8) Vérification des inscriptions de l'utilisateur 100 (GET /events/registrations?userId=100)"
curl -s "$BASE_URL/events/registrations?userId=100" | python3 -m json.tool
echo ""

echo "9) Cas d'erreur volontaire : consulter un événement inexistant (doit renvoyer 404)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE_URL/events/99999"

echo "10) Cas d'erreur volontaire : s'inscrire à un événement inexistant (doit renvoyer 404)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" -X POST "$BASE_URL/events/99999/register" \
  -H "Content-Type: application/json" -d '{"userId": 400}'

echo -e "\nTerminé. Étapes attendues :"
echo "  1,2,3,4,6,8 -> JSON cohérent (200/201)"
echo "  5 -> 409 (déjà inscrit)"
echo "  7 -> 409 (événement complet)"
echo "  9,10 -> 404 (événement inexistant)"