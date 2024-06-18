<script setup lang="tsx">
import { ref, watch } from 'vue';
import type { Ref } from 'vue';
import { NButton, NPopconfirm, NSpace, NTag } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import { fetchDeleteEventType, fetchListEventType } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import EventTypeOperateModal, { type OperateType } from './modules/event-type-operate-modal.vue';

const appStore = useAppStore();

// 数据
const { bool: visible, setTrue: openModal } = useBoolean();

const wrapperRef = ref<HTMLElement | null>(null);

const { columns, columnChecks, data, loading, getData } = useTable({
  apiFn: fetchListEventType,
  columns: () => [
    {
      type: 'selection',
      align: 'center',
      width: 48
    },
    {
      key: 'name',
      title: $t('page.event.eventType.name'),
      align: 'left',
      width: 200
    },
    {
      key: 'level',
      title: $t('page.event.eventType.level.title'),
      align: 'center',
      width: 200,
      render: row => {
        return $t(`page.event.eventType.level.${row.level || 'NA'}`);
      }
    },
    {
      key: 'numEvent',
      title: $t('page.event.eventType.numEvent'),
      align: 'center',
      width: 200
    },
    {
      key: 'fieldSchema',
      title: $t('page.event.eventType.fieldSchema'),
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
      width: 230,
      render: row => (
        <div class="flex-center justify-end gap-8px">
          <NButton type="primary" ghost size="small" onClick={() => handleAddChildType(row)}>
            {$t('page.event.eventType.form.addChildType')}
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
const expandedRowKeys = ref<Array<string | number>>([]);

const operateType = ref<OperateType>('add');
const editingData: Ref<Api.Event.EventType | null> = ref(null);

watch(data, () => {
  function getRowKeys(types: Api.Event.EventType[]) {
    let keys = types.map(t => t.id);
    for (const t of types) {
      if (t.children) {
        keys = [...keys, ...getRowKeys(t.children)];
      }
    }

    return keys;
  }

  expandedRowKeys.value = getRowKeys(data.value);
});

// 处理事件

function handleUpdateExpandedRowKeys(keys: Array<string | number>) {
  expandedRowKeys.value = keys;
}

function handleAdd() {
  operateType.value = 'add';
  openModal();
}

async function handleBatchDelete() {
  const id = checkedRowKeys.value.join(',');
  const { error } = await fetchDeleteEventType({
    id
  });

  if (!error) {
    onBatchDeleted();
  }
}

async function handleDelete(id: string) {
  const { error } = await fetchDeleteEventType({
    id
  });

  if (!error) {
    onDeleted();
  }
}

function handleEdit(item: Api.Event.EventType) {
  operateType.value = 'edit';
  editingData.value = { ...item };

  openModal();
}

function handleAddChildType(item: Api.Event.EventType) {
  operateType.value = 'addChild';

  editingData.value = { ...item };

  openModal();
}
</script>

<template>
  <div ref="wrapperRef" class="flex-col-stretch gap-16px overflow-hidden lt-sm:overflow-auto">
    <NCard
      :title="$t('page.event.eventType.title')"
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
        :row-key="row => row.id"
        :expanded-row-keys="expandedRowKeys"
        :on-update:expanded-row-keys="handleUpdateExpandedRowKeys"
        remote
        default-expand-all
        class="sm:h-full"
      />
      <EventTypeOperateModal
        v-model:visible="visible"
        :operate-type="operateType"
        :row-data="editingData"
        @submitted="getData"
      />
    </NCard>
  </div>
</template>

<style scoped></style>
