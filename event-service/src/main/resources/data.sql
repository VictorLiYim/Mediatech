INSERT INTO events (title, description, event_date, event_type, author_id, capacity)
SELECT 'Lecture publique : Les Misérables', 'Lecture à voix haute d''extraits choisis, suivie d''un échange.', TIMESTAMP '2026-10-10 18:00:00', 'LECTURE', 3, 40
WHERE NOT EXISTS (SELECT 1 FROM events WHERE title = 'Lecture publique : Les Misérables');

INSERT INTO events (title, description, event_date, event_type, author_id, capacity)
SELECT 'Projection : Dune', 'Projection du film adapté du roman de Frank Herbert.', TIMESTAMP '2026-10-17 20:00:00', 'FILM_PROJECTION', 4, 60
WHERE NOT EXISTS (SELECT 1 FROM events WHERE title = 'Projection : Dune');

INSERT INTO events (title, description, event_date, event_type, author_id, capacity)
SELECT 'Séance de dédicace : Persepolis', 'Rencontre et dédicace autour de la bande dessinée Persepolis.', TIMESTAMP '2026-11-05 15:00:00', 'DEDICATION', 11, 25
WHERE NOT EXISTS (SELECT 1 FROM events WHERE title = 'Séance de dédicace : Persepolis');

INSERT INTO events (title, description, event_date, event_type, author_id, capacity)
SELECT 'Atelier jeunesse : Jules Verne', 'Lecture et atelier dessin autour de Vingt mille lieues sous les mers.', TIMESTAMP '2026-11-19 14:30:00', 'LECTURE', 7, 20
WHERE NOT EXISTS (SELECT 1 FROM events WHERE title = 'Atelier jeunesse : Jules Verne');
