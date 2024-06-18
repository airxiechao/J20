<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { createReusableTemplate } from '@vueuse/core';
import dayjs from 'dayjs';
import { $t } from '@/locales';
import { fetchCountEvent, fetchSumEvent } from '@/service/api';

defineOptions({
  name: 'CardData'
});

// 数据
interface Props {
  charts: Api.Dashbaord.Chart[];
}

const props = defineProps<Props>();

interface CardData {
  key: string;
  title: string;
  value: number;
  unit: string;
  color: {
    start: string;
    end: string;
  };
  icon: string;
}

type Count = {
  name: string;
  value: number;
};

const counts = ref<Count[]>([]);

const cardData = computed<CardData[]>(() => [
  {
    key: '0',
    title: counts.value[0]?.name || $t('page.event.eventType.level.NA'),
    value: counts.value[0]?.value || 0,
    unit: '',
    color: {
      start: '#ec4786',
      end: '#b955a4'
    },
    icon: 'ant-design:bar-chart-outlined'
  },
  {
    key: '1',
    title: counts.value[1]?.name || $t('page.event.eventType.level.NA'),
    value: counts.value[1]?.value || 0,
    unit: '',
    color: {
      start: '#865ec0',
      end: '#5144b4'
    },
    icon: 'flowbite:arrow-up-down-outline'
  },
  {
    key: '2',
    title: counts.value[2]?.name || $t('page.event.eventType.level.NA'),
    value: counts.value[2]?.value || 0,
    unit: '',
    color: {
      start: '#56cdf3',
      end: '#719de3'
    },
    icon: 'hugeicons:user-block-01'
  },
  {
    key: '3',
    title: counts.value[3]?.name || $t('page.event.eventType.level.NA'),
    value: counts.value[3]?.value || 0,
    unit: '',
    color: {
      start: '#fcbc25',
      end: '#f68057'
    },
    icon: 'ic:outline-terminal'
  }
]);

watch(
  () => props.charts,
  () => {
    init();
  }
);

// 处理渐变

interface GradientBgProps {
  gradientColor: string;
}

const [DefineGradientBg, GradientBg] = createReusableTemplate<GradientBgProps>();

function getGradientColor(color: CardData['color']) {
  return `linear-gradient(to bottom right, ${color.start}, ${color.end})`;
}

// 处理初始化

async function initChart(chart: Api.Dashbaord.Chart) {
  const beginTime = dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss');
  const endTime = dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss');

  let value = 0;
  switch (chart.aggregateType) {
    case 'COUNT':
      {
        const { error, data } = await fetchCountEvent({
          beginTime,
          endTime,
          eventTypeId: chart.eventTypeId
        });

        if (!error) {
          value = data || 0;
        }
      }
      break;
    case 'SUM':
      {
        const { error, data } = await fetchSumEvent({
          beginTime,
          endTime,
          eventTypeId: chart.eventTypeId,
          sumField: chart.aggregateField
        });

        if (!error) {
          value = data || 0;
        }
      }
      break;
    default:
      break;
  }

  return {
    name: chart.name,
    value
  };
}

async function init() {
  const countsData = [];

  if (props.charts[0]) {
    countsData[0] = await initChart(props.charts[0]);
  }
  if (props.charts[1]) {
    countsData[1] = await initChart(props.charts[1]);
  }
  if (props.charts[2]) {
    countsData[2] = await initChart(props.charts[2]);
  }
  if (props.charts[3]) {
    countsData[3] = await initChart(props.charts[3]);
  }

  counts.value = countsData;
}
</script>

<template>
  <NCard :bordered="false" size="small" class="card-wrapper">
    <!-- define component start: GradientBg -->
    <DefineGradientBg v-slot="{ $slots, gradientColor }">
      <div class="rd-8px px-16px pb-4px pt-8px text-white" :style="{ backgroundImage: gradientColor }">
        <component :is="$slots.default" />
      </div>
    </DefineGradientBg>
    <!-- define component end: GradientBg -->

    <NGrid cols="s:1 m:2 l:4" responsive="screen" :x-gap="16" :y-gap="16">
      <NGi v-for="item in cardData" :key="item.key">
        <GradientBg :gradient-color="getGradientColor(item.color)" class="flex-1">
          <h3 class="text-16px">{{ item.title }}</h3>
          <div class="flex justify-between pt-12px">
            <SvgIcon :icon="item.icon" class="text-32px" />
            <CountTo
              :prefix="item.unit"
              :start-value="0"
              :end-value="item.value"
              class="text-30px text-white dark:text-dark"
            />
          </div>
        </GradientBg>
      </NGi>
    </NGrid>
  </NCard>
</template>

<style scoped></style>
