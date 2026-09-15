<template>
  <div class="auth-hero">
    <article class="glass pa-6" style="width: 420px; max-width: 100%">
      <p class="text-subtitle mb-4">Créer un compte</p>

      <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4">
        {{ errorMessage }}
      </v-alert>

      <v-form v-model="formValid">
        <v-text-field
            v-model="userName"
            label="Nom d'utilisateur"
            variant="outlined"
            density="comfortable"
            :rules="userNameRules"
        />

        <v-text-field
            v-model="email"
            label="Email"
            variant="outlined"
            density="comfortable"
            :rules="emailRules"
        />

        <v-text-field
            v-model="password"
            label="Mot de passe"
            type="password"
            variant="outlined"
            density="comfortable"
            :rules="passwordRules"
        />
      </v-form>

      <v-btn
          variant="flat"
          block
          size="large"
          :disabled="!formValid"
          :loading="loading"
          class="btn-pill mt-2"
          @click="handleRegister"
      >
        S'inscrire
      </v-btn>

      <div class="text-center mt-4">
        Déjà inscrit ?
        <RouterLink to="/login">Se connecter</RouterLink>
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
const email = ref("");
const password = ref("");
const errorMessage = ref("");
const formValid = ref(false);
const loading = ref(false);

const userStore = useUserStore();
const router = useRouter();

const userNameRules = [
  (v) => !!v || "Nom d'utilisateur requis",
  (v) => (v.length >= 3 && v.length <= 50) || "Entre 3 et 50 caractères",
];

const emailRules = [
  (v) => !!v || "Email requis",
  (v) => /.+@.+\..+/.test(v) || "Email invalide",
];

const passwordRules = [
  (v) => !!v || "Mot de passe requis",
  (v) => v.length >= 8 || "Au moins 8 caractères",
];

async function handleRegister() {
  errorMessage.value = "";
  loading.value = true;
  try {
    await userStore.register({
      userName: userName.value,
      email: email.value,
      password: password.value,
    });
    router.push({ name: "home" });
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Inscription impossible");
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