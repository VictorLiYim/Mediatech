<template>
  <v-container class="mt-6 mb-10">
    <h1 class="text-title on-bg mb-6">Mes emprunts</h1>

    <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4">{{ errorMessage }}</v-alert>

    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <article v-else-if="loans.length" class="glass list-panel">
      <div v-for="loan in loans" :key="loan.id" class="loan-row">
        <div class="cover-thumb" style="width: 52px; height: 70px">
          <v-icon icon="mdi-book-open-page-variant" color="white" size="24" />
        </div>

        <div class="flex-grow-1">
          <RouterLink :to="{ name: 'book-detail', params: { id: loan.bookId } }" class="text-subtitle-3 d-block mb-1">
            {{ bookTitles[loan.bookId] ?? `Livre #${loan.bookId}` }}
          </RouterLink>
          <p class="text-caption-brand mb-1">
            Emprunté le {{ loan.borrowDate }} · à rendre avant le {{ loan.dueDate }}
          </p>
          <v-chip v-if="loan.returnDate" color="success" size="small" variant="tonal">Rendu le {{ loan.returnDate }}</v-chip>
          <v-chip v-else color="warning" size="small" variant="tonal">En cours</v-chip>
        </div>

        <v-btn
            v-if="!loan.returnDate"
            size="small"
            class="btn-pill btn-pill--secondary"
            variant="flat"
            :loading="returning === loan.id"
            @click="handleReturn(loan)"
        >
          Rendre
        </v-btn>
      </div>
    </article>

    <p v-else class="glass pa-4 text-caption-brand">Tu n'as encore emprunté aucun livre.</p>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { extractErrorMessage } from "@/api/client";
import { getLoansForUser, returnBook, getBook } from "@/api/books";

const userStore = useUserStore();
const loans = ref([]);
const bookTitles = ref({});
const loading = ref(true);
const errorMessage = ref("");
const returning = ref(null);

async function loadLoans() {
  loans.value = await getLoansForUser(userStore.currentUser.id);
  const uniqueBookIds = [...new Set(loans.value.map((loan) => loan.bookId))];
  const books = await Promise.all(uniqueBookIds.map((id) => getBook(id).catch(() => null)));
  bookTitles.value = Object.fromEntries(
      books.filter(Boolean).map((book) => [book.id, book.title])
  );
}

async function handleReturn(loan) {
  errorMessage.value = "";
  returning.value = loan.id;
  try {
    await returnBook(loan.bookId, userStore.currentUser.id);
    await loadLoans();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Retour impossible");
  } finally {
    returning.value = null;
  }
}

onMounted(async () => {
  try {
    await loadLoans();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Impossible de charger tes emprunts");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.list-panel {
  padding: 8px;
}

.loan-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 14px;
}

.loan-row + .loan-row {
  margin-top: 2px;
}
</style>