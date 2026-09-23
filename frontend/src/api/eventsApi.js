import { backendClient } from './client'

export async function fetchEvents() {
  const { data } = await backendClient.get('/events')
  return data
}

export async function fetchRegistrations(userId) {
  const { data } = await backendClient.get('/events/registrations', { params: { userId } })
  return data
}

export async function registerToEvent(eventId, userId) {
  const { data } = await backendClient.post(`/events/${eventId}/register`, { userId })
  return data
}

export async function unregisterFromEvent(eventId, userId) {
  await backendClient.delete(`/events/${eventId}/register`, { params: { userId } })
}
