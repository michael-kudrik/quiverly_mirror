<script setup lang="ts">
import {computed} from 'vue'
import type {SurfBoard} from '~/composables/useSurfboards'

const props = defineProps<{ board: SurfBoard }>()

// check if cover image exists and search for it
// if none exist, fallback
const coverImage = computed(() => {
  if (!props.board.images || props.board.images.length === 0) return null
  return props.board.images.find(img => img.cover) || props.board.images[0]
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

// translate 9.4 to 9'4"
const formattedLength = computed(() => {
  const val = props.board.length
  if (val === null || val === undefined || val === 0) return ''

  const strVal = String(val)
  if (strVal.includes('.')) {
    const [feet, inches] = strVal.split('.')
    return inches && inches !== '0' ? `${feet}'${inches}"` : `${feet}'`
  }

  return `${val}'`
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
      <h2 class="card-title flex justify-between items-center">
        <span>{{ board.model }}</span>
        <span v-if="formattedLength">{{ formattedLength }}</span>
      </h2>
      <p>Shaped by <span class="font-bold">{{ board.shaper }}</span></p>

      <div class="mt-3 flex items-center justify-between">
        <div class="flex flex-wrap gap-2 items-center">
          <div v-if="board.volume" class="badge">Volume: {{ board.volume }}L</div>
          <div v-if="board.width" class="badge">Width: {{ board.width }}</div>
          <div v-if="board.boardType" class="badge">{{ board.boardType }}</div>
          <!--          <div v-if="board.purchasedAt" class="badge whitespace-nowrap">Purchased: {{ board.purchasedAt }}</div>-->
        </div>
        <div class="card-actions justify-end">
          <button class="btn btn-outline">Details</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>