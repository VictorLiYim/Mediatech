import { writeJson } from '@/utils/storage'
import { libraryStorageKey } from '../modules/library'

export function libraryPersistencePlugin(store) {
  store.subscribe((mutation, state) => {
    if (!mutation.type.startsWith('library/')) return
    const { ownerId, items } = state.library
    if (ownerId !== null) writeJson(libraryStorageKey(ownerId), items)
  })
}
