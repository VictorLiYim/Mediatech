<template>
  <v-app>
    <v-app-bar color="primary" density="comfortable">
      <v-app-bar-title>Médiathèque</v-app-bar-title>
      <v-spacer />
      <template v-if="userStore.isLoggedIn">
        <span class="mr-4">{{ userStore.currentUser.userName }}</span>
        <v-btn variant="text" @click="handleLogout">Déconnexion</v-btn>
      </template>
    </v-app-bar>

    <v-main>
      <RouterView />
    </v-main>
  </v-app>
</template>

<script setup>
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const router = useRouter();

function handleLogout() {
  userStore.logout();
  router.push({ name: "login" });
}
</script>