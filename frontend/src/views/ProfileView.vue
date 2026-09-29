<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiAccountCircle } from '@mdi/js'
import { fetchUser } from '@/api/usersApi'
import AppIcon from '@/components/AppIcon.vue'
import LibraryStatistics from '@/components/LibraryStatistics.vue'

const store = useStore()
const router = useRouter()

const profile = ref(null)
const isAdmin = computed(() => store.getters['auth/isAdmin'])

function logout() {
  store.dispatch('auth/logout')
  router.push({ name: 'home' })
}

onMounted(async () => {
  try {
    profile.value = await fetchUser(store.getters['auth/currentUserId'])
  } catch {
    profile.value = null
  }
})
</script>

<template>
  <div class="page-container profile-view">
    <section class="profile-view__card glass-panel">
      <AppIcon :path="mdiAccountCircle" :size="88" class="profile-view__avatar" />
      <h1 class="profile-view__name">{{ store.getters['auth/userName'] }}</h1>
      <p v-if="profile" class="profile-view__email">{{ profile.email }}</p>
      <span class="badge" :class="{ 'badge--warning': isAdmin }">{{ isAdmin ? 'Administrateur' : 'Lecteur' }}</span>

      <LibraryStatistics linked class="profile-view__statistics" />

      <div class="profile-view__actions">
        <RouterLink v-if="isAdmin" :to="{ name: 'admin-stock' }" class="button button--secondary">Gérer le stock</RouterLink>
        <button type="button" class="button button--danger" @click="logout">Se déconnecter</button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.profile-view__card {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 40px 24px;
  text-align: center;
}

.profile-view__avatar {
  color: var(--color-primary);
}

.profile-view__name {
  font-size: 1.6rem;
}

.profile-view__email {
  color: var(--color-text-muted);
}

.profile-view__statistics {
  margin: 20px 0;
}

.profile-view__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
}
</style>
