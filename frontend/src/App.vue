<script setup>
import AppHeader from '@/components/AppHeader.vue'
import ToastContainer from '@/components/ToastContainer.vue'
</script>

<template>
  <AppHeader />
  <main class="app-main">
    <RouterView />
  </main>
  <ToastContainer />
</template>

<style scoped>
.app-main {
  min-height: calc(100vh - var(--header-height));
}
</style>
