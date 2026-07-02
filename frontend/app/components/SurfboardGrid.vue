<script setup lang="ts">
const {getMyBoards} = useSurfboards()

const { data: boardsData, status, error, refresh } = getMyBoards()

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
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
      </svg>
      <span>Error! Task failed successfully.</span>
    </div>
  </div>
<p>{{boardsData}}</p>
</template>

<style scoped>

</style>