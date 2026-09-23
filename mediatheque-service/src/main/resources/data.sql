INSERT INTO authors (name, bio) SELECT 'Antoine de Saint-Exupéry', 'Aviateur et écrivain français.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Antoine de Saint-Exupéry');
INSERT INTO authors (name, bio) SELECT 'Albert Camus', 'Écrivain et philosophe français, prix Nobel 1957.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Albert Camus');
INSERT INTO authors (name, bio) SELECT 'Victor Hugo', 'Poète, dramaturge et romancier du XIXe siècle.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Victor Hugo');
INSERT INTO authors (name, bio) SELECT 'Frank Herbert', 'Auteur américain de science-fiction.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Frank Herbert');
INSERT INTO authors (name, bio) SELECT 'Gustave Flaubert', 'Romancier français, figure du réalisme.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Gustave Flaubert');
INSERT INTO authors (name, bio) SELECT 'Émile Zola', 'Chef de file du naturalisme.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Émile Zola');
INSERT INTO authors (name, bio) SELECT 'Jules Verne', 'Pionnier du roman d''aventures scientifiques.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Jules Verne');
INSERT INTO authors (name, bio) SELECT 'Umberto Eco', 'Sémiologue et romancier italien.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Umberto Eco');
INSERT INTO authors (name, bio) SELECT 'René Goscinny', 'Scénariste de bande dessinée.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'René Goscinny');
INSERT INTO authors (name, bio) SELECT 'Albert Uderzo', 'Dessinateur de bande dessinée.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Albert Uderzo');
INSERT INTO authors (name, bio) SELECT 'Marjane Satrapi', 'Autrice et dessinatrice franco-iranienne.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Marjane Satrapi');
INSERT INTO authors (name, bio) SELECT 'Charles Baudelaire', 'Poète français.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Charles Baudelaire');
INSERT INTO authors (name, bio) SELECT 'Yuval Noah Harari', 'Historien israélien.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Yuval Noah Harari');
INSERT INTO authors (name, bio) SELECT 'Stephen King', 'Maître américain du roman d''horreur.' WHERE NOT EXISTS (SELECT 1 FROM authors WHERE name = 'Stephen King');

INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Le Petit Prince', '9782950737656', 'ROMAN', 'Un aviateur échoué dans le désert rencontre un petit prince venu d''une autre planète.', 4, 4, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782950737656');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'L''Étranger', '9782761657419', 'ROMAN', 'Meursault, employé à Alger, commet un meurtre et semble indifférent à tout.', 3, 3, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782761657419');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Les Misérables', '9782848300443', 'ROMAN', 'Le destin de Jean Valjean, ancien forçat, dans la France du XIXe siècle.', 3, 3, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782848300443');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Notre-Dame de Paris', '9782080704412', 'ROMAN', 'Esmeralda, Quasimodo et Frollo dans le Paris du XVe siècle.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782080704412');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Dune', '9782266307307', 'ROMAN', 'Paul Atréides sur Arrakis, planète désertique convoitée pour son épice.', 5, 5, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782266307307');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Madame Bovary', '9782070403523', 'ROMAN', 'Emma Bovary, épouse d''un médecin de campagne, rêve d''une autre vie.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782070403523');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Germinal', '9782891330947', 'ROMAN', 'Une grève de mineurs dans le nord de la France.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782891330947');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Vingt mille lieues sous les mers', '9782090318098', 'ROMAN', 'Le professeur Aronnax à bord du Nautilus du capitaine Nemo.', 3, 3, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782090318098');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Le Nom de la rose', '9782246245124', 'ROMAN', 'Enquête sur des meurtres dans une abbaye bénédictine en 1327.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782246245124');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Astérix le Gaulois', '9782012101333', 'BD', 'Un village gaulois résiste encore et toujours à l''envahisseur.', 4, 4, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782012101333');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Persepolis', '9782844141040', 'BD', 'L''enfance et l''adolescence de Marjane en Iran.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782844141040');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Les Fleurs du mal', '9782070409044', 'POESIE', 'Recueil de poèmes entre spleen et idéal.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782070409044');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Sapiens', '9782226257017', 'ESSAI', 'Une brève histoire de l''humanité.', 3, 3, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782226257017');
INSERT INTO books (title, isbn, type, description, total_copies, available_copies, average_rating)
SELECT 'Shining', '9782709612012', 'ROMAN', 'Jack Torrance devient gardien d''un hôtel isolé pendant l''hiver.', 2, 2, 0 WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9782709612012');

INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782950737656' AND a.name = 'Antoine de Saint-Exupéry'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782950737656'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'AVENTURE' FROM books b WHERE b.isbn = '9782950737656'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'AVENTURE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782761657419' AND a.name = 'Albert Camus'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782761657419'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CONTEMPORAIN' FROM books b WHERE b.isbn = '9782761657419'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CONTEMPORAIN');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782848300443' AND a.name = 'Victor Hugo'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782848300443'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782848300443'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782080704412' AND a.name = 'Victor Hugo'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782080704412'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782080704412'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782266307307' AND a.name = 'Frank Herbert'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'SCIENCE_FICTION' FROM books b WHERE b.isbn = '9782266307307'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'SCIENCE_FICTION');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'AVENTURE' FROM books b WHERE b.isbn = '9782266307307'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'AVENTURE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782070403523' AND a.name = 'Gustave Flaubert'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782070403523'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782891330947' AND a.name = 'Émile Zola'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782891330947'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782891330947'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782090318098' AND a.name = 'Jules Verne'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'AVENTURE' FROM books b WHERE b.isbn = '9782090318098'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'AVENTURE');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'SCIENCE_FICTION' FROM books b WHERE b.isbn = '9782090318098'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'SCIENCE_FICTION');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782246245124' AND a.name = 'Umberto Eco'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'POLICIER' FROM books b WHERE b.isbn = '9782246245124'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'POLICIER');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782246245124'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782012101333' AND a.name = 'René Goscinny'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782012101333' AND a.name = 'Albert Uderzo'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'AVENTURE' FROM books b WHERE b.isbn = '9782012101333'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'AVENTURE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782844141040' AND a.name = 'Marjane Satrapi'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CONTEMPORAIN' FROM books b WHERE b.isbn = '9782844141040'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CONTEMPORAIN');
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782844141040'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782070409044' AND a.name = 'Charles Baudelaire'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'CLASSIQUE' FROM books b WHERE b.isbn = '9782070409044'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'CLASSIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782226257017' AND a.name = 'Yuval Noah Harari'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HISTORIQUE' FROM books b WHERE b.isbn = '9782226257017'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HISTORIQUE');
INSERT INTO book_authors (book_id, author_id)
SELECT b.id, a.id FROM books b, authors a WHERE b.isbn = '9782709612012' AND a.name = 'Stephen King'
AND NOT EXISTS (SELECT 1 FROM book_authors ba WHERE ba.book_id = b.id AND ba.author_id = a.id);
INSERT INTO book_genres (book_id, genre)
SELECT b.id, 'HORREUR' FROM books b WHERE b.isbn = '9782709612012'
AND NOT EXISTS (SELECT 1 FROM book_genres bg WHERE bg.book_id = b.id AND bg.genre = 'HORREUR');
