<script setup lang="ts">
import { watch } from 'vue';
import dayjs from 'dayjs';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useEcharts } from '@/hooks/common/echarts';
import { fetchHistogramEvent } from '@/service/api';

const appStore = useAppStore();

defineOptions({
  name: 'LineChart'
});

// 数据
interface Props {
  chart: Api.Dashbaord.Chart;
}

const props = defineProps<Props>();

const { domRef, updateOptions } = useEcharts(() => ({
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {
    data: ['']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'time',
    boundaryGap: false,
    axisLine: {
      show: false
    }
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      color: '#8e9dff',
      name: '',
      type: 'line',
      smooth: true,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 0.25,
              color: '#8e9dff'
            },
            {
              offset: 1,
              color: '#fff'
            }
          ]
        }
      },
      data: [] as [number, number][]
    }
  ]
}));

watch(
  () => props.chart,
  () => {
    init();
  }
);

// 初始化
async function initChart(chart: Api.Dashbaord.Chart) {
  const beginTime = dayjs().startOf('day').format('YYYY-MM-DD HH:mm:ss');
  const endTime = dayjs().endOf('day').format('YYYY-MM-DD HH:mm:ss');

  switch (chart.aggregateType) {
    case 'HISTOGRAM':
      {
        const { error, data } = await fetchHistogramEvent({
          beginTime,
          endTime,
          eventTypeId: chart.eventTypeId,
          interval: 'hour',
          sumField: chart.aggregateField
        });

        if (!error) {
          return data;
        }
      }
      break;
    default:
      break;
  }

  return [];
}

async function init() {
  if (!props.chart) {
    return;
  }

  const data = await initChart(props.chart);

  updateOptions(opts => {
    if (data.length > 0) {
      opts.legend.data = [props.chart.name];
      opts.series[0].name = props.chart.name;
      opts.series[0].data = data.map(d => [d.key, d.value]);
      opts.xAxis.axisLine.show = true;
    } else {
      opts.graphic = {
        type: 'text',
        left: 'center',
        top: 'middle',
        style: { text: `${props.chart.name || $t('page.event.eventType.level.NA')} - ${$t('common.noData')}` }
      };
    }

    return opts;
  });
}
</script>

<template>
  <NCard :bordered="false" class="card-wrapper">
    <div ref="domRef" class="h-360px overflow-hidden"></div>
  </NCard>
</template>

<style scoped></style>
