/**
 * Namespace Api
 *
 * All backend api type
 */
declare namespace Api {
  namespace Common {
    /** common params of paginating */
    interface PaginatingCommonParams {
      /** current page number */
      current: number;
      /** page size */
      size: number;
      /** total count */
      total: number;
    }

    /** common params of paginating query list data */
    interface PaginatingQueryRecord<T = any> extends PaginatingCommonParams {
      records: T[];
    }

    /**
     * enable status
     *
     * - "1": enabled
     * - "2": disabled
     */
    type EnableStatus = '1' | '2';

    /** common record */
    type CommonRecord<T = any> = {
      /** record id */
      id: string;
      /** record creator */
      createBy: string;
      /** record create time */
      createTime: string;
      /** record updater */
      updateBy: string;
      /** record update time */
      updateTime: string;
      /** record status */
      status?: EnableStatus | null;
    } & T;

    // schema

    type SchemaField = {
      key: string;
      value: string;
    };

    type SchemaFieldValue = {
      field: string;
      name: string;
      value: any;
    };
  }

  /**
   * namespace Auth
   *
   * backend api module: "auth"
   */
  namespace Auth {
    interface LoginToken {
      token: string;
      refreshToken: string;
    }

    interface UserInfo {
      userId: string;
      username: string;
      roles: string[];
      buttons: string[];
    }
  }

  /**
   * namespace Route
   *
   * backend api module: "route"
   */
  namespace Route {
    type ElegantConstRoute = import('@elegant-router/types').ElegantConstRoute;

    interface MenuRoute extends ElegantConstRoute {
      id: string;
    }

    interface UserRoute {
      routes: MenuRoute[];
      home: import('@elegant-router/types').LastLevelRouteKey;
    }
  }

  /**
   * namespace SystemManage
   *
   * backend api module: "systemManage"
   */
  namespace SystemManage {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    /** role */
    type Role = Common.CommonRecord<{
      /** role name */
      roleName: string;
      /** role code */
      roleCode: string;
      /** role description */
      roleDesc: string;
    }>;

    /** role search params */
    type RoleSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.Role, 'roleName' | 'roleCode' | 'status'> & CommonSearchParams
    >;

    /** role list */
    type RoleList = Common.PaginatingQueryRecord<Role>;

    /** all role */
    type AllRole = Pick<Role, 'id' | 'roleName' | 'roleCode'>;

    /**
     * user gender
     *
     * - "1": "male"
     * - "2": "female"
     */
    type UserGender = '1' | '2';

    /** user */
    type User = Common.CommonRecord<{
      /** user name */
      username: string;
      /** user gender */
      userGender: UserGender | null;
      /** user nick name */
      nickName: string;
      /** user phone */
      userPhone: string;
      /** user email */
      userEmail: string;
      /** user role code collection */
      userRoles: string[];
    }>;

    /** user search params */
    type UserSearchParams = CommonType.RecordNullable<
      Pick<Api.SystemManage.User, 'username' | 'userGender' | 'nickName' | 'userPhone' | 'userEmail' | 'status'> &
        CommonSearchParams
    >;

    /** user list */
    type UserList = Common.PaginatingQueryRecord<User>;

    /**
     * menu type
     *
     * - "1": directory
     * - "2": menu
     */
    type MenuType = '1' | '2';

    type MenuButton = {
      /**
       * button code
       *
       * it can be used to control the button permission
       */
      code: string;
      /** button description */
      desc: string;
    };

    /**
     * icon type
     *
     * - "1": iconify icon
     * - "2": local icon
     */
    type IconType = '1' | '2';

    type MenuPropsOfRoute = Pick<
      import('vue-router').RouteMeta,
      | 'i18nKey'
      | 'keepAlive'
      | 'constant'
      | 'order'
      | 'href'
      | 'hideInMenu'
      | 'activeMenu'
      | 'multiTab'
      | 'fixedIndexInTab'
      | 'query'
    >;

    type Menu = Common.CommonRecord<{
      /** parent menu id */
      parentId: number;
      /** menu type */
      menuType: MenuType;
      /** menu name */
      menuName: string;
      /** route name */
      routeName: string;
      /** route path */
      routePath: string;
      /** component */
      component?: string;
      /** iconify icon name or local icon name */
      icon: string;
      /** icon type */
      iconType: IconType;
      /** buttons */
      buttons?: MenuButton[] | null;
      /** children menu */
      children?: Menu[] | null;
    }> &
      MenuPropsOfRoute;

    /** menu list */
    type MenuList = Common.PaginatingQueryRecord<Menu>;

    type MenuTree = {
      id: number;
      label: string;
      pId: number;
      children?: MenuTree[];
    };
  }

  namespace Dashbaord {
    // chart
    type Chart = {
      name: string;
      eventTypeId: string;
      aggregateType: string;
      groupByField: string;
      aggregateField: string;
    };

    type UpdateChartParam = {
      charts: CommonType.RecordNullable<Chart>[];
    };
  }

  namespace Event {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    // event type

    type EventType = Common.CommonRecord<{
      parentId: string | null;
      name: string;
      level: string;
      fieldSchema: Common.SchemaField[];
      numEvent?: number;
      children?: EventType[];
    }>;

