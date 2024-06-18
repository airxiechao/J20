<script setup lang="tsx">
import { NButton, NPopconfirm } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import type { Ref } from 'vue';
import { ref } from 'vue';
import { fetchDeleteDatasource, fetchListDatasource } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import DatasourceOperateModal, { type OperateType } from './modules/datasource-operate-modal.vue';
import DatasourceSearch from './modules/datasource-search.vue';

const appStore = useAppStore();

// 数据
const { bool: visible, setTrue: openModal } = useBoolean();
const operateType = ref<OperateType>('add');
const editingData: Ref<Api.Detection.Datasource | null> = ref(null);

const { columns, columnChecks, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn: fetchListDatasource,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 20,
    name: null
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'name',
      title: $t('page.detection.datasource.name'),
      align: 'center',
      minWidth: 250
    },
    {
      key: 'bootstrapServers',
      title: $t('page.detection.datasource.bootstrapServers'),
      align: 'center'
    },
    {
      key: 'topic',
      title: $t('page.detection.datasource.topic'),
      align: 'center'
    },
    {
      key: 'numPartition',
      title: $t('page.detection.datasource.numPartition'),
      align: 'center'
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton type="primary" ghost size="small" onClick={() => handleEdit(row)}>
            {$t('common.edit')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
            {{
              default: () => $t('common.confirmDelete'),
              trigger: () => (
                <NButton type="error" ghost size="small">
                  {$t('common.delete')}
                </NButton>
              )
            }}
          </NPopconfirm>
        </div>
      )
    }
  ]
});

const { checkedRowKeys, onBatchDeleted, onDeleted } = useTableOperate(data, getData);

// 处理事件

function handleReset() {
  resetSearchParams();
  getData();
}

function handleAdd() {
  operateType.value = 'add';
  openModal();
}

function handleEdit(item: Api.Detection.Datasource) {
  operateType.value = 'edit';
  editingData.value = { ...item };

  openModal();
}

async function handleBatchDelete() {
  const id = checkedRowKeys.value.join(',');
  const { error } = await fetchDeleteDatasource({
    id
  });

  if (!error) {
    onBatchDeleted();
  }
}

async function handleDelete(id: string) {
  const { error } = await fetchDeleteDatasource({
    id
  });

  if (!error) {
    onDeleted();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <DatasourceSearch v-model:model="searchParams" @reset="handleReset" @search="getData" />
    <NCard
      :title="$t('page.detection.datasource.title')"
      :bordered="false"
      size="small"
      class="sm:flex-1-hidden card-wrapper"
    >
      <template #header-extra>
        <TableHeaderOperation
          v-model:columns="columnChecks"
          :disabled-delete="checkedRowKeys.length === 0"
          :loading="loading"
          @add="handleAdd"
          @delete="handleBatchDelete"
          @refresh="getData"
        />
      </template>
      <NDataTable
        v-model:checked-row-keys="checkedRowKeys"
        :columns="columns"
        :data="data"
        size="small"
        :flex-height="!appStore.isMobile"
        :loading="loading"
        remote
        :row-key="row => row.id"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <DatasourceOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
