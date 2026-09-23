<script setup>
import { onBeforeUnmount, onMounted } from 'vue'
import { mdiClose } from '@mdi/js'
import AppIcon from './AppIcon.vue'

defineProps({
  title: { type: String, required: true },
})

const emit = defineEmits(['close'])

function closeOnEscape(event) {
  if (event.key === 'Escape') emit('close')
}

onMounted(() => document.addEventListener('keydown', closeOnEscape))
onBeforeUnmount(() => document.removeEventListener('keydown', closeOnEscape))
</script>

<template>
  <Teleport to="body">
    <div class="modal-dialog__backdrop" @click.self="emit('close')">
      <section class="modal-dialog glass-panel" role="dialog" aria-modal="true" :aria-label="title">
        <header class="modal-dialog__header">
          <h2 class="modal-dialog__title">{{ title }}</h2>
          <button type="button" class="modal-dialog__close-button" @click="emit('close')">
            <AppIcon :path="mdiClose" :size="22" label="Fermer" />
          </button>
        </header>
        <div class="modal-dialog__body">
          <slot />
        </div>
      </section>
    </div>
  </Teleport>
</template>

<style scoped>
.modal-dialog__backdrop {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: rgba(30, 20, 60, 0.45);
}

.modal-dialog {
  width: 100%;
  max-width: 640px;
  max-height: calc(100vh - 32px);
  overflow-y: auto;
  background: rgba(245, 242, 255, 0.95);
}

.modal-dialog__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 20px 24px 0;
}

.modal-dialog__title {
  font-size: 1.25rem;
}

.modal-dialog__close-button {
  display: inline-flex;
  padding: 6px;
  border: none;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.modal-dialog__close-button:hover {
  background: var(--color-primary-soft);
}

.modal-dialog__body {
  padding: 16px 24px 24px;
}
</style>
