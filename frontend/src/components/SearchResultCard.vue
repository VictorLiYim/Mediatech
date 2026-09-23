<script setup>
import { mdiCheck, mdiLibrary } from '@mdi/js'
import AppIcon from './AppIcon.vue'
import BookCover from './BookCover.vue'

defineProps({
  result: { type: Object, required: true },
  isInStock: { type: Boolean, default: false },
  isInLibrary: { type: Boolean, default: false },
  isSelected: { type: Boolean, default: false },
})

const emit = defineEmits(['select'])
</script>

<template>
  <button
    type="button"
    class="search-result-card glass-panel"
    :class="{ 'search-result-card--selected': isSelected }"
    :aria-pressed="isSelected"
    @click="emit('select', result)"
  >
    <BookCover :src="result.coverUrl" :title="result.title" />
    <div class="search-result-card__body">
      <h3 class="search-result-card__title">{{ result.title }}</h3>
      <p class="search-result-card__authors">{{ result.authors.join(', ') || 'Auteur inconnu' }}</p>
      <p class="search-result-card__year">{{ result.year ?? 'Année inconnue' }}</p>
      <div class="search-result-card__badges">
        <span v-if="isInStock" class="badge badge--success">
          <AppIcon :path="mdiLibrary" :size="14" /> En médiathèque
        </span>
        <span v-if="isInLibrary" class="badge">
          <AppIcon :path="mdiCheck" :size="14" /> Dans ma bibliothèque
        </span>
      </div>
    </div>
  </button>
</template>

<style scoped>
.search-result-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 14px;
  border-radius: var(--radius-medium);
  text-align: left;
  cursor: pointer;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast), border-color var(--transition-fast);
}

.search-result-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-card-hover);
}

.search-result-card--selected {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary), var(--shadow-card-hover);
}

.search-result-card__body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.search-result-card__title {
  font-size: 1rem;
  font-weight: 700;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.search-result-card__authors {
  font-size: 0.9rem;
  color: var(--color-text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.search-result-card__year {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

.search-result-card__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 6px;
}
</style>
