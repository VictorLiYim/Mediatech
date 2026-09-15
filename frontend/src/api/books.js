import { bookApi } from "./client";

/**
 * GET /books — liste du catalogue.
 */
export async function getAllBooks() {
    const { data } = await bookApi.get("/books");
    return data;
}

/**
 * GET /books/{id} — détail d'un livre.
 */
export async function getBook(id) {
    const { data } = await bookApi.get(`/books/${id}`);
    return data;
}

/**
 * PUT /books/{id}/borrow — emprunter un exemplaire.
 */
export async function borrowBook(id, userId) {
    const { data } = await bookApi.put(`/books/${id}/borrow`, { userId });
    return data;
}

/**
 * PUT /books/{id}/return — rendre un exemplaire.
 */
export async function returnBook(id, userId) {
    const { data } = await bookApi.put(`/books/${id}/return`, { userId });
    return data;
}

/**
 * GET /books/loans?userId= — emprunts d'un utilisateur.
 */
export async function getLoansForUser(userId) {
    const { data } = await bookApi.get("/books/loans", { params: { userId } });
    return data;
}

/**
 * GET /books/{id}/reviews — reviews d'un livre.
 */
export async function getReviews(bookId) {
    const { data } = await bookApi.get(`/books/${bookId}/reviews`);
    return data;
}

/**
 * POST /books/{id}/reviews — poster une review.
 */
export async function postReview(bookId, { userId, rating, comment }) {
    const { data } = await bookApi.post(`/books/${bookId}/reviews`, { userId, rating, comment });
    return data;
}