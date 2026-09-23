<script setup>
import { mdiAlertCircleOutline, mdiCheckCircleOutline } from '@mdi/js'
import AppIcon from './AppIcon.vue'

defineProps({
  type: { type: String, default: 'error', validator: (value) => ['error', 'success'].includes(value) },
  message: { type: String, required: true },
})
</script>

<template>
  <p class="status-message" :class="`status-message--${type}`" :role="type === 'error' ? 'alert' : 'status'">
    <AppIcon :path="type === 'error' ? mdiAlertCircleOutline : mdiCheckCircleOutline" :size="20" />
    <span>{{ message }}</span>
  </p>
</template>

<style scoped>
.status-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: var(--radius-small);
  font-weight: 500;
}

.status-message--error {
  background: rgba(255, 235, 235, 0.9);
  color: var(--color-danger);
}

.status-message--success {
  background: rgba(230, 250, 238, 0.9);
  color: var(--color-success);
}
</style>
