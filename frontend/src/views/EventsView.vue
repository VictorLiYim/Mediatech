<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { mdiAccountGroup, mdiCalendarBlank, mdiCalendarRemove, mdiFeather } from '@mdi/js'
import { fetchAuthors } from '@/api/authorsApi'
import { extractErrorMessage } from '@/api/client'
import { fetchEvents, fetchRegistrations, registerToEvent, unregisterFromEvent } from '@/api/eventsApi'
import AppIcon from '@/components/AppIcon.vue'
import EmptyState from '@/components/EmptyState.vue'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import StatusMessage from '@/components/StatusMessage.vue'
import { formatDateTime } from '@/utils/dates'
import { EVENT_TYPE_LABELS } from '@/utils/labels'

const store = useStore()
const route = useRoute()
const router = useRouter()

const events = ref([])
const authorNames = ref({})
const registeredEventIds = ref(new Set())
const isLoading = ref(true)
const errorMessage = ref('')
const pendingEventId = ref(null)

const isAuthenticated = computed(() => store.getters['auth/isAuthenticated'])
const currentUserId = computed(() => store.getters['auth/currentUserId'])

const upcomingEvents = computed(() => {
  const now = Date.now()
  return events.value
    .filter((event) => new Date(event.eventDate).getTime() >= now)
    .sort((a, b) => new Date(a.eventDate) - new Date(b.eventDate))
})

async function toggleRegistration(event) {
  if (!isAuthenticated.value) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  pendingEventId.value = event.id
  errorMessage.value = ''
  const updatedIds = new Set(registeredEventIds.value)
  try {
    if (updatedIds.has(event.id)) {
      await unregisterFromEvent(event.id, currentUserId.value)
      updatedIds.delete(event.id)
    } else {
      await registerToEvent(event.id, currentUserId.value)
      updatedIds.add(event.id)
    }
    registeredEventIds.value = updatedIds
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "L'inscription a échoué")
  } finally {
    pendingEventId.value = null
  }
}

onMounted(async () => {
  try {
    events.value = await fetchEvents()
    const authors = await fetchAuthors().catch(() => [])
    authorNames.value = Object.fromEntries(authors.map((author) => [author.id, author.name]))
    if (isAuthenticated.value) {
      const registrations = await fetchRegistrations(currentUserId.value)
      registeredEventIds.value = new Set(registrations.map((registration) => registration.eventId))
    }
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, 'Impossible de charger les événements')
  } finally {
    isLoading.value = false
  }
})
</script>

<template>
  <div class="page-container events-view">
    <header class="page-header">
      <div>
        <h1 class="page-title">Événements</h1>
        <p class="page-subtitle">Lectures, projections et séances de dédicace à la médiathèque.</p>
      </div>
    </header>

    <StatusMessage v-if="errorMessage" :message="errorMessage" />
    <LoadingSpinner v-if="isLoading" />

    <EmptyState
      v-else-if="upcomingEvents.length === 0"
      :icon-path="mdiCalendarRemove"
      title="Aucun événement programmé"
      message="Revenez bientôt, de nouveaux rendez-vous arrivent régulièrement."
    />

    <ul v-else class="events-view__list">
      <li v-for="event in upcomingEvents" :key="event.id" class="events-view__event glass-panel">
        <div class="events-view__event-header">
          <span class="badge">{{ EVENT_TYPE_LABELS[event.type] ?? event.type }}</span>
          <span v-if="registeredEventIds.has(event.id)" class="badge badge--success">Inscrit</span>
        </div>
        <h2 class="events-view__event-title">{{ event.title }}</h2>
        <p v-if="event.description" class="events-view__event-description">{{ event.description }}</p>
        <ul class="events-view__event-facts">
          <li>
            <AppIcon :path="mdiCalendarBlank" :size="18" />
            {{ formatDateTime(event.eventDate) }}
          </li>
          <li v-if="authorNames[event.authorId]">
            <AppIcon :path="mdiFeather" :size="18" />
            {{ authorNames[event.authorId] }}
          </li>
          <li>
            <AppIcon :path="mdiAccountGroup" :size="18" />
            {{ event.capacity }} places
          </li>
        </ul>
        <button
          type="button"
          class="button events-view__registration-button"
          :class="registeredEventIds.has(event.id) ? 'button--danger' : 'button--primary'"
          :disabled="pendingEventId === event.id"
          @click="toggleRegistration(event)"
        >
          {{ registeredEventIds.has(event.id) ? 'Se désinscrire' : "S'inscrire" }}
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped>
.events-view__list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.events-view__event {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 24px;
}

.events-view__event-header {
  display: flex;
  gap: 6px;
}

.events-view__event-title {
  font-size: 1.2rem;
  line-height: 1.3;
}

.events-view__event-description {
  color: var(--color-text-muted);
}

.events-view__event-facts {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin: 0;
  padding: 0;
  list-style: none;
  font-size: 0.92rem;
}

.events-view__event-facts li {
  display: flex;
  align-items: center;
  gap: 8px;
}

.events-view__registration-button {
  align-self: flex-start;
  margin-top: auto;
}

@media (max-width: 400px) {
  .events-view__list {
    grid-template-columns: 1fr;
  }
}
</style>
