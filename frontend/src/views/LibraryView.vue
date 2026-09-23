<script setup>
import { computed, ref } from 'vue'
import { useStore } from 'vuex'
import { mdiBookshelf, mdiFilterRemoveOutline } from '@mdi/js'
import EmptyState from '@/components/EmptyState.vue'
import LibraryItemCard from '@/components/LibraryItemCard.vue'

const store = useStore()

const STATUS_FILTERS = [
  { value: 'all', label: 'Tous', getter: 'library/allItems' },
  { value: 'unread', label: 'À lire', getter: 'library/unreadItems' },
  { value: 'read', label: 'Lus', getter: 'library/readItems' },
  { value: 'favorites', label: 'Favoris', getter: 'library/favoriteItems' },
]

const activeFilter = ref('all')
const textFilter = ref('')

const totalCount = computed(() => store.getters['library/itemCount'])
const readCount = computed(() => store.getters['library/readItems'].length)

function countFor(filter) {
  return store.getters[filter.getter].length
}

const displayedItems = computed(() => {
  const filter = STATUS_FILTERS.find((entry) => entry.value === activeFilter.value)
  const text = textFilter.value.trim().toLowerCase()
  return store.getters[filter.getter].filter((item) => !text
    || item.title.toLowerCase().includes(text)
    || item.authors.some((author) => author.toLowerCase().includes(text)))
})
</script>

<template>
  <div class="page-container library-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Ma bibliothèque</h1>
        <p class="page-subtitle">{{ totalCount }} livre(s) · {{ readCount }} lu(s)</p>
      </div>
      <RouterLink :to="{ name: 'search' }" class="button button--primary">Ajouter des livres</RouterLink>
    </header>

    <EmptyState
      v-if="totalCount === 0"
      :icon-path="mdiBookshelf"
      title="Votre bibliothèque est vide"
      message="Recherchez un livre puis cliquez sur « Ajouter à ma bibliothèque »."
    >
      <RouterLink :to="{ name: 'search' }" class="button button--primary">Rechercher un livre</RouterLink>
      <RouterLink :to="{ name: 'catalog' }" class="button button--secondary">Parcourir le catalogue</RouterLink>
    </EmptyState>

    <template v-else>
      <div class="library-view__toolbar">
        <div class="library-view__filter-tabs" role="tablist" aria-label="Filtrer par statut">
          <button
            v-for="filter in STATUS_FILTERS"
            :key="filter.value"
            type="button"
            role="tab"
            class="library-view__filter-tab"
            :class="{ 'library-view__filter-tab--active': activeFilter === filter.value }"
            :aria-selected="activeFilter === filter.value"
            @click="activeFilter = filter.value"
          >
            {{ filter.label }}
            <span class="library-view__filter-count">{{ countFor(filter) }}</span>
          </button>
        </div>
        <input
          v-model="textFilter"
          class="form-input library-view__text-filter"
          type="search"
          placeholder="Filtrer par titre ou auteur"
          aria-label="Filtrer par titre ou auteur"
        >
      </div>

      <EmptyState
        v-if="displayedItems.length === 0"
        :icon-path="mdiFilterRemoveOutline"
        title="Aucun livre dans cette sélection"
      />
      <div v-else class="book-grid">
        <LibraryItemCard v-for="item in displayedItems" :key="item.id" :item="item" />
      </div>
    </template>
  </div>
</template>

<style scoped>
.library-view__toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.library-view__filter-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 4px;
  border-radius: var(--radius-pill);
  background: var(--color-surface);
  border: 1px solid var(--color-surface-border);
  backdrop-filter: var(--blur-glass);
  -webkit-backdrop-filter: var(--blur-glass);
}

.library-view__filter-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: none;
  border-radius: var(--radius-pill);
  background: transparent;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--transition-fast);
}

.library-view__filter-tab:hover {
  background: rgba(255, 255, 255, 0.5);
}

.library-view__filter-tab--active {
  background: var(--color-primary);
  color: var(--color-text-on-dark);
}

.library-view__filter-tab--active:hover {
  background: var(--color-primary-hover);
}

.library-view__filter-count {
  font-size: 0.78rem;
  opacity: 0.8;
}

.library-view__text-filter {
  max-width: 280px;
}

@media (max-width: 600px) {
  .library-view__text-filter {
    max-width: none;
  }
}
</style>
