<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import {
  mdiBookOpenPageVariant, mdiCheckCircle, mdiDeleteOutline, mdiHeart, mdiHeartOutline,
} from '@mdi/js'
import AppIcon from './AppIcon.vue'
import BookCover from './BookCover.vue'

const props = defineProps({
  item: { type: Object, required: true },
})

const store = useStore()
const isConfirmingRemoval = ref(false)

function toggleRead() {
  store.dispatch('library/toggleRead', props.item.id)
}

function toggleFavorite() {
  store.dispatch('library/toggleFavorite', props.item.id)
}

function removeItem() {
  store.dispatch('library/removeItem', props.item.id)
}
</script>

<template>
  <article class="library-item-card glass-panel">
    <button
      type="button"
      class="library-item-card__favorite-button"
      :class="{ 'library-item-card__favorite-button--active': item.favorite }"
      :aria-pressed="item.favorite"
      @click="toggleFavorite"
    >
      <AppIcon
        :path="item.favorite ? mdiHeart : mdiHeartOutline"
        :size="22"
        :label="item.favorite ? 'Retirer des favoris' : 'Ajouter aux favoris'"
      />
    </button>

    <BookCover :src="item.coverUrl" :title="item.title" />

    <div class="library-item-card__body">
      <h3 class="library-item-card__title">{{ item.title }}</h3>
      <p class="library-item-card__authors">{{ item.authors.join(', ') || 'Auteur inconnu' }}</p>
      <p v-if="item.year" class="library-item-card__year">{{ item.year }}</p>
      <div class="library-item-card__badges">
        <span class="badge" :class="item.read ? 'badge--success' : 'badge--warning'">
          {{ item.read ? 'Lu' : 'À lire' }}
        </span>
        <span v-if="item.source === 'stock'" class="badge">Médiathèque</span>
      </div>
    </div>

    <div class="library-item-card__actions">
      <button type="button" class="button button--secondary button--small" @click="toggleRead">
        <AppIcon :path="item.read ? mdiBookOpenPageVariant : mdiCheckCircle" :size="16" />
        {{ item.read ? 'Marquer non lu' : 'Marquer lu' }}
      </button>
      <RouterLink
        v-if="item.stockBookId"
        :to="{ name: 'book-detail', params: { bookId: item.stockBookId } }"
        class="button button--secondary button--small"
      >
        Emprunter
      </RouterLink>
      <button
        v-if="!isConfirmingRemoval"
        type="button"
        class="button button--danger button--small"
        @click="isConfirmingRemoval = true"
      >
        <AppIcon :path="mdiDeleteOutline" :size="16" />
        Retirer
      </button>
      <div v-else class="library-item-card__confirmation">
        <span>Retirer ce livre ?</span>
        <button type="button" class="button button--danger button--small" @click="removeItem">Oui</button>
        <button type="button" class="button button--secondary button--small" @click="isConfirmingRemoval = false">
          Non
        </button>
      </div>
    </div>
  </article>
</template>

<style scoped>
.library-item-card {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  border-radius: var(--radius-medium);
}

.library-item-card__favorite-button {
  position: absolute;
  top: 22px;
  right: 22px;
  z-index: 1;
  display: inline-flex;
  padding: 8px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: var(--color-text-muted);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: transform var(--transition-fast), color var(--transition-fast);
}

.library-item-card__favorite-button:hover {
  transform: scale(1.1);
}

.library-item-card__favorite-button--active {
  color: var(--color-favorite);
}

.library-item-card__body {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.library-item-card__title {
  font-size: 1rem;
  line-height: 1.3;
}

.library-item-card__authors,
.library-item-card__year {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.library-item-card__badges {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 6px;
}

.library-item-card__actions {
  margin-top: auto;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.library-item-card__confirmation {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  font-weight: 600;
}
</style>
