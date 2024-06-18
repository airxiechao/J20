import { computed, ref } from 'vue';
import { fetchAllProtocol } from '@/service/api';

/** 协议 */
export default function useProtocol(model: Pick<Api.Detection.Rule, 'protocol'>) {
  const protocols = ref<Api.Detection.Protocol[]>([]);

  async function getProtocols() {
    const { data: respData } = await fetchAllProtocol();
    protocols.value = respData ?? [];
  }

  // 协议选项
  const protocolOptions = computed(() => {
    return protocols.value.map(p => {
      return {
        label: p.code,
        value: p.code
      };
    });
  });

  // 协议字段选项
  const protocolFieldOptions = computed(() => {
    return (
      protocols.value
        .find(p => p.code === model.protocol)
        ?.fieldSchema?.map(f => {
          return {
            label: `${f.value} - ${f.key}`,
            value: f.key
          };
        }) ?? []
    );
  });

  getProtocols();

  return {
    protocols,
    protocolOptions,
    protocolFieldOptions
  };
}
