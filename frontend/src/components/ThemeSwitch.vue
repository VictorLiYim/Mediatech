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
  outline: 2px solid var(--color-header-text);
}

.theme-switch__icon {
  color: var(--color-header-text-muted);
  opacity: 0.7;
  transition: opacity var(--transition-fast), color var(--transition-fast);
}

.theme-switch__icon--active {
  color: var(--color-header-text);
  opacity: 1;
}

.theme-switch__track {
  position: relative;
  width: 40px;
  height: 22px;
  border-radius: var(--radius-pill);
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.35);
  transition: background var(--transition-fast);
}

.theme-switch__track--on {
  background: rgba(255, 255, 255, 0.3);
}

.theme-switch__thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #ffffff;
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
