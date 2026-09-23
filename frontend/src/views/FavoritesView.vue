<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { mdiHeartOutline } from '@mdi/js'
import EmptyState from '@/components/EmptyState.vue'
import LibraryItemCard from '@/components/LibraryItemCard.vue'

const store = useStore()

const favoriteItems = computed(() => store.getters['library/favoriteItems'])
</script>

<template>
  <div class="page-container favorites-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Mes favoris</h1>
        <p class="page-subtitle">{{ favoriteItems.length }} livre(s) coup de cœur</p>
      </div>
    </header>

    <EmptyState
      v-if="favoriteItems.length === 0"
      :icon-path="mdiHeartOutline"
      title="Aucun favori pour l'instant"
      message="Cliquez sur le cœur d'un livre de votre bibliothèque pour l'ajouter ici."
    >
      <RouterLink :to="{ name: 'library' }" class="button button--primary">Aller à ma bibliothèque</RouterLink>
    </EmptyState>

    <div v-else class="book-grid">
      <LibraryItemCard v-for="item in favoriteItems" :key="item.id" :item="item" />
    </div>
  </div>
</template>
