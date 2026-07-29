<script setup lang="ts">
import {computed} from 'vue'
import {useAuthStore} from '~/stores/auth'
import {getImageUrl} from '~/composables/useSurfboards'

const props = defineProps<{
  avatarUrl?: string | null
  username?: string | null
}>()

const authStore = useAuthStore()

const initial = computed(() => {
  const name = props.username || authStore.username || ''
  return name ? name.charAt(0).toUpperCase() : 'U'
})

// check if URL is preview or a saved background image
const displaySrc = computed(() => {
  if (!props.avatarUrl) return null
  return props.avatarUrl.startsWith('blob:') ? props.avatarUrl : getImageUrl(props.avatarUrl)
})
</script>

<template>
  <div
      class="rounded-full overflow-hidden bg-neutral text-neutral-content flex items-center justify-center shrink-0 shadow-inner">
    <img v-if="displaySrc" :src="displaySrc" alt="Avatar" class="w-full h-full object-cover"/>
    <span v-else class="font-bold uppercase select-none">{{ initial }}</span>
  </div>
</template>
