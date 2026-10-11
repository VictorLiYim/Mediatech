<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import {
  mdiCheckCircle, mdiClose, mdiDeleteOutline, mdiHeart, mdiHeartOutline,
} from '@mdi/js'
import AppIcon from './AppIcon.vue'

const ICONS = {
  success: mdiCheckCircle,
  remove: mdiDeleteOutline,
  favorite: mdiHeart,
  unfavorite: mdiHeartOutline,
}

const store = useStore()
const toasts = computed(() => store.getters['notifications/toasts'])
</script>

<template>
  <div class="toast-container" aria-live="polite">
    <TransitionGroup name="toast">
      <div v-for="toast in toasts" :key="toast.id" class="toast" :class="`toast--${toast.kind}`" role="status">
        <AppIcon :path="ICONS[toast.kind] ?? mdiCheckCircle" :size="20" class="toast__icon" />
        <span class="toast__message">{{ toast.message }}</span>
        <button
          v-if="toast.undo"
          type="button"
          class="toast__undo-button"
          @click="store.dispatch('notifications/undo', toast)"
        >
          Annuler
        </button>
        <button type="button" class="toast__close-button" @click="store.dispatch('notifications/dismiss', toast.id)">
          <AppIcon :path="mdiClose" :size="18" label="Fermer la notification" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-container {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 60;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  width: min(420px, calc(100vw - 32px));
  pointer-events: none;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px 12px 12px 16px;
  border: 1px solid var(--color-surface-border);
  border-left: 3px solid var(--color-success);
  border-radius: var(--radius-small);
  background: var(--color-modal);
  box-shadow: var(--shadow-overlay);
  pointer-events: auto;
}

.toast__icon {
  flex-shrink: 0;
  color: var(--color-success);
}

.toast--remove {
  border-left-color: var(--color-danger);
}

.toast--remove .toast__icon {
  color: var(--color-danger);
}

.toast--favorite,
.toast--unfavorite {
  border-left-color: var(--color-favorite);
}

.toast--favorite .toast__icon,
.toast--unfavorite .toast__icon {
  color: var(--color-favorite);
}

.toast__message {
  flex: 1;
  min-width: 0;
  font-size: 0.95rem;
}

.toast__undo-button {
  flex-shrink: 0;
  padding: 4px 8px;
  border: none;
  border-radius: var(--radius-small);
  background: transparent;
  color: var(--color-primary);
  font-weight: 600;
  text-decoration: underline;
  cursor: pointer;
}

.toast__undo-button:hover {
  background: var(--color-primary-soft);
}

.toast__close-button {
  flex-shrink: 0;
  display: inline-flex;
  padding: 4px;
  border: none;
  border-radius: var(--radius-small);
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
}

.toast__close-button:hover {
  background: var(--color-primary-soft);
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 150ms ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
}

@media (max-width: 600px) {
  .toast-container {
    right: 16px;
    left: 16px;
    bottom: 16px;
    width: auto;
  }
}
</style>
