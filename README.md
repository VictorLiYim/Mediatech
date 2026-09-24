# Mediatech

Projet réalisé dans le cadre du cours **ST2OOS (OO Systems Development)**, Efrei par Mathis BUCLON - Ayuna CHAMPAIN - Victor LI YIM

Application de gestion de médiathèque côté usager : catalogue de livres, emprunts, notes/avis, événements (dédicaces, lectures, projections) et inscriptions.

> Ce README couvre uniquement le **backend** (3 microservices Spring Boot + infrastructure). Le frontend Vue.js dispose de son propre `frontend/README.md`.

---

## Architecture

Trois microservices Spring Boot indépendants, chacun avec sa propre base de données, communiquant entre eux en **gRPC** et exposant chacun une **API REST** consommée par le frontend via un reverse proxy unique.

```
                         ┌─────────────────────────┐
   Navigateur  ───────▶  │   httpd (port 80)       │  ← point d'entrée unique
                         │   sert le front Vue.js   │
                         │   + reverse proxy /api/* │
                         └───────────┬─────────────┘
                                     │ HTTP interne (réseau docker)
              ┌──────────────────────┼──────────────────────┐
              ▼                      ▼                      ▼
     ┌─────────────────┐   ┌──────────────────────┐   ┌──────────────────┐
     │  user-service    │   │  mediatheque-service │   │  event-service    │
     │  REST : 8083     │◀──│  REST : 8081         │◀──│  REST : 8082      │
     │  gRPC  : 9091    │   │  gRPC  : 9090        │   │  (client gRPC     │
     │                  │   │  (client + serveur)   │   │   uniquement)     │
     └────────┬─────────┘   └──────────┬───────────┘   └────────┬──────────┘
              │                        │                         │
              └────────────────────────┼─────────────────────────┘
                                        ▼
                               ┌─────────────────┐
                               │  MariaDB (1      │
                               │  conteneur,       │
                               │  3 bases isolées) │
                               │  jamais exposée    │
                               │  hors du réseau    │
                               │  docker interne    │
                               └─────────────────┘
```

### Répartition des responsabilités

| Service | Domaine | Base | Port REST | Port gRPC |
|---|---|---|---|---|
| `user-service` | Comptes utilisateurs, authentification | `user_db` | 8083 | 9091 (serveur) |
| `mediatheque-service` | Catalogue (livres, auteurs), emprunts, avis | `mediatheque_db` | 8081 | 9090 (serveur) + client vers `user-service` |
| `event-service` | Événements, inscriptions | `event_db` | 8082 | client vers `user-service` et `mediatheque-service` |

### Pourquoi gRPC entre les services

Chaque service est propriétaire exclusif de sa base ce qui nous permet d'avoir aucune requête SQL croisée entre `mediatheque_db`, `event_db` et `user_db`.
Lorsqu'un un service a besoin d'une donnée qui vit dans un autre service (ex : `event-service` doit vérifier qu'un `authorId` existe avant de créer une dédicace), 
il passe par un appel gRPC plutôt que par un accès direct à une base qui ne lui appartient pas.

```
Exemple :
event-service ──gRPC──▶ mediatheque-service : AuthorInfoService.getAuthor(authorId)
event-service ──gRPC──▶ user-service        : UserInfoService.getUser(userId)
mediatheque-service ──gRPC──▶ user-service  : UserInfoService.getUser(userId)
```

Les contrats sont définis dans des fichiers `.proto` (un par domaine : `user.proto`, `author.proto`, `book.proto`), dupliqués dans chaque service qui en a besoin (pas de module Gradle partagé, volontairement, pour garder "un dossier par service" strict). 
Bibliothèque utilisée : **Spring gRPC** (`org.springframework.grpc`)

---

## Couches et conventions (communes aux 3 services)

Chaque service suit strictement :

```
com.<service>/
├── models/         entités JPA
├── repositories/    interfaces Spring Data JPA
├── services/        interface + implémentation (IoC — injection par constructeur)
├── controllers/      REST, aucune logique métier
├── dto/             records d'entrée (Create...Request) / sortie (...Response)
├── exceptions/       exceptions métier + GlobalExceptionHandler
├── grpc/             clients et/ou serveur gRPC (selon le service)
└── config/           CorsConfig
```

- **IoC** : injection par constructeur exclusivement
- **Logger** : SLF4J partout, aucun `System.out.println`.
- **Exceptions** : chaque service a son propre jeu d'exceptions métier (`BookNotFoundException`, `EventFullException`, `UserAlreadyExistsException`, etc.) 
traduites en réponses HTTP par un `GlobalExceptionHandler` dédié à chaque service.
- **Repositories → Optional**, traduit en exception métier au niveau service, jamais au niveau controller.

---

## API REST

### `user-service` (8083)
```
POST   /users                 # inscription
POST   /users/login            # connexion
GET    /users/{id}
```

### `mediatheque-service` (8081)
```
POST   /authors                 GET /authors                 GET /authors/{id}
POST   /books                   GET /books                   GET /books/{id}
PUT    /books/{id}/borrow        PUT /books/{id}/return        GET /books/loans?userId=
POST   /books/{id}/reviews       GET /books/{id}/reviews
```

### `event-service` (8082)
```
POST   /events                  GET /events                  GET /events/{id}
POST   /events/{id}/register     GET /events/registrations?userId=
```

Via le reverse proxy `httpd`, ces routes sont accessibles depuis le frontend sous `/api/users`, `/api/books`, `/api/authors`, `/api/events` (voir `docker/httpd/httpd.conf`).

---

## Lancer le projet

### Ne pas oublier

Créer un fichier .env à la source en partant du **.env.example** comme example et changer les valeurs.

### Docker (MariaDB persistante, tout le projet d'un coup)
> **Note pour les Mac Apple Silicon** : les Dockerfiles utilisent `eclipse-temurin:17-jdk-jammy` / `17-jre-jammy` plutôt que les variantes `-alpine`.
> Les tags `-alpine` d'Eclipse Temurin ne publient qu'une image `amd64`, jamais `arm64`.
> Pour ceux qui souhaitent une image plus légère dans chaque docker file de chaque service il faut remplacer jammy par alpine (3 changements à faire)
```bash
docker compose -f docker/docker-compose.yml up --build
```
Une fois le téléchargement terminé rendez vous sur le l'adresse : `http://localhost`

Pour tout réinitialiser (recréer les bases à zéro) :
```bash
docker compose -f docker/docker-compose.yml down -v
```
