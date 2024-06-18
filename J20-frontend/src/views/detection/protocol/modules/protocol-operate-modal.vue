<script setup lang="tsx">
import { computed, reactive, watch } from 'vue';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAddProtocol, fetchUpdateProtocol } from '@/service/api';

defineOptions({
  name: 'ProtocolOperateModal'
});

// 数据

export type OperateType = NaiveUI.TableOperateType;

interface Props {
  operateType: OperateType;
  rowData?: Api.Detection.Protocol | null;
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

const title = computed(() => {
  const titles: Record<OperateType, string> = {
    add: $t('page.detection.protocol.form.addProtocol'),
    edit: $t('page.detection.protocol.form.editProtocol')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.Detection.Protocol, 'code' | 'fieldSchema'>;
const model: Model = reactive(createDefaultModel());
function createDefaultModel(): Model {
  return {
    code: '',
    fieldSchema: []
  };
}

type RuleKey = Extract<keyof Model, 'code'>;
const rules: Record<RuleKey, App.Global.FormRule> = {
  code: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

// 处理初始化
function handleInitModel() {
  Object.assign(model, createDefaultModel());

  if (!props.rowData) return;

  if (props.operateType === 'edit') {
    Object.assign(model, props.rowData);
  }

  if (!model.fieldSchema) {
    model.fieldSchema = [];
  }
}

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();

  if (props.operateType === 'add') {
    const { error } = await fetchAddProtocol(model);

    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeModal();
      emit('submitted');
    }
  } else if (props.operateType === 'edit') {
    const { error } = await fetchUpdateProtocol(model);

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
      <NFormItem :label="$t('page.detection.protocol.code')" path="code">
        <NInput v-model:value="model.code" :placeholder="$t('page.detection.protocol.form.code')" />
      </NFormItem>
      <NFormItem :label="$t('page.detection.protocol.fieldSchema')" path="fieldSchema">
        <NDynamicInput
          v-model:value="model.fieldSchema!"
          preset="pair"
          show-sort-button
          :key-placeholder="$t('page.detection.protocol.form.fieldPath')"
          :value-placeholder="$t('page.detection.protocol.form.fieldName')"
        ></NDynamicInput>
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
