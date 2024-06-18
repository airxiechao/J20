<script setup lang="ts">
const url = import.meta.env.VITE_ATTCK_URL;
</script>

<template>
  <div class="h-full pa-0">
    <iframe id="iframePage" class="size-full" :src="url"></iframe>
  </div>
</template>

<style scoped></style>
