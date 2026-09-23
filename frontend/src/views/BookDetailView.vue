<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiArrowLeft, mdiBookPlus, mdiCheck } from '@mdi/js'
import { extractErrorMessage } from '@/api/client'
import { borrowBook, fetchBook, fetchLoans, fetchReviews, postReview, returnBook } from '@/api/booksApi'
import { coverUrlFromIsbn, fetchFirstPublishYearByIsbn } from '@/api/openLibraryApi'
import { fetchUser } from '@/api/usersApi'
import AppIcon from '@/components/AppIcon.vue'
import BookCover from '@/components/BookCover.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import StarRating from '@/components/StarRating.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { formatDate } from '@/utils/dates'
import { BOOK_TYPE_LABELS, GENRE_LABELS } from '@/utils/labels'
import { libraryItemFromStockBook } from '@/utils/libraryItems'

const props = defineProps({
  bookId: { type: String, required: true },
})

const store = useStore()
const route = useRoute()
const router = useRouter()

const book = ref(null)
const publishYear = ref(null)
const reviews = ref([])
const reviewerNames = reactive({})
const activeLoan = ref(null)
const isLoading = ref(true)
const loadError = ref('')
const actionMessage = ref(null)
const isProcessingLoan = ref(false)

const reviewForm = reactive({ rating: 0, comment: '' })
const reviewError = ref('')
const isPostingReview = ref(false)

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const currentUserId = computed(() => store.getters['auth/currentUserId'])
const authorNames = computed(() => book.value?.authors.map((author) => author.name).join(', ') ?? '')
const libraryItem = computed(() => (book.value ? libraryItemFromStockBook({ ...book.value, year: publishYear.value }) : null))
const isInLibrary = computed(() => libraryItem.value && store.getters['library/isInLibrary'](libraryItem.value.id))
const sortedReviews = computed(() => [...reviews.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)))

function requireLogin() {
  router.push({ name: 'login', query: { redirect: route.fullPath } })
}

async function loadReviewerNames() {
  const unknownIds = [...new Set(reviews.value.map((review) => review.userId))].filter((id) => !reviewerNames[id])
  await Promise.all(unknownIds.map(async (userId) => {
    try {
      reviewerNames[userId] = (await fetchUser(userId)).userName
    } catch {
      reviewerNames[userId] = `Lecteur n°${userId}`
    }
  }))
}

async function loadActiveLoan() {
  if (!isAuthenticated.value) return
  const loans = await fetchLoans(currentUserId.value)
  activeLoan.value = loans.find((loan) => loan.bookId === book.value.id && !loan.returnDate) ?? null
}

async function refreshBook() {
  book.value = await fetchBook(props.bookId)
  store.commit('catalog/UPDATE_BOOK', book.value)
}

async function toggleLoan() {
  if (!isAuthenticated.value) return requireLogin()
  isProcessingLoan.value = true
  actionMessage.value = null
  try {
    if (activeLoan.value) {
      await returnBook(book.value.id, currentUserId.value)
      activeLoan.value = null
      actionMessage.value = { type: 'success', text: 'Livre rendu, merci !' }
    } else {
      activeLoan.value = await borrowBook(book.value.id, currentUserId.value)
      actionMessage.value = { type: 'success', text: `Emprunt enregistré, à rendre avant le ${formatDate(activeLoan.value.dueDate)}.` }
    }
    await refreshBook()
  } catch (error) {
    actionMessage.value = { type: 'error', text: extractErrorMessage(error) }
  } finally {
    isProcessingLoan.value = false
  }
}

function addToLibrary() {
  if (!isAuthenticated.value) return requireLogin()
  store.dispatch('library/addItem', libraryItem.value)
}

async function submitReview() {
  reviewError.value = ''
  if (reviewForm.rating === 0) {
    reviewError.value = 'Choisissez une note entre 1 et 5 étoiles.'
    return
  }
  isPostingReview.value = true
  try {
    const review = await postReview(book.value.id, {
      userId: currentUserId.value,
      rating: reviewForm.rating,
      comment: reviewForm.comment.trim(),
    })
    reviews.value.push(review)
    reviewForm.rating = 0
    reviewForm.comment = ''
    await Promise.all([refreshBook(), loadReviewerNames()])
  } catch (error) {
    reviewError.value = extractErrorMessage(error, "Impossible d'envoyer l'avis")
  } finally {
    isPostingReview.value = false
  }
}

onMounted(async () => {
  try {
    await refreshBook()
    reviews.value = await fetchReviews(props.bookId)
    await Promise.all([loadReviewerNames(), loadActiveLoan()])
  } catch (error) {
    loadError.value = extractErrorMessage(error, 'Livre introuvable')
  } finally {
    isLoading.value = false
  }
  if (book.value) {
    fetchFirstPublishYearByIsbn(book.value.isbn).then((year) => { publishYear.value = year }).catch(() => {})
  }
})
</script>

