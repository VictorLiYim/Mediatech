import { createAuthor, fetchAuthors } from '@/api/authorsApi'
import { createBook, fetchBooks } from '@/api/booksApi'
import { normalizeIsbn } from '@/utils/isbn'

export default {
  namespaced: true,

  state: () => ({
    books: [],
    isLoaded: false,
    isLoading: false,
  }),

  getters: {
    allBooks: (state) => state.books,
    bookById: (state) => (bookId) => state.books.find((book) => book.id === Number(bookId)) ?? null,

    booksByIsbn: (state) => {
      const index = new Map()
      state.books.forEach((book) => index.set(normalizeIsbn(book.isbn), book))
      return index
    },

    findStockBook: (state, getters) => (isbns = []) => {
      for (const isbn of isbns) {
        const match = getters.booksByIsbn.get(normalizeIsbn(isbn))
        if (match) return match
      }
      return null
    },

    latestBooks: (state) => [...state.books].sort((a, b) => b.id - a.id),
  },

  mutations: {
    SET_BOOKS(state, books) {
      state.books = books
      state.isLoaded = true
    },
    ADD_BOOK(state, book) {
      state.books.push(book)
    },
    UPDATE_BOOK(state, updatedBook) {
      const index = state.books.findIndex((book) => book.id === updatedBook.id)
      if (index !== -1) state.books.splice(index, 1, updatedBook)
    },
    SET_LOADING(state, isLoading) {
      state.isLoading = isLoading
    },
  },

  actions: {
    async fetchCatalog({ state, commit }, { force = false } = {}) {
      if (state.isLoaded && !force) return state.books
      commit('SET_LOADING', true)
      try {
        const books = await fetchBooks()
        commit('SET_BOOKS', books)
        return books
      } finally {
        commit('SET_LOADING', false)
      }
    },

    async addBookToStock({ commit, rootGetters }, { authorNames, ...book }) {
      const adminId = rootGetters['auth/currentUserId']
      const existingAuthors = await fetchAuthors()
      const authorIds = []
      for (const name of authorNames) {
        const existing = existingAuthors.find((author) => author.name.toLowerCase() === name.toLowerCase())
        const author = existing ?? (await createAuthor(name, adminId))
        authorIds.push(author.id)
      }
      const created = await createBook({ ...book, authorIds }, adminId)
      commit('ADD_BOOK', created)
      return created
    },
  },
}
