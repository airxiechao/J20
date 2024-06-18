<script setup lang="tsx">
import { computed, reactive, watch } from 'vue';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAddEventType, fetchUpdateEventType } from '@/service/api';

defineOptions({
  name: 'EventTypeOperateModal'
});

// 数据
export type OperateType = NaiveUI.TableOperateType | 'addChild';

interface Props {
  operateType: OperateType;
  rowData?: Api.Event.EventType | null;
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
    add: $t('page.event.eventType.form.addType'),
    addChild: $t('page.event.eventType.form.addChildType'),
    edit: $t('page.event.eventType.form.editType')
  };
  return titles[props.operateType];
});

type Model = Pick<Api.Event.EventType, 'id' | 'name' | 'level' | 'parentId' | 'fieldSchema'>;

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Model {
  return {
    id: '',
    name: '',
    level: '',
    parentId: null,
    fieldSchema: []
  };
}

type RuleKey = Extract<keyof Model, 'name' | 'level'>;
const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  level: defaultRequiredRule
};

const levelOptions = computed(() =>
  ['LOG', 'ALERT'].map(i => ({
    label: $t(`page.event.eventType.level.${i}`),
    value: i
  }))
);

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

// 处理关闭
function closeModal() {
  visible.value = false;
}

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();
  if (props.operateType === 'add' || props.operateType === 'addChild') {
    const { error } = await fetchAddEventType(model);

    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeModal();
      emit('submitted');
    }
  } else if (props.operateType === 'edit') {
    const { error } = await fetchUpdateEventType(model);

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

  if (!props.rowData) return;

  if (props.operateType === 'addChild') {
    const { id } = props.rowData;
    Object.assign(model, { parentId: id });
  } else if (props.operateType === 'edit') {
    Object.assign(model, props.rowData);
  }

  if (!model.fieldSchema) {
    model.fieldSchema = [];
  }
}
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-800px">
    <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
      <NFormItem :label="$t('page.event.eventType.name')" path="name">
        <NInput v-model:value="model.name" :placeholder="$t('page.event.eventType.form.name')" />
      </NFormItem>
      <NFormItem :label="$t('page.event.eventType.level.title')" path="level">
        <NSelect
          v-model:value="model.level"
          :options="levelOptions"
          :placeholder="$t('page.event.eventType.form.level')"
        />
      </NFormItem>
      <NFormItem :label="$t('page.event.eventType.fieldSchema')" path="fieldSchema">
        <NDynamicInput
          v-model:value="model.fieldSchema!"
          preset="pair"
          show-sort-button
          :key-placeholder="$t('page.event.eventType.form.fieldPath')"
          :value-placeholder="$t('page.event.eventType.form.fieldName')"
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
