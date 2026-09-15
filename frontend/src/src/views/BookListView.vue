<template>
  <v-container class="mt-6 mb-10">
    <div class="d-flex flex-wrap align-center mb-6 ga-4">
      <h1 class="text-title on-bg">Catalogue</h1>
      <v-spacer />
      <v-text-field
          v-model="search"
          placeholder="Chercher un livre"
          variant="solo"
          density="comfortable"
          hide-details
          clearable
          rounded="pill"
          prepend-inner-icon="mdi-magnify"
          class="icon-ink"
          style="max-width: 360px"
      />
    </div>

    <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4">
      {{ errorMessage }}
    </v-alert>

    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <template v-else>
      <article v-if="filteredBooks.length" class="glass list-panel">
        <RouterLink
            v-for="book in filteredBooks"
            :key="book.id"
            :to="{ name: 'book-detail', params: { id: book.id } }"
            class="book-row"
        >
          <div class="cover-thumb" style="width: 52px; height: 70px">
            <v-icon icon="mdi-book-open-page-variant" color="white" size="24" />
          </div>

          <div class="flex-grow-1">
            <p class="text-subtitle-3 mb-1">{{ book.title }}</p>
            <p class="text-caption-brand mb-1">
              {{ authorNames(book) }} · {{ book.availableCopies > 0 ? "Disponible" : "Indisponible" }}
            </p>
            <div>
              <v-rating :model-value="book.averageRating ?? 0" density="compact" size="x-small" readonly half-increments color="primary" />
              <v-chip v-for="genre in book.genres" :key="genre" size="x-small" variant="tonal" color="secondary" class="ml-1">
                {{ genre }}
              </v-chip>
            </div>
          </div>

          <span class="book-row__see">Voir</span>
        </RouterLink>
      </article>

      <p v-else class="glass pa-4 text-caption-brand">Aucun livre ne correspond à cette recherche.</p>
    </template>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getAllBooks } from "@/api/books";
import { extractErrorMessage } from "@/api/client";

const route = useRoute();
const books = ref([]);
const search = ref(route.query.q ?? "");
const loading = ref(true);
const errorMessage = ref("");

function authorNames(book) {
  return book.authors?.map((a) => a.name).join(", ") || "Auteur inconnu";
}

const filteredBooks = computed(() => {
  const term = search.value?.trim().toLowerCase();
  if (!term) return books.value;
  return books.value.filter(
      (book) =>
          book.title.toLowerCase().includes(term) ||
          authorNames(book).toLowerCase().includes(term)
  );
});

onMounted(async () => {
  try {
    books.value = await getAllBooks();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Impossible de charger le catalogue");
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.list-panel {
  padding: 8px;
}

.book-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 14px;
  text-decoration: none;
  color: inherit;
  transition: background-color 0.15s ease;
}

.book-row:hover {
  background-color: rgba(255, 255, 255, 0.35);
}

.book-row + .book-row {
  margin-top: 2px;
}

.book-row__see {
  flex: none;
  font-weight: 500;
  color: var(--color-ink);
}
</style>