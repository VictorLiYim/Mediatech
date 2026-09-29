import { readJson } from '@/utils/storage'

export const libraryStorageKey = (userId) => `mediatech.library.${userId}`
export const librarySortStorageKey = (userId) => `mediatech.library.${userId}.sort`

export const DEFAULT_SORT_KEY = 'addedAt'

const byTitle = (a, b) => a.title.localeCompare(b.title, 'fr')

// Les livres sans année ou sans note passent en dernier
const SORT_COMPARATORS = {
  addedAt: (a, b) => b.addedAt - a.addedAt,
  title: byTitle,
  yearDesc: (a, b) => (b.year ?? -Infinity) - (a.year ?? -Infinity) || byTitle(a, b),
  yearAsc: (a, b) => (a.year ?? Infinity) - (b.year ?? Infinity) || byTitle(a, b),
  rating: (a, b) => (b.rating ?? 0) - (a.rating ?? 0) || byTitle(a, b),
}

function notify(dispatch, message, kind, undo = null) {
  dispatch('notifications/notify', { message, kind, undo }, { root: true })
}

export default {
  namespaced: true,

  state: () => ({
    ownerId: null,
    items: [],
    sortKey: DEFAULT_SORT_KEY,
  }),

  getters: {
    allItems: (state) => [...state.items].sort(SORT_COMPARATORS[state.sortKey] ?? SORT_COMPARATORS[DEFAULT_SORT_KEY]),
    favoriteItems: (state, getters) => getters.allItems.filter((item) => item.favorite),
    readItems: (state, getters) => getters.allItems.filter((item) => item.read),
    unreadItems: (state, getters) => getters.allItems.filter((item) => !item.read),
    itemCount: (state) => state.items.length,
    ratedItems: (state) => state.items.filter((item) => item.rating),
    // null tant qu'aucun livre n'est noté
    averageRating: (state, getters) => {
      const rated = getters.ratedItems
      if (rated.length === 0) return null
      return rated.reduce((sum, item) => sum + item.rating, 0) / rated.length
    },
    isInLibrary: (state) => (itemId) => state.items.some((item) => item.id === itemId),
    itemById: (state) => (itemId) => state.items.find((item) => item.id === itemId) ?? null,
  },

  mutations: {
    SET_LIBRARY(state, { ownerId, items, sortKey }) {
      state.ownerId = ownerId
      state.items = items
      state.sortKey = sortKey
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
    SET_RATING(state, { itemId, rating }) {
      const item = state.items.find((entry) => entry.id === itemId)
      if (item) item.rating = rating
    },
    SET_SORT_KEY(state, sortKey) {
      state.sortKey = sortKey
    },
    RESET_LIBRARY(state) {
      state.ownerId = null
      state.items = []
      state.sortKey = DEFAULT_SORT_KEY
    },
  },

  actions: {
    loadLibrary({ commit }, userId) {
      const items = readJson(libraryStorageKey(userId), [])
      const savedSortKey = readJson(librarySortStorageKey(userId))
      const sortKey = savedSortKey in SORT_COMPARATORS ? savedSortKey : DEFAULT_SORT_KEY
      commit('SET_LIBRARY', { ownerId: userId, items, sortKey })
    },

    addItem({ commit, dispatch, getters }, book) {
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
        rating: null,
        addedAt: Date.now(),
      })
      notify(dispatch, `« ${book.title} » ajouté à votre bibliothèque`, 'success')
    },

    removeItem({ commit, dispatch, getters, state }, itemId) {
      const removedItem = getters.itemById(itemId)
      if (!removedItem) return
      commit('REMOVE_ITEM', itemId)
      notify(dispatch, `« ${removedItem.title} » retiré de votre bibliothèque`, 'remove', {
        action: 'library/restoreItem',
        payload: { ownerId: state.ownerId, item: { ...removedItem } },
      })
    },

    // « Annuler » d'un retrait : remet le livre tel quel (note, lu, favori, date d'ajout)
    restoreItem({ commit, dispatch, getters, state }, { ownerId, item }) {
      if (state.ownerId !== ownerId || getters.isInLibrary(item.id)) return
      commit('ADD_ITEM', item)
      notify(dispatch, `« ${item.title} » remis dans votre bibliothèque`, 'success')
    },

    toggleFavorite({ commit, dispatch, getters }, itemId) {
      commit('TOGGLE_FAVORITE', itemId)
      const item = getters.itemById(itemId)
      if (!item) return
      if (item.favorite) notify(dispatch, `« ${item.title} » ajouté aux favoris`, 'favorite')
      else notify(dispatch, `« ${item.title} » retiré des favoris`, 'unfavorite')
    },

    toggleRead({ commit }, itemId) {
      commit('TOGGLE_READ', itemId)
    },

    // 0 = retirer la note
    setRating({ commit }, { itemId, rating }) {
      commit('SET_RATING', { itemId, rating: rating || null })
    },

    setSortKey({ commit }, sortKey) {
      if (sortKey in SORT_COMPARATORS) commit('SET_SORT_KEY', sortKey)
    },
  },
}
