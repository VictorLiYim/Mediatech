import { writeJson } from '@/utils/storage'
import { THEME_STORAGE_KEY } from '../modules/theme'

// Applique le thème sur <html data-theme> et mémorise le choix de l'utilisateur
export function themePersistencePlugin(store) {
  const applyTheme = (theme) => {
    document.documentElement.dataset.theme = theme
  }

  applyTheme(store.state.theme.theme)
  store.subscribe((mutation, state) => {
    if (mutation.type !== 'theme/SET_THEME') return
    applyTheme(state.theme.theme)
    writeJson(THEME_STORAGE_KEY, state.theme.theme)
  })
}
