import { writeJson } from '@/utils/storage'
import { librarySortStorageKey, libraryStorageKey } from '../modules/library'

export function libraryPersistencePlugin(store) {
  store.subscribe((mutation, state) => {
    if (!mutation.type.startsWith('library/')) return
    const { ownerId, items, sortKey } = state.library
    if (ownerId === null) return
    writeJson(libraryStorageKey(ownerId), items)
    writeJson(librarySortStorageKey(ownerId), sortKey)
  })
}
