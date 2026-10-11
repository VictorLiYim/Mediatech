<script setup>
import EmptyState from '@/components/EmptyState.vue'
</script>

<template>
  <div class="page-container">
    <EmptyState title="Page introuvable" message="Cette page n'existe pas ou a été déplacée.">
      <RouterLink :to="{ name: 'home' }" class="button button--primary">Retour à l'accueil</RouterLink>
    </EmptyState>
  </div>
</template>
