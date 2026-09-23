<script setup>
import { computed, ref } from 'vue'
import { mdiStar, mdiStarHalfFull, mdiStarOutline } from '@mdi/js'
import AppIcon from './AppIcon.vue'

const rating = defineModel({ type: Number, default: 0 })

const props = defineProps({
  readonly: { type: Boolean, default: false },
  size: { type: Number, default: 20 },
})

const hoveredValue = ref(0)
const displayedValue = computed(() => hoveredValue.value || rating.value)

function iconFor(position) {
  if (displayedValue.value >= position) return mdiStar
  if (props.readonly && displayedValue.value >= position - 0.5) return mdiStarHalfFull
  return mdiStarOutline
}
</script>

<template>
  <div
    class="star-rating"
    :class="{ 'star-rating--interactive': !readonly }"
    :role="readonly ? 'img' : 'radiogroup'"
    :aria-label="readonly ? `Note : ${rating.toFixed(1)} sur 5` : 'Choisir une note'"
    @mouseleave="hoveredValue = 0"
  >
    <template v-if="readonly">
      <AppIcon v-for="position in 5" :key="position" :path="iconFor(position)" :size="size" class="star-rating__star" />
    </template>
    <template v-else>
      <button
        v-for="position in 5"
        :key="position"
        type="button"
        class="star-rating__button"
        role="radio"
        :aria-checked="rating === position"
        :aria-label="`${position} sur 5`"
        @mouseenter="hoveredValue = position"
        @click="rating = position"
      >
        <AppIcon :path="iconFor(position)" :size="size" class="star-rating__star" />
      </button>
    </template>
  </div>
</template>

<style scoped>
.star-rating {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.star-rating__star {
  color: var(--color-star);
}

.star-rating__button {
  display: inline-flex;
  padding: 2px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: transform var(--transition-fast);
}

.star-rating__button:hover {
  transform: scale(1.15);
}
</style>
