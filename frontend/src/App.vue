<template>
  <div id="app-root">
    <Navbar v-if="!isAdminRoute" />
    <router-view />
    <Footer v-if="!isAdminRoute && !isAuthRoute" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import Footer from '@/components/Footer.vue'

const route = useRoute()
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const isAuthRoute = computed(() => ['/login', '/register'].includes(route.path))
</script>

<style scoped>
#app-root {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
