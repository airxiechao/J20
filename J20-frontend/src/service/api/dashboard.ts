import { request } from '../request';

// chart

export function fetchListDashboardChart() {
  return request<Api.Dashbaord.Chart[]>({
    url: '/dashboard/chart/list',
    method: 'post'
  });
}

export function fetchUpdateDashboardChart(data: Api.Dashbaord.UpdateChartParam) {
  return request({
    url: '/dashboard/chart/update',
    method: 'post',
    data
  });
}
