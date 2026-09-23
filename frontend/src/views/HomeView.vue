<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiBookClockOutline, mdiCalendarStar, mdiNewBox } from '@mdi/js'
import { coverUrlFromIsbn } from '@/api/openLibraryApi'
import { fetchLoans } from '@/api/booksApi'
import { fetchEvents } from '@/api/eventsApi'
import { daysUntil, formatDate, formatDateTime } from '@/utils/dates'
import { EVENT_TYPE_LABELS } from '@/utils/labels'
import BookCover from '@/components/BookCover.vue'
import DashboardCard from '@/components/DashboardCard.vue'
import SearchBar from '@/components/SearchBar.vue'

const PREVIEW_SIZE = 3

const store = useStore()
const router = useRouter()

const searchQuery = ref('')
const upcomingEvents = ref([])
const activeLoans = ref([])

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const userName = computed(() => store.getters['auth/userName'])
const latestBooks = computed(() => store.getters['catalog/latestBooks'].slice(0, PREVIEW_SIZE))

function goToSearch(query) {
  router.push({ name: 'search', query: { q: query } })
}

function bookTitle(bookId) {
  return store.getters['catalog/bookById'](bookId)?.title ?? `Livre n°${bookId}`
}

function dueLabel(dueDate) {
  const days = daysUntil(dueDate)
  if (days < 0) return { text: `En retard de ${-days} j`, modifier: 'badge--danger' }
  if (days <= 3) return { text: `Dans ${days} j`, modifier: 'badge--warning' }
  return { text: formatDate(dueDate), modifier: '' }
}

onMounted(() => {
  store.dispatch('catalog/fetchCatalog').catch(() => {})

  fetchEvents()
    .then((events) => {
      const now = Date.now()
      upcomingEvents.value = events
        .filter((event) => new Date(event.eventDate).getTime() >= now)
        .sort((a, b) => new Date(a.eventDate) - new Date(b.eventDate))
        .slice(0, PREVIEW_SIZE)
    })
    .catch(() => {})

  if (isAuthenticated.value) {
    fetchLoans(store.getters['auth/currentUserId'])
      .then((loans) => {
        activeLoans.value = loans
          .filter((loan) => !loan.returnDate)
          .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
          .slice(0, PREVIEW_SIZE)
      })
      .catch(() => {})
  }
})
</script>

<template>
  <div class="page-container home-view">
    <section class="home-view__hero">
      <h1 class="page-title home-view__greeting">
        {{ isAuthenticated ? `Bonjour ${userName} !` : 'Bienvenue à la médiathèque' }}
      </h1>
      <p class="page-subtitle">Cherchez un livre parmi des millions de titres grâce à Open Library.</p>
      <SearchBar v-model="searchQuery" class="home-view__search-bar" @search="goToSearch" />
    </section>

    <div class="home-view__dashboard">
      <DashboardCard title="Nouveautés !" :icon-path="mdiNewBox" :link-to="{ name: 'catalog' }" link-label="Voir le catalogue">
        <p v-if="latestBooks.length === 0" class="home-view__empty-text">Le catalogue est vide pour l'instant.</p>
        <ul v-else class="home-view__list">
          <li v-for="book in latestBooks" :key="book.id">
            <RouterLink :to="{ name: 'book-detail', params: { bookId: book.id } }" class="home-view__book-row">
              <BookCover :src="coverUrlFromIsbn(book.isbn, 'S')" :title="book.title" size="small" />
              <span class="home-view__row-text">
                <strong>{{ book.title }}</strong>
                <span>{{ book.authors.map((author) => author.name).join(', ') }}</span>
              </span>
            </RouterLink>
          </li>
        </ul>
      </DashboardCard>

      <DashboardCard title="Prochains événements" :icon-path="mdiCalendarStar" :link-to="{ name: 'events' }">
        <p v-if="upcomingEvents.length === 0" class="home-view__empty-text">Aucun événement programmé.</p>
        <ul v-else class="home-view__list">
          <li v-for="event in upcomingEvents" :key="event.id" class="home-view__event-row">
            <span class="badge">{{ EVENT_TYPE_LABELS[event.type] ?? event.type }}</span>
            <strong>{{ event.title }}</strong>
            <span class="home-view__row-date">{{ formatDateTime(event.eventDate) }}</span>
          </li>
        </ul>
      </DashboardCard>

      <DashboardCard
        title="Prochains rendus"
        :icon-path="mdiBookClockOutline"
        :link-to="isAuthenticated ? { name: 'loans' } : null"
        link-label="Mes emprunts"
      >
        <p v-if="!isAuthenticated" class="home-view__empty-text">
          <RouterLink :to="{ name: 'login' }">Connectez-vous</RouterLink> pour suivre vos emprunts.
        </p>
        <p v-else-if="activeLoans.length === 0" class="home-view__empty-text">Aucun emprunt en cours.</p>
        <ul v-else class="home-view__list">
          <li v-for="loan in activeLoans" :key="loan.id" class="home-view__loan-row">
            <span class="home-view__loan-title">{{ bookTitle(loan.bookId) }}</span>
            <span class="badge" :class="dueLabel(loan.dueDate).modifier">{{ dueLabel(loan.dueDate).text }}</span>
          </li>
        </ul>
      </DashboardCard>
    </div>
  </div>
</template>

<style scoped>
.home-view__hero {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding-top: 16px;
  text-align: center;
}

.home-view__search-bar {
  margin-top: 20px;
}

.home-view__dashboard {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.home-view__empty-text {
  color: var(--color-text-muted);
}

.home-view__list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.home-view__book-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 6px;
  border-radius: var(--radius-small);
  text-decoration: none;
  transition: background var(--transition-fast);
}

.home-view__book-row:hover {
  background: rgba(255, 255, 255, 0.4);
}

.home-view__row-text {
  display: flex;
  flex-direction: column;
  min-width: 0;
  font-size: 0.9rem;
}

.home-view__row-text span {
  color: var(--color-text-muted);
}

.home-view__event-row {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.home-view__row-date {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

.home-view__loan-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.home-view__loan-title {
  font-weight: 600;
}
</style>
