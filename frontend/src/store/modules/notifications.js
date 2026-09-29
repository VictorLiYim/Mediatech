const DEFAULT_DURATION = 4000
const UNDO_DURATION = 6000
const MAX_TOASTS = 3

let nextToastId = 1

export default {
  namespaced: true,

  state: () => ({
    toasts: [],
  }),

  getters: {
    toasts: (state) => state.toasts,
  },

  mutations: {
    ADD_TOAST(state, toast) {
      state.toasts.push(toast)
      if (state.toasts.length > MAX_TOASTS) state.toasts.shift()
    },
    REMOVE_TOAST(state, toastId) {
      state.toasts = state.toasts.filter((toast) => toast.id !== toastId)
    },
  },

  actions: {
    // kind : success | remove | favorite | unfavorite
    // undo : { action, payload } → action Vuex rejouée par le bouton « Annuler »
    notify({ commit }, { message, kind = 'success', undo = null }) {
      const id = nextToastId++
      commit('ADD_TOAST', { id, message, kind, undo })
      setTimeout(() => commit('REMOVE_TOAST', id), undo ? UNDO_DURATION : DEFAULT_DURATION)
    },

    dismiss({ commit }, toastId) {
      commit('REMOVE_TOAST', toastId)
    },

    async undo({ commit, dispatch }, toast) {
      commit('REMOVE_TOAST', toast.id)
      await dispatch(toast.undo.action, toast.undo.payload, { root: true })
    },
  },
}
