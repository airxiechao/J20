import { request } from '../request';

// event type

export function fetchListEventType() {
  return request<Api.Event.EventTypeListResp>({
    url: '/detection/event/type/list',
    method: 'post'
  });
}

export function fetchAddEventType(data: Pick<Api.Event.EventType, 'parentId' | 'name' | 'level' | 'fieldSchema'>) {
  return request<Api.Event.EventType>({
    url: '/detection/event/type/add',
    method: 'post',
    data
  });
}

export function fetchUpdateEventType(data: Pick<Api.Event.EventType, 'id' | 'name' | 'level' | 'fieldSchema'>) {
  return request({
    url: '/detection/event/type/update',
    method: 'post',
    data
  });
}

export function fetchDeleteEventType(data: Pick<Api.Event.EventType, 'id'>) {
  return request({
    url: '/detection/event/type/delete',
    method: 'post',
    data
  });
}

// event

export function fetchListEvent(data: Api.Event.EventListParams) {
  return request<Api.Event.EventListResp>({
    url: '/detection/event/list',
    method: 'post',
    data
  });
}

export function fetchGetEvent(data: Api.Event.EventGetParams) {
  return request<Api.Event.Event>({
    url: '/detection/event/get',
    method: 'post',
    data
  });
}
