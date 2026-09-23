<script setup>
import { computed } from 'vue'
import { coverUrlFromIsbn } from '@/api/openLibraryApi'
import { BOOK_TYPE_LABELS } from '@/utils/labels'
import BookCover from './BookCover.vue'
import StarRating from './StarRating.vue'

const props = defineProps({
  book: { type: Object, required: true },
})

const authorNames = computed(() => props.book.authors.map((author) => author.name).join(', '))
const isAvailable = computed(() => props.book.availableCopies > 0)
</script>

<template>
  <RouterLink :to="{ name: 'book-detail', params: { bookId: book.id } }" class="stock-book-card glass-panel">
    <BookCover :src="coverUrlFromIsbn(book.isbn)" :title="book.title" />
    <div class="stock-book-card__body">
      <span class="stock-book-card__type">{{ BOOK_TYPE_LABELS[book.type] ?? book.type }}</span>
      <h3 class="stock-book-card__title">{{ book.title }}</h3>
      <p class="stock-book-card__authors">{{ authorNames }}</p>
      <StarRating v-if="book.averageRating > 0" :model-value="book.averageRating" readonly :size="16" />
      <span class="badge" :class="isAvailable ? 'badge--success' : 'badge--danger'">
        {{ isAvailable ? `${book.availableCopies} disponible(s)` : 'Tous empruntés' }}
      </span>
    </div>
  </RouterLink>
</template>

<style scoped>
.stock-book-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  border-radius: var(--radius-medium);
  text-decoration: none;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.stock-book-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover);
}

.stock-book-card__body {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  min-width: 0;
}

.stock-book-card__type {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-primary);
}

.stock-book-card__title {
  font-size: 1rem;
  line-height: 1.3;
}

.stock-book-card__authors {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}
</style>
