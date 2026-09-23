<script setup>
import AppIcon from './AppIcon.vue'

defineProps({
  iconPath: { type: String, required: true },
  title: { type: String, required: true },
  message: { type: String, default: '' },
})
</script>

<template>
  <div class="empty-state glass-panel">
    <AppIcon :path="iconPath" :size="48" class="empty-state__icon" />
    <h2 class="empty-state__title">{{ title }}</h2>
    <p v-if="message" class="empty-state__message">{{ message }}</p>
    <div v-if="$slots.default" class="empty-state__actions">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 48px 24px;
  text-align: center;
}

.empty-state__icon {
  color: var(--color-primary);
  opacity: 0.7;
}

.empty-state__title {
  font-size: 1.2rem;
}

.empty-state__message {
  max-width: 420px;
  color: var(--color-text-muted);
}

.empty-state__actions {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
}
</style>
