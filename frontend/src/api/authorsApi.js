import { backendClient } from './client'

export async function fetchAuthors() {
  const { data } = await backendClient.get('/authors')
  return data
}

export async function createAuthor(name, adminId) {
  const { data } = await backendClient.post('/authors', { name, userId: adminId })
  return data
}
