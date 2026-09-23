<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  src: { type: String, default: null },
  title: { type: String, required: true },
  size: { type: String, default: 'medium', validator: (value) => ['small', 'medium', 'large'].includes(value) },
})

const hasError = ref(false)
watch(() => props.src, () => {
  hasError.value = false
})

const showImage = computed(() => props.src && !hasError.value)
const initials = computed(() =>
  props.title
    .split(/\s+/)
    .filter((word) => word.length > 2)
    .slice(0, 2)
    .map((word) => word[0].toUpperCase())
    .join('') || props.title.slice(0, 1).toUpperCase(),
)
</script>

<template>
  <div class="book-cover" :class="`book-cover--${size}`">
    <img
      v-if="showImage"
      class="book-cover__image"
      :src="src"
      :alt="`Couverture de ${title}`"
      loading="lazy"
      @error="hasError = true"
    >
    <div v-else class="book-cover__placeholder" aria-hidden="true">
      <span class="book-cover__initials">{{ initials }}</span>
    </div>
  </div>
</template>

<style scoped>
.book-cover {
  flex-shrink: 0;
  aspect-ratio: 2 / 3;
  overflow: hidden;
  border-radius: var(--radius-small);
  box-shadow: 0 6px 18px rgba(38, 25, 84, 0.25);
  background: var(--color-primary-soft);
}

.book-cover--small {
  width: 56px;
}

.book-cover--medium {
  width: 100%;
}

.book-cover--large {
  width: 220px;
  max-width: 100%;
}

.book-cover__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-cover__placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #8b7fc7, #4f3f8f);
}

.book-cover__initials {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 700;
  font-size: 1.4rem;
  letter-spacing: 0.05em;
}

.book-cover--small .book-cover__initials {
  font-size: 0.9rem;
}
</style>
