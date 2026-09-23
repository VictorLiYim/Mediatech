#!/bin/bash
# Teste le flux complet de mediatheque-service.
# Lancez MediathequeServiceApplication (profil dev/H2) avant d'exécuter ce script.
set -e
BASE_URL="http://localhost:8081"

echo "1) Création d'un auteur"
AUTHOR_ID=$(curl -s -X POST "$BASE_URL/authors" \
  -H "Content-Type: application/json" \
  -d '{"name": "Victor Hugo", "bio": "Écrivain français du XIXe siècle", "userId": 1}' | tee /dev/stderr | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo -e "\n-> author id = $AUTHOR_ID\n"

echo "2) Création d'un livre avec 3 exemplaires"
BOOK_ID=$(curl -s -X POST "$BASE_URL/books" \
  -H "Content-Type: application/json" \
  -d "{\"title\": \"Les Misérables\", \"authorIds\": [$AUTHOR_ID], \"isbn\": \"978-2-07-040969-3\", \"type\": \"ROMAN\", \"description\": \"Une fresque sociale\", \"genres\": [\"CLASSIQUE\", \"HISTORIQUE\"], \"totalCopies\": 3, \"userId\": 1}" | tee /dev/stderr | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo -e "\n-> book id = $BOOK_ID\n"

echo "3) Vérification du catalogue (GET /books)"
curl -s "$BASE_URL/books" | python3 -m json.tool
echo ""

echo "4) Emprunt du livre par l'utilisateur 42"
curl -s -X PUT "$BASE_URL/books/$BOOK_ID/borrow" \
  -H "Content-Type: application/json" \
  -d '{"userId": 42}' | python3 -m json.tool
echo ""

echo "5) Vérification que availableCopies est passé de 3 à 2"
curl -s "$BASE_URL/books/$BOOK_ID" | python3 -m json.tool
echo ""

echo "6) Cas d'erreur volontaire : emprunter un livre inexistant (doit renvoyer 404)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" "$BASE_URL/books/99999"

echo "7) Poster une review"
curl -s -X POST "$BASE_URL/books/$BOOK_ID/reviews" \
  -H "Content-Type: application/json" \
  -d '{"userId": 42, "rating": 5, "comment": "Un chef-d'\''œuvre"}' | python3 -m json.tool
echo ""

echo "8) Vérification que averageRating est bien calculé (doit être 5.0)"
curl -s "$BASE_URL/books/$BOOK_ID" | python3 -m json.tool
echo ""

echo "9) Retour du livre par l'utilisateur 42"
curl -s -X PUT "$BASE_URL/books/$BOOK_ID/return" \
  -H "Content-Type: application/json" \
  -d '{"userId": 42}' | python3 -m json.tool
echo ""

echo "10) Cas d'erreur volontaire : retourner un livre non emprunté (doit renvoyer 409)"
curl -s -o /dev/null -w "HTTP %{http_code}\n" -X PUT "$BASE_URL/books/$BOOK_ID/return" \
  -H "Content-Type: application/json" -d '{"userId": 42}'

echo -e "\nTerminé. Si les étapes 1-9 renvoient du JSON cohérent et que les étapes 6 et 10 renvoient bien 404/409, l'API fonctionne comme prévu."
