import { createStore } from 'vuex'
import auth from './modules/auth'
import catalog from './modules/catalog'
import library from './modules/library'
import { libraryPersistencePlugin } from './plugins/libraryPersistence'

const store = createStore({
  modules: { auth, catalog, library },
  plugins: [libraryPersistencePlugin],
  strict: import.meta.env.DEV,
})

const restoredUserId = store.getters['auth/currentUserId']
if (restoredUserId !== null) {
  store.dispatch('library/loadLibrary', restoredUserId)
}

export default store
