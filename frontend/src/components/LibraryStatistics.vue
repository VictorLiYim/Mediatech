<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import StarRating from './StarRating.vue'

// Bandeau de stats de la bibliothèque perso (profil + /library)
const props = defineProps({
  // Tuiles cliquables vers /library et /favorites
  linked: { type: Boolean, default: false },
})

const store = useStore()

const averageRating = computed(() => store.getters['library/averageRating'])
const ratedCount = computed(() => store.getters['library/ratedItems'].length)

const counters = computed(() => [
  { label: 'Livres', value: store.getters['library/itemCount'], to: { name: 'library' } },
  { label: 'Lus', value: store.getters['library/readItems'].length, to: { name: 'library' } },
  { label: 'À lire', value: store.getters['library/unreadItems'].length, to: { name: 'library' } },
  { label: 'Favoris', value: store.getters['library/favoriteItems'].length, to: { name: 'favorites' } },
])

const averageLabel = computed(() => (averageRating.value === null
  ? '—'
  : `${averageRating.value.toLocaleString('fr-FR', { maximumFractionDigits: 1 })} / 5`))

const tileComponent = computed(() => (props.linked ? 'RouterLink' : 'div'))
</script>

<template>
  <div class="library-statistics">
    <component
      :is="tileComponent"
      v-for="counter in counters"
      :key="counter.label"
      :to="linked ? counter.to : undefined"
      class="library-statistics__tile"
      :class="{ 'library-statistics__tile--linked': linked }"
    >
      <strong class="library-statistics__value">{{ counter.value }}</strong>
      <span class="library-statistics__label">{{ counter.label }}</span>
    </component>

    <div class="library-statistics__tile">
      <strong class="library-statistics__value">{{ averageLabel }}</strong>
      <StarRating v-if="averageRating !== null" :model-value="averageRating" readonly :size="16" />
      <span class="library-statistics__label">
        {{ ratedCount ? `Note moyenne · ${ratedCount} livre(s) noté(s)` : 'Aucun livre noté' }}
      </span>
    </div>
  </div>
</template>

<style scoped>
.library-statistics {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  gap: 12px;
}

.library-statistics__tile {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 16px 8px;
  border-radius: var(--radius-medium);
  background: var(--color-tile);
  text-align: center;
  text-decoration: none;
}

.library-statistics__tile--linked {
  transition: background var(--transition-fast);
}

.library-statistics__tile--linked:hover {
  background: var(--color-tile-hover);
}

.library-statistics__value {
  font-size: 1.8rem;
  line-height: 1.1;
  color: var(--color-primary);
}

.library-statistics__label {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}
</style>