    type EventTypeListResp = Common.PaginatingQueryRecord<EventType>;

    // event

    type Event = Common.CommonRecord<{
      eventTypeId: string;
      eventTypeName: string;
      level: string;
      message: string;
      taskName: string;
      ruleName: string;
      originalLog: object;
      property: Record<string, Common.SchemaFieldValue>;
      timestamp: number;
    }>;

    type EventListParams = CommonType.RecordNullable<
      Pick<Api.Event.Event, 'eventTypeId'> & {
        beginTime?: string;
        endTime?: string;
        level?: string;
        query?: string;
      } & CommonSearchParams
    >;

    type EventListResp = Common.PaginatingQueryRecord<Event>;

    type EventGetParams = Pick<Api.Event.Event, 'id' | 'timestamp'>;
  }

  namespace Detection {
    type CommonSearchParams = Pick<Common.PaginatingCommonParams, 'current' | 'size'>;

    // statistics

    type EventCountParam = {
      beginTime?: string;
      endTime?: string;
      level?: string;
      eventTypeId?: string;
    };

    type EventSumParam = {
      beginTime?: string;
      endTime?: string;
      level?: string;
      eventTypeId: string;
      sumField: string;
    };

    type EventGroupByParam = {
      beginTime?: string;
      endTime?: string;
      level?: string;
      eventTypeId?: string;
      groupByField: string;
      sumField?: string;
    };

    type EventHistogramParam = {
      beginTime?: string;
      endTime?: string;
      level?: string;
      eventTypeId?: string;
      interval: string;
      sumField?: string;
    };

    type EventGroupValue = {
      key: string;
      value: number;
    };

    type EventHistogramValue = {
      key: number;
      value: number;
    };

    // rule

    type RuleFilterNode = {
      nodeType: string;
      operator?: string;
      field?: string;
      value?: string;
      children?: RuleFilterNode[];
      editing?: boolean;
    };

    type RuleSlideWindow = {
      size: number;
      sizeUnit: string;
      slide: number;
      slideUnit: string;
    };

    type RuleStatistics = {
      enabled: boolean;
      window: RuleSlideWindow;
      field: string | null;
      aggregate: string | null;
      operator: string | null;
      value: number;
      groupBy: string[];
      eager: boolean;
    };

    type RuleCriteriaCondition = {
      filter: RuleFilterNode;
      statistics: RuleStatistics;
    };

    type RuleOutput = {
      level: string;
      messageTemplate: string;
      eventTypeId: string;
      eventTypeName: string;
      idTemplate: string;
      propertyFields: Common.SchemaField[];
    };

    type RuleFrequency = {
      enabled: boolean;
      groupBy: string[];
      time: number;
      unit: string;
      threshhold: number;
    };

    type RuleAggregation = {
      enabled: boolean;
      groupBy: string[];
      time: number;
      unit: string;
      emitWhich: number;
    };

    type Rule = Common.CommonRecord<{
      name: string;
      description: string;
      protocol: string;
      criteriaType: string;
      criteria: RuleCriteriaCondition;
      output: RuleOutput;
      frequncy?: RuleFrequency;
      aggregation?: RuleAggregation;
      createTime: string;
      lastUpdateTime: string;
      outputEventTypeId: string;
      outputEventTypeName: string;
    }>;

    type RuleListResp = Common.PaginatingQueryRecord<Rule>;

    type RuleListParams = CommonType.RecordNullable<
      Pick<Api.Detection.Rule, 'name' | 'criteriaType' | 'outputEventTypeId'> & CommonSearchParams
    >;

    // task

    type Task = Common.CommonRecord<{
      name: string;
      description: string;
      srcDataSourceId: string;
      startingOffsetStrategy: string;
      rules: Rule[];
      jobId?: string;
      jobStatus?: string;
      jobNumLogIn?: number;
      jobNumEventOut?: number;
      jobLastUpdateTime?: string;
      createTime: string;
      lastUpdateTime: string;
    }>;

    type TaskListResp = Common.PaginatingQueryRecord<Task>;

    type TaskListParams = CommonType.RecordNullable<Pick<Api.Detection.Task, 'name'> & CommonSearchParams>;

    // protocol

    type Protocol = Common.CommonRecord<{
      code: string;
      fieldSchema: Common.SchemaField[] | null;
    }>;

    type ProtocolListResp = Common.PaginatingQueryRecord<Protocol>;

    type ProtocolListParams = CommonType.RecordNullable<Pick<Api.Detection.Protocol, 'code'> & CommonSearchParams>;

    type ProtocolGetParams = Pick<Api.Detection.Protocol, 'code'>;

    // datasource

    type Datasource = Common.CommonRecord<{
      name: string;
      bootstrapServers: string;
      topic: string;
      numPartition: number;
    }>;

    type DatasourceListParams = CommonType.RecordNullable<Pick<Api.Detection.Datasource, 'name'> & CommonSearchParams>;

    type DatasourceListResp = Common.PaginatingQueryRecord<Datasource>;

    type DatasourceGetParams = Pick<Api.Detection.Datasource, 'id'>;
  }
}