<template>
  <div class="page-container book-detail-view">
    <RouterLink :to="{ name: 'catalog' }" class="book-detail-view__back-link">
      <AppIcon :path="mdiArrowLeft" :size="18" /> Retour au catalogue
    </RouterLink>

    <LoadingSpinner v-if="isLoading" />
    <StatusMessage v-else-if="loadError" :message="loadError" />

    <template v-else-if="book">
      <article class="book-detail-view__summary glass-panel">
        <BookCover :src="coverUrlFromIsbn(book.isbn, 'L')" :title="book.title" size="large" />

        <div class="book-detail-view__information">
          <span class="book-detail-view__type">{{ BOOK_TYPE_LABELS[book.type] ?? book.type }}</span>
          <h1 class="book-detail-view__title">{{ book.title }}</h1>
          <p class="book-detail-view__authors">
            {{ authorNames }}<template v-if="publishYear"> · {{ publishYear }}</template>
          </p>

          <div class="book-detail-view__rating">
            <StarRating :model-value="book.averageRating" readonly />
            <span>{{ reviews.length ? `${book.averageRating.toFixed(1)} / 5 · ${reviews.length} avis` : 'Pas encore noté' }}</span>
          </div>

          <div class="book-detail-view__genres">
            <span v-for="genre in book.genres" :key="genre" class="badge">{{ GENRE_LABELS[genre] ?? genre }}</span>
          </div>

          <p v-if="book.description" class="book-detail-view__description">{{ book.description }}</p>

          <div class="book-detail-view__stock">
            <span class="badge" :class="book.availableCopies > 0 ? 'badge--success' : 'badge--danger'">
              {{ book.availableCopies }} / {{ book.totalCopies }} exemplaire(s) disponible(s)
            </span>
            <span v-if="activeLoan" class="badge badge--warning">
              Emprunté par vous · à rendre le {{ formatDate(activeLoan.dueDate) }}
            </span>
          </div>

          <StatusMessage v-if="actionMessage" :type="actionMessage.type" :message="actionMessage.text" />

          <div class="book-detail-view__actions">
            <button
              type="button"
              class="button button--primary"
              :disabled="isProcessingLoan || (!activeLoan && book.availableCopies === 0)"
              @click="toggleLoan"
            >
              {{ activeLoan ? 'Rendre ce livre' : 'Emprunter' }}
            </button>
            <button type="button" class="button button--secondary" :disabled="isInLibrary" @click="addToLibrary">
              <AppIcon :path="isInLibrary ? mdiCheck : mdiBookPlus" :size="18" />
              {{ isInLibrary ? 'Dans ma bibliothèque' : 'Ajouter à ma bibliothèque' }}
            </button>
          </div>
        </div>
      </article>

      <section class="book-detail-view__reviews glass-panel">
        <h2 class="book-detail-view__section-title">Avis des lecteurs</h2>

        <form v-if="isAuthenticated" class="book-detail-view__review-form" @submit.prevent="submitReview">
          <div class="form-field">
            <span class="form-label">Votre note</span>
            <StarRating v-model="reviewForm.rating" :size="28" />
          </div>
          <div class="form-field">
            <label class="form-label" for="review-comment">Votre commentaire</label>
            <textarea
              id="review-comment"
              v-model="reviewForm.comment"
              class="form-input"
              rows="3"
              maxlength="1000"
              placeholder="Qu'avez-vous pensé de ce livre ?"
            />
          </div>
          <StatusMessage v-if="reviewError" :message="reviewError" />
          <button type="submit" class="button button--primary book-detail-view__review-submit" :disabled="isPostingReview">
            {{ isPostingReview ? 'Envoi…' : 'Publier mon avis' }}
          </button>
        </form>
        <p v-else class="book-detail-view__muted">
          <RouterLink :to="{ name: 'login', query: { redirect: route.fullPath } }">Connectez-vous</RouterLink> pour donner votre avis.
        </p>

        <p v-if="sortedReviews.length === 0" class="book-detail-view__muted">Aucun avis pour le moment.</p>
        <ul v-else class="book-detail-view__review-list">
          <li v-for="review in sortedReviews" :key="review.id" class="book-detail-view__review">
            <div class="book-detail-view__review-header">
              <strong>{{ reviewerNames[review.userId] ?? '…' }}</strong>
              <StarRating :model-value="review.rating" readonly :size="16" />
              <span class="book-detail-view__review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <p v-if="review.comment">{{ review.comment }}</p>
          </li>
        </ul>
      </section>
    </template>
  </div>
</template>

<style scoped>
.book-detail-view__back-link {
  align-self: flex-start;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--color-text-on-dark);
  font-weight: 600;
  text-decoration: none;
}

.book-detail-view__back-link:hover {
  text-decoration: underline;
}

.book-detail-view__summary {
  display: flex;
  gap: 32px;
  padding: 32px;
}

.book-detail-view__information {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.book-detail-view__type {
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  color: var(--color-primary);
}

.book-detail-view__title {
  font-size: 2rem;
  line-height: 1.15;
}

.book-detail-view__authors {
  font-size: 1.1rem;
  color: var(--color-text-muted);
}

.book-detail-view__rating,
.book-detail-view__genres,
.book-detail-view__stock,
.book-detail-view__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.book-detail-view__rating span {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.book-detail-view__reviews {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 28px 32px;
}

.book-detail-view__section-title {
  font-size: 1.3rem;
}

.book-detail-view__review-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-surface-border);
}

.book-detail-view__review-submit {
  align-self: flex-start;
}

.book-detail-view__muted {
  color: var(--color-text-muted);
}

.book-detail-view__review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.book-detail-view__review {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 14px 16px;
  border-radius: var(--radius-small);
  background: rgba(255, 255, 255, 0.4);
}

.book-detail-view__review-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.book-detail-view__review-date {
  margin-left: auto;
  font-size: 0.82rem;
  color: var(--color-text-muted);
}

@media (max-width: 700px) {
  .book-detail-view__summary {
    flex-direction: column;
    align-items: center;
    padding: 20px;
  }

  .book-detail-view__title {
    font-size: 1.5rem;
  }

  .book-detail-view__reviews {
    padding: 20px;
  }
}
</style>
