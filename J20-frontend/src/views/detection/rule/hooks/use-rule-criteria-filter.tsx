import { NButton, NInput, NSelect, NSpace, type TreeOption } from 'naive-ui';
import type { ComputedRef } from 'vue';
import { computed } from 'vue';
import { $t } from '@/locales';

/** 过滤条件 */
export default function useRuleCriteriaFileter(
  model: Pick<Api.Detection.Rule, 'criteria'>,
  protocolFieldOptions: ComputedRef<TreeOption[]>
) {
  // 过滤条件的显示对象
  const criteriaFilter = computed(() => {
    function converCriteriaFilterToTreeOptions(
      nodeType: string | null,
      nodes?: Api.Detection.RuleFilterNode[],
      parentKey?: string
    ): TreeOption[] | undefined {
      if (!nodes) {
        return nodeType === 'GROUP' ? [] : undefined;
      }

      return nodes.map((node, i) => {
        if (node.editing === undefined) {
          node.editing = false;
        }

        const currentKey = parentKey ? `${parentKey}-${i}` : `${i}`;
        return {
          key: currentKey,
          label:
            node.nodeType === 'GROUP'
              ? $t(`page.detection.rule.criteria.filter.groupOperator.${node.operator}`)
              : `${protocolFieldOptions.value.find(p => p.value === node.field)?.label ?? node.field ?? $t('common.noData')} ${$t(`page.detection.rule.criteria.filter.fieldOperator.${node.operator}`) ?? node.operator} ${node.value ?? $t('common.noData')}`,
          node,
          children: converCriteriaFilterToTreeOptions(node.nodeType, node.children, currentKey)
        };
      });
    }

    return converCriteriaFilterToTreeOptions(null, [model.criteria.filter]);
  });

  // 组操作符型选项
  const groupOoperatorOptions = computed(() =>
    ['AND', 'OR'].map(t => {
      return {
        label: $t(`page.detection.rule.criteria.filter.groupOperator.${t}`),
        value: t
      };
    })
  );

  // 字段操作符选项
  const fieldOperatorOptions = computed(() =>
    [
      'EQUALS',
      'NOT_EQUALS',
      'GREATER_THAN',
      'GREATER_THAN_OR_EQUALS',
      'LESS_THAN',
      'LESS_THAN_OR_EQUALS',
      'EXISTS',
      'CONTAINS',
      'STARTS_WITH',
      'ENDS_WITH',
      'IN',
      'NOT_IN',
      'REGEX_MATCH',
      'EXPRESS'
    ].map(t => {
      return {
        label: $t(`page.detection.rule.criteria.filter.fieldOperator.${t}`),
        value: t
      };
    })
  );

  // 绘制条件节点
  function renderCriteriaFilterNode({ option }: { option: TreeOption }) {
    const node: Api.Detection.RuleFilterNode = option.node as Api.Detection.RuleFilterNode;

    if (node.editing) {
      if (node.nodeType === 'GROUP') {
        return (
          <NSelect
            class="w-80px"
            size="small"
            value={node.operator}
            onUpdate:value={value => (node.operator = value)}
            options={groupOoperatorOptions.value}
          />
        );
      }

      return (
        <NSpace>
          <NSelect
            class="w-150px"
            size="small"
            placeholder={$t('page.detection.rule.form.criteriaFilterNodeField')}
            value={node.field}
            onUpdate:value={value => (node.field = value)}
            options={protocolFieldOptions.value}
          />
          <NSelect
            class="w-100px"
            size="small"
            value={node.operator}
            onUpdate:value={value => (node.operator = value)}
            options={fieldOperatorOptions.value}
          />
          <NInput
            size="small"
            placeholder={$t('page.detection.rule.form.criteriaFilterNodeValue')}
            defaultValue={node.value}
            onChange={value => {
              node.value = value;
            }}
          />
        </NSpace>
      );
    }

    return `${option.label}`;
  }

  // 绘制条件节点操作
  function renderCriteriaFilterNodeSuffix({ option }: { option: TreeOption }) {
    const node: Api.Detection.RuleFilterNode = option.node as Api.Detection.RuleFilterNode;

    if (node.editing) {
      return (
        <NButton text type="success" onClick={() => (node.editing = false)}>
          {$t('common.confirm')}
        </NButton>
      );
    }

    return (
      <NSpace>
        <NButton text type="primary" onClick={() => (node.editing = true)}>
          {$t('common.edit')}
        </NButton>
        {node.nodeType === 'GROUP' ? (
          <NSpace>
            <NButton text type="primary" onClick={() => addCriteriaFilterNode(option.key as string, 'GROUP')}>
              {$t('page.detection.rule.form.addCriteriaFileterNodeGroup')}
            </NButton>
            <NButton text type="primary" onClick={() => addCriteriaFilterNode(option.key as string, 'FIELD')}>
              {$t('page.detection.rule.form.addCriteriaFileterNodeField')}
            </NButton>
          </NSpace>
        ) : undefined}
        {(option.key as string).split('-').length > 1 ? (
          <NButton text type="warning" onClick={() => deleteCriteriaFilterNode(option.key as string)}>
            {$t('common.delete')}
          </NButton>
        ) : undefined}
      </NSpace>
    );
  }

  // 删除条件节点
  function deleteCriteriaFilterNode(key: string) {
    const tokens = key.split('-');
    if (tokens.length <= 1) {
      return;
    }

    let node: Api.Detection.RuleFilterNode | undefined = model.criteria.filter;

    for (let t = 1; t < tokens.length; t += 1) {
      const i = Number.parseInt(tokens[t], 10);

      if (t < tokens.length - 1) {
        node = node.children?.[i];
        if (!node) {
          return;
        }
      } else {
        node.children?.splice(i, 1);
      }
    }
  }

  // 添加条件节点
  function addCriteriaFilterNode(key: string, nodeType: 'GROUP' | 'FIELD') {
    let newNode: Api.Detection.RuleFilterNode;
    if (nodeType === 'GROUP') {
      newNode = {
        nodeType: 'GROUP',
        operator: 'AND',
        editing: true,
        children: []
      };
    } else {
      newNode = {
        nodeType: 'FIELD',
        operator: 'EQUALS',
        editing: true
      };
    }

    const tokens = key.split('-');
    let node: Api.Detection.RuleFilterNode | undefined = model.criteria.filter;
    for (let t = 1; t < tokens.length; t += 1) {
      const i = Number.parseInt(tokens[t], 10);
      node = node.children?.[i];
      if (!node) {
        return;
      }
    }

    const newChildren = node.children || [];
    newChildren.push(newNode);
    node.children = newChildren;
  }

  return {
    criteriaFilter,
    renderCriteriaFilterNode,
    renderCriteriaFilterNodeSuffix
  };
}
