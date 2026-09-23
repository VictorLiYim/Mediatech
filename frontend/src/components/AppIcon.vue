<script setup>
defineProps({
  path: { type: String, required: true },
  size: { type: [Number, String], default: 20 },
  label: { type: String, default: '' },
})
</script>

<template>
  <svg
    class="app-icon"
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    :role="label ? 'img' : undefined"
    :aria-label="label || undefined"
    :aria-hidden="label ? undefined : 'true'"
  >
    <path :d="path" fill="currentColor" />
  </svg>
</template>

<style scoped>
.app-icon {
  flex-shrink: 0;
  display: inline-block;
  vertical-align: middle;
}
</style>
