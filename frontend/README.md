# Médiatech — frontend

Application Vue.js de la médiathèque : recherche de livres via une API externe, bibliothèque personnelle
(lu / non lu, favoris), catalogue et emprunts de la médiathèque, événements.

## Stack

- **Vue 3** (`<script setup>`) + **Vite**
- **Vue Router** : pages et gardes de navigation (connexion, rôle admin)
- **Vuex 4** : modules `auth`, `catalog`, `library` (state, getters, mutations, actions)
- **localStorage** : session et bibliothèque personnelle (une par utilisateur)
- **axios** : appels au back et à Open Library
- CSS écrit à la main (`<style scoped>` + `src/assets/styles/`), sans framework UI

## API externe : Open Library

Documentation : https://openlibrary.org/developers/api (gratuite, sans clé).

| Usage | Endpoint |
|---|---|
| Recherche (titre, auteur, année, couverture, ISBN) | `GET https://openlibrary.org/search.json?q=…` |
| Résumé d'une œuvre | `GET https://openlibrary.org/works/{id}.json` |
| Couverture | `https://covers.openlibrary.org/b/id/{coverId}-M.jpg` ou `/b/isbn/{isbn}-M.jpg` |
| Année d'un livre du stock | `GET https://openlibrary.org/search.json?isbn=…` |

Un résultat de recherche est rapproché du stock de la médiathèque par son ISBN : s'il est en stock, il
peut être emprunté.

## Installation

Prérequis : Node.js 22.18+ (ou 24.12+), et les 3 services Spring Boot lancés (voir le README racine).

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173
```

En dev, Vite redirige `/api/users`, `/api/books`, `/api/authors` et `/api/events` vers les services locaux
(8083, 8081, 8082), comme le fait httpd en Docker.

`npm run build` produit la version de production dans `dist/`.

## Comptes

- Admin de démo : `admin` / `admin1234` (créé par `user-service/src/main/resources/data.sql`).
  Il peut ajouter des livres au stock (page **Stock** ou bouton « Ajouter au stock » dans la recherche).
- Les autres comptes se créent depuis la page d'inscription.

## Pages

| Route | Contenu |
|---|---|
| `/` | Accueil : recherche, nouveautés, prochains événements, prochains rendus |
| `/search` | Recherche Open Library → sélection d'un résultat → ajout à la bibliothèque |
| `/catalog`, `/catalog/:id` | Stock de la médiathèque, détail, emprunt, avis |
| `/library` | Bibliothèque personnelle : lu / non lu, favoris, suppression |
| `/favorites` | Favoris uniquement (getter Vuex `library/favoriteItems`) |
| `/loans` | Emprunts en cours et historique |
| `/events` | Événements, inscription / désinscription |
| `/profile` | Profil et statistiques |
| `/admin/stock` | Gestion du stock (admin) |

## Structure

```
src/
├── api/          # clients HTTP : back (usersApi, booksApi, authorsApi, eventsApi) et openLibraryApi
├── assets/styles # variables, styles de base, styles partagés
├── components/   # composants réutilisables (cartes, couverture, notation, modale…)
├── router/       # routes + gardes
├── store/        # Vuex : modules auth, catalog, library + plugin de persistance localStorage
├── utils/        # formatage des dates, libellés, ISBN, accès localStorage
└── views/        # une vue par page
```
