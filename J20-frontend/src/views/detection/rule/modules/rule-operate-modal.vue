<script setup lang="tsx">
import { computed, reactive, ref, watch } from 'vue';
import _ from 'lodash';
import { useLoading } from '@sa/hooks';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAddRule, fetchUpdateRule } from '@/service/api';
import useProtocol from '../hooks/use-protocol';
import useRuleCriteriaFileter from '../hooks/use-rule-criteria-filter';
import useRuleCriteriaStatistics from '../hooks/use-rule-criteria-statistics';
import useEventType from '../hooks/use-event-type';

defineOptions({
  name: 'RuleOperateModal'
});

// 数据
export type OperateType = NaiveUI.TableOperateType;

interface Props {
  operateType: OperateType;
  rowData?: Api.Detection.Rule | null;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'submitted'): void;
}
const emit = defineEmits<Emits>();

const { loading, startLoading, endLoading } = useLoading();
const { formRef, validate, restoreValidation } = useNaiveForm();
const { defaultRequiredRule } = useFormRules();
const propertyFieldRefs = ref<Record<string, any | null>>({});
const propertyFieldValueRefs = ref<Record<string, any | null>>({});

const title = computed(() => {
  const titles: Record<OperateType, string> = {
    add: $t('page.detection.rule.form.addRule'),
    edit: $t('page.detection.rule.form.editRule')
  };
  return titles[props.operateType];
});

const visible = defineModel<boolean>('visible', {
  default: false
});

function closeModal() {
  visible.value = false;
}

/** 规则 */
type Model = Pick<
  Api.Detection.Rule,
  'id' | 'name' | 'description' | 'protocol' | 'criteriaType' | 'criteria' | 'output'
>;
const model: Model = reactive(createDefaultModel());
function createDefaultModel(): Model {
  return {
    id: '',
    name: '',
    description: '',
    protocol: '',
    criteriaType: 'CONDITION',
    criteria: {
      filter: {
        nodeType: 'GROUP',
        operator: 'AND',
        children: []
      },
      statistics: {
        enabled: false,
        window: {
          size: 1,
          sizeUnit: 'MINUTE',
          slide: 1,
          slideUnit: 'MINUTE'
        },
        field: null,
        aggregate: null,
        operator: null,
        value: 0,
        groupBy: [],
        eager: false
      }
    },
    output: {
      level: '',
      messageTemplate: '',
      eventTypeId: '',
      eventTypeName: '',
      idTemplate: '',
      propertyFields: []
    }
  };
}

/** 协议 */
const { protocolOptions, protocolFieldOptions } = useProtocol(model);

/** 过滤条件 */
const { criteriaFilter, renderCriteriaFilterNode, renderCriteriaFilterNodeSuffix } = useRuleCriteriaFileter(
  model,
  protocolFieldOptions
);

/** 统计条件 */
const { windowUnitOptions, windowAggregateOptions, windowOperatorOptions } = useRuleCriteriaStatistics();

/** 事件类型 */
const { eventTypeOptions, eventTypeFieldOptions } = useEventType(model);

/** 表单验证 */
type RuleKey = Extract<keyof Model, 'name' | 'protocol'> & {
  'output.eventTypeId': string;
  'output.messageTemplate': string;
};
const rules: Record<RuleKey, App.Global.FormRule> = {
  name: defaultRequiredRule,
  protocol: defaultRequiredRule,
  'output.eventTypeId': defaultRequiredRule,
  'output.messageTemplate': defaultRequiredRule
};

watch(
  () => model.output.eventTypeId,
  () => {
    if (props.operateType === 'add') {
      model.output.propertyFields = eventTypeFieldOptions.value.map(f => ({
        key: f.value,
        value: ''
      }));
    }
  }
);

watch(visible, () => {
  if (visible.value) {
    handleInitModel();
    restoreValidation();
  }
});

