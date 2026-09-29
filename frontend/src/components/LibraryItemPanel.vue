<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import {
  mdiBookOpenPageVariant, mdiCheckCircle, mdiDeleteOutline, mdiHeart, mdiHeartOutline,
} from '@mdi/js'
import { formatDate } from '@/utils/dates'
import AppIcon from './AppIcon.vue'
import StarRating from './StarRating.vue'

// Bloc « Ma bibliothèque » des pages détail : lu, favori, note perso
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

function setRating(rating) {
  store.dispatch('library/setRating', { itemId: props.item.id, rating })
}

function removeItem() {
  store.dispatch('library/removeItem', props.item.id)
}
</script>

<template>
  <section class="library-item-panel glass-panel">
    <header class="library-item-panel__header">
      <h2 class="library-item-panel__title">Ma bibliothèque</h2>
      <span class="library-item-panel__added-at">Ajouté le {{ formatDate(item.addedAt) }}</span>
    </header>

    <div class="library-item-panel__row">
      <span class="library-item-panel__label">Ma note</span>
      <StarRating :model-value="item.rating ?? 0" clearable :size="28" @update:model-value="setRating" />
      <span class="library-item-panel__muted">{{ item.rating ? `${item.rating} / 5` : 'Pas encore noté' }}</span>
    </div>

    <div class="library-item-panel__row">
      <span class="badge" :class="item.read ? 'badge--success' : 'badge--warning'">
        {{ item.read ? 'Lu' : 'À lire' }}
      </span>
      <span v-if="item.favorite" class="badge">Favori</span>
    </div>

    <div class="library-item-panel__row">
      <button type="button" class="button button--secondary" @click="toggleRead">
        <AppIcon :path="item.read ? mdiBookOpenPageVariant : mdiCheckCircle" :size="18" />
        {{ item.read ? 'Marquer non lu' : 'Marquer lu' }}
      </button>
      <button type="button" class="button button--secondary" :aria-pressed="item.favorite" @click="toggleFavorite">
        <AppIcon :path="item.favorite ? mdiHeart : mdiHeartOutline" :size="18" class="library-item-panel__heart" />
        {{ item.favorite ? 'Retirer des favoris' : 'Ajouter aux favoris' }}
      </button>
      <button
        v-if="!isConfirmingRemoval"
        type="button"
        class="button button--danger"
        @click="isConfirmingRemoval = true"
      >
        <AppIcon :path="mdiDeleteOutline" :size="18" />
        Retirer de ma bibliothèque
      </button>
      <template v-else>
        <span class="library-item-panel__confirmation">Retirer ce livre ?</span>
        <button type="button" class="button button--danger" @click="removeItem">Oui</button>
        <button type="button" class="button button--secondary" @click="isConfirmingRemoval = false">Non</button>
      </template>
    </div>
  </section>
</template>

<style scoped>
.library-item-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 28px 32px;
}

.library-item-panel__header {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  justify-content: space-between;
  gap: 8px;
}

.library-item-panel__title {
  font-size: 1.3rem;
}

.library-item-panel__added-at,
.library-item-panel__muted {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.library-item-panel__row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.library-item-panel__label,
.library-item-panel__confirmation {
  font-weight: 600;
}

.library-item-panel__heart {
  color: var(--color-favorite);
}

@media (max-width: 700px) {
  .library-item-panel {
    padding: 20px;
  }
}
</style>
