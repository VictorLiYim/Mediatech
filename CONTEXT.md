# Projet Médiathèque — Contexte pour Claude Code

## Situation

Projet de cours **ST2OOS "OO Systems Development"** (Efrei, prof. Benoît Charroux), en binôme, **deadline 27 septembre**, dépôt sur Moodle. Sujet libre mais devant respecter les contraintes techniques du cours (web service REST/RPC, accès base de données). Référence du prof : https://github.com/charroux/rentalservice

**Notre projet** : application de gestion de médiathèque côté usager :
- Créer un compte / se connecter
- Consulter le catalogue de livres
- Gérer ses emprunts (emprunter / rendre)
- Noter des livres, poster des commentaires / reviews sur les livres lus
- Consulter les événements de la médiathèque (dont séances de dédicaces) et s'y inscrire

## Stack choisie

- **Backend** : Java + Spring Boot (Gradle), Spring Data JPA
- **Frontend** : Vue.js 3 (Vite) + Vuetify + axios
- **BDD** : MariaDB via Docker (avec phpMyAdmin), une base par service
- **Inter-services** : gRPC (grpc-spring-boot-starter net.devh)
- Environnement : Arch Linux, IntelliJ IDEA, Git/GitHub en SSH

## Architecture — 3 microservices

**Règle : REST vers l'extérieur (navigateur), gRPC à l'intérieur (entre services).**

```
Vue.js SPA (5173)
   │ REST/JSON (axios)
   ├──────────> user-service (8083) ─────────────> MariaDB user_db
   │                  ▲ gRPC (9091) : UserInfoService
   │                  │
   ├──────────> mediatheque-service (8081) ──────> MariaDB mediatheque_db
   │                  ▲ │ gRPC (9090) : BookInfoService
   │                  │ └── client gRPC ──> user-service (vérifier un userId)
   └──────────> event-service (8082) ────────────> MariaDB event_db
                      └── client gRPC ──> mediatheque-service (infos livre)
                      └── client gRPC ──> user-service (vérifier un userId)
```

### Services

| Service | Responsabilités | Port HTTP | Port gRPC |
|---|---|---|---|
| `user-service` | Comptes utilisateurs, login | 8083 | 9091 (serveur) |
| `mediatheque-service` | Livres, emprunts, notes, reviews | 8081 | 9090 (serveur) + client vers user-service |
| `event-service` | Événements, dédicaces, inscriptions | 8082 | client vers mediatheque-service et user-service |
| `frontend` | SPA Vue.js | 5173 | — |

Chaque service a sa propre base : jamais d'accès SQL croisé, le gRPC est le seul canal entre services.

Cas d'usage gRPC :
- `mediatheque-service` → `user-service` : vérifier qu'un `userId` existe avant de créer un emprunt ou une review
- `event-service` → `user-service` : vérifier qu'un `userId` existe avant une inscription
- `event-service` → `mediatheque-service` : récupérer titre/auteur d'un livre lié à une dédicace

### Structure du repo (consigne : un dossier par service)

```
mediatheque-project/
├── user-service/
│   └── src/main/java/com/users/
│       ├── controllers/  services/  repositories/  models/  exceptions/
│       └── grpc/            # serveur gRPC (expose UserInfoService)
├── mediatheque-service/
│   └── src/main/java/com/mediatheque/
│       ├── controllers/  services/  repositories/  models/  exceptions/
│       └── grpc/            # serveur BookInfoService + client vers user-service
├── event-service/
│   └── src/main/java/com/events/
│       ├── controllers/  services/  repositories/  models/  exceptions/
│       └── grpc/            # clients vers mediatheque-service et user-service
├── frontend/                # Vue.js (src/views, src/components, src/api, src/router)
└── docker-compose.yml       # MariaDB + phpMyAdmin
```

## API REST prévue

```
# user-service (8083)
POST   /users                     # créer un compte
POST   /users/login               # vérifier identifiants → renvoie id + userName
GET    /users/{id}                # profil

# mediatheque-service (8081)
GET    /books                     # liste des livres
GET    /books/{id}                # détail
PUT    /books/{id}/borrow         # emprunter (body: userId)
PUT    /books/{id}/return         # rendre
GET    /users/{userId}/loans      # mes emprunts
POST   /books/{id}/reviews        # poster une review (note + commentaire, userId)
GET    /books/{id}/reviews        # lire les reviews

# event-service (8082)
GET    /events
GET    /events/{id}
POST   /events/{id}/register      # s'inscrire (body: userId)
```

