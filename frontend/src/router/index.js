import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'
import HomeView from '@/views/HomeView.vue'

const routes = [
  { path: '/', name: 'home', component: HomeView, meta: { title: 'Accueil' } },
  { path: '/search', name: 'search', component: () => import('@/views/SearchView.vue'), meta: { title: 'Rechercher' } },
  { path: '/catalog', name: 'catalog', component: () => import('@/views/CatalogView.vue'), meta: { title: 'Catalogue' } },
  {
    path: '/catalog/:bookId',
    name: 'book-detail',
    component: () => import('@/views/BookDetailView.vue'),
    props: true,
    meta: { title: 'Livre' },
  },
  {
    path: '/library',
    name: 'library',
    component: () => import('@/views/LibraryView.vue'),
    meta: { title: 'Ma bibliothèque', requiresAuth: true },
  },
  {
    path: '/favorites',
    name: 'favorites',
    component: () => import('@/views/FavoritesView.vue'),
    meta: { title: 'Favoris', requiresAuth: true },
  },
  {
    path: '/loans',
    name: 'loans',
    component: () => import('@/views/LoansView.vue'),
    meta: { title: 'Mes emprunts', requiresAuth: true },
  },
  { path: '/events', name: 'events', component: () => import('@/views/EventsView.vue'), meta: { title: 'Événements' } },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { title: 'Profil', requiresAuth: true },
  },
  {
    path: '/admin/stock',
    name: 'admin-stock',
    component: () => import('@/views/AdminStockView.vue'),
    meta: { title: 'Gestion du stock', requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: 'Connexion', guestOnly: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView.vue'),
    meta: { title: 'Inscription', guestOnly: true },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: 'Page introuvable' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to) => {
  const isAuthenticated = store.getters['auth/isAuthenticated']

  if (to.meta.requiresAuth && !isAuthenticated) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.meta.requiresAdmin && !store.getters['auth/isAdmin']) {
    return { name: 'home' }
  }
  if (to.meta.guestOnly && isAuthenticated) {
    return { name: 'home' }
  }
  return true
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · Médiatech` : 'Médiatech'
})

export default router
