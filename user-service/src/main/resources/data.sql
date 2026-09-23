INSERT INTO users (user_name, email, password_hash, role)
SELECT 'admin', 'admin@mediatech.fr', '$2a$10$LjRobspZ8z/FBF4AmvIuIeQFvuB2FWO8LtQOpHGZR2NOIw8oNAIL.', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');
