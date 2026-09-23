import axios from 'axios'

export const backendClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

export function extractErrorMessage(error, fallback = 'Une erreur est survenue') {
  return error?.response?.data?.message ?? error?.message ?? fallback
}
