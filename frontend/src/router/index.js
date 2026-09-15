import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import HomeView from "@/views/HomeView.vue";
import BookListView from "@/views/BookListView.vue";
import BookDetailView from "@/views/BookDetailView.vue";
import MyLoansView from "@/views/MyLoansView.vue";
import EventListView from "@/views/EventListView.vue";
import ProfileView from "@/views/ProfileView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/login", name: "login", component: LoginView },
    { path: "/register", name: "register", component: RegisterView },
    { path: "/", name: "home", component: HomeView, meta: { requiresAuth: true } },
    { path: "/books", name: "books", component: BookListView, meta: { requiresAuth: true } },
    { path: "/books/:id", name: "book-detail", component: BookDetailView, meta: { requiresAuth: true } },
    { path: "/loans", name: "loans", component: MyLoansView, meta: { requiresAuth: true } },
    { path: "/events", name: "events", component: EventListView, meta: { requiresAuth: true } },
    { path: "/profile", name: "profile", component: ProfileView, meta: { requiresAuth: true } },
  ],
});

router.beforeEach((to) => {
  const userStore = useUserStore();
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return { name: "login" };
  }
  if ((to.name === "login" || to.name === "register") && userStore.isLoggedIn) {
    return { name: "home" };
  }
});

export default router;