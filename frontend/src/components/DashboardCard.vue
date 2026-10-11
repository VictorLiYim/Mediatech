<script setup>
defineProps({
  title: { type: String, required: true },
  linkTo: { type: Object, default: null },
  linkLabel: { type: String, default: 'Tout voir' },
})
</script>

<template>
  <section class="dashboard-card panel">
    <header class="dashboard-card__header">
      <h2 class="dashboard-card__title">{{ title }}</h2>
    </header>
    <div class="dashboard-card__content">
      <slot />
    </div>
    <RouterLink v-if="linkTo" :to="linkTo" class="dashboard-card__link">{{ linkLabel }}</RouterLink>
  </section>
</template>

<style scoped>
.dashboard-card {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 20px;
}

.dashboard-card__header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--color-surface-border);
}

.dashboard-card__title {
  font-size: 1.15rem;
  font-weight: 700;
}

.dashboard-card__content {
  flex: 1;
}

.dashboard-card__link {
  align-self: flex-end;
  font-weight: 600;
  font-size: 0.95rem;
  color: var(--color-primary);
}

.dashboard-card__link:hover {
  text-decoration: underline;
}
</style>
