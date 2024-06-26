<script setup lang="ts">
import { watch } from 'vue';
import dayjs from 'dayjs';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchGroupByEvent } from '@/service/api';

const appStore = useAppStore();

// 数据
defineOptions({
  name: 'NewsChart'
});

const { domRef, updateOptions } = useEcharts(() => ({
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  tooltip: {
    trigger: 'item'
  },
  series: {
    type: 'pie',
    roseType: 'area',
    itemStyle: {
      borderRadius: 8
    },
    data: []
  }
}));

// 处理初始化

async function initChart() {
  const beginTime = dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss');
  const endTime = dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss');

  const { error, data } = await fetchGroupByEvent({
    beginTime,
    endTime,
    level: 'ALERT',
    groupByField: 'eventTypeName.keyword'
  });

  if (!error) {
    return data;
  }

  return [];
}

async function init() {
  const data = await initChart();

  updateOptions(opts => {
    if (data.length > 0) {
      opts.series.data = data.map(d => ({
        name: d.key,
        value: d.value
      }));
      opts.graphic = undefined;
    } else {
      opts.graphic = {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: $t('common.noData') }
      };
    }

    return opts;
  });
}

init();
</script>

<template>
  <NCard :bordered="false" class="card-wrapper">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </NCard>
</template>

<style scoped></style>
