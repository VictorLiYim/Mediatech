<template>
  <v-app>
    <v-app-bar height="64" flat elevation="0" class="glass nav-bar">
      <div v-if="userStore.isLoggedIn" class="nav-links">
        <v-btn variant="text" class="btn-pill icon-ink nav-link" :to="{ name: 'profile' }">Profil</v-btn>
        <v-btn variant="text" class="btn-pill icon-ink nav-link" :to="{ name: 'books' }">Catalogue</v-btn>
        <v-btn variant="text" class="btn-pill icon-ink nav-link" :to="{ name: 'home' }">Accueil</v-btn>
        <v-btn variant="text" class="btn-pill icon-ink nav-link" :to="{ name: 'loans' }">Mes emprunts</v-btn>
        <v-btn variant="text" class="btn-pill icon-ink nav-link" :to="{ name: 'events' }">Événements</v-btn>
      </div>
    </v-app-bar>

    <v-main class="app-main">
      <RouterView />
    </v-main>
  </v-app>
</template>

<script setup>
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
</script>

<style scoped>
.nav-bar {
  border-radius: 0 !important;
  border-top: none !important;
  border-left: none !important;
  border-right: none !important;
  padding: 0 24px;
}

.nav-links {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
}

.nav-link {
  font-size: 0.85rem;
}
</style>
