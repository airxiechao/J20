import type { TreeOption } from 'naive-ui';
import { computed, ref } from 'vue';
import { fetchListEventType } from '@/service/api';

/** 事件类型 */
export default function useEvenType(model: Pick<Api.Detection.Rule, 'output'>) {
  const eventTypes = ref<Api.Event.EventType[]>([]);

  async function getEventTypes() {
    const { data: respData } = await fetchListEventType();
    eventTypes.value = respData?.records ?? [];
  }

  function converEventTypesToTreeOptions(types: Api.Event.EventType[] | undefined): TreeOption[] | undefined {
    if (!types || types.length === 0) {
      return undefined;
    }

    return types.map(t => {
      return {
        key: t.id,
        label: t.name || '',
        children: converEventTypesToTreeOptions(t.children)
      };
    });
  }

  // 事件类型选项
  const eventTypeOptions = computed(() => {
    return converEventTypesToTreeOptions(eventTypes.value) || [];
  });

  // 事件字段选项
  const eventTypeFieldOptions = computed(() => {
    function searchEventType(eventTypeId: string, types?: Api.Event.EventType[]): Api.Event.EventType | null {
      if (!types || types.length === 0) {
        return null;
      }

      for (const t of types) {
        if (t.id === eventTypeId) {
          return t;
        }

        const foundType = searchEventType(eventTypeId, t.children);
        if (foundType) {
          return foundType;
        }
      }

      return null;
    }

    return (
      searchEventType(model.output.eventTypeId, eventTypes.value)?.fieldSchema?.map(f => {
        return {
          label: `${f.value} - ${f.key}`,
          value: f.key
        };
      }) ?? []
    );
  });

  getEventTypes();

  return {
    eventTypeOptions,
    eventTypeFieldOptions
  };
}
