<script setup lang="tsx">
import { ref } from 'vue';
import type { Ref } from 'vue';
import { NButton, NPopconfirm, type TreeOption } from 'naive-ui';
import { useBoolean } from '@sa/hooks';
import dayjs from 'dayjs';
import { fetchDeleteEvent, fetchListEvent } from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useTable } from '@/hooks/common/table';
import { $t } from '@/locales';
import { useEventTypes } from '@/hooks/business/eventTypes';
import EventSearch from './modules/event-search.vue';
import EventViewModal from './modules/event-view-modal.vue';

const appStore = useAppStore();

// 数据
const { columns, data, getData, loading, mobilePagination, searchParams, resetSearchParams } = useTable({
  apiFn: fetchListEvent,
  showTotal: false,
  apiParams: {
    current: 1,
    size: 20,
    beginTime: dayjs().startOf('day').subtract(1, 'month').format('YYYY-MM-DD HH:mm:ss'),
    endTime: dayjs().add(1, 'day').startOf('day').format('YYYY-MM-DD HH:mm:ss'),
    eventTypeId: null,
    level: null,
    query: null
  },
  columns: () => [
    {
      key: 'eventTypeName',
      title: $t('page.event.eventList.eventTypeName'),
      align: 'center',
      width: 200
    },
    {
      key: 'level',
      title: $t('page.event.eventList.level'),
      align: 'center',
      width: 150,
      render: row => {
        return $t(`page.event.eventType.level.${row.level || 'NA'}`);
      }
    },
    {
      key: 'message',
      title: $t('page.event.eventList.message'),
      align: 'left'
    },
    {
      key: 'taskName',
      title: $t('page.event.eventList.taskName'),
      align: 'center'
    },
    {
      key: 'ruleName',
      title: $t('page.event.eventList.ruleName'),
      align: 'center'
    },
    {
      key: 'timestamp',
      title: $t('page.event.eventList.timestamp'),
      align: 'center',
      render: row => {
        return dayjs(row.timestamp).format('YYYY-MM-DD HH:mm:ss');
      }
    },
    {
      key: 'operate',
      title: $t('common.operate'),
      align: 'center',
      width: 200,
      render: row => (
        <div class="flex-center gap-8px">
          <NButton
            type="primary"
            ghost
            size="small"
            onClick={() => handleView({ id: row.id, timestamp: row.timestamp })}
          >
            {$t('common.view')}
          </NButton>
          <NPopconfirm onPositiveClick={() => handleDelete({ id: row.id, timestamp: row.timestamp })}>
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
const { bool: visible, setTrue: openModal } = useBoolean();
const wrapperRef = ref<HTMLElement | null>(null);
const selectedKeys = ref<string[]>([]);

const editingData: Ref<Pick<Api.Event.Event, 'id' | 'timestamp'> | null> = ref(null);

const { eventTypeWithNumEventOptions } = useEventTypes();

// 处理查看事件
function handleView({ id, timestamp }: Pick<Api.Event.Event, 'id' | 'timestamp'>) {
  editingData.value = {
    id,
    timestamp
  };

  openModal();
}

// 处理删除事件
async function handleDelete({ id, timestamp }: Pick<Api.Event.Event, 'id' | 'timestamp'>) {
  const { error } = await fetchDeleteEvent({
    id,
    timestamp
  });

  if (!error) {
    loading.value = true;
    setTimeout(() => getData(), 1000);
  }
}

// 处理重置查询
function handleReset() {
  selectedKeys.value = [];
  resetSearchParams();
  getData();
}

// 处理点击类型节点
function handleClickTreeNode({ option }: { option: TreeOption }) {
  return {
    onClick() {
      const key = option.key as string;
      if (searchParams.eventTypeId === key) {
        searchParams.eventTypeId = null;
        searchParams.query = null;
        selectedKeys.value = [];
      } else {
        searchParams.eventTypeId = key;
        searchParams.query = null;
        selectedKeys.value = [searchParams.eventTypeId];
      }

      getData();
    }
  };
}
</script>

<template>
  <div ref="wrapperRef" class="flex gap-16px overflow-hidden lt-sm:overflow-auto">
    <!--
    <NCard :title="$t('page.event.eventType.title')" :bordered="false" size="small" class="w-280px card-wrapper">
      <NTree
        block-line
        :data="eventTypeWithNumEventOptions"
        :selected-keys="selectedKeys"
        selectable
        default-expand-all
        :node-props="handleClickTreeNode"
      />
    </NCard>
    -->
    <div class="flex-col-stretch sm:flex-1-hidden gap-16px overflow-hidden lt-sm:overflow-auto">
      <EventSearch v-model:model="searchParams" @reset="handleReset" @search="getData" />
      <NCard
        :title="$t('page.event.eventList.title')"
        :bordered="false"
        size="small"
        class="sm:flex-1-hidden card-wrapper"
      >
        <NDataTable
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
        <EventViewModal v-model:visible="visible" :row-data="editingData" />
      </NCard>
    </div>
  </div>
</template>

<style scoped></style>
