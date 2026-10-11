<script setup>
defineProps({
  label: { type: String, default: 'Chargement…' },
})
</script>

<template>
  <div class="loading-spinner" role="status">
    <span class="loading-spinner__circle" aria-hidden="true" />
    <span class="loading-spinner__label">{{ label }}</span>
  </div>
</template>

<style scoped>
.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 32px;
  color: var(--color-text-muted);
}

.loading-spinner__circle {
  width: 26px;
  height: 26px;
  border: 3px solid var(--color-primary-soft);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: loading-spinner-rotate 0.8s linear infinite;
}

@keyframes loading-spinner-rotate {
  to {
    transform: rotate(360deg);
  }
}
</style>
