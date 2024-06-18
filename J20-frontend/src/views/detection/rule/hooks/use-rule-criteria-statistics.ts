import { computed } from 'vue';
import { $t } from '@/locales';

/** 统计条件 */
export default function useRuleCriteriaStatistics() {
  // 窗口单位选项
  const windowUnitOptions = computed(() =>
    ['SECOND', 'MINUTE', 'HOUR', 'DAY'].map(t => {
      return {
        label: $t(`page.detection.rule.criteria.statistics.windowUnit.${t}`),
        value: t
      };
    })
  );

  // 窗口聚合函数选项
  const windowAggregateOptions = computed(() =>
    ['SUM', 'COUNT', 'COUNT_UNIQUE', 'MAX', 'MIN'].map(t => {
      return {
        label: $t(`page.detection.rule.criteria.statistics.windowAggregate.${t}`),
        value: t
      };
    })
  );

  // 窗口操作符选项
  const windowOperatorOptions = computed(() =>
    ['EQUALS', 'NOT_EQUALS', 'GREATER_THAN', 'GREATER_THAN_OR_EQUALS', 'LESS_THAN', 'LESS_THAN_OR_EQUALS'].map(t => {
      return {
        label: $t(`page.detection.rule.criteria.statistics.windowOperator.${t}`),
        value: t
      };
    })
  );

  return { windowUnitOptions, windowAggregateOptions, windowOperatorOptions };
}
