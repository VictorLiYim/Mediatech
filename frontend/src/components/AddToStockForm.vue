<script setup>
import { reactive, ref } from 'vue'
import { useStore } from 'vuex'
import { extractErrorMessage } from '@/api/client'
import { BOOK_TYPE_LABELS, GENRE_LABELS } from '@/utils/labels'
import StatusMessage from './StatusMessage.vue'

const props = defineProps({
  initialBook: { type: Object, default: null },
})

const emit = defineEmits(['created', 'cancel'])

const store = useStore()

const form = reactive({
  title: props.initialBook?.title ?? '',
  authorNames: props.initialBook?.authors?.join(', ') ?? '',
  isbn: props.initialBook?.isbn ?? '',
  type: 'ROMAN',
  genres: [],
  description: props.initialBook?.description ?? '',
  totalCopies: 1,
})
const errorMessage = ref('')
const isSubmitting = ref(false)

async function submitForm() {
  errorMessage.value = ''
  const authorNames = form.authorNames.split(',').map((name) => name.trim()).filter(Boolean)
  if (authorNames.length === 0) {
    errorMessage.value = 'Indiquez au moins un auteur.'
    return
  }
  isSubmitting.value = true
  try {
    const created = await store.dispatch('catalog/addBookToStock', {
      title: form.title.trim(),
      authorNames,
      isbn: form.isbn.trim(),
      type: form.type,
      genres: form.genres,
      description: form.description.trim().slice(0, 2000),
      totalCopies: form.totalCopies,
    })
    emit('created', created)
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Impossible d'ajouter ce livre au stock")
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <form class="add-to-stock-form" @submit.prevent="submitForm">
    <div class="form-field">
      <label class="form-label" for="stock-title">Titre</label>
      <input id="stock-title" v-model="form.title" class="form-input" required>
    </div>

    <div class="form-field">
      <label class="form-label" for="stock-authors">Auteur(s), séparés par des virgules</label>
      <input id="stock-authors" v-model="form.authorNames" class="form-input" required>
    </div>

    <div class="add-to-stock-form__row">
      <div class="form-field">
        <label class="form-label" for="stock-isbn">ISBN</label>
        <input id="stock-isbn" v-model="form.isbn" class="form-input" required>
      </div>
      <div class="form-field">
        <label class="form-label" for="stock-type">Type</label>
        <select id="stock-type" v-model="form.type" class="form-input">
          <option v-for="(label, value) in BOOK_TYPE_LABELS" :key="value" :value="value">{{ label }}</option>
        </select>
      </div>
      <div class="form-field">
        <label class="form-label" for="stock-copies">Exemplaires</label>
        <input id="stock-copies" v-model.number="form.totalCopies" class="form-input" type="number" min="1" required>
      </div>
    </div>

    <fieldset class="add-to-stock-form__genres">
      <legend class="form-label">Genres</legend>
      <label v-for="(label, value) in GENRE_LABELS" :key="value" class="add-to-stock-form__genre-option">
        <input v-model="form.genres" type="checkbox" :value="value">
        <span>{{ label }}</span>
      </label>
    </fieldset>

    <div class="form-field">
      <label class="form-label" for="stock-description">Description</label>
      <textarea id="stock-description" v-model="form.description" class="form-input" rows="4" maxlength="2000" />
    </div>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />

    <div class="add-to-stock-form__actions">
      <button type="button" class="button button--secondary" @click="emit('cancel')">Annuler</button>
      <button type="submit" class="button button--primary" :disabled="isSubmitting">
        {{ isSubmitting ? 'Ajout…' : 'Ajouter au stock' }}
      </button>
    </div>
  </form>
</template>

<style scoped>
.add-to-stock-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.add-to-stock-form__row {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr;
  gap: 12px;
}

.add-to-stock-form__genres {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 0;
  padding: 0;
  border: none;
}

.add-to-stock-form__genres legend {
  margin-bottom: 8px;
}

.add-to-stock-form__genre-option {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: 1px solid var(--color-surface-border);
  border-radius: var(--radius-pill);
  background: var(--color-surface-strong);
  font-size: 0.88rem;
  cursor: pointer;
}

.add-to-stock-form__genre-option:has(input:checked) {
  background: var(--color-primary-soft);
  border-color: var(--color-primary);
}

.add-to-stock-form__actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 600px) {
  .add-to-stock-form__row {
    grid-template-columns: 1fr;
  }
}
</style>
