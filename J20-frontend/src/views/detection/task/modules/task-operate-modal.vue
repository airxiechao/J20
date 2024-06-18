<script setup lang="tsx">
import { computed, reactive, watch } from 'vue';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAddTask, fetchListRule, fetchUpdateTask } from '@/service/api';
import { useTable, useTableOperate } from '@/hooks/common/table';
import useDatasource from '../hooks/use-datasource';

defineOptions({
  name: 'TaskOperateModal'
});

// 数据
export type OperateType = NaiveUI.TableOperateType;

interface Props {
  operateType: OperateType;
  rowData?: Api.Detection.Task | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}
const emit = defineEmits<Emits>();

const { loading, startLoading, endLoading } = useLoading();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const title = computed(() => {
  const titles: Record<OperateType, string> = {
    add: $t('page.detection.task.form.addTask'),
    edit: $t('page.detection.task.form.editTask')
  };
  return titles[props.operateType];
});

/** 规则 */
const {
  columns: ruleColumns,
  data: ruleData,
  getData: getRuleData,
  loading: ruleLoading,
  mobilePagination: rulePagination
} = useTable({
  apiFn: fetchListRule,
  showTotal: true,
  apiParams: {
    current: 1,
    size: 10,
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
    }
  ]
});

const { checkedRowKeys: checkedRuleKeys } = useTableOperate(ruleData, getRuleData);

/** 数据源 */
const { datasourceOptions, startingOffsetOptions } = useDatasource();

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

/** 任务 */

type Model = Pick<
  Api.Detection.Task,
  'id' | 'name' | 'description' | 'srcDataSourceId' | 'startingOffsetStrategy' | 'rules'
>;
const model: Model = reactive(createDefaultModel());
function createDefaultModel(): Model {
  return {
    id: '',
    name: '',
    description: '',
    srcDataSourceId: '',
    startingOffsetStrategy: 'LATEST',
    rules: []
  };
}

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

watch(checkedRuleKeys, () => {
  model.rules = checkedRuleKeys.value.map(k => {
    return {
      id: k
    } as Api.Detection.Rule;
  });
});

/** 表单验证 */

type TaskKey = Extract<keyof Model, 'name' | 'srcDataSourceId' | 'startingOffsetStrategy' | 'rules'>;
const formRules: Record<TaskKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  srcDataSourceId: defaultRequiredRule,
  startingOffsetStrategy: defaultRequiredRule,
  rules: defaultRequiredRule
};

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();

  if (props.operateType === 'add') {
    const { error } = await fetchAddTask(model);

    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeModal();
      emit('submitted');
    }
  } else if (props.operateType === 'edit') {
    const { error } = await fetchUpdateTask(model);

    if (!error) {
      window.$message?.success($t('common.updateSuccess'));
      closeModal();
      emit('submitted');
    }
  }

  endLoading();
}

// 处理初始化
function handleInitModel() {
  Object.assign(model, createDefaultModel());
  checkedRuleKeys.value = [];

  if (!props.rowData) return;

  if (props.operateType === 'edit') {
    Object.assign(model, props.rowData);
  }

  checkedRuleKeys.value = model.rules.map(r => r.id) || [];

  if (!model.startingOffsetStrategy) {
    model.startingOffsetStrategy = 'LATEST';
  }
}
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-800px">
    <NForm ref="formRef" :model="model" :rules="formRules" label-placement="left" :label-width="100">
      <NFormItem :label="$t('page.detection.task.name')" path="name">
        <NInput v-model:value="model.name" :placeholder="$t('page.detection.task.form.name')" />
      </NFormItem>
      <NFormItem :label="$t('page.detection.task.description')" path="description">
        <NInput
          v-model:value="model.description"
          :placeholder="$t('page.detection.task.form.description')"
          type="textarea"
        />
      </NFormItem>
      <NGrid cols="1 s:2" responsive="screen">
        <NFormItemGi :label="$t('page.detection.task.srcDataSourceId')" path="srcDataSourceId">
          <NSelect v-model:value="model.srcDataSourceId" :options="datasourceOptions" clearable />
        </NFormItemGi>
        <NFormItemGi :label="$t('page.detection.task.startingOffsetStrategy')" path="startingOffsetStrategy">
          <NSelect v-model:value="model.startingOffsetStrategy" :options="startingOffsetOptions" clearable />
        </NFormItemGi>
      </NGrid>
      <NFormItem :label="$t('page.detection.task.rules')" path="rules">
        <NBadge :value="checkedRuleKeys.length" processing type="info" class="w-100%">
          <NDataTable
            v-model:checked-row-keys="checkedRuleKeys"
            :columns="ruleColumns"
            :data="ruleData"
            size="small"
            :loading="ruleLoading"
            remote
            :row-key="row => row.id"
            :pagination="rulePagination"
          />
        </NBadge>
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
