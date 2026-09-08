<template>
  <v-container class="mt-8">
    <h1 class="text-h4 mb-2">Bonjour {{ userStore.currentUser.userName }}</h1>
    <p class="text-medium-emphasis">{{ userStore.currentUser.email }}</p>

    <v-alert type="success" variant="tonal" class="mt-6">
      Connexion réussie. Le catalogue arrivera ici une fois mediatheque-service en place.
    </v-alert>
  </v-container>
</template>

<script setup>
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
</script>