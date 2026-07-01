<script setup lang="ts">
const { getMyBoards } = useSurfboards()

const { data, status, error, refresh } = await getMyBoards()
</script>

<template>
<p>{{data}}</p>
</template>

<style scoped>

</style>