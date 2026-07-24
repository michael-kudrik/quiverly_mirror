<script setup lang="ts">
import {computed} from 'vue'
import type {SurfBoard} from '~/composables/useSurfboards'

const props = defineProps<{ board: SurfBoard }>()

const coverImage = computed(() => {
  return props.board.images?.find(img => img.isCover) || props.board.images?.[0]
})

// random ahh fallback for if there are no board images found
const fallbacks = [
  "https://images.unsplash.com/photo-1502680390469-be75c86b636f?w=600&auto=format&fit=crop&q=80",
]

const resolvedImageUrl = computed(() => {
  const url = coverImage.value?.url
  if (!url) return fallbacks[props.board.id % fallbacks.length]
  return url.startsWith('/') ? `http://localhost:8080${url}` : url // this resolves the local images to fetch from backend, will need to change
})


// aspect clamping based on dimensions
const clampedRatio = computed(() => {
  const width = coverImage.value?.imageWidth || 400
  const height = coverImage.value?.imageHeight || (300 + ((props.board.id * 83) % 250))
  return Math.max(2 / 3, Math.min(4 / 3, width / height))
})
</script>

<template>
  <div class="card bg-base-200 shadow-md overflow-hidden w-full">
    <figure :style="{ aspectRatio: clampedRatio.toString() }" class="w-full relative bg-base-300">
      <img
          :src="resolvedImageUrl"
          class="w-full h-full object-cover absolute inset-0 block"
          :alt="board.model"
      />
    </figure>
    <div class="card-body">
      <h2 class="card-title">{{ board.model }}</h2>
      <p>A card component has a figure, a body part, and inside body there are title and actions parts</p>
      <div class="card-actions justify-end">
        <button class="btn btn-primary">Buy Now</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>