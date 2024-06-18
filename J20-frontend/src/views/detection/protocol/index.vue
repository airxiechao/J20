<script setup lang="tsx">
import { NButton, NPopconfirm, NSpace, NTag } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import type { Ref } from 'vue';
import { ref } from 'vue';
import { fetchDeleteProtocol, fetchListProtocol } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import ProtocolOperateModal, { type OperateType } from './modules/protocol-operate-modal.vue';
import ProtocolSearch from './modules/protocol-search.vue';

// 创建数据

const appStore = useAppStore();
const { bool: visible, setTrue: openModal } = useBoolean();
const operateType = ref<OperateType>('add');
const editingData: Ref<Api.Detection.Protocol | null> = ref(null);

const { columns, columnChecks, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn: fetchListProtocol,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 20,
    code: null
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'code',
      title: $t('page.detection.protocol.code'),
      align: 'center',
      width: 200
    },
    {
      key: 'fieldSchema',
      title: $t('page.detection.protocol.fieldSchema'),
      align: 'center',
      render: row => (
        <NSpace justify="center">
          {(row.fieldSchema || []).map(field => {
            return (
              <NTag>
                {field.value}（{field.key}）
              </NTag>
            );
          })}
        </NSpace>
      )
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
          <NPopconfirm onPositiveClick={() => handleDelete(row.code)}>
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

function handleEdit(item: Api.Detection.Protocol) {
  operateType.value = 'edit';
  editingData.value = { ...item };

  openModal();
}

async function handleBatchDelete() {
  const code = checkedRowKeys.value.join(',');
  const { error } = await fetchDeleteProtocol({
    code
  });

  if (!error) {
    onBatchDeleted();
  }
}

async function handleDelete(code: string) {
  const { error } = await fetchDeleteProtocol({
    code
  });

  if (!error) {
    onDeleted();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <ProtocolSearch v-model:model="searchParams" @reset="handleReset" @search="getData" />
    <NCard
      :title="$t('page.detection.protocol.title')"
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
        :row-key="row => row.code"
        :pagination="mobilePagination"
        class="sm:h-full"
      />
      <ProtocolOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
