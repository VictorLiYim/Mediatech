<script setup>
import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { mdiBookshelf, mdiFilterRemoveOutline } from '@mdi/js'
import { extractErrorMessage } from '@/api/client'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import StockBookCard from '@/components/StockBookCard.vue'
import { BOOK_TYPE_LABELS, GENRE_LABELS } from '@/utils/labels'

const store = useStore()

const textFilter = ref('')
const typeFilter = ref('')
const genreFilter = ref('')
const onlyAvailable = ref(false)
const errorMessage = ref('')

const isLoading = computed(() => store.state.catalog.isLoading)
const books = computed(() => store.getters['catalog/allBooks'])

const filteredBooks = computed(() => {
  const text = textFilter.value.trim().toLowerCase()
  return books.value
    .filter((book) => !text
      || book.title.toLowerCase().includes(text)
      || book.authors.some((author) => author.name.toLowerCase().includes(text)))
    .filter((book) => !typeFilter.value || book.type === typeFilter.value)
    .filter((book) => !genreFilter.value || book.genres.includes(genreFilter.value))
    .filter((book) => !onlyAvailable.value || book.availableCopies > 0)
    .sort((a, b) => a.title.localeCompare(b.title, 'fr'))
})

function resetFilters() {
  textFilter.value = ''
  typeFilter.value = ''
  genreFilter.value = ''
  onlyAvailable.value = false
}

onMounted(async () => {
  try {
    await store.dispatch('catalog/fetchCatalog', { force: true })
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Impossible de charger le catalogue')
  }
})
</script>

<template>
  <div class="page-container catalog-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Catalogue de la médiathèque</h1>
        <p class="page-subtitle">{{ books.length }} livre(s) en stock, empruntables sur place.</p>
      </div>
    </header>

    <section class="catalog-view__filters glass-panel" aria-label="Filtres">
      <input v-model="textFilter" class="form-input" type="search" placeholder="Titre ou auteur" aria-label="Filtrer par titre ou auteur">
      <select v-model="typeFilter" class="form-input" aria-label="Type">
        <option value="">Tous les types</option>
        <option v-for="(label, value) in BOOK_TYPE_LABELS" :key="value" :value="value">{{ label }}</option>
      </select>
      <select v-model="genreFilter" class="form-input" aria-label="Genre">
        <option value="">Tous les genres</option>
        <option v-for="(label, value) in GENRE_LABELS" :key="value" :value="value">{{ label }}</option>
      </select>
      <label class="catalog-view__checkbox">
        <input v-model="onlyAvailable" type="checkbox">
        Disponibles uniquement
      </label>
    </section>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />
    <LoadingSpinner v-if="isLoading && books.length === 0" />

    <EmptyState
      v-else-if="!errorMessage && filteredBooks.length === 0"
      :icon-path="books.length === 0 ? mdiBookshelf : mdiFilterRemoveOutline"
      :title="books.length === 0 ? 'Le catalogue est vide' : 'Aucun livre ne correspond'"
      :message="books.length === 0 ? '' : 'Modifiez ou réinitialisez les filtres.'"
    >
      <button v-if="books.length > 0" type="button" class="button button--secondary" @click="resetFilters">
        Réinitialiser les filtres
      </button>
    </EmptyState>

    <div v-else class="book-grid">
      <StockBookCard v-for="book in filteredBooks" :key="book.id" :book="book" />
    </div>
  </div>
</template>

<style scoped>
.catalog-view__filters {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: var(--radius-medium);
}

.catalog-view__checkbox {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  white-space: nowrap;
  cursor: pointer;
}

@media (max-width: 800px) {
  .catalog-view__filters {
    grid-template-columns: 1fr 1fr;
  }

  .catalog-view__filters > :first-child {
    grid-column: 1 / -1;
  }
}
</style>
