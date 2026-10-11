<script setup>
defineProps({
  type: { type: String, default: 'error', validator: (value) => ['error', 'success'].includes(value) },
  message: { type: String, required: true },
})
</script>

<template>
  <p class="status-message" :class="`status-message--${type}`" :role="type === 'error' ? 'alert' : 'status'">
    {{ message }}
  </p>
</template>

<style scoped>
.status-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-left: 3px solid currentColor;
  border-radius: var(--radius-small);
}

.status-message--error {
  background: var(--color-status-error);
  color: var(--color-danger);
}

.status-message--success {
  background: var(--color-status-success);
  color: var(--color-success);
}
</style>
