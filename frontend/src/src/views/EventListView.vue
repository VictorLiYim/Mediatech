<template>
  <v-container class="mt-6 mb-10">
    <h1 class="text-title on-bg mb-6">Événements</h1>

    <v-alert v-if="errorMessage" type="error" density="compact" class="mb-4">{{ errorMessage }}</v-alert>

    <div v-if="loading" class="text-center py-8">
      <v-progress-circular indeterminate color="primary" />
    </div>

    <v-row v-else>
      <v-col v-for="event in events" :key="event.id" cols="12" sm="6" md="4">
        <article class="glass h-100 d-flex flex-column pa-5">
          <p class="text-subtitle-3 mb-1">{{ event.title }}</p>
          <p class="text-caption-brand mb-3">{{ formatDate(event.eventDate) }} · {{ typeLabel(event.type) }}</p>
          <p class="flex-grow-1 mb-3">{{ event.description }}</p>
          <v-chip size="small" variant="tonal" color="secondary" class="align-self-start mb-4">Capacité : {{ event.capacity }}</v-chip>

          <v-btn
              v-if="!registeredEventIds.has(event.id)"
              class="btn-pill"
              variant="flat"
              :loading="registering === event.id"
              @click="handleRegister(event)"
          >
            S'inscrire
          </v-btn>
          <v-chip v-else color="success" variant="tonal" class="align-self-start">Inscrit(e)</v-chip>
        </article>
      </v-col>

      <v-col v-if="!events.length" cols="12">
        <p class="glass pa-4 text-caption-brand">Aucun événement à venir.</p>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { extractErrorMessage } from "@/api/client";
import { getAllEvents, registerForEvent, getRegistrationsForUser } from "@/api/events";

const userStore = useUserStore();
const events = ref([]);
const registeredEventIds = ref(new Set());
const loading = ref(true);
const errorMessage = ref("");
const registering = ref(null);

const typeLabels = {
  DEDICATION: "Dédicace",
  FILM_PROJECTION: "Projection",
  LECTURE: "Conférence",
};

function typeLabel(type) {
  return typeLabels[type] ?? type;
}

function formatDate(isoDateTime) {
  return new Date(isoDateTime).toLocaleString("fr-FR", { dateStyle: "medium", timeStyle: "short" });
}

async function loadEvents() {
  const [allEvents, registrations] = await Promise.all([
    getAllEvents(),
    getRegistrationsForUser(userStore.currentUser.id),
  ]);
  events.value = allEvents;
  registeredEventIds.value = new Set(registrations.map((r) => r.eventId));
}

async function handleRegister(event) {
  errorMessage.value = "";
  registering.value = event.id;
  try {
    await registerForEvent(event.id, userStore.currentUser.id);
    registeredEventIds.value = new Set([...registeredEventIds.value, event.id]);
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Inscription impossible");
  } finally {
    registering.value = null;
  }
}

onMounted(async () => {
  try {
    await loadEvents();
  } catch (error) {
    errorMessage.value = extractErrorMessage(error, "Impossible de charger les événements");
  } finally {
    loading.value = false;
  }
});
</script>