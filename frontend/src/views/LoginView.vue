<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { extractErrorMessage } from '@/api/client'
import StatusMessage from '@/components/StatusMessage.vue'

const store = useStore()
const route = useRoute()
const router = useRouter()

const credentials = reactive({ userName: '', password: '' })
const errorMessage = ref('')
const isSubmitting = ref(false)

async function submitLogin() {
  errorMessage.value = ''
  isSubmitting.value = true
  try {
    await store.dispatch('auth/login', { ...credentials })
    router.replace(route.query.redirect ?? { name: 'home' })
  } catch (error) {
    errorMessage.value = error.response?.status === 401
      ? "Nom d'utilisateur ou mot de passe incorrect."
      : extractErrorMessage(error, 'Connexion impossible')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="page-container auth-page">
    <form class="auth-page__card glass-panel" @submit.prevent="submitLogin">
      <h1 class="auth-page__title">Connexion</h1>
      <p class="auth-page__subtitle">Retrouvez votre bibliothèque, vos favoris et vos emprunts.</p>

      <div class="form-field">
        <label class="form-label" for="login-user-name">Nom d'utilisateur</label>
        <input id="login-user-name" v-model="credentials.userName" class="form-input" autocomplete="username" required>
      </div>
      <div class="form-field">
        <label class="form-label" for="login-password">Mot de passe</label>
        <input
          id="login-password"
          v-model="credentials.password"
          class="form-input"
          type="password"
          autocomplete="current-password"
          required
        >
      </div>

      <StatusMessage v-if="errorMessage" :message="errorMessage" />

      <button type="submit" class="button button--primary auth-page__submit" :disabled="isSubmitting">
        {{ isSubmitting ? 'Connexion…' : 'Se connecter' }}
      </button>
      <p class="auth-page__switch">
        Pas encore de compte ?
        <RouterLink :to="{ name: 'register', query: route.query }">Créer un compte</RouterLink>
      </p>
    </form>
  </div>
</template>

<style scoped src="@/assets/styles/auth-page.css"></style>