// 处理提交
async function handleSubmit() {
  await validate();

  startLoading();
  if (props.operateType === 'add') {
    const { error } = await fetchAddRule(model);

    if (!error) {
      window.$message?.success($t('common.addSuccess'));
      closeModal();
      emit('submitted');
    }
  } else if (props.operateType === 'edit') {
    const { error } = await fetchUpdateRule(model);

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

  if (props.operateType === 'edit') {
    Object.assign(model, _.cloneDeep(props.rowData));
  }

  if (!model.criteriaType) {
    model.criteriaType = 'CONDITION';
  }
}
</script>

<template>
  <NModal v-model:show="visible" :title="title" preset="card" class="w-800px">
    <NForm ref="formRef" :model="model" :rules="rules" label-placement="left" :label-width="100">
      <NFormItem :label="$t('page.detection.rule.name')" path="name">
        <NInput v-model:value="model.name" :placeholder="$t('page.detection.rule.form.name')" />
      </NFormItem>
      <NFormItem :label="$t('page.detection.rule.description')" path="description">
        <NInput
          v-model:value="model.description"
          :placeholder="$t('page.detection.rule.form.description')"
          type="textarea"
        />
      </NFormItem>
      <NDivider title-placement="left" class="text-12px text-#666 !mt-0">
        {{ $t('page.detection.rule.input') }}
      </NDivider>
      <NFormItem :label="$t('page.detection.rule.protocol')" path="protocol">
        <NSelect
          v-model:value="model.protocol"
          :options="protocolOptions"
          clearable
          :placeholder="$t('page.detection.rule.form.protocol')"
        />
      </NFormItem>
      <NFormItem :label="$t('page.detection.rule.criteria.filter.title')" path="criteria.filter">
        <NTree
          :data="criteriaFilter"
          :render-label="renderCriteriaFilterNode"
          :render-suffix="renderCriteriaFilterNodeSuffix"
          table="false"
          block-line
          class="flex-1"
          default-expand-all
        />
      </NFormItem>
      <NFormItem :label="$t('page.detection.rule.criteria.statistics.title')" path="criteria.statistics">
        <NSpace vertical>
          <NSwitch v-model:value="model.criteria.statistics.enabled" size="small">
            <template #checked>{{ $t('page.detection.rule.criteria.statistics.enabled') }}</template>
            <template #unchecked>{{ $t('page.detection.rule.criteria.statistics.disabled') }}</template>
          </NSwitch>
          <NInputGroup>
            <NInputGroupLabel size="small">
              {{ $t('page.detection.rule.criteria.statistics.windowSize') }}
            </NInputGroupLabel>
            <NInputNumber
              v-model:value="model.criteria.statistics.window.size"
              size="small"
              min="1"
              :placeholder="$t('page.detection.rule.form.windowSize')"
              class="min-w-100px"
            />
            <NSelect
              v-model:value="model.criteria.statistics.window.sizeUnit"
              size="small"
              :options="windowUnitOptions"
              :placeholder="$t('page.detection.rule.form.windowUnit')"
            />
            <NInputGroupLabel size="small">
              {{ $t('page.detection.rule.criteria.statistics.windowSlide') }}
            </NInputGroupLabel>
            <NInputNumber
              v-model:value="model.criteria.statistics.window.slide"
              size="small"
              :placeholder="$t('page.detection.rule.form.windowSize')"
              class="min-w-100px"
            />
            <NSelect
              v-model:value="model.criteria.statistics.window.slideUnit"
              size="small"
              :options="windowUnitOptions"
              :placeholder="$t('page.detection.rule.form.windowUnit')"
            />
          </NInputGroup>
          <NInputGroup>
            <NInputGroupLabel size="small">
              {{ $t('page.detection.rule.criteria.statistics.groupBy') }}
            </NInputGroupLabel>
            <NSelect
              v-model:value="model.criteria.statistics.groupBy"
              size="small"
              multiple
              :options="protocolFieldOptions"
              :placeholder="$t('page.detection.rule.form.groupBy')"
            />
            <NInputGroupLabel size="small">{{ $t('page.detection.rule.criteria.statistics.field') }}</NInputGroupLabel>
            <NSelect
              v-model:value="model.criteria.statistics.field"
              size="small"
              :options="protocolFieldOptions"
              :placeholder="$t('page.detection.rule.form.field')"
            />
          </NInputGroup>
          <NInputGroup>
            <NInputGroupLabel size="small">
              {{ $t('page.detection.rule.criteria.statistics.aggregate') }}
            </NInputGroupLabel>
            <NSelect
              v-model:value="model.criteria.statistics.aggregate"
              size="small"
              :options="windowAggregateOptions"
              :placeholder="$t('page.detection.rule.form.aggregate')"
            />
            <NSelect
              v-model:value="model.criteria.statistics.operator"
              size="small"
              :options="windowOperatorOptions"
              :placeholder="$t('page.detection.rule.form.operator')"
            />
            <NInputNumber
              v-model:value="model.criteria.statistics.value"
              size="small"
              :placeholder="$t('page.detection.rule.form.value')"
              class="min-w-248px"
            />
          </NInputGroup>
          <NCheckbox v-model:checked="model.criteria.statistics.eager">
            {{ $t('page.detection.rule.criteria.statistics.eager') }}
          </NCheckbox>
        </NSpace>
      </NFormItem>
      <NDivider title-placement="left" class="text-12px text-#666 !mt-0">
        {{ $t('page.detection.rule.output.title') }}
      </NDivider>
      <NFormItem :label="$t('page.detection.rule.output.eventTypeId')" path="output.eventTypeId">
        <NTreeSelect
          v-model:value="model.output.eventTypeId"
          :options="eventTypeOptions"
          :placeholder="$t('page.detection.rule.form.eventTypeId')"
          clearable
          class="w-240px"
        />
      </NFormItem>
      <NFormItem :label="$t('page.detection.rule.output.messageTempalte')" path="output.messageTemplate">
        <NInput
          v-model:value="model.output.messageTemplate"
          :placeholder="$t('page.detection.rule.form.eventMessageTemplate')"
          type="textarea"
        />
      </NFormItem>
      <NFormItem :label="$t('page.detection.rule.output.propertyFields')" path="output.propertyFields">
        <NDynamicInput
          v-model:value="model.output.propertyFields"
          show-sort-button
          :on-create="() => ({ key: null, value: null })"
        >
          <template #default="{ value }">
            <NSpace class="w-100%" item-class="flex-1">
              <NSelect
                :ref="el => (propertyFieldRefs[value.key] = el)"
                v-model:value="value.key"
                :options="eventTypeFieldOptions"
                clearable
                :placeholder="$t('page.detection.rule.form.eventPropertyFiled')"
              />
              <NSelect
                :ref="el => (propertyFieldValueRefs[value.key] = el)"
                v-model:value="value.value"
                :options="protocolFieldOptions"
                clearable
                :placeholder="$t('page.detection.rule.form.eventPropertyValue')"
                class="max-w-250px"
              >
                <template #action>
                  <NInput
                    v-model:value="value.value"
                    type="text"
                    size="small"
                    @keyup="
                      e => {
                        if (e.key === 'Enter') {
                          propertyFieldRefs[value.key]?.focus();
                          propertyFieldValueRefs[value.key]?.focus();
                        }
                      }
                    "
                  />
                </template>
              </NSelect>
            </NSpace>
          </template>
        </NDynamicInput>
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
