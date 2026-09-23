import { login, register } from '@/api/usersApi'
import { readJson, removeKey, writeJson } from '@/utils/storage'

export const AUTH_STORAGE_KEY = 'mediatech.user'

export default {
  namespaced: true,

  state: () => ({
    currentUser: readJson(AUTH_STORAGE_KEY),
  }),

  getters: {
    isAuthenticated: (state) => state.currentUser !== null,
    isAdmin: (state) => state.currentUser?.role === 'ADMIN',
    currentUserId: (state) => state.currentUser?.id ?? null,
    userName: (state) => state.currentUser?.userName ?? '',
  },

  mutations: {
    SET_CURRENT_USER(state, user) {
      state.currentUser = user
    },
    CLEAR_CURRENT_USER(state) {
      state.currentUser = null
    },
  },

  actions: {
    async login({ commit, dispatch }, { userName, password }) {
      const user = await login(userName, password)
      commit('SET_CURRENT_USER', user)
      writeJson(AUTH_STORAGE_KEY, user)
      dispatch('library/loadLibrary', user.id, { root: true })
      return user
    },

    async register({ dispatch }, { userName, email, password }) {
      await register({ userName, email, password })
      return dispatch('login', { userName, password })
    },

    logout({ commit }) {
      commit('CLEAR_CURRENT_USER')
      removeKey(AUTH_STORAGE_KEY)
      commit('library/RESET_LIBRARY', null, { root: true })
    },
  },
}
