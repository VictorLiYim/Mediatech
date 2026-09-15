<template>
  <div class="auth-hero">
    <article class="glass pa-6" style="width: 420px; max-width: 100%">
      <p class="text-subtitle mb-4">Connexion</p>

      <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4">
        {{ errorMessage }}
      </v-alert>

      <v-text-field
          v-model="userName"
          label="Nom d'utilisateur"
          variant="outlined"
          density="comfortable"
          @keyup.enter="handleLogin"
      />

      <v-text-field
          v-model="password"
          label="Mot de passe"
          type="password"
          variant="outlined"
          density="comfortable"
          @keyup.enter="handleLogin"
      />

      <v-btn
          variant="flat"
          block
          size="large"
          :loading="loading"
          class="btn-pill mt-2"
          @click="handleLogin"
      >
        Se connecter
      </v-btn>

      <div class="text-center mt-4">
        Pas encore de compte ?
        <RouterLink to="/register">Créer un compte</RouterLink>
      </div>
    </article>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { extractErrorMessage } from "@/api/client";

const userName = ref("");
const password = ref("");
const errorMessage = ref("");
const loading = ref(false);

const userStore = useUserStore();
const router = useRouter();

async function handleLogin() {
  errorMessage.value = "";
  loading.value = true;
  try {
    await userStore.login({ userName: userName.value, password: password.value });
    router.push({ name: "home" });
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Identifiants invalides");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.auth-hero {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}
</style>