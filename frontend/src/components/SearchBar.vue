<script setup>
import { mdiMagnify } from '@mdi/js'
import AppIcon from './AppIcon.vue'

const query = defineModel({ type: String, default: '' })

defineProps({
  placeholder: { type: String, default: 'Chercher un livre' },
  isLoading: { type: Boolean, default: false },
})

const emit = defineEmits(['search'])

function submitSearch() {
  const trimmed = query.value.trim()
  if (trimmed) emit('search', trimmed)
}
</script>

<template>
  <form class="search-bar" role="search" @submit.prevent="submitSearch">
    <label class="visually-hidden" for="search-bar-input">{{ placeholder }}</label>
    <input
      id="search-bar-input"
      v-model="query"
      class="search-bar__input"
      type="search"
      :placeholder="placeholder"
      autocomplete="off"
    >
    <button type="submit" class="search-bar__button" :disabled="isLoading || !query.trim()">
      <span v-if="isLoading" class="search-bar__spinner" aria-hidden="true" />
      <AppIcon v-else :path="mdiMagnify" :size="24" label="Rechercher" />
    </button>
  </form>
</template>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  padding: 6px 6px 6px 22px;
  background: var(--color-surface-strong);
  border: 1px solid var(--color-surface-border);
  border-radius: var(--radius-pill);
  box-shadow: var(--shadow-card);
  backdrop-filter: var(--blur-glass);
  -webkit-backdrop-filter: var(--blur-glass);
  transition: box-shadow var(--transition-fast);
}

.search-bar:focus-within {
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.6), var(--shadow-card);
}

.search-bar__input {
  flex: 1;
  min-width: 0;
  padding: 10px 0;
  border: none;
  background: transparent;
  font-size: 1.05rem;
  outline: none;
}

.search-bar__input::placeholder {
  color: var(--color-text-muted);
  opacity: 0.8;
}

.search-bar__button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: var(--color-text);
  cursor: pointer;
  transition: background var(--transition-fast);
}

.search-bar__button:not(:disabled):hover {
  background: var(--color-primary-soft);
}

.search-bar__button:disabled {
  opacity: 0.5;
  cursor: default;
}

.search-bar__spinner {
  width: 20px;
  height: 20px;
  border: 3px solid var(--color-primary-soft);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: search-bar-spin 0.8s linear infinite;
}

@keyframes search-bar-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
