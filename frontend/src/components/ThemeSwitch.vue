<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { mdiWeatherNight, mdiWeatherSunny } from '@mdi/js'
import AppIcon from './AppIcon.vue'

const store = useStore()
const isDark = computed(() => store.getters['theme/isDark'])
</script>

<template>
  <button
    type="button"
    role="switch"
    class="theme-switch"
    :aria-checked="isDark"
    :title="isDark ? 'Passer en mode clair' : 'Passer en mode sombre'"
    @click="store.dispatch('theme/toggleTheme')"
  >
    <AppIcon :path="mdiWeatherSunny" :size="18" class="theme-switch__icon" :class="{ 'theme-switch__icon--active': !isDark }" />
    <span class="theme-switch__track" :class="{ 'theme-switch__track--on': isDark }">
      <span class="theme-switch__thumb" />
    </span>
    <AppIcon :path="mdiWeatherNight" :size="18" class="theme-switch__icon" :class="{ 'theme-switch__icon--active': isDark }" />
    <span class="theme-switch__label">Mode sombre</span>
  </button>
</template>

<style scoped>
.theme-switch {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 8px;
  border: none;
  border-radius: var(--radius-pill);
  background: transparent;
  cursor: pointer;
}

.theme-switch:focus-visible {
  outline: 3px solid var(--color-primary);
}

.theme-switch__icon {
  color: var(--color-text-muted);
  opacity: 0.6;
  transition: opacity var(--transition-fast), color var(--transition-fast);
}

.theme-switch__icon--active {
  color: var(--color-primary);
  opacity: 1;
}

.theme-switch__track {
  position: relative;
  width: 40px;
  height: 22px;
  border-radius: var(--radius-pill);
  background: var(--color-primary-soft);
  border: 1px solid var(--color-surface-border);
  transition: background var(--transition-fast);
}

.theme-switch__track--on {
  background: var(--color-primary);
}

.theme-switch__thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
  transition: transform var(--transition-fast);
}

.theme-switch__track--on .theme-switch__thumb {
  transform: translateX(18px);
}

/* Texte pour les lecteurs d'écran uniquement */
.theme-switch__label {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip: rect(0 0 0 0);
  white-space: nowrap;
}
</style>
