<script setup>
import AppIcon from './AppIcon.vue'

defineProps({
  title: { type: String, required: true },
  iconPath: { type: String, required: true },
  linkTo: { type: Object, default: null },
  linkLabel: { type: String, default: 'Tout voir' },
})
</script>

<template>
  <section class="dashboard-card glass-panel">
    <header class="dashboard-card__header">
      <AppIcon :path="iconPath" :size="22" class="dashboard-card__icon" />
      <h2 class="dashboard-card__title">{{ title }}</h2>
    </header>
    <div class="dashboard-card__content">
      <slot />
    </div>
    <RouterLink v-if="linkTo" :to="linkTo" class="dashboard-card__link">{{ linkLabel }} →</RouterLink>
  </section>
</template>

<style scoped>
.dashboard-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
}

.dashboard-card__header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dashboard-card__icon {
  color: var(--color-primary);
}

.dashboard-card__title {
  font-size: 1.25rem;
  font-weight: 700;
}

.dashboard-card__content {
  flex: 1;
}

.dashboard-card__link {
  align-self: flex-end;
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--color-primary);
  text-decoration: none;
}

.dashboard-card__link:hover {
  text-decoration: underline;
}
</style>
