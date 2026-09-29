import { readJson } from '@/utils/storage'

export const THEME_STORAGE_KEY = 'mediatech.theme'

const THEMES = ['light', 'dark']

function systemTheme() {
  try {
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  } catch {
    return 'light'
  }
}

export default {
  namespaced: true,

  // Choix mémorisé, sinon le thème du système
  state: () => {
    const savedTheme = readJson(THEME_STORAGE_KEY)
    return { theme: THEMES.includes(savedTheme) ? savedTheme : systemTheme() }
  },

  getters: {
    isDark: (state) => state.theme === 'dark',
  },

  mutations: {
    SET_THEME(state, theme) {
      state.theme = theme
    },
  },

  actions: {
    toggleTheme({ commit, getters }) {
      commit('SET_THEME', getters.isDark ? 'light' : 'dark')
    },
  },
}
