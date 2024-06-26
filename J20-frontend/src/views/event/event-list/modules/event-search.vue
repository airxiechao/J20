<script setup lang="ts">
import { computed } from 'vue';
import { $t } from '@/locales';
import { useNaiveForm } from '@/hooks/common/form';
import { useEventTypes } from '@/hooks/business/eventTypes';

// 数据
defineOptions({
  name: 'UserSearch'
});

interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const { formRef, validate, restoreValidation } = useNaiveForm();
const { eventTypeOptions } = useEventTypes();

const model = defineModel<Api.Event.EventListParams>('model', { required: true });

const levelOptions = computed(() =>
  ['LOG', 'ALERT'].map(i => ({
    label: $t(`page.event.eventType.level.${i}`),
    value: i
  }))
);

// 处理重置
async function reset() {
  await restoreValidation();
  emit('reset');
}

// 处理搜索
async function search() {
  await validate();
  emit('search');
}
</script>

<template>
  <NCard :title="$t('common.search')" :bordered="false" size="small" class="card-wrapper">
    <NForm ref="formRef" :model="model" label-placement="left" :label-width="100">
      <NGrid responsive="screen" item-responsive>
        <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.beginTime')" path="beginTime" class="pr-24px">
          <NDatePicker
            v-model:formatted-value="model.beginTime"
            :placeholder="$t('page.event.eventList.form.beginTime')"
            type="datetime"
            clearable
            class="w-full"
          />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.endTime')" path="endTime" class="pr-24px">
          <NDatePicker
            v-model:formatted-value="model.endTime"
            :placeholder="$t('page.event.eventList.form.endTime')"
            type="datetime"
            clearable
            class="w-full"
          />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.level')" path="level" class="pr-24px">
          <NSelect
            v-model:value="model.level"
            :options="levelOptions"
            :placeholder="$t('page.event.eventList.form.level')"
            clearable
          />
        </NFormItemGi>
        <NFormItemGi
          span="24 s:12 m:8"
          :label="$t('page.event.eventList.eventTypeId')"
          path="eventTypeId"
          class="pr-24px"
        >
          <NTreeSelect
            v-model:value="model.eventTypeId"
            :options="eventTypeOptions"
            :placeholder="$t('page.event.eventList.form.eventTypeId')"
            clearable
          />
        </NFormItemGi>
        <NFormItemGi span="24 s:12 m:8" :label="$t('page.event.eventList.query')" path="query" class="pr-24px">
          <NInput v-model:value="model.query" :placeholder="$t('page.event.eventList.form.query')" clearable />
        </NFormItemGi>

        <NFormItemGi span="24 s:12 m:8">
          <NSpace class="w-full" justify="start">
            <NButton type="primary" ghost @click="search">
              <template #icon>
                <icon-ic-round-search class="text-icon" />
              </template>
              {{ $t('common.search') }}
            </NButton>
            <NButton @click="reset">
              <template #icon>
                <icon-ic-round-refresh class="text-icon" />
              </template>
              {{ $t('common.reset') }}
            </NButton>
          </NSpace>
        </NFormItemGi>
      </NGrid>
    </NForm>
  </NCard>
</template>

<style scoped></style>
