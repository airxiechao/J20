<script setup lang="tsx">
import { computed, ref, watch } from 'vue';
import { useLoading } from '@sa/hooks';
import { useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchUpdateDashboardChart } from '@/service/api';
import { useEventTypes } from '@/hooks/business/eventTypes';

defineOptions({
  name: 'DashboardOperateModal'
});

// 数据
export type OperateType = NaiveUI.TableOperateType;

interface Props {
  charts: Api.Dashbaord.Chart[];
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}
const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { loading, startLoading, endLoading } = useLoading();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { eventTypes, eventTypeOptions, searchEventType } = useEventTypes();

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

const aggregateTypeOptions = computed(() => {
  const opts = [
    {
      label: $t('page.dashboard.aggregateType.COUNT'),
      value: 'COUNT'
    },
    {
      label: $t('page.dashboard.aggregateType.SUM'),
      value: 'SUM'
    },
    {
      label: $t('page.dashboard.aggregateType.GROUP_BY'),
      value: 'GROUP_BY'
    },
    {
      label: $t('page.dashboard.aggregateType.HISTOGRAM'),
      value: 'HISTOGRAM'
    }
  ];

  return opts;
});

type Model = CommonType.RecordNullable<Api.Dashbaord.Chart>[];
const model = ref<Model>(createDefaultModel());
function createDefaultModel(): Model {
  return [
    {
      name: null,
      eventTypeId: null,
      aggregateType: null,
      groupByField: null,
      aggregateField: null
    },
    {
      name: null,
      eventTypeId: null,
      aggregateType: null,
      groupByField: null,
      aggregateField: null
    },
    {
      name: null,
      eventTypeId: null,
      aggregateType: null,
      groupByField: null,
      aggregateField: null
    },
    {
      name: null,
      eventTypeId: null,
      aggregateType: null,
      groupByField: null,
      aggregateField: null
    },
    {
      name: null,
      eventTypeId: null,
      aggregateType: 'HISTOGRAM',
      groupByField: null,
      aggregateField: null
    },
    {
      name: null,
      eventTypeId: null,
      aggregateType: 'GROUP_BY',
      groupByField: null,
      aggregateField: null
    }
  ];
}

function getEventTypeFields(i: number) {
  return computed(() => {
    return (
      searchEventType(model.value[i].eventTypeId, eventTypes.value)?.fieldSchema?.map(f => {
        return {
          label: `${f.value} - ${f.key}`,
          value: f.key
        };
      }) ?? []
    );
  });
}

const eventTypeFieldOptions = [
  getEventTypeFields(0),
  getEventTypeFields(1),
  getEventTypeFields(2),
  getEventTypeFields(3),
  getEventTypeFields(4),
  getEventTypeFields(5)
];

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();
  const { error } = await fetchUpdateDashboardChart({ charts: model.value });

  if (!error) {
    closeModal();
    emit('submitted');
  }

  endLoading();
}

// 处理关闭
function closeModal() {
  visible.value = false;
}

// 处理初始化
function handleInitModel() {
  Object.assign(model.value, createDefaultModel());
  if (props.charts) {
    Object.assign(model.value, props.charts);
  }
}
</script>

<template>
  <NModal v-model:show="visible" :title="$t('page.dashboard.config')" preset="card" class="w-920px">
    <NForm ref="formRef" :model="model" label-placement="left" :label-width="70">
      <NFormItem :label="$t('page.dashboard.chart1')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[0].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[0].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[0].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[0].groupByField"
              :options="eventTypeFieldOptions[0].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[0].aggregateField"
              :options="eventTypeFieldOptions[0].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
      <NFormItem :label="$t('page.dashboard.chart2')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[1].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[1].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[1].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[1].groupByField"
              :options="eventTypeFieldOptions[1].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[1].aggregateField"
              :options="eventTypeFieldOptions[1].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
      <NFormItem :label="$t('page.dashboard.chart3')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[2].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[2].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[2].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[2].groupByField"
              :options="eventTypeFieldOptions[2].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[2].aggregateField"
              :options="eventTypeFieldOptions[2].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
      <NFormItem :label="$t('page.dashboard.chart4')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[3].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[3].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[3].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[3].groupByField"
              :options="eventTypeFieldOptions[3].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[3].aggregateField"
              :options="eventTypeFieldOptions[3].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
      <NFormItem :label="$t('page.dashboard.chart5')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[4].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[4].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[4].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[4].groupByField"
              :options="eventTypeFieldOptions[4].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[4].aggregateField"
              :options="eventTypeFieldOptions[4].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
      <NFormItem :label="$t('page.dashboard.chart6')">
        <NSpace vertical>
          <NInputGroup>
            <NInput v-model:value="model[5].name" :placeholder="$t('page.dashboard.name')" />
            <NTreeSelect
              v-model:value="model[5].eventTypeId"
              :options="eventTypeOptions"
              :placeholder="$t('page.dashboard.eventTypeId')"
              clearable
              class="min-w-200px"
            />
            <NSelect
              v-model:value="model[5].aggregateType"
              :placeholder="$t('page.dashboard.aggregateType.title')"
              :options="aggregateTypeOptions"
              clearable
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[5].groupByField"
              :options="eventTypeFieldOptions[5].value"
              clearable
              :placeholder="$t('page.dashboard.groupByField')"
              class="min-w-150px"
            />
            <NSelect
              v-model:value="model[5].aggregateField"
              :options="eventTypeFieldOptions[5].value"
              clearable
              :placeholder="$t('page.dashboard.aggregateField')"
              class="min-w-150px"
            />
          </NInputGroup>
        </NSpace>
      </NFormItem>
    </NForm>

    <template #footer>
      <NSpace justify="end" :size="16">
        <NButton @click="closeModal">{{ $t('common.cancel') }}</NButton>
        <NButton type="primary" :loading="loading" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
