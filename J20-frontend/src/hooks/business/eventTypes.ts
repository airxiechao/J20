import { computed, ref } from 'vue';
import type { TreeOption } from 'naive-ui';
import { fetchListEventType } from '@/service/api';

export function useEventTypes() {
  // 事件类型
  const eventTypes = ref<Api.Event.EventType[]>([]);

  async function getEventTypes() {
    const { data: respData } = await fetchListEventType();
    eventTypes.value = respData?.records ?? [];
  }

  function converEventTypesToTreeOptions(
    types: Api.Event.EventType[] | undefined,
    withNumEvent?: boolean
  ): TreeOption[] | undefined {
    if (!types || types.length === 0) {
      return undefined;
    }

    return types.map(t => {
      return {
        key: t.id,
        label: (t.name || '') + (withNumEvent ? ` (${t.numEvent || 0})` : ''),
        children: converEventTypesToTreeOptions(t.children, withNumEvent)
      };
    });
  }

  // 事件类型选项
  const eventTypeOptions = computed(() => {
    return converEventTypesToTreeOptions(eventTypes.value) || [];
  });

  // 事件类型带事件数量选项
  const eventTypeWithNumEventOptions = computed(() => {
    return converEventTypesToTreeOptions(eventTypes.value, true) || [];
  });

  // 事件字段选项
  function searchEventType(eventTypeId?: string | null, types?: Api.Event.EventType[]): Api.Event.EventType | null {
    if (!eventTypeId) {
      return null;
    }

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

  getEventTypes();

  return {
    eventTypes,
    eventTypeOptions,
    eventTypeWithNumEventOptions,
    searchEventType
  };
}
