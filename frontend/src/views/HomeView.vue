<template>
  <v-container class="py-8" style="max-width: 1200px">
    <v-form class="hero-search mx-auto mb-8" @submit.prevent="goSearch">
      <v-text-field
          v-model="search"
          placeholder="Chercher un livre"
          variant="solo"
          density="comfortable"
          hide-details
          rounded="pill"
          append-inner-icon="mdi-magnify"
          class="icon-ink search-glass"
          @click:append-inner="goSearch"
      />
    </v-form>

    <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4 mx-auto" max-width="480">
      {{ errorMessage }}
    </v-alert>

    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <v-row v-else dense>
      <v-col cols="12" md="4">
        <article class="glass h-100 pa-5">
          <div class="d-flex align-center justify-space-between mb-4">
            <p class="glass-title"><v-icon icon="mdi-sparkles" size="18" color="primary" /> Nouveautés !</p>
            <span class="glass-info">i</span>
          </div>
          <div v-if="latestBook">
            <div class="d-flex ga-3">
              <div class="cover-thumb" style="width: 56px; height: 76px">
                <v-icon icon="mdi-book-open-page-variant" color="white" size="26" />
              </div>
              <div class="d-flex flex-column justify-center">
                <p class="text-subtitle-3 mb-1">{{ latestBook.title }}</p>
                <p class="text-caption-brand">{{ authorNames(latestBook) }}</p>
              </div>
            </div>
            <v-btn
                class="btn-pill btn-pill--secondary mt-4"
                variant="flat"
                block
                :to="{ name: 'book-detail', params: { id: latestBook.id } }"
            >
              Voir la disponibilité
            </v-btn>
          </div>
          <p v-else class="text-caption-brand">Le catalogue est vide pour l'instant.</p>
        </article>
      </v-col>

      <v-col cols="12" md="4">
        <article class="glass h-100 pa-5">
          <div class="d-flex align-center justify-space-between mb-4">
            <p class="glass-title"><v-icon icon="mdi-calendar-star" size="18" color="primary" /> Prochains évènements</p>
            <span class="glass-info">i</span>
          </div>
          <div v-if="nextEvent" class="text-center">
            <p class="text-subtitle-3 mb-1">{{ nextEvent.title }}</p>
            <p class="text-caption-brand mb-4">{{ formatDate(nextEvent.eventDate) }}</p>
            <div class="d-flex ga-2 justify-center">
              <v-btn class="btn-pill btn-pill--secondary" variant="flat" :to="{ name: 'events' }">Infos</v-btn>
              <v-btn class="btn-pill btn-pill--secondary" variant="flat" :to="{ name: 'events' }">Inscriptions</v-btn>
            </div>
          </div>
          <p v-else class="text-caption-brand">Aucun événement programmé.</p>
        </article>
      </v-col>

      <v-col cols="12" md="4">
        <article class="glass h-100 pa-5">
          <p class="glass-title mb-4"><v-icon icon="mdi-bookshelf" size="18" color="primary" /> Prochains rendus</p>
          <div class="loans-body">
            <template v-if="upcomingLoans.length">
              <div v-for="entry in upcomingLoans" :key="entry.loan.id" class="loan-row">
                <div class="flex-grow-1">
                  <p class="text-subtitle-3 mb-0">{{ entry.book?.title ?? `Livre #${entry.loan.bookId}` }}</p>
                  <p class="text-caption-brand mb-2">{{ formatDueDate(entry.loan.dueDate) }}</p>
                  <div class="d-flex ga-2">
                    <v-btn size="small" class="btn-pill btn-pill--secondary" variant="flat" :to="{ name: 'book-detail', params: { id: entry.loan.bookId } }">
                      Infos
                    </v-btn>
                    <v-btn
                        size="small"
                        class="btn-pill btn-pill--secondary"
                        variant="flat"
                        :loading="returningLoanId === entry.loan.id"
                        @click="handleReturn(entry.loan)"
                    >
                      Rendre
                    </v-btn>
                  </div>
                </div>
                <div class="cover-thumb" style="width: 44px; height: 60px">
                  <v-icon icon="mdi-book-open-page-variant" color="white" size="20" />
                </div>
              </div>
            </template>
            <p v-else class="text-caption-brand">Aucun emprunt en cours.</p>
          </div>
        </article>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import { getAllBooks, getLoansForUser, getBook, returnBook } from "@/api/books";
import { getAllEvents } from "@/api/events";
import { extractErrorMessage } from "@/api/client";

const router = useRouter();
const userStore = useUserStore();

const search = ref("");
const loading = ref(true);
const errorMessage = ref("");

const latestBook = ref(null);
const nextEvent = ref(null);
const upcomingLoans = ref([]);
const returningLoanId = ref(null);

function goSearch() {
  router.push({ name: "books", query: search.value ? { q: search.value } : {} });
}

function authorNames(book) {
  return book?.authors?.map((a) => a.name).join(", ") || "Auteur inconnu";
}

function formatDate(isoDateTime) {
  return new Date(isoDateTime).toLocaleString("fr-FR", { dateStyle: "medium", timeStyle: "short" });
}

function formatDueDate(isoDate) {
  return new Date(isoDate).toLocaleDateString("fr-FR", { dateStyle: "medium" });
}

async function loadUpcomingLoans() {
  const loans = await getLoansForUser(userStore.currentUser.id);
  const active = loans
      .filter((loan) => !loan.returnDate)
      .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
      .slice(0, 4);
  const books = await Promise.all(active.map((loan) => getBook(loan.bookId).catch(() => null)));
  upcomingLoans.value = active.map((loan, index) => ({ loan, book: books[index] }));
}

async function handleReturn(loan) {
  returningLoanId.value = loan.id;
  try {
    await returnBook(loan.bookId, userStore.currentUser.id);
    await loadUpcomingLoans();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Retour impossible");
  } finally {
    returningLoanId.value = null;
  }
}

onMounted(async () => {
  try {
    const [books, events] = await Promise.all([getAllBooks(), getAllEvents()]);

    latestBook.value = [...books].sort((a, b) => b.id - a.id)[0] ?? null;

    const now = Date.now();
    const upcomingEvents = events
        .filter((event) => new Date(event.eventDate).getTime() >= now)
        .sort((a, b) => new Date(a.eventDate) - new Date(b.eventDate));
    nextEvent.value = upcomingEvents[0] ?? events[0] ?? null;

    await loadUpcomingLoans();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Impossible de charger le tableau de bord");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.hero-search {
  max-width: 640px;
}

.loans-body {
  max-height: 320px;
  overflow-y: auto;
}

.loan-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.loan-row + .loan-row {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(32, 32, 30, 0.1);
}
</style>
