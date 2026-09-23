<script setup>
import { computed, onMounted, ref } from 'vue'
import { useStore } from 'vuex'
import { mdiBookClockOutline } from '@mdi/js'
import { extractErrorMessage } from '@/api/client'
import { fetchLoans, returnBook } from '@/api/booksApi'
import { coverUrlFromIsbn } from '@/api/openLibraryApi'
import BookCover from '@/components/BookCover.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { daysUntil, formatDate } from '@/utils/dates'

const store = useStore()

const loans = ref([])
const isLoading = ref(true)
const errorMessage = ref('')
const returningLoanId = ref(null)

const currentUserId = computed(() => store.getters['auth/currentUserId'])
const activeLoans = computed(() =>
  loans.value.filter((loan) => !loan.returnDate).sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate)),
)
const returnedLoans = computed(() =>
  loans.value.filter((loan) => loan.returnDate).sort((a, b) => new Date(b.returnDate) - new Date(a.returnDate)),
)

function bookFor(loan) {
  return store.getters['catalog/bookById'](loan.bookId)
}

function dueStatus(loan) {
  const days = daysUntil(loan.dueDate)
  if (days < 0) return { text: `En retard de ${-days} jour(s)`, modifier: 'badge--danger' }
  if (days <= 3) return { text: `À rendre dans ${days} jour(s)`, modifier: 'badge--warning' }
  return { text: `À rendre le ${formatDate(loan.dueDate)}`, modifier: 'badge--success' }
}

async function loadLoans() {
  loans.value = await fetchLoans(currentUserId.value)
}

async function giveBack(loan) {
  returningLoanId.value = loan.id
  errorMessage.value = ''
  try {
    await returnBook(loan.bookId, currentUserId.value)
    await Promise.all([loadLoans(), store.dispatch('catalog/fetchCatalog', { force: true })])
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Le retour a échoué')
  } finally {
    returningLoanId.value = null
  }
}

onMounted(async () => {
  try {
    await Promise.all([loadLoans(), store.dispatch('catalog/fetchCatalog')])
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Impossible de charger vos emprunts')
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="page-container loans-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Mes emprunts</h1>
        <p class="page-subtitle">Durée d'un emprunt : 3 semaines.</p>
      </div>
    </header>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />
    <LoadingSpinner v-if="isLoading" />

    <template v-else>
      <EmptyState
        v-if="loans.length === 0"
        :icon-path="mdiBookClockOutline"
        title="Aucun emprunt"
        message="Empruntez un livre depuis le catalogue de la médiathèque."
      >
        <RouterLink :to="{ name: 'catalog' }" class="button button--primary">Voir le catalogue</RouterLink>
      </EmptyState>

      <section v-if="activeLoans.length" class="loans-view__section">
        <h2 class="loans-view__section-title">En cours ({{ activeLoans.length }})</h2>
        <ul class="loans-view__list">
          <li v-for="loan in activeLoans" :key="loan.id" class="loans-view__loan glass-panel">
            <BookCover :src="coverUrlFromIsbn(bookFor(loan)?.isbn, 'S')" :title="bookFor(loan)?.title ?? '?'" size="small" />
            <div class="loans-view__loan-information">
              <RouterLink :to="{ name: 'book-detail', params: { bookId: loan.bookId } }" class="loans-view__loan-title">
                {{ bookFor(loan)?.title ?? `Livre n°${loan.bookId}` }}
              </RouterLink>
              <span class="loans-view__loan-dates">Emprunté le {{ formatDate(loan.borrowDate) }}</span>
              <span class="badge" :class="dueStatus(loan).modifier">{{ dueStatus(loan).text }}</span>
            </div>
            <button
              type="button"
              class="button button--secondary"
              :disabled="returningLoanId === loan.id"
              @click="giveBack(loan)"
            >
              {{ returningLoanId === loan.id ? 'Retour…' : 'Rendre' }}
            </button>
          </li>
        </ul>
      </section>

      <section v-if="returnedLoans.length" class="loans-view__section">
        <h2 class="loans-view__section-title">Historique ({{ returnedLoans.length }})</h2>
        <ul class="loans-view__list">
          <li v-for="loan in returnedLoans" :key="loan.id" class="loans-view__loan loans-view__loan--returned glass-panel">
            <BookCover :src="coverUrlFromIsbn(bookFor(loan)?.isbn, 'S')" :title="bookFor(loan)?.title ?? '?'" size="small" />
            <div class="loans-view__loan-information">
              <RouterLink :to="{ name: 'book-detail', params: { bookId: loan.bookId } }" class="loans-view__loan-title">
                {{ bookFor(loan)?.title ?? `Livre n°${loan.bookId}` }}
              </RouterLink>
              <span class="loans-view__loan-dates">
                Du {{ formatDate(loan.borrowDate) }} au {{ formatDate(loan.returnDate) }}
              </span>
            </div>
          </li>
        </ul>
      </section>
    </template>
  </div>
</template>

<style scoped>
.loans-view__section {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.loans-view__section-title {
  color: var(--color-text-on-dark);
  font-size: 1.2rem;
}

.loans-view__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.loans-view__loan {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  border-radius: var(--radius-medium);
}

.loans-view__loan--returned {
  opacity: 0.8;
}

.loans-view__loan-information {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  min-width: 0;
}

.loans-view__loan-title {
  font-weight: 700;
  text-decoration: none;
}

.loans-view__loan-title:hover {
  text-decoration: underline;
}

.loans-view__loan-dates {
  font-size: 0.88rem;
  color: var(--color-text-muted);
}

@media (max-width: 520px) {
  .loans-view__loan {
    flex-wrap: wrap;
  }
}
</style>
