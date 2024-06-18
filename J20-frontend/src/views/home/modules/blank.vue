<script setup lang="ts">
import { $t } from '@/locales';

defineOptions({
  name: 'Blank'
});
</script>

<template>
  <NCard title="" :bordered="false" size="small" class="h-full card-wrapper">
    <div class="h-full flex-center">
      <icon-local-banner class="text-400px text-primary sm:text-320px" />
    </div>
  </NCard>
</template>

<style scoped></style>
