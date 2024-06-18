import { computed, ref } from 'vue';
import { fetchAllDatasource } from '@/service/api';
import { $t } from '@/locales';

/** 数据源 */
export default function useDatasource() {
  const datasources = ref<Api.Detection.Datasource[]>([]);

  async function getDatasources() {
    const { data: respData } = await fetchAllDatasource();
    datasources.value = respData ?? [];
  }

  // 数据源选项
  const datasourceOptions = computed(() => {
    return datasources.value.map(d => {
      return {
        label: d.name,
        value: d.id
      };
    });
  });

  getDatasources();

  // 起始位置选项
  const startingOffsetOptions = computed(() =>
    ['EARLIEST', 'LATEST'].map(t => {
      return {
        label: $t(`page.detection.task.startingOffset.${t}`),
        value: t
      };
    })
  );

  return {
    datasources,
    datasourceOptions,
    startingOffsetOptions
  };
}
