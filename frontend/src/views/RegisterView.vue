<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { extractErrorMessage } from '@/api/client'
import StatusMessage from '@/components/StatusMessage.vue'

const MIN_PASSWORD_LENGTH = 8

const store = useStore()
const route = useRoute()
const router = useRouter()

const account = reactive({ userName: '', email: '', password: '', passwordConfirmation: '' })
const errorMessage = ref('')
const fieldErrors = ref({})
const isSubmitting = ref(false)

const passwordMismatch = computed(() =>
  account.passwordConfirmation.length > 0 && account.password !== account.passwordConfirmation,
)

async function submitRegistration() {
  errorMessage.value = ''
  fieldErrors.value = {}
  if (passwordMismatch.value) return
  isSubmitting.value = true
  try {
    await store.dispatch('auth/register', {
      userName: account.userName.trim(),
      email: account.email.trim(),
      password: account.password,
    })
    router.replace(route.query.redirect ?? { name: 'home' })
  } catch (error) {
    fieldErrors.value = error.response?.data?.fieldErrors ?? {}
    errorMessage.value = extractErrorMessage(error, 'Inscription impossible')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="page-container auth-page">
    <form class="auth-page__card glass-panel" @submit.prevent="submitRegistration">
      <h1 class="auth-page__title">Créer un compte</h1>
      <p class="auth-page__subtitle">Gratuit, pour emprunter et construire votre bibliothèque.</p>

      <div class="form-field">
        <label class="form-label" for="register-user-name">Nom d'utilisateur</label>
        <input
          id="register-user-name"
          v-model="account.userName"
          class="form-input"
          autocomplete="username"
          minlength="3"
          maxlength="50"
          required
        >
        <span v-if="fieldErrors.userName" class="form-error">{{ fieldErrors.userName }}</span>
      </div>
      <div class="form-field">
        <label class="form-label" for="register-email">E-mail</label>
        <input id="register-email" v-model="account.email" class="form-input" type="email" autocomplete="email" required>
        <span v-if="fieldErrors.email" class="form-error">{{ fieldErrors.email }}</span>
      </div>
      <div class="form-field">
        <label class="form-label" for="register-password">Mot de passe ({{ MIN_PASSWORD_LENGTH }} caractères minimum)</label>
        <input
          id="register-password"
          v-model="account.password"
          class="form-input"
          type="password"
          autocomplete="new-password"
          :minlength="MIN_PASSWORD_LENGTH"
          required
        >
        <span v-if="fieldErrors.password" class="form-error">{{ fieldErrors.password }}</span>
      </div>
      <div class="form-field">
        <label class="form-label" for="register-password-confirmation">Confirmer le mot de passe</label>
        <input
          id="register-password-confirmation"
          v-model="account.passwordConfirmation"
          class="form-input"
          type="password"
          autocomplete="new-password"
          required
        >
        <span v-if="passwordMismatch" class="form-error">Les mots de passe ne correspondent pas.</span>
      </div>

      <StatusMessage v-if="errorMessage" :message="errorMessage" />

      <button type="submit" class="button button--primary auth-page__submit" :disabled="isSubmitting || passwordMismatch">
        {{ isSubmitting ? 'Création…' : 'Créer mon compte' }}
      </button>
      <p class="auth-page__switch">
        Déjà inscrit ?
        <RouterLink :to="{ name: 'login', query: route.query }">Se connecter</RouterLink>
      </p>
    </form>
  </div>
</template>

<style scoped src="@/assets/styles/auth-page.css"></style>
