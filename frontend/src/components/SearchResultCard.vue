<script setup>
import { mdiCheck, mdiPlus } from '@mdi/js'
import AppIcon from './AppIcon.vue'
import BookCover from './BookCover.vue'

defineProps({
  result: { type: Object, required: true },
  isInStock: { type: Boolean, default: false },
  isInLibrary: { type: Boolean, default: false },
})

const emit = defineEmits(['add'])
</script>

<template>
  <article class="search-result-card panel">
    <button
      type="button"
      class="search-result-card__add-button"
      :class="{ 'search-result-card__add-button--done': isInLibrary }"
      :disabled="isInLibrary"
      @click="emit('add', result)"
    >
      <AppIcon
        :path="isInLibrary ? mdiCheck : mdiPlus"
        :size="22"
        :label="isInLibrary ? 'Déjà dans ma bibliothèque' : 'Ajouter à ma bibliothèque'"
      />
    </button>

    <BookCover :src="result.coverUrl" :title="result.title" />
    <div class="search-result-card__body">
      <!-- Le lien couvre toute la carte (::after) -->
      <RouterLink
        :to="{ name: 'open-library-book', params: { workId: result.externalId } }"
        class="search-result-card__title"
      >
        {{ result.title }}
      </RouterLink>
      <p class="search-result-card__authors">{{ result.authors.join(', ') || 'Auteur inconnu' }}</p>
      <p class="search-result-card__year">{{ result.year ?? 'Année inconnue' }}</p>
      <div class="search-result-card__badges">
        <span v-if="isInStock" class="badge badge--success">
          En médiathèque
        </span>
        <span v-if="isInLibrary" class="badge">
          Dans ma bibliothèque
        </span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.search-result-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  padding: 14px;
  transition: border-color var(--transition-fast);
}

.search-result-card:hover {
  border-color: var(--color-primary);
}

.search-result-card__add-button {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 2;
  display: inline-flex;
  padding: 5px;
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-small);
  background: var(--color-primary);
  color: var(--color-on-primary);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.search-result-card__add-button:hover:not(:disabled) {
  background: var(--color-primary-hover);
}

.search-result-card__add-button--done {
  border-color: var(--color-surface-border);
  background: var(--color-floating-button);
  color: var(--color-success);
  cursor: default;
}

.search-result-card__body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.search-result-card__title {
  font-size: 1.05rem;
  font-weight: 600;
  line-height: 1.3;
  text-decoration: none;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.search-result-card__title::after {
  content: '';
  position: absolute;
  inset: 0;
  z-index: 1;
  border-radius: var(--radius-medium);
}

.search-result-card__title:focus-visible::after {
  outline: 2px solid var(--color-primary);
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