## Contrats gRPC

```protobuf
// user.proto — servi par user-service
syntax = "proto3";

service UserInfoService {
  rpc getUser(UserRequest) returns (UserReply) {}
}

message UserRequest {
  string userId = 1;
}

message UserReply {
  bool exists = 1;
  string userName = 2;
}
```

```protobuf
// book.proto — servi par mediatheque-service
syntax = "proto3";

service BookInfoService {
  rpc getBook(BookRequest) returns (BookReply) {}
}

message BookRequest {
  string bookId = 1;
}

message BookReply {
  string title = 1;
  string author = 2;
}
```

## Contraintes de notation (IMPÉRATIF — le prof note là-dessus)

1. **Couches strictes** : `controller → service → repository`. Chaque couche ne parle qu'à celle directement en dessous. Aucun import de la couche web (Spring Web, ResponseEntity, etc.) dans les services ou repositories.
2. **IoC** : injection de dépendances **par constructeur** (pas de `@Autowired` sur champ).
3. **Logger** : SLF4J obligatoire, `System.out.println` interdit.
4. **Exceptions** : exceptions métier custom (`UserNotFoundException`, `BookNotFoundException`, `BookAlreadyBorrowedException`…) + handler global `@RestControllerAdvice` qui les traduit en codes HTTP avec messages clairs. Les repositories renvoient `Optional`, les services traduisent l'absence en exception métier.
5. **JPA** : éviter les mots réservés SQL dans les noms de colonnes (`begin`, `end`…) → `@Column(name = "begin_date")`.
6. **Git** : un dossier par service, commits propres.
7. Code en **anglais**, explications/commentaires de doc possibles en français.

## Frontend

- Créé avec `npm create vue@latest frontend` (Router: yes), Vuetify + `@mdi/font` installés.
- Pages prévues (`src/views/`) : `LoginView.vue` (login / création de compte), `BookList.vue` (catalogue en cartes avec `v-rating`), `BookDetail.vue` (détail + reviews + formulaire), `MyLoans.vue` (mes emprunts), `EventList.vue` (événements + inscription).
- Après login : utilisateur stocké dans un store Pinia (id + userName), envoyé dans les appels API qui en ont besoin.
- Appels API centralisés dans `src/api/` (axios, 3 `baseURL` : 8083, 8081, 8082).
- **CORS** à configurer sur les trois services Spring (autoriser `http://localhost:5173`).

## Sécurité (périmètre volontairement simple)

- Mots de passe hachés avec BCrypt côté `user-service` (jamais en clair).
- Pas de JWT/session : les consignes ne demandent aucune authentification avancée. Le front garde l'id utilisateur après login et le passe dans les requêtes. À mentionner dans le README comme simplification assumée (en production : API Gateway + tokens).

## État actuel

- Rien n'est encore codé pour ce projet. On part de zéro, mais je maîtrise déjà la stack : j'ai fait un TP rent-a-car (Spring Boot, JPA, H2 puis MariaDB, exceptions + `@RestControllerAdvice`) qui suit exactement les mêmes conventions.
- Docker MariaDB + phpMyAdmin déjà fonctionnels sur ma machine (phpMyAdmin occupe le port 8080, d'où les ports 8081/8082/8083 pour les services).

## Ordre de travail souhaité

1. `user-service` en REST (création de compte + login) — petit et vite bouclé
2. `mediatheque-service` complet en REST (entités `Book`, `Loan`, `Review` ; CRUD + emprunt/retour + reviews)
3. Frontend Vue branché dessus (login → catalogue → emprunts → reviews)
4. `event-service` (entités `Event`, `Registration`) en REST
5. gRPC : `UserInfoService`, puis `BookInfoService` et les clients
6. Docker-compose final + README

## Modèle de données

- `User` (user_db) : id, userName, email, passwordHash
- `Book` (mediatheque_db) : id, title, author, isbn, available (bool), averageRating
- `Loan` (mediatheque_db) : id, bookId, userId, borrow_date, due_date, return_date (nullable)
- `Review` (mediatheque_db) : id, bookId, userId, rating (1–5), comment, created_at
- `Event` (event_db) : id, title, description, event_date, type (SIGNING, WORKSHOP…), bookId (nullable, pour les dédicaces), capacity
- `Registration` (event_db) : id, eventId, userId, registered_at

Note : `userId`/`bookId` entre services sont de simples identifiants (pas de clés étrangères JPA croisées entre bases) — la cohérence est vérifiée via gRPC.
