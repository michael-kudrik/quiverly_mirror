<script setup lang="ts">
import {computed} from 'vue'
import SurfboardCard from "~/components/SurfboardCard.vue";

const {getMyBoards} = useSurfboards()
const {data: boardsData, status} = getMyBoards()

const displayBoards = computed(() => {
  return boardsData.value
})
</script>

<template>
  <div v-if="status === 'pending'">
    <div class="flex gap-4">
      <div v-for="n in 6"
           :key="n"
           class="skeleton h-32 w-32">
      </div>
    </div>
  </div>
  <div v-if="status === 'error'">
    <div role="alert" class="alert alert-error">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 shrink-0 stroke-current" fill="none" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"/>
      </svg>
      <span>Error! Task failed successfully.</span>
    </div>
  </div>
  <div v-if="status === 'success'" class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 items-start">
    <SurfboardCard
        v-for="board in displayBoards"
        :key="board.id"
        :board="board"
    />
  </div>
</template>

<style scoped>
</style>