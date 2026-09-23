import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const services = {
  '/api/users': 'http://localhost:8083',
  '/api/books': 'http://localhost:8081',
  '/api/authors': 'http://localhost:8081',
  '/api/events': 'http://localhost:8082',
}

const proxy = Object.fromEntries(
  Object.entries(services).map(([prefix, target]) => [
    prefix,
    { target, changeOrigin: true, rewrite: (path) => path.replace(/^\/api/, '') },
  ]),
)

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: { proxy },
})
