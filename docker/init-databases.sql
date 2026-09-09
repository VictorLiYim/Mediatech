-- Exécuté une seule fois, au tout premier démarrage du conteneur (volume vide).
-- MARIADB_DATABASE ne crée qu'une seule base ; ce script crée les deux autres
-- et donne les droits au même utilisateur applicatif sur les trois.

CREATE DATABASE IF NOT EXISTS mediatheque_db;
CREATE DATABASE IF NOT EXISTS event_db;
CREATE DATABASE IF NOT EXISTS user_db;

GRANT ALL PRIVILEGES ON mediatheque_db.* TO 'mediatheque'@'%';
GRANT ALL PRIVILEGES ON event_db.* TO 'mediatheque'@'%';
GRANT ALL PRIVILEGES ON user_db.* TO 'mediatheque'@'%';

FLUSH PRIVILEGES;