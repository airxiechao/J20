<script setup lang="ts">
import { computed, ref } from 'vue';
import { useBoolean } from '@sa/hooks';
import { useAppStore } from '@/store/modules/app';
import { fetchListDashboardChart } from '@/service/api';
import CardData from './modules/card-data.vue';
import LineChart from './modules/line-chart.vue';
import BarChart from './modules/bar-chart.vue';
import News from './modules/news.vue';
import Blank from './modules/blank.vue';
import DashbaordOperateModal from './modules/dashboard-operate-modal.vue';

const appStore = useAppStore();

// 数据
const gap = computed(() => (appStore.isMobile ? 0 : 16));
const { bool: visible, setTrue: openModal } = useBoolean();
const charts = ref<Api.Dashbaord.Chart[]>([]);

// 处理修改配置
function handleEdit() {
  openModal();
}

// 处理初始化
async function init() {
  const { error, data } = await fetchListDashboardChart();
  if (!error) {
    charts.value = data;
  }
}

init();
</script>

<template>
  <NSpace vertical :size="16">
    <NCard :bordered="false" class="card-wrapper">
      <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
        <NGi span="12" class="self-center">
          <h3 class="text-18px font-semibold">{{ $t(`page.dashboard.title`) }}</h3>
        </NGi>
        <NGi span="12">
          <NSpace justify="end">
            <NButton ghost size="small" @click="handleEdit">
              <template #icon>
                <icon-mdi-cog-outline class="text-icon" />
              </template>
              {{ $t(`page.dashboard.config`) }}
            </NButton>
          </NSpace>
        </NGi>
      </NGrid>
    </NCard>

    <CardData :charts="charts" />
    <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:14">
        <NCard :bordered="false" class="card-wrapper">
          <LineChart :chart="charts[4]" />
        </NCard>
      </NGi>
      <NGi span="24 s:24 m:10">
        <NCard :bordered="false" class="card-wrapper">
          <BarChart :chart="charts[5]" />
        </NCard>
      </NGi>
    </NGrid>
    <NGrid :x-gap="gap" :y-gap="16" responsive="screen" item-responsive>
      <NGi span="24 s:24 m:14">
        <News />
      </NGi>
      <NGi span="24 s:24 m:10">
        <Blank />
      </NGi>
    </NGrid>
    <DashbaordOperateModal v-model:visible="visible" :charts="charts" @submitted="init" />
  </NSpace>
</template>

<style scoped></style>
