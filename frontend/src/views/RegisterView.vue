<template>
  <v-container class="d-flex justify-center align-center" style="min-height: 80vh">
    <v-card width="420" class="pa-6">
      <v-card-title class="text-h5 mb-4">Créer un compte</v-card-title>

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
          color="primary"
          block
          size="large"
          :disabled="!formValid"
          :loading="loading"
          class="mt-2"
          @click="handleRegister"
      >
        S'inscrire
      </v-btn>

      <div class="text-center mt-4">
        Déjà inscrit ?
        <RouterLink to="/login">Se connecter</RouterLink>
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