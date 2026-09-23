<script setup>
import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { mdiPlus } from '@mdi/js'
import { extractErrorMessage } from '@/api/client'
import AddToStockForm from '@/components/AddToStockForm.vue'
import AppIcon from '@/components/AppIcon.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import ModalDialog from '@/components/ModalDialog.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { BOOK_TYPE_LABELS } from '@/utils/labels'

const store = useStore()

const isFormOpen = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const isLoading = computed(() => store.state.catalog.isLoading)
const books = computed(() => [...store.getters['catalog/allBooks']].sort((a, b) => a.title.localeCompare(b.title, 'fr')))
const totalCopies = computed(() => books.value.reduce((sum, book) => sum + book.totalCopies, 0))
const borrowedCopies = computed(() => books.value.reduce((sum, book) => sum + book.totalCopies - book.availableCopies, 0))

function onBookCreated(book) {
  isFormOpen.value = false
  successMessage.value = `« ${book.title} » a été ajouté au stock.`
}

onMounted(async () => {
  try {
    await store.dispatch('catalog/fetchCatalog', { force: true })
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Impossible de charger le stock')
  }
})
</script>

<template>
  <div class="page-container admin-stock-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Gestion du stock</h1>
        <p class="page-subtitle">
          {{ books.length }} titre(s) · {{ totalCopies }} exemplaire(s) · {{ borrowedCopies }} emprunté(s)
        </p>
      </div>
      <div class="admin-stock-view__header-actions">
        <RouterLink :to="{ name: 'search' }" class="button button--secondary">Ajouter depuis Open Library</RouterLink>
        <button type="button" class="button button--primary" @click="isFormOpen = true">
          <AppIcon :path="mdiPlus" :size="18" /> Saisie manuelle
        </button>
      </div>
    </header>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />
    <StatusMessage v-if="successMessage" type="success" :message="successMessage" />
    <LoadingSpinner v-if="isLoading && books.length === 0" />

    <div v-else class="admin-stock-view__table-wrapper glass-panel">
      <table class="admin-stock-view__table">
        <thead>
          <tr>
            <th scope="col">Titre</th>
            <th scope="col">Auteur(s)</th>
            <th scope="col">Type</th>
            <th scope="col">ISBN</th>
            <th scope="col" class="admin-stock-view__numeric">Disponibles</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="book in books" :key="book.id">
            <td>
              <RouterLink :to="{ name: 'book-detail', params: { bookId: book.id } }" class="admin-stock-view__book-link">
                {{ book.title }}
              </RouterLink>
            </td>
            <td>{{ book.authors.map((author) => author.name).join(', ') }}</td>
            <td>{{ BOOK_TYPE_LABELS[book.type] ?? book.type }}</td>
            <td class="admin-stock-view__isbn">{{ book.isbn }}</td>
            <td class="admin-stock-view__numeric">
              <span class="badge" :class="book.availableCopies > 0 ? 'badge--success' : 'badge--danger'">
                {{ book.availableCopies }} / {{ book.totalCopies }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-if="isFormOpen" title="Ajouter un livre au stock" @close="isFormOpen = false">
      <AddToStockForm @created="onBookCreated" @cancel="isFormOpen = false" />
    </ModalDialog>
  </div>
</template>

<style scoped>
.admin-stock-view__header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.admin-stock-view__table-wrapper {
  overflow-x: auto;
  border-radius: var(--radius-medium);
}

.admin-stock-view__table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.93rem;
}

.admin-stock-view__table th,
.admin-stock-view__table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--color-surface-border);
}

.admin-stock-view__table th {
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--color-text-muted);
}

.admin-stock-view__table tbody tr:last-child td {
  border-bottom: none;
}

.admin-stock-view__table tbody tr:hover {
  background: rgba(255, 255, 255, 0.35);
}

.admin-stock-view__book-link {
  font-weight: 600;
  text-decoration: none;
}

.admin-stock-view__book-link:hover {
  text-decoration: underline;
}

.admin-stock-view__isbn {
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.admin-stock-view__numeric {
  text-align: right !important;
}
</style>
