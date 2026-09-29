<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiArrowLeft, mdiBookPlus, mdiCheck, mdiPackageVariantPlus } from '@mdi/js'
import { coverUrlFromCoverId, fetchBookByWorkId, fetchWorkDescription } from '@/api/openLibraryApi'
import AddToStockForm from '@/components/AddToStockForm.vue'
import AppIcon from '@/components/AppIcon.vue'
import BookCover from '@/components/BookCover.vue'
import LibraryItemPanel from '@/components/LibraryItemPanel.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import ModalDialog from '@/components/ModalDialog.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { libraryItemFromSearchResult, libraryItemFromStockBook } from '@/utils/libraryItems'

const MAX_SUBJECTS = 6

const props = defineProps({
  workId: { type: String, required: true },
})

const store = useStore()
const route = useRoute()
const router = useRouter()

const book = ref(null)
const description = ref(null)
const isLoading = ref(true)
const loadError = ref('')
const actionMessage = ref(null)
const isAddingToStock = ref(false)

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const isAdmin = computed(() => store.getters['auth/isAdmin'])
const stockBook = computed(() => (book.value ? store.getters['catalog/findStockBook'](book.value.allIsbns) : null))

// Un livre présent au stock est rangé dans la bibliothèque sous son id du stock
const libraryItem = computed(() => {
  if (!book.value) return null
  return stockBook.value
    ? libraryItemFromStockBook({ ...stockBook.value, year: book.value.year })
    : libraryItemFromSearchResult(book.value)
})
const savedItem = computed(() => {
  if (!libraryItem.value) return null
  return store.getters['library/itemById'](libraryItem.value.id)
    ?? store.getters['library/itemById'](`openlibrary:${props.workId}`)
})

const factsLine = computed(() => [
  book.value?.isbn && `ISBN ${book.value.isbn}`,
  book.value?.pageCount && `${book.value.pageCount} pages`,
].filter(Boolean).join(' · '))

async function loadBook(workId) {
  isLoading.value = true
  loadError.value = ''
  actionMessage.value = null
  book.value = null
  description.value = null
  try {
    book.value = await fetchBookByWorkId(workId)
    if (!book.value) loadError.value = 'Livre introuvable sur Open Library.'
  } catch {
    loadError.value = 'Impossible de contacter Open Library, réessayez plus tard.'
  } finally {
    isLoading.value = false
  }
  if (book.value) {
    fetchWorkDescription(workId).then((text) => { description.value = text }).catch(() => {})
  }
}

function goBack() {
  if (window.history.state?.back) router.back()
  else router.push({ name: 'search' })
}

function addToLibrary() {
  if (!isAuthenticated.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  store.dispatch('library/addItem', libraryItem.value)
}

function onBookAddedToStock(created) {
  isAddingToStock.value = false
  actionMessage.value = { type: 'success', text: `« ${created.title} » a été ajouté au stock de la médiathèque.` }
}

watch(() => props.workId, loadBook, { immediate: true })

onMounted(() => {
  store.dispatch('catalog/fetchCatalog').catch(() => {})
})
</script>

<template>
  <div class="page-container open-library-book-view">
    <button type="button" class="book-detail-page__back-link" @click="goBack">
      <AppIcon :path="mdiArrowLeft" :size="18" /> Retour
    </button>

    <LoadingSpinner v-if="isLoading" />
    <StatusMessage v-else-if="loadError" :message="loadError" />

    <template v-else-if="book">
      <article class="book-detail-page__summary glass-panel">
        <BookCover :src="coverUrlFromCoverId(book.coverId, 'L')" :title="book.title" size="large" />

        <div class="book-detail-page__information">
          <span class="book-detail-page__type">Open Library</span>
          <h1 class="book-detail-page__title">{{ book.title }}</h1>
          <p class="book-detail-page__authors">
            {{ book.authors.join(', ') || 'Auteur inconnu' }}<template v-if="book.year"> · {{ book.year }}</template>
          </p>

          <div v-if="book.subjects.length" class="book-detail-page__row">
            <span v-for="subject in book.subjects.slice(0, MAX_SUBJECTS)" :key="subject" class="badge">{{ subject }}</span>
          </div>

          <p v-if="description" class="book-detail-page__description">{{ description }}</p>
          <p v-else class="book-detail-page__muted">Aucun résumé disponible sur Open Library.</p>

          <p v-if="factsLine" class="book-detail-page__muted">{{ factsLine }}</p>

          <div class="book-detail-page__row">
            <template v-if="stockBook">
              <span class="badge badge--success">Disponible à la médiathèque</span>
              <span class="book-detail-page__muted">
                {{ stockBook.availableCopies }} / {{ stockBook.totalCopies }} exemplaire(s) libre(s)
              </span>
            </template>
            <span v-else class="badge badge--warning">Pas dans le stock de la médiathèque</span>
          </div>

          <StatusMessage v-if="actionMessage" :type="actionMessage.type" :message="actionMessage.text" />

          <div class="book-detail-page__row">
            <button type="button" class="button button--primary" :disabled="!!savedItem" @click="addToLibrary">
              <AppIcon :path="savedItem ? mdiCheck : mdiBookPlus" :size="18" />
              {{ savedItem ? 'Dans ma bibliothèque' : 'Ajouter à ma bibliothèque' }}
            </button>
            <RouterLink
              v-if="stockBook"
              :to="{ name: 'book-detail', params: { bookId: stockBook.id } }"
              class="button button--secondary"
            >
              Voir / emprunter
            </RouterLink>
            <button v-else-if="isAdmin" type="button" class="button button--secondary" @click="isAddingToStock = true">
              <AppIcon :path="mdiPackageVariantPlus" :size="18" />
              Ajouter au stock
            </button>
          </div>
        </div>
      </article>

      <LibraryItemPanel v-if="savedItem" :item="savedItem" />
    </template>

    <ModalDialog v-if="isAddingToStock" title="Ajouter au stock de la médiathèque" @close="isAddingToStock = false">
      <AddToStockForm
        :initial-book="{ ...book, description }"
        @created="onBookAddedToStock"
        @cancel="isAddingToStock = false"
      />
    </ModalDialog>
  </div>
</template>

<style scoped src="@/assets/styles/book-detail-page.css"></style>
