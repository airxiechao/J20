import { request } from '../request';

// statistics

export function fetchCountEvent(data: Api.Detection.EventCountParam) {
  return request<number>({
    url: '/detection/event/statistics/count',
    method: 'post',
    data
  });
}

export function fetchSumEvent(data: Api.Detection.EventSumParam) {
  return request<number>({
    url: '/detection/event/statistics/sum',
    method: 'post',
    data
  });
}

export function fetchGroupByEvent(data: Api.Detection.EventGroupByParam) {
  return request<Api.Detection.EventGroupValue[]>({
    url: '/detection/event/statistics/groupBy',
    method: 'post',
    data
  });
}

export function fetchHistogramEvent(data: Api.Detection.EventHistogramParam) {
  return request<Api.Detection.EventHistogramValue[]>({
    url: '/detection/event/statistics/histogram',
    method: 'post',
    data
  });
}

// rule

export function fetchListRule(data: Api.Detection.RuleListParams) {
  return request<Api.Detection.RuleListResp>({
    url: '/detection/rule/list',
    method: 'post',
    data
  });
}

export function fetchAddRule(
  data: Pick<Api.Detection.Rule, 'name' | 'description' | 'protocol' | 'criteriaType' | 'criteria' | 'output'>
) {
  return request<Api.Detection.Rule>({
    url: '/detection/rule/add',
    method: 'post',
    data
  });
}

export function fetchUpdateRule(
  data: Pick<Api.Detection.Rule, 'id' | 'name' | 'description' | 'protocol' | 'criteriaType' | 'criteria' | 'output'>
) {
  return request({
    url: '/detection/rule/update',
    method: 'post',
    data
  });
}

export function fetchDeleteRule(data: Pick<Api.Detection.Rule, 'id'>) {
  return request({
    url: '/detection/rule/delete',
    method: 'post',
    data
  });
}

export function fetchTestRule(data: Pick<Api.Detection.Rule, 'id'> & { input: object }) {
  return request({
    url: '/detection/rule/test',
    method: 'post',
    data
  });
}

// task

export function fetchListTask(data: Api.Detection.TaskListParams) {
  return request<Api.Detection.TaskListResp>({
    url: '/detection/task/list',
    method: 'post',
    data
  });
}

export function fetchAddTask(
  data: Pick<Api.Detection.Task, 'name' | 'description' | 'srcDataSourceId' | 'startingOffsetStrategy' | 'rules'>
) {
  return request<Api.Detection.Task>({
    url: '/detection/task/add',
    method: 'post',
    data
  });
}

export function fetchUpdateTask(
  data: Pick<Api.Detection.Task, 'id' | 'name' | 'description' | 'srcDataSourceId' | 'startingOffsetStrategy' | 'rules'>
) {
  return request({
    url: '/detection/task/update',
    method: 'post',
    data
  });
}

export function fetchDeleteTask(data: Pick<Api.Detection.Task, 'id'>) {
  return request({
    url: '/detection/task/delete',
    method: 'post',
    data
  });
}

export function fetchStartTask(data: Pick<Api.Detection.Task, 'id'>) {
  return request({
    url: '/detection/task/start',
    method: 'post',
    data
  });
}

export function fetchStopTask(data: Pick<Api.Detection.Task, 'id'>) {
  return request({
    url: '/detection/task/stop',
    method: 'post',
    data
  });
}

// protocol

export function fetchListProtocol(data: Api.Detection.ProtocolListParams) {
  return request<Api.Detection.ProtocolListResp>({
    url: '/detection/protocol/list',
    method: 'post',
    data
  });
}

export function fetchAllProtocol(data?: Pick<Api.Detection.Protocol, 'code'>) {
  return request<Api.Detection.Protocol[]>({
    url: '/detection/protocol/all',
    method: 'post',
    data
  });
}

export function fetchAddProtocol(data: Pick<Api.Detection.Protocol, 'code' | 'fieldSchema'>) {
  return request<Api.Detection.Protocol>({
    url: '/detection/protocol/add',
    method: 'post',
    data
  });
}

export function fetchUpdateProtocol(data: Pick<Api.Detection.Protocol, 'code' | 'fieldSchema'>) {
  return request({
    url: '/detection/protocol/update',
    method: 'post',
    data
  });
}

export function fetchDeleteProtocol(data: Pick<Api.Detection.Protocol, 'code'>) {
  return request({
    url: '/detection/protocol/delete',
    method: 'post',
    data
  });
}

// datasource

export function fetchListDatasource(data: Api.Detection.DatasourceListParams) {
  return request<Api.Detection.DatasourceListResp>({
    url: '/detection/datasource/list',
    method: 'post',
    data
  });
}

export function fetchAllDatasource(data?: Pick<Api.Detection.Datasource, 'name'>) {
  return request<Api.Detection.Datasource[]>({
    url: '/detection/datasource/all',
    method: 'post',
    data
  });
}

export function fetchAddDatasource(data: Pick<Api.Detection.Datasource, 'name' | 'topic' | 'numPartition'>) {
  return request<Api.Detection.Datasource>({
    url: '/detection/datasource/add',
    method: 'post',
    data
  });
}

export function fetchUpdateDatasource(data: Pick<Api.Detection.Datasource, 'id' | 'name' | 'topic' | 'numPartition'>) {
  return request({
    url: '/detection/datasource/update',
    method: 'post',
    data
  });
}

export function fetchDeleteDatasource(data: Pick<Api.Detection.Datasource, 'id'>) {
  return request({
    url: '/detection/datasource/delete',
    method: 'post',
    data
  });
}
