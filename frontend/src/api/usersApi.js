import { backendClient } from './client'

export async function login(userName, password) {
  const { data } = await backendClient.post('/users/login', { userName, password })
  return data
}

export async function register({ userName, email, password }) {
  const { data } = await backendClient.post('/users', { userName, email, password })
  return data
}

export async function fetchUser(userId) {
  const { data } = await backendClient.get(`/users/${userId}`)
  return data
}
