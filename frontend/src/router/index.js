import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";

import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import HomeView from "@/views/HomeView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/login", name: "login", component: LoginView },
    { path: "/register", name: "register", component: RegisterView },
    { path: "/", name: "home", component: HomeView, meta: { requiresAuth: true } },
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