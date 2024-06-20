<script setup lang="ts">
import { ref } from 'vue';
import dayjs from 'dayjs';
import { $t } from '@/locales';
import { fetchListEvent } from '@/service/api';
import { useRouterPush } from '@/hooks/common/router';

const { routerPushByKey } = useRouterPush();

defineOptions({
  name: 'News'
});

// 数据

interface NewsItem {
  id: string;
  content: string;
  time: string;
}

const newses = ref<NewsItem[]>([]);
const loading = ref(false);

// 处理初始化
async function init() {
  loading.value = true;

  const { error, data } = await fetchListEvent({
    current: 1,
    size: 5,
    level: 'ALERT'
  });

  if (!error) {
    newses.value = data.records.map(e => ({
      id: e.id,
      content: e.message,
      time: dayjs(e.timestamp).format('YYYY-MM-DD HH:mm:ss')
    }));
  }

  loading.value = false;
}

init();
</script>

<template>
  <NCard :title="$t('page.statistics.news.title')" :bordered="false" size="small" segmented class="card-wrapper">
    <template #header-extra>
      <NButton text type="primary" @click="routerPushByKey('event_event-list')">
        {{ $t('page.statistics.news.more') }}
      </NButton>
    </template>
    <div v-if="loading" class="h-380px flex place-content-center">
      <NSpin :stroke-width="35" size="small">
        <template #description>loading</template>
      </NSpin>
    </div>
    <NList v-else-if="newses.length > 0">
      <NListItem v-for="item in newses" :key="item.id">
        <template #prefix>
          <icon-material-symbols:chat-info-rounded class="font-size-35px color-gray" />
        </template>
        <NThing :title="item.content" :description="item.time" />
      </NListItem>
    </NList>
    <NEmpty v-else class="h-380px place-content-center" />
  </NCard>
</template>

<style scoped></style>
