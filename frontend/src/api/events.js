import { eventApi } from "./client";

/**
 * GET /events — liste des événements.
 */
export async function getAllEvents() {
    const { data } = await eventApi.get("/events");
    return data;
}

/**
 * GET /events/{id} — détail d'un événement.
 */
export async function getEvent(id) {
    const { data } = await eventApi.get(`/events/${id}`);
    return data;
}

/**
 * POST /events/{id}/register — s'inscrire à un événement.
 */
export async function registerForEvent(id, userId) {
    const { data } = await eventApi.post(`/events/${id}/register`, { userId });
    return data;
}

/**
 * GET /events/registrations?userId= — inscriptions d'un utilisateur.
 */
export async function getRegistrationsForUser(userId) {
    const { data } = await eventApi.get("/events/registrations", { params: { userId } });
    return data;
}