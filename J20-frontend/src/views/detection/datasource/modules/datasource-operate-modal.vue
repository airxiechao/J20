<script setup lang="tsx">
import { computed, reactive, watch } from 'vue';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAddDatasource, fetchUpdateDatasource } from '@/service/api';

defineOptions({
  name: 'DatasourceOperateModal'
});

// 数据

export type OperateType = NaiveUI.TableOperateType;

interface Props {
  operateType: OperateType;
  rowData?: Api.Detection.Datasource | null;
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
const { defaultRequiredRule } = useFormRules();

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

const title = computed(() => {
  const titles: Record<OperateType, string> = {
    add: $t('page.detection.datasource.form.addDatasource'),
    edit: $t('page.detection.datasource.form.editDatasource')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.Detection.Datasource, 'id' | 'name' | 'bootstrapServers' | 'topic' | 'numPartition'>;
const model: Model = reactive(createDefaultModel());
function createDefaultModel(): Model {
  return {
    id: '',
    bootstrapServers: '',
    name: '',
    topic: '',
    numPartition: 1
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'bootstrapServers' | 'topic' | 'numPartition'>;
const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  bootstrapServers: defaultRequiredRule,
  topic: defaultRequiredRule,
  numPartition: defaultRequiredRule
};

// 处理初始化
function handleInitModel() {
  Object.assign(model, createDefaultModel());

  if (!props.rowData) return;

  if (props.operateType === 'edit') {
    Object.assign(model, props.rowData);
  }
}

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();
  if (props.operateType === 'add') {
    const { error } = await fetchAddDatasource(model);

    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeModal();
      emit('submitted');
    }
  } else if (props.operateType === 'edit') {
    const { error } = await fetchUpdateDatasource(model);

    if (!error) {
      window.$message?.success($t('common.updateSuccess'));
      closeModal();
      emit('submitted');
    }
  }

  endLoading();
}

// 处理关闭
function closeModal() {
  visible.value = false;
}
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-800px">
    <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
      <NFormItem :label="$t('page.detection.datasource.name')" path="name">
        <NInput v-model:value="model.name" :placeholder="$t('page.detection.datasource.form.name')" />
      </NFormItem>
      <NFormItem :label="$t('page.detection.datasource.bootstrapServers')" path="bootstrapServers">
        <NInput
          v-model:value="model.bootstrapServers"
          :placeholder="$t('page.detection.datasource.form.bootstrapServers')"
        />
      </NFormItem>
      <NFormItem :label="$t('page.detection.datasource.topic')" path="topic">
        <NInput v-model:value="model.topic" :placeholder="$t('page.detection.datasource.form.topic')" />
      </NFormItem>
      <NFormItem :label="$t('page.detection.datasource.numPartition')" path="numPartition">
        <NInputNumber
          v-model:value="model.numPartition"
          :placeholder="$t('page.detection.datasource.form.numPartition')"
          min="1"
          clearable
        />
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
