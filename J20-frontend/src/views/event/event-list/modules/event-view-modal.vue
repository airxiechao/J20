<script setup lang="tsx">
import { type Ref, ref, watch } from 'vue';
import CodeMirror from 'vue-codemirror6';
import { json } from '@codemirror/lang-json';
import dayjs from 'dayjs';
import { $t } from '@/locales';
import { fetchGetEvent } from '@/service/api';

defineOptions({
  name: 'EventViewModal'
});

// 数据
interface Props {
  rowData: Pick<Api.Event.Event, 'id' | 'timestamp'> | null;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const jsonLang = json();

const loading = ref(false);
const model: Ref<Api.Event.Event | null> = ref(null);

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
  }
});

// 处理关闭
function closeModal() {
  visible.value = false;
}

// 处理初始化
async function handleInitModel() {
  if (!props.rowData) {
    return;
  }

  model.value = null;
  loading.value = true;

  const { error, data } = await fetchGetEvent({
    id: props.rowData.id,
    timestamp: props.rowData.timestamp
  });

  loading.value = false;
  if (!error) {
    model.value = data;
  }
}

// 处理时间戳
function formatTimestamp(timestamp?: number) {
  if (!timestamp) {
    return '';
  }

  return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss');
}
</script>

<template>
  <NModal v-model:show="visible" :title="$t('page.event.eventList.detail')" preset="card" class="w-1200px">
    <NSkeleton v-if="loading" text :repeat="5" />
    <template v-else>
      <NForm label-placement="left" :label-width="100">
        <NGrid responsive="screen" item-responsive x-gap="8" y-gap="8">
          <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.id')">
            <NInput :value="model?.id" type="text" readonly />
          </NFormItemGi>
          <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.eventTypeName')">
            <NInput :value="model?.eventTypeName" type="text" readonly />
          </NFormItemGi>
          <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.level')">
            <NInput :value="$t(`page.event.eventType.level.${model?.level || 'NA'}`)" type="text" readonly />
          </NFormItemGi>
          <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.timestamp')">
            <NInput :value="formatTimestamp(model?.timestamp)" type="text" readonly />
          </NFormItemGi>
          <NFormItemGi span="24" :label="$t('page.event.eventList.message')">
            <NInput :value="model?.message" type="textarea" readonly />
          </NFormItemGi>
          <NFormItemGi span="24" :label="$t('page.event.eventList.property')">
            <NSpace v-if="Object.keys(model?.property || []).length > 0">
              <div v-for="item in Object.keys(model?.property || {})" :key="item">
                <span>{{ model?.property[item].name }}：</span>
                <NTag>{{ model?.property[item].value }}</NTag>
              </div>
            </NSpace>
            <NTag v-else>{{ $t('common.noData') }}</NTag>
          </NFormItemGi>
          <NFormItemGi span="24" :label="$t('page.event.eventList.originalLog')">
            <CodeMirror
              v-if="model"
              :model-value="JSON.stringify(model.originalLog, null, 2)"
              :lang="jsonLang"
              basic
              readonly
              class="max-h-400px flex-1 overflow-auto border-1"
            />
            <NTag v-else>{{ $t('common.noData') }}</NTag>
          </NFormItemGi>
        </NGrid>
      </NForm>
    </template>

    <template #footer>
      <NSpace justify="end" :size="16">
        <NButton type="primary" @click="closeModal">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
