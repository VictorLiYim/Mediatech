import { readJson } from '@/utils/storage'

export const libraryStorageKey = (userId) => `mediatech.library.${userId}`

export default {
  namespaced: true,

  state: () => ({
    ownerId: null,
    items: [],
  }),

  getters: {
    allItems: (state) => [...state.items].sort((a, b) => b.addedAt - a.addedAt),
    favoriteItems: (state, getters) => getters.allItems.filter((item) => item.favorite),
    readItems: (state, getters) => getters.allItems.filter((item) => item.read),
    unreadItems: (state, getters) => getters.allItems.filter((item) => !item.read),
    itemCount: (state) => state.items.length,
    isInLibrary: (state) => (itemId) => state.items.some((item) => item.id === itemId),
    itemById: (state) => (itemId) => state.items.find((item) => item.id === itemId) ?? null,
  },

  mutations: {
    SET_LIBRARY(state, { ownerId, items }) {
      state.ownerId = ownerId
      state.items = items
    },
    ADD_ITEM(state, item) {
      state.items.push(item)
    },
    REMOVE_ITEM(state, itemId) {
      state.items = state.items.filter((item) => item.id !== itemId)
    },
    TOGGLE_FAVORITE(state, itemId) {
      const item = state.items.find((entry) => entry.id === itemId)
      if (item) item.favorite = !item.favorite
    },
    TOGGLE_READ(state, itemId) {
      const item = state.items.find((entry) => entry.id === itemId)
      if (item) item.read = !item.read
    },
    RESET_LIBRARY(state) {
      state.ownerId = null
      state.items = []
    },
  },

  actions: {
    loadLibrary({ commit }, userId) {
      const items = readJson(libraryStorageKey(userId), [])
      commit('SET_LIBRARY', { ownerId: userId, items })
    },

    addItem({ commit, getters }, book) {
      if (getters.isInLibrary(book.id)) return
      commit('ADD_ITEM', {
        id: book.id,
        source: book.source,
        stockBookId: book.stockBookId ?? null,
        externalId: book.externalId ?? null,
        title: book.title,
        authors: book.authors,
        year: book.year ?? null,
        coverUrl: book.coverUrl ?? null,
        isbn: book.isbn ?? null,
        favorite: false,
        read: false,
        addedAt: Date.now(),
      })
    },

    removeItem({ commit }, itemId) {
      commit('REMOVE_ITEM', itemId)
    },

    toggleFavorite({ commit }, itemId) {
      commit('TOGGLE_FAVORITE', itemId)
    },

    toggleRead({ commit }, itemId) {
      commit('TOGGLE_READ', itemId)
    },
  },
}
