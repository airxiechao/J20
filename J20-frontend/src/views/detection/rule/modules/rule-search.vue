<script setup lang="ts">
import { $t } from '@/locales';
import { useNaiveForm } from '@/hooks/common/form';
import { useEventTypes } from '@/hooks/business/eventTypes';

defineOptions({
  name: 'RuleSearch'
});

// 数据
interface Emits {
  (e: 'reset'): void;
  (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const { formRef, validate, restoreValidation } = useNaiveForm();

const model = defineModel<Api.Detection.RuleListParams>('model', { required: true });
const { eventTypeOptions } = useEventTypes();

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
        <NFormItemGi span="24 s:12 m:8" :label="$t('page.detection.rule.name')" path="name" class="pr-24px">
          <NInput v-model:value="model.name" :placeholder="$t('page.detection.rule.form.name')" type="text" clearable />
        </NFormItemGi>
        <NFormItemGi
          span="24 s:12 m:8"
          :label="$t('page.detection.rule.outputEventTypeId')"
          path="outputEventTypeId"
          class="pr-24px"
        >
          <NTreeSelect
            v-model:value="model.outputEventTypeId"
            :options="eventTypeOptions"
            :placeholder="$t('page.detection.rule.form.outputEventTypeId')"
            clearable
          />
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
