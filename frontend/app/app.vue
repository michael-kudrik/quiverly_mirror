<script setup>
import { onMounted } from 'vue'
import { useThemeStore } from './stores/theme' // Nuxt 4 store path

const themeStore = useThemeStore()

// This keeps your <html> tag synchronized reactively
useHead({
  htmlAttrs: {
    'data-theme': computed(() => themeStore.activeTheme)
  }
})

onMounted(() => {
  themeStore.initTheme()
})
</script>

<template>
    <NuxtRouteAnnouncer />
    <NuxtLayout>
      <NuxtPage/>
    </NuxtLayout>
</template>
