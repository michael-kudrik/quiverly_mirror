<script setup>
import { useThemeStore } from './stores/theme' // Nuxt 4 store path

const themeStore = useThemeStore()

// This keeps your <html> tag synchronized reactively
useHead({
  htmlAttrs: {
    'data-theme': computed(() => themeStore.activeTheme)
  }
})
</script>

<template>
    <NuxtRouteAnnouncer />
    <NuxtLayout>
      <NuxtPage/>
    </NuxtLayout>
</template>
