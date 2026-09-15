<template>
  <v-container class="mt-6 mb-10" style="max-width: 560px">
    <h1 class="text-title on-bg mb-6">Mon profil</h1>

    <article class="glass pa-6 text-center mb-4">
      <div class="avatar mx-auto mb-4">{{ initials }}</div>
      <p class="text-subtitle mb-1">{{ userStore.currentUser?.userName }}</p>
      <p class="text-caption-brand">{{ userStore.currentUser?.email }}</p>
    </article>

    <div class="d-flex ga-3 mb-4">
      <RouterLink :to="{ name: 'loans' }" class="glass glass--tight stat-card">
        <p class="stat-card__value">{{ loading ? "…" : activeLoanCount }}</p>
        <p class="text-caption-brand">Emprunt(s) en cours</p>
      </RouterLink>
      <RouterLink :to="{ name: 'events' }" class="glass glass--tight stat-card">
        <p class="stat-card__value">{{ loading ? "…" : registrationCount }}</p>
        <p class="text-caption-brand">Événement(s) suivi(s)</p>
      </RouterLink>
    </div>

    <article class="glass pa-4">
      <v-btn class="btn-pill btn-pill--secondary" variant="flat" block prepend-icon="mdi-logout" @click="handleLogout">
        Déconnexion
      </v-btn>
    </article>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { getLoansForUser } from "@/api/books";
import { getRegistrationsForUser } from "@/api/events";

const userStore = useUserStore();
const router = useRouter();

const loading = ref(true);
const activeLoanCount = ref(0);
const registrationCount = ref(0);

const initials = computed(() => {
  const name = userStore.currentUser?.userName ?? "";
  return name.slice(0, 2).toUpperCase();
});

function handleLogout() {
  userStore.logout();
  router.push({ name: "login" });
}

onMounted(async () => {
  try {
    const [loans, registrations] = await Promise.all([
      getLoansForUser(userStore.currentUser.id),
      getRegistrationsForUser(userStore.currentUser.id),
    ]);
    activeLoanCount.value = loans.filter((loan) => !loan.returnDate).length;
    registrationCount.value = registrations.length;
  } catch {
    // Les compteurs restent à 0 si l'appel échoue — non bloquant pour la page profil.
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(160deg, var(--color-lavender-bright), var(--color-lavender));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.5rem;
}

.stat-card {
  flex: 1;
  padding: 20px;
  text-align: center;
  text-decoration: none;
  color: inherit;
}

.stat-card__value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--color-lavender);
}
</style>
