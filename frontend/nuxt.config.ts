// https://nuxt.com/docs/api/configuration/nuxt-config
import tailwindcss from "@tailwindcss/vite";

export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  telemetry: false,
  vite: {
    optimizeDeps: {
      include: []
    },
    plugins: [tailwindcss() as any],
  },
  css: ['~/assets/main.css'],
  modules: ['@pinia/nuxt', '@nuxt/fonts',],
})