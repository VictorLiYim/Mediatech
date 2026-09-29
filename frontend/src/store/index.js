import { createStore } from 'vuex'
import auth from './modules/auth'
import catalog from './modules/catalog'
import library from './modules/library'
import notifications from './modules/notifications'
import theme from './modules/theme'
import { libraryPersistencePlugin } from './plugins/libraryPersistence'
import { themePersistencePlugin } from './plugins/themePersistence'

const store = createStore({
  modules: { auth, catalog, library, notifications, theme },
  plugins: [libraryPersistencePlugin, themePersistencePlugin],
  strict: import.meta.env.DEV,
})

const restoredUserId = store.getters['auth/currentUserId']
if (restoredUserId !== null) {
  store.dispatch('library/loadLibrary', restoredUserId)
}

export default store
