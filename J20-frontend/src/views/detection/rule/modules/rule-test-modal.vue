<script setup lang="tsx">
import { reactive, watch } from 'vue';
import CodeMirror from 'vue-codemirror6';
import { json } from '@codemirror/lang-json';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchTestRule } from '@/service/api';

defineOptions({
  name: 'RuleTestModal'
});

// 数据
interface Props {
  rowData?: Api.Detection.Rule | null;
}

const props = defineProps<Props>();

const visible = defineModel<boolean>('visible', {
  default: false
});

const { loading, startLoading, endLoading } = useLoading();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();

const jsonLang = json();

/** 测试数据 */
type Model = Pick<Api.Detection.Rule, 'id'> & {
  input: string;
  output: string;
};
const model: Model = reactive(createDefaultModel());
function createDefaultModel(): Model {
  return {
    id: '',
    input: '',
    output: ''
  };
}

/** 表单验证 */
type RuleKey = Extract<keyof Model, 'input'>;
const rules: Record<RuleKey, App.Global.FormRule> = {
  input: defaultRequiredRule
};

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

// 处理关闭
function closeModel() {
  visible.value = false;
}

// 处理提交
async function handleSubmit() {
  await validate();

  try {
    JSON.parse(model.input);
  } catch (error) {
    window.$message?.error($t('page.detection.rule.form.badJson'));
    return;
  }

  startLoading();
  const { error, data } = await fetchTestRule({
    id: model.id,
    input: JSON.parse(model.input)
  });

  if (!error) {
    model.output = JSON.stringify(data, null, 2);
  }

  endLoading();
}

// 处理初始化
function handleInitModel() {
  Object.assign(model, createDefaultModel());

  if (props.rowData) {
    const { id, protocol } = props.rowData;
    model.id = id;
    model.input = JSON.stringify(
      [
        {
          protocol
        }
      ],
      null,
      2
    );
  }
}
</script>

<template>
  <NModal
    v-model:show="visible"
    :title="$t('page.detection.rule.form.testRule')"
    preset="card"
    class="w-1200px"
    content-class="!pb-0"
  >
    <NForm ref="formRef" :model="model" :rules="rules">
      <NGrid responsive="screen" item-responsive x-gap="16" y-gap="16">
        <NFormItemGi span="24 s:12" :label="$t('page.detection.rule.test.input')" path="input">
          <CodeMirror v-model="model.input" :lang="jsonLang" basic class="h-500px flex-1 overflow-auto border-1" />
        </NFormItemGi>
        <NFormItemGi span="24 s:12" :label="$t('page.detection.rule.test.output')" path="output">
          <CodeMirror
            v-model="model.output"
            :lang="jsonLang"
            basic
            readonly
            class="h-500px flex-1 overflow-auto border-1"
          />
        </NFormItemGi>
      </NGrid>
    </NForm>

    <template #footer>
      <NSpace justify="end" :size="16">
        <NButton @click="closeModel">{{ $t('common.cancel') }}</NButton>
        <NButton type="primary" :loading="loading" @click="handleSubmit">{{ $t('common.test') }}</NButton>
      </NSpace>
    </template>
  </NModal>
</template>

<style scoped></style>
