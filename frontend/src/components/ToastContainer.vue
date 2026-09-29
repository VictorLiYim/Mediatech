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
  border-radius: var(--radius-medium);
  background: var(--color-modal);
  box-shadow: var(--shadow-card-hover);
  pointer-events: auto;
}

.toast__icon {
  flex-shrink: 0;
  color: var(--color-success);
}

.toast--remove .toast__icon {
  color: var(--color-danger);
}

.toast--favorite .toast__icon,
.toast--unfavorite .toast__icon {
  color: var(--color-favorite);
}

.toast__message {
  flex: 1;
  min-width: 0;
  font-size: 0.92rem;
  font-weight: 500;
}

.toast__undo-button {
  flex-shrink: 0;
  padding: 6px 10px;
  border: none;
  border-radius: var(--radius-pill);
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
}

.toast__undo-button:hover {
  background: var(--color-navigation-active);
}

.toast__close-button {
  flex-shrink: 0;
  display: inline-flex;
  padding: 4px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
}

.toast__close-button:hover {
  background: var(--color-primary-soft);
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 200ms ease, transform 200ms ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(24px);
}

@media (max-width: 600px) {
  .toast-container {
    right: 16px;
    left: 16px;
    bottom: 16px;
    width: auto;
  }

  .toast-enter-from,
  .toast-leave-to {
    transform: translateY(24px);
  }
}
</style>
