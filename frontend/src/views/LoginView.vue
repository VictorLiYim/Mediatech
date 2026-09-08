<template>
  <v-container class="d-flex justify-center align-center" style="min-height: 80vh">
    <v-card width="420" class="pa-6">
      <v-card-title class="text-h5 mb-4">Connexion</v-card-title>

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
          color="primary"
          block
          size="large"
          :loading="loading"
          class="mt-2"
          @click="handleLogin"
      >
        Se connecter
      </v-btn>

      <div class="text-center mt-4">
        Pas encore de compte ?
        <RouterLink to="/register">Créer un compte</RouterLink>
      </div>
    </v-card>
  </v-container>
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