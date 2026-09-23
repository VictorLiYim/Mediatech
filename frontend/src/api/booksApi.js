import { backendClient } from './client'

export async function fetchBooks() {
  const { data } = await backendClient.get('/books')
  return data
}

export async function fetchBook(bookId) {
  const { data } = await backendClient.get(`/books/${bookId}`)
  return data
}

export async function createBook(book, adminId) {
  const { data } = await backendClient.post('/books', { ...book, userId: adminId })
  return data
}

export async function borrowBook(bookId, userId) {
  const { data } = await backendClient.put(`/books/${bookId}/borrow`, { userId })
  return data
}

export async function returnBook(bookId, userId) {
  const { data } = await backendClient.put(`/books/${bookId}/return`, { userId })
  return data
}

export async function fetchLoans(userId) {
  const { data } = await backendClient.get('/books/loans', { params: { userId } })
  return data
}

export async function fetchReviews(bookId) {
  const { data } = await backendClient.get(`/books/${bookId}/reviews`)
  return data
}

export async function postReview(bookId, { userId, rating, comment }) {
  const { data } = await backendClient.post(`/books/${bookId}/reviews`, { userId, rating, comment })
  return data
}
