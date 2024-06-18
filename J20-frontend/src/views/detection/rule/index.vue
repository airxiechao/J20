<script setup lang="tsx">
import { NButton, NPopconfirm } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import type { Ref } from 'vue';
import { ref } from 'vue';
import { fetchDeleteRule, fetchListRule } from '@/service/api';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import RuleOperateModal, { type OperateType } from './modules/rule-operate-modal.vue';
import RuleTestModal from './modules/rule-test-modal.vue';
import RuleSearch from './modules/rule-search.vue';

const appStore = useAppStore();

// 数据

const { bool: visible, setTrue: openModal } = useBoolean();
const { bool: testVisible, setTrue: openTestModal } = useBoolean();
const operateType = ref<OperateType>('add');
const editingData: Ref<Api.Detection.Rule | null> = ref(null);
const testingData: Ref<Api.Detection.Rule | null> = ref(null);

const { columns, columnChecks, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn: fetchListRule,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 20,
    name: null,
    outputEventTypeId: null
  },
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'name',
      title: $t('page.detection.rule.name'),
      align: 'center',
      width: 200
    },
    {
      key: 'outputEventTypeName',
      title: $t('page.detection.rule.outputEventTypeName'),
      align: 'center'
    },
    {
      key: 'protocol',
      title: $t('page.detection.rule.protocol'),
      align: 'center'
    },
    {
      key: 'createTime',
      title: $t('page.detection.rule.createTime'),
      align: 'center'
    },
    {
      key: 'lastUpdateTime',
      title: $t('page.detection.rule.lastUpdateTime'),
      align: 'center'
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton type="default" ghost size="small" onClick={() => handleTest(row)}>
            {$t('common.test')}
          </NButton>
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

function handleEdit(item: Api.Detection.Rule) {
  operateType.value = 'edit';
  editingData.value = { ...item };

  openModal();
}

function handleTest(item: Api.Detection.Rule) {
  testingData.value = { ...item };

  openTestModal();
}

async function handleBatchDelete() {
  const id = checkedRowKeys.value.join(',');
  const { error } = await fetchDeleteRule({
    id
  });

  if (!error) {
    onBatchDeleted();
  }
}

async function handleDelete(id: string) {
  const { error } = await fetchDeleteRule({
    id
  });

  if (!error) {
    onDeleted();
  }
}
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <RuleSearch v-model:model="searchParams" @reset="handleReset" @search="getData" />
    <NCard
      :title="$t('page.detection.rule.title')"
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
      <RuleOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
      <RuleTestModal v-model:visible="testVisible" :row-data="testingData" />
    </NCard>
  </div>
</template>

<style scoped></style>
