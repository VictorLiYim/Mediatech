<template>
  <v-container class="mt-6 mb-10" v-if="book">
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'books' }" class="on-bg mb-4">
      Retour au catalogue
    </v-btn>

    <v-row>
      <v-col cols="12" md="8">
        <article class="glass pa-6">
        <div class="d-flex ga-4">
          <div class="cover-thumb" style="width: 72px; height: 100px">
            <v-icon icon="mdi-book-open-page-variant" color="white" size="32" />
          </div>
          <div>
            <h1 class="text-title font-italic">{{ book.title }}</h1>
            <p class="text-caption-brand">{{ authorNames }}</p>
            <v-rating :model-value="book.averageRating ?? 0" density="compact" readonly half-increments color="primary" class="mt-1" />
          </div>
        </div>

        <div class="mt-4">
          <v-chip v-for="genre in book.genres" :key="genre" size="small" variant="tonal" color="secondary" class="mr-1 mb-1">{{ genre }}</v-chip>
        </div>

        <p class="mt-4">{{ book.description }}</p>

        <v-alert v-if="actionError" type="error" density="compact" class="mt-4">{{ actionError }}</v-alert>

        <v-chip :color="book.availableCopies > 0 ? 'success' : 'error'" class="mt-4 mr-4" variant="tonal">
          {{ book.availableCopies }} / {{ book.totalCopies }} exemplaire(s) disponible(s)
        </v-chip>

        <v-btn
            v-if="!activeLoan"
            variant="flat"
            class="btn-pill mt-4"
            :disabled="book.availableCopies <= 0"
            :loading="actionLoading"
            @click="handleBorrow"
        >
          Emprunter
        </v-btn>
        <v-btn v-else class="btn-pill btn-pill--secondary mt-4" variant="flat" :loading="actionLoading" @click="handleReturn">
          Rendre (emprunté le {{ activeLoan.borrowDate }})
        </v-btn>
        </article>
      </v-col>
    </v-row>

    <v-divider class="my-6" />

    <h2 class="text-subtitle on-bg mb-4">Avis des lecteurs</h2>

    <v-alert v-if="reviewError" type="error" density="compact" class="mb-4">{{ reviewError }}</v-alert>

    <article class="glass pa-4 mb-6" style="max-width: 500px">
      <p class="text-subtitle-3 mb-2">Laisser un avis</p>
      <v-rating v-model="newRating" density="comfortable" />
      <v-textarea v-model="newComment" label="Commentaire" variant="outlined" rows="3" />
      <v-btn variant="flat" class="btn-pill" :loading="submittingReview" :disabled="!newRating" @click="handlePostReview">
        Publier
      </v-btn>
    </article>

    <article v-if="reviews.length" class="glass pa-2">
      <v-list bg-color="transparent">
        <v-list-item v-for="review in reviews" :key="review.id">
          <template #prepend>
            <v-rating :model-value="review.rating" density="compact" size="small" readonly />
          </template>
          <v-list-item-title>{{ review.comment || "(sans commentaire)" }}</v-list-item-title>
          <v-list-item-subtitle>Utilisateur #{{ review.userId }}</v-list-item-subtitle>
        </v-list-item>
      </v-list>
    </article>
    <p v-else class="glass pa-4 text-caption-brand">Aucun avis pour le moment.</p>
  </v-container>

  <v-container v-else-if="loadError" class="mt-6">
    <v-alert type="error">{{ loadError }}</v-alert>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useUserStore } from "@/stores/user";
import { extractErrorMessage } from "@/api/client";
import { getBook, borrowBook, returnBook, getReviews, postReview, getLoansForUser } from "@/api/books";

const route = useRoute();
const userStore = useUserStore();
const bookId = route.params.id;

const book = ref(null);
const reviews = ref([]);
const activeLoan = ref(null);
const loadError = ref("");
const actionError = ref("");
const reviewError = ref("");
const actionLoading = ref(false);
const submittingReview = ref(false);
const newRating = ref(0);
const newComment = ref("");

const authorNames = computed(() => book.value?.authors?.map((a) => a.name).join(", ") || "Auteur inconnu");

async function loadBook() {
  book.value = await getBook(bookId);
}

async function loadReviews() {
  reviews.value = await getReviews(bookId);
}

async function loadActiveLoan() {
  const loans = await getLoansForUser(userStore.currentUser.id);
  activeLoan.value = loans.find((loan) => String(loan.bookId) === String(bookId) && !loan.returnDate) ?? null;
}

async function handleBorrow() {
  actionError.value = "";
  actionLoading.value = true;
  try {
    await borrowBook(bookId, userStore.currentUser.id);
    await Promise.all([loadBook(), loadActiveLoan()]);
  } catch (error) {
    actionError.value = extractErrorMessage(error, "Emprunt impossible");
  } finally {
    actionLoading.value = false;
  }
}

async function handleReturn() {
  actionError.value = "";
  actionLoading.value = true;
  try {
    await returnBook(bookId, userStore.currentUser.id);
    await Promise.all([loadBook(), loadActiveLoan()]);
  } catch (error) {
    actionError.value = extractErrorMessage(error, "Retour impossible");
  } finally {
    actionLoading.value = false;
  }
}

async function handlePostReview() {
  reviewError.value = "";
  submittingReview.value = true;
  try {
    await postReview(bookId, {
      userId: userStore.currentUser.id,
      rating: newRating.value,
      comment: newComment.value,
    });
    newRating.value = 0;
    newComment.value = "";
    await Promise.all([loadReviews(), loadBook()]);
  } catch (error) {
    reviewError.value = extractErrorMessage(error, "Impossible de publier l'avis");
  } finally {
    submittingReview.value = false;
  }
}

onMounted(async () => {
  try {
    await Promise.all([loadBook(), loadReviews(), loadActiveLoan()]);
  } catch (error) {
    loadError.value = extractErrorMessage(error, "Livre introuvable");
  }
});
</script>