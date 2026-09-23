<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiBookPlus, mdiCheck, mdiClose, mdiPackageVariantPlus } from '@mdi/js'
import { fetchWorkDescription } from '@/api/openLibraryApi'
import { libraryItemFromSearchResult, libraryItemFromStockBook } from '@/utils/libraryItems'
import AppIcon from './AppIcon.vue'
import BookCover from './BookCover.vue'

const props = defineProps({
  result: { type: Object, required: true },
  stockBook: { type: Object, default: null },
})

const emit = defineEmits(['close', 'add-to-stock'])

const store = useStore()
const router = useRouter()
const route = useRoute()

const description = ref(null)
const isLoadingDescription = ref(false)

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const isAdmin = computed(() => store.getters['auth/isAdmin'])

const libraryItem = computed(() =>
  props.stockBook ? libraryItemFromStockBook({ ...props.stockBook, year: props.result.year }) : libraryItemFromSearchResult(props.result),
)
const isInLibrary = computed(() => store.getters['library/isInLibrary'](libraryItem.value.id))

watch(
  () => props.result.externalId,
  async (externalId) => {
    description.value = null
    isLoadingDescription.value = true
    try {
      description.value = await fetchWorkDescription(externalId)
    } catch {
      description.value = null
    } finally {
      isLoadingDescription.value = false
    }
  },
  { immediate: true },
)

function addToLibrary() {
  if (!isAuthenticated.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  store.dispatch('library/addItem', libraryItem.value)
}
</script>

<template>
  <article class="search-result-detail glass-panel">
    <button type="button" class="search-result-detail__close-button" @click="emit('close')">
      <AppIcon :path="mdiClose" :size="22" label="Fermer le détail" />
    </button>

    <div class="search-result-detail__layout">
      <BookCover :src="result.coverUrl" :title="result.title" size="large" />

      <div class="search-result-detail__information">
        <h2 class="search-result-detail__title">{{ result.title }}</h2>
        <dl class="search-result-detail__facts">
          <div class="search-result-detail__fact">
            <dt>Auteur</dt>
            <dd>{{ result.authors.join(', ') || 'Inconnu' }}</dd>
          </div>
          <div class="search-result-detail__fact">
            <dt>Année</dt>
            <dd>{{ result.year ?? 'Inconnue' }}</dd>
          </div>
          <div v-if="result.pageCount" class="search-result-detail__fact">
            <dt>Pages</dt>
            <dd>{{ result.pageCount }}</dd>
          </div>
          <div v-if="result.isbn" class="search-result-detail__fact">
            <dt>ISBN</dt>
            <dd>{{ result.isbn }}</dd>
          </div>
        </dl>

        <p v-if="isLoadingDescription" class="search-result-detail__description search-result-detail__description--muted">
          Chargement du résumé…
        </p>
        <p v-else-if="description" class="search-result-detail__description">{{ description }}</p>
        <p v-else class="search-result-detail__description search-result-detail__description--muted">
          Aucun résumé disponible sur Open Library.
        </p>

        <div class="search-result-detail__availability">
          <template v-if="stockBook">
            <span class="badge badge--success">Disponible à la médiathèque</span>
            <span class="search-result-detail__copies">
              {{ stockBook.availableCopies }} / {{ stockBook.totalCopies }} exemplaire(s) libre(s)
            </span>
          </template>
          <span v-else class="badge badge--warning">Pas dans le stock de la médiathèque</span>
        </div>

        <div class="search-result-detail__actions">
          <button
            type="button"
            class="button button--primary"
            :disabled="isInLibrary"
            @click="addToLibrary"
          >
            <AppIcon :path="isInLibrary ? mdiCheck : mdiBookPlus" :size="18" />
            {{ isInLibrary ? 'Dans ma bibliothèque' : 'Ajouter à ma bibliothèque' }}
          </button>
          <RouterLink
            v-if="stockBook"
            :to="{ name: 'book-detail', params: { bookId: stockBook.id } }"
            class="button button--secondary"
          >
            Voir / emprunter
          </RouterLink>
          <button
            v-else-if="isAdmin"
            type="button"
            class="button button--secondary"
            @click="emit('add-to-stock', { ...result, description })"
          >
            <AppIcon :path="mdiPackageVariantPlus" :size="18" />
            Ajouter au stock
          </button>
        </div>
      </div>
    </div>
  </article>
</template>

<style scoped>
.search-result-detail {
  position: relative;
  padding: 28px;
}

.search-result-detail__close-button {
  position: absolute;
  top: 14px;
  right: 14px;
  display: inline-flex;
  padding: 6px;
  border: none;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}

.search-result-detail__close-button:hover {
  background: var(--color-primary-soft);
}

.search-result-detail__layout {
  display: flex;
  gap: 28px;
}

.search-result-detail__information {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 0;
}

.search-result-detail__title {
  padding-right: 32px;
  font-size: 1.6rem;
  line-height: 1.2;
}

.search-result-detail__facts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 28px;
  margin: 0;
}

.search-result-detail__fact dt {
  font-size: 0.78rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--color-text-muted);
}

.search-result-detail__fact dd {
  margin: 0;
  font-weight: 500;
}

.search-result-detail__description {
  max-height: 180px;
  overflow-y: auto;
  white-space: pre-line;
}

.search-result-detail__description--muted {
  color: var(--color-text-muted);
  font-style: italic;
}

.search-result-detail__availability {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.search-result-detail__copies {
  font-size: 0.9rem;
  color: var(--color-text-muted);
}

.search-result-detail__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

@media (max-width: 700px) {
  .search-result-detail {
    padding: 20px;
  }

  .search-result-detail__layout {
    flex-direction: column;
    align-items: center;
  }

  .search-result-detail__title {
    font-size: 1.3rem;
  }
}
</style>
