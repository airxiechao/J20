<script setup lang="tsx">
import { NButton, NPopconfirm, NTag } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import type { Ref } from 'vue';
import { ref } from 'vue';
import { fetchDeleteTask, fetchListTask, fetchStartTask, fetchStopTask } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import TaskOperateModal, { type OperateType } from './modules/task-operate-modal.vue';
import TaskSearch from './modules/task-search.vue';

const appStore = useAppStore();

// 数据

const { bool: visible, setTrue: openModal } = useBoolean();
const operateType = ref<OperateType>('add');
const editingData = ref<Api.Detection.Task | null>(null);
const startLoadings = ref<Record<string, boolean>>({});
const stopLoadings = ref<Record<string, boolean>>({});
const deletetLoadings = ref<Record<string, boolean>>({});

const { columns, columnChecks, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn: fetchListTask,
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
      title: $t('page.detection.task.name'),
      align: 'center',
      width: 250
    },
    {
      key: 'jobStatus',
      title: $t('page.detection.task.jobStatus.title'),
      align: 'center',
      render: row => (
        <NTag type={['CREATING', 'RUNNING'].includes(row.jobStatus ?? '') ? 'success' : 'default'}>
          {$t(`page.detection.task.jobStatus.${row.jobStatus}`)}
        </NTag>
      )
    },
    {
      key: 'jobNumLogIn',
      title: $t('page.detection.task.jobNumLogIn'),
      align: 'center'
    },
    {
      key: 'jobNumEventOut',
      title: $t('page.detection.task.jobNumEventOut'),
      align: 'center'
    },
    {
      key: 'jobLastUpdateTime',
      title: $t('page.detection.task.jobLastUpdateTime'),
      align: 'center'
    },
    {
      key: 'createTime',
      title: $t('page.detection.task.createTime'),
      align: 'center'
    },
    {
      key: 'lastUpdateTime',
      title: $t('page.detection.task.lastUpdateTime'),
      align: 'center'
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 300,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton
            type="primary"
            ghost
            size="small"
            loading={startLoadings.value[row.id]}
            onClick={() => handleStart(row.id)}
          >
            {$t('common.start')}
          </NButton>
          <NButton
            type="primary"
            ghost
            size="small"
            loading={stopLoadings.value[row.id]}
            onClick={() => handleStop(row.id)}
          >
            {$t('common.stop')}
          </NButton>
          <NButton type="primary" ghost size="small" onClick={() => handleEdit(row)}>
            {$t('common.edit')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
            {{
              default: () => $t('common.confirmDelete'),
              trigger: () => (
                <NButton type="error" ghost size="small" loading={deletetLoadings.value[row.id]}>
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

function handleEdit(item: Api.Detection.Task) {
  operateType.value = 'edit';
  editingData.value = { ...item };

  openModal();
}

async function handleStart(id: string) {
  startLoadings.value[id] = true;

  const { error } = await fetchStartTask({
    id
  });

  if (!error) {
    window.$message?.success?.($t('common.startSuccess'));
  }

  startLoadings.value[id] = false;

  getData();
}

async function handleStop(id: string) {
  stopLoadings.value[id] = true;

  const { error } = await fetchStopTask({
    id
  });

  if (!error) {
    window.$message?.success?.($t('common.stopSuccess'));
  }

  stopLoadings.value[id] = false;

  getData();
}

async function handleBatchDelete() {
  const id = checkedRowKeys.value.join(',');
  const { error } = await fetchDeleteTask({
    id
  });

  if (!error) {
    onBatchDeleted();
  }
}

async function handleDelete(id: string) {
  deletetLoadings.value[id] = true;

  const { error } = await fetchDeleteTask({
    id
  });

  if (!error) {
    onDeleted();
  }

  deletetLoadings.value[id] = false;
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <TaskSearch v-model:model="searchParams" @reset="handleReset" @search="getData" />
    <NCard
      :title="$t('page.detection.task.title')"
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
      <TaskOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
