<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiBookSearchOutline, mdiEmoticonSadOutline } from '@mdi/js'
import { extractErrorMessage } from '@/api/client'
import { SEARCH_PAGE_SIZE, searchBooks } from '@/api/openLibraryApi'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import SearchBar from '@/components/SearchBar.vue'
import SearchResultCard from '@/components/SearchResultCard.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { libraryItemFromSearchResult, libraryItemFromStockBook } from '@/utils/libraryItems'

const store = useStore()
const route = useRoute()
const router = useRouter()

const searchInput = ref(route.query.q ?? '')
const results = ref([])
const totalResults = ref(0)
const currentPage = ref(1)
const isSearching = ref(false)
const isLoadingMore = ref(false)
const errorMessage = ref('')

const activeQuery = computed(() => route.query.q ?? '')
const hasMoreResults = computed(() => results.value.length < totalResults.value)

function stockBookFor(result) {
  return store.getters['catalog/findStockBook'](result.allIsbns)
}

function libraryItemFor(result) {
  const stockBook = stockBookFor(result)
  return stockBook ? libraryItemFromStockBook({ ...stockBook, year: result.year }) : libraryItemFromSearchResult(result)
}

function isInLibrary(result) {
  return store.getters['library/isInLibrary'](libraryItemFor(result).id)
}

function addToLibrary(result) {
  if (!store.getters['auth/isAuthenticated']) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  store.dispatch('library/addItem', libraryItemFor(result))
}

function submitSearch(query) {
  router.push({ name: 'search', query: { q: query } })
}

async function runSearch(query) {
  results.value = []
  totalResults.value = 0
  errorMessage.value = ''
  if (!query) return
  isSearching.value = true
  currentPage.value = 1
  try {
    const response = await searchBooks(query, 1)
    results.value = response.results
    totalResults.value = response.total
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'La recherche Open Library a échoué')
  } finally {
    isSearching.value = false
  }
}

async function loadMoreResults() {
  isLoadingMore.value = true
  try {
    const response = await searchBooks(activeQuery.value, currentPage.value + 1)
    currentPage.value += 1
    results.value.push(...response.results)
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Impossible de charger plus de résultats')
  } finally {
    isLoadingMore.value = false
  }
}

watch(activeQuery, (query) => {
  searchInput.value = query
  runSearch(query)
})

onMounted(() => {
  store.dispatch('catalog/fetchCatalog').catch(() => {})
  runSearch(activeQuery.value)
})
</script>

<template>
  <div class="page-container search-view">
    <header class="search-view__header">
      <h1 class="page-title">Rechercher un livre</h1>
      <p class="page-subtitle">Résultats fournis par l'API Open Library. Les livres présents à la médiathèque sont signalés.</p>
      <SearchBar
        v-model="searchInput"
        class="search-view__search-bar"
        placeholder="Titre, auteur, ISBN…"
        :is-loading="isSearching"
        @search="submitSearch"
      />
    </header>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />

    <LoadingSpinner v-if="isSearching" label="Recherche en cours…" />

    <template v-else-if="activeQuery && !errorMessage">
      <p class="search-view__result-count">
        {{ totalResults.toLocaleString('fr-FR') }} résultat(s) pour « {{ activeQuery }} »
      </p>

      <EmptyState
        v-if="results.length === 0"
        :icon-path="mdiEmoticonSadOutline"
        title="Aucun résultat"
        message="Essayez avec un autre titre ou le nom de l'auteur."
      />

      <div v-else class="book-grid">
        <SearchResultCard
          v-for="result in results"
          :key="result.externalId"
          :result="result"
          :is-in-stock="stockBookFor(result) !== null"
          :is-in-library="isInLibrary(result)"
          @add="addToLibrary"
        />
      </div>

      <button
        v-if="hasMoreResults"
        type="button"
        class="button button--secondary search-view__load-more"
        :disabled="isLoadingMore"
        @click="loadMoreResults"
      >
        {{ isLoadingMore ? 'Chargement…' : `Voir ${SEARCH_PAGE_SIZE} résultats de plus` }}
      </button>
    </template>

    <EmptyState
      v-else-if="!activeQuery"
      :icon-path="mdiBookSearchOutline"
      title="Que voulez-vous lire ?"
      message="Tapez un titre (ex. « Dune ») ou un auteur. Cliquez sur un livre pour voir sa fiche, ou sur « + » pour l'ajouter à votre bibliothèque."
    />
  </div>
</template>

<style scoped>
.search-view__header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
}

.search-view__search-bar {
  margin-top: 16px;
}

.search-view__result-count {
  color: var(--color-text-on-dark);
  font-weight: 500;
}

.search-view__load-more {
  align-self: center;
}
</style>
