<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiAccountCircle, mdiBookshelf, mdiClose, mdiLogout, mdiMenu } from '@mdi/js'
import AppIcon from './AppIcon.vue'

const store = useStore()
const route = useRoute()
const router = useRouter()

const isMenuOpen = ref(false)
const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const isAdmin = computed(() => store.getters['auth/isAdmin'])
const userName = computed(() => store.getters['auth/userName'])
const favoriteCount = computed(() => store.getters['library/favoriteItems'].length)

const navigationLinks = computed(() => {
  const links = [
    { to: { name: 'home' }, label: 'Accueil' },
    { to: { name: 'search' }, label: 'Rechercher' },
    { to: { name: 'catalog' }, label: 'Catalogue' },
    { to: { name: 'events' }, label: 'Événements' },
  ]
  if (isAuthenticated.value) {
    links.push(
      { to: { name: 'library' }, label: 'Ma bibliothèque' },
      { to: { name: 'favorites' }, label: 'Favoris', count: favoriteCount.value },
      { to: { name: 'loans' }, label: 'Mes emprunts' },
    )
  }
  if (isAdmin.value) {
    links.push({ to: { name: 'admin-stock' }, label: 'Stock' })
  }
  return links
})

watch(() => route.fullPath, () => {
  isMenuOpen.value = false
})

function logout() {
  store.dispatch('auth/logout')
  router.push({ name: 'home' })
}
</script>

<template>
  <header class="app-header">
    <div class="app-header__inner">
      <RouterLink :to="{ name: 'home' }" class="app-header__brand">
        <AppIcon :path="mdiBookshelf" :size="26" />
        <span>Médiatech</span>
      </RouterLink>

      <button
        type="button"
        class="app-header__menu-toggle"
        :aria-expanded="isMenuOpen"
        aria-controls="main-navigation"
        @click="isMenuOpen = !isMenuOpen"
      >
        <AppIcon :path="isMenuOpen ? mdiClose : mdiMenu" :size="24" label="Menu" />
      </button>

      <nav
        id="main-navigation"
        class="app-header__navigation"
        :class="{ 'app-header__navigation--open': isMenuOpen }"
      >
        <RouterLink
          v-for="link in navigationLinks"
          :key="link.label"
          :to="link.to"
          class="app-header__link"
          active-class="app-header__link--active"
          :exact-active-class="link.to.name === 'home' ? 'app-header__link--active' : undefined"
        >
          {{ link.label }}
          <span v-if="link.count" class="app-header__link-count">{{ link.count }}</span>
        </RouterLink>

        <div class="app-header__account">
          <template v-if="isAuthenticated">
            <RouterLink :to="{ name: 'profile' }" class="app-header__profile-link" active-class="app-header__link--active">
              <AppIcon :path="mdiAccountCircle" :size="22" />
              <span>{{ userName }}</span>
            </RouterLink>
            <button type="button" class="app-header__logout-button" title="Se déconnecter" @click="logout">
              <AppIcon :path="mdiLogout" :size="20" label="Se déconnecter" />
            </button>
          </template>
          <RouterLink v-else :to="{ name: 'login' }" class="button button--primary button--small">
            Se connecter
          </RouterLink>
        </div>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: var(--color-header);
  border-bottom: 1px solid var(--color-surface-border);
  backdrop-filter: var(--blur-glass);
  -webkit-backdrop-filter: var(--blur-glass);
}

.app-header__inner {
  max-width: var(--page-max-width);
  min-height: var(--header-height);
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.app-header__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--color-primary);
  text-decoration: none;
}

.app-header__navigation {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 4px;
}

.app-header__link,
.app-header__profile-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: var(--radius-pill);
  font-weight: 500;
  font-size: 0.95rem;
  text-decoration: none;
  white-space: nowrap;
  transition: background var(--transition-fast);
}

.app-header__link:hover,
.app-header__profile-link:hover {
  background: var(--color-primary-soft);
}

.app-header__link--active {
  background: rgba(79, 63, 143, 0.2);
  font-weight: 600;
}

.app-header__link-count {
  min-width: 20px;
  padding: 0 6px;
  border-radius: var(--radius-pill);
  background: var(--color-favorite);
  color: #fff;
  font-size: 0.72rem;
  line-height: 20px;
  text-align: center;
}

.app-header__account {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
}

.app-header__logout-button,
.app-header__menu-toggle {
  display: inline-flex;
  padding: 8px;
  border: none;
  border-radius: var(--radius-pill);
  background: transparent;
  cursor: pointer;
  transition: background var(--transition-fast);
}

.app-header__logout-button:hover,
.app-header__menu-toggle:hover {
  background: var(--color-primary-soft);
}

.app-header__menu-toggle {
  display: none;
  margin-left: auto;
}

@media (max-width: 1024px) {
  .app-header__menu-toggle {
    display: inline-flex;
  }

  .app-header__navigation {
    display: none;
    position: absolute;
    top: var(--header-height);
    left: 0;
    right: 0;
    flex-direction: column;
    align-items: stretch;
    padding: 12px 16px 16px;
    background: var(--color-header);
    border-bottom: 1px solid var(--color-surface-border);
    backdrop-filter: var(--blur-glass);
    -webkit-backdrop-filter: var(--blur-glass);
  }

  .app-header__navigation--open {
    display: flex;
  }

  .app-header__account {
    margin-left: 0;
    padding-top: 8px;
    border-top: 1px solid var(--color-surface-border);
  }
}
</style>
