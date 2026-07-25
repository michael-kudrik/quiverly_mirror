<script setup lang="ts">
import Silk from '~/components/Silk.vue';
import {computed} from 'vue';
import {useThemeStore} from '~/stores/theme';

const themeStore = useThemeStore();

const bgColor = computed(() => {
  return themeStore.activeTheme === 'quiverly' ? '#e4d8b4' : '#2b5468'
})
</script>

<template>
  <div class="relative min-h-[calc(100vh-93px)] flex items-center justify-center overflow-hidden">
    <Silk
        class="absolute inset-0 w-full h-full pointer-events-none"
        :speed="5"
        :scale="1.2"
        :color="bgColor"
        :noise-intensity="1.2"
        :rotation="4.1"
    />
    <div class="relative z-10 p-4">
      <Login/>
    </div>
  </div>
</template>

<style scoped>

</style>