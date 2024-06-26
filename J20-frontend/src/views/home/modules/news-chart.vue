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
  dataset: [
    {
      dimensions: ['key', 'value'],
      source: []
    },
    {
      transform: {
        type: 'sort',
        config: { dimension: 'value', order: 'asc' }
      }
    }
  ],
  tooltip: {
    trigger: 'axis'
  },
  grid: { containLabel: true, x: 0, y: 10, x2: 20, y2: 10 },
  xAxis: {
    axisLabel: {
      formatter(value: number) {
        const formattedValue = value.toExponential();
        return formattedValue;
      }
    }
  },
  yAxis: {
    type: 'category',
    axisLine: {
      show: false
    }
  },
  series: {
    type: 'bar',
    encode: {
      x: 'value',
      y: 'key'
    },
    itemStyle: {
      color: '#5da8ff'
    },
    datasetIndex: 1
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
      opts.dataset[0].source = data.map(d => [d.key, d.value]);
      opts.yAxis.axisLine.show = true;
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
