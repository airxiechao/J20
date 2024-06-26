const local: App.I18n.Schema = {
  system: {
    title: '鲲擎安全态势感知'
  },
  common: {
    action: '操作',
    view: '查看',
    save: '保存',
    add: '新增',
    test: '测试',
    start: '启动',
    stop: '停止',
    choosed: '已选',
    startSuccess: '启动成功',
    stopSuccess: '停止成功',
    addSuccess: '添加成功',
    backToHome: '返回首页',
    batchDelete: '批量删除',
    cancel: '取消',
    close: '关闭',
    check: '勾选',
    expandColumn: '展开列',
    columnSetting: '列设置',
    config: '配置',
    confirm: '确认',
    delete: '删除',
    deleteSuccess: '删除成功',
    confirmDelete: '确认删除吗？',
    edit: '编辑',
    index: '序号',
    keywordSearch: '请输入关键词搜索',
    logout: '退出登录',
    logoutConfirm: '确认退出登录吗？',
    lookForward: '敬请期待',
    modify: '修改',
    modifySuccess: '修改成功',
    noData: '无数据',
    operate: '操作',
    pleaseCheckValue: '请检查输入的值是否合法',
    refresh: '刷新',
    reset: '重置',
    search: '搜索',
    switch: '切换',
    tip: '提示',
    trigger: '触发',
    update: '更新',
    updateSuccess: '更新成功',
    userCenter: '个人中心',
    yesOrNo: {
      yes: '是',
      no: '否'
    }
  },
  request: {
    logout: '请求失败后登出用户',
    logoutMsg: '用户状态失效，请重新登录',
    logoutWithModal: '请求失败后弹出模态框再登出用户',
    logoutWithModalMsg: '用户状态失效，请重新登录',
    refreshToken: '请求的token已过期，刷新token',
    tokenExpired: 'token已过期'
  },
  theme: {
    themeSchema: {
      title: '主题模式',
      light: '亮色模式',
      dark: '暗黑模式',
      auto: '跟随系统'
    },
    grayscale: '灰度模式',
    layoutMode: {
      title: '布局模式',
      vertical: '左侧菜单模式',
      'vertical-mix': '左侧菜单混合模式',
      horizontal: '顶部菜单模式',
      'horizontal-mix': '顶部菜单混合模式'
    },
    recommendColor: '应用推荐算法的颜色',
    recommendColorDesc: '推荐颜色的算法参照',
    themeColor: {
      title: '主题颜色',
      primary: '主色',
      info: '信息色',
      success: '成功色',
      warning: '警告色',
      error: '错误色',
      followPrimary: '跟随主色'
    },
    scrollMode: {
      title: '滚动模式',
      wrapper: '外层滚动',
      content: '主体滚动'
    },
    page: {
      animate: '页面切换动画',
      mode: {
        title: '页面切换动画类型',
        'fade-slide': '滑动',
        fade: '淡入淡出',
        'fade-bottom': '底部消退',
        'fade-scale': '缩放消退',
        'zoom-fade': '渐变',
        'zoom-out': '闪现',
        none: '无'
      }
    },
    fixedHeaderAndTab: '固定头部和标签栏',
    header: {
      height: '头部高度',
      breadcrumb: {
        visible: '显示面包屑',
        showIcon: '显示面包屑图标'
      }
    },
    tab: {
      visible: '显示标签栏',
      cache: '缓存标签页',
      height: '标签栏高度',
      mode: {
        title: '标签栏风格',
        chrome: '谷歌风格',
        button: '按钮风格'
      }
    },
    sider: {
      inverted: '深色侧边栏',
      width: '侧边栏宽度',
      collapsedWidth: '侧边栏折叠宽度',
      mixWidth: '混合布局侧边栏宽度',
      mixCollapsedWidth: '混合布局侧边栏折叠宽度',
      mixChildMenuWidth: '混合布局子菜单宽度'
    },
    footer: {
      visible: '显示底部',
      fixed: '固定底部',
      height: '底部高度',
      right: '底部局右'
    },
    themeDrawerTitle: '主题配置',
    pageFunTitle: '页面功能',
    configOperation: {
      copyConfig: '复制配置',
      copySuccessMsg: '复制成功，请替换 src/theme/settings.ts 中的变量 themeSettings',
      resetConfig: '重置配置',
      resetSuccessMsg: '重置成功'
    }
  },
  route: {
    login: '登录',
    403: '无权限',
    404: '页面不存在',
    500: '服务器错误',
    'iframe-page': '外链页面',
    home: '首页',
    document: '文档',
    document_project: '项目文档',
    'document_project-link': '项目文档(外链)',
    document_vue: 'Vue文档',
    document_vite: 'Vite文档',
    document_unocss: 'UnoCSS文档',
    document_naive: 'Naive UI文档',
    document_antd: 'Ant Design Vue文档',
    'user-center': '个人中心',
    about: '关于',
    manage: '系统管理',
    exception: '异常页',
    exception_403: '403',
    exception_404: '404',
    exception_500: '500',
    event: '安全事件',
    'event_event-list': '事件列表',
    'detection_event-type': '事件类型',
    detection: '网络安全检测',
    detection_datasource: '日志数据源',
    detection_protocol: '日志协议',
    detection_rule: '策略规则',
    detection_task: '检测任务',
    event_attck: 'ATT&CK'
  },
  page: {
    login: {
      common: {
        loginOrRegister: '登录 / 注册',
        usernamePlaceholder: '请输入用户名',
        phonePlaceholder: '请输入手机号',
        codePlaceholder: '请输入验证码',
        passwordPlaceholder: '请输入密码',
        confirmPasswordPlaceholder: '请再次输入密码',
        codeLogin: '验证码登录',
        confirm: '确定',
        back: '返回',
        validateSuccess: '验证成功',
        loginSuccess: '登录成功',
        registerSuccess: '注册成功',
        welcomeBack: '欢迎回来，{username} ！'
      },
      pwdLogin: {
        title: '登录',
        rememberMe: '记住我',
        forgetPassword: '忘记密码？',
        register: '注册账号',
        noAccount: '没有账号？',
        otherAccountLogin: '其他账号登录',
        otherLoginMode: '其他登录方式',
        superAdmin: '超级管理员',
        admin: '管理员',
        user: '普通用户'
      },
      codeLogin: {
        title: '验证码登录',
        getCode: '获取验证码',
        reGetCode: '{time}秒后重新获取',
        sendCodeSuccess: '验证码发送成功',
        imageCodePlaceholder: '请输入图片验证码'
      },
      register: {
        title: '注册',
        agreement: '我已经仔细阅读并接受',
        protocol: '《用户协议》',
        policy: '《隐私权政策》'
      },
      resetPwd: {
        title: '重置密码'
      },
      bindWeChat: {
        title: '绑定微信'
      }
    },
    about: {
      title: '关于',
      introduction: `鲲擎-J20 是一个安全检测与态势感知系统。`,
      projectInfo: {
        title: '项目信息',
        version: '版本',
        latestBuildTime: '最新构建时间',
        githubLink: 'Github 地址',
        previewLink: '预览地址'
      },
      prdDep: '生产依赖',
      devDep: '开发依赖'
    },
    home: {
      projectCount: '项目数',
      todo: '待办',
      message: '消息',
      downloadCount: '下载量',
      registerCount: '注册量',
      schedule: '作息安排',
      study: '学习',
      work: '工作',
      rest: '休息',
      entertainment: '娱乐',
      visitCount: '访问量',
      turnover: '成交额',
      dealCount: '成交量',
      projectNews: {
        title: '项目动态',
        moreNews: '更多动态'
      },
      creativity: '创意'
    },
    function: {
      tab: {
        tabOperate: {
          title: '标签页操作',
          addTab: '添加标签页',
          addTabDesc: '跳转到关于页面',
          closeTab: '关闭标签页',
          closeCurrentTab: '关闭当前标签页',
          closeAboutTab: '关闭"关于"标签页',
          addMultiTab: '添加多标签页',
          addMultiTabDesc1: '跳转到多标签页页面',
          addMultiTabDesc2: '跳转到多标签页页面(带有查询参数)'
        },
        tabTitle: {
          title: '标签页标题',
          changeTitle: '修改标题',
          change: '修改',
          resetTitle: '重置标题',
          reset: '重置'
        }
      },
      multiTab: {
        routeParam: '路由参数',
        backTab: '返回 function_tab'
      },
      toggleAuth: {
        toggleAccount: '切换账号',
        authHook: '权限钩子函数 `hasAuth`',
        superAdminVisible: '超级管理员可见',
        adminVisible: '管理员可见',
        adminOrUserVisible: '管理员和用户可见'
      },
      request: {
        repeatedErrorOccurOnce: '重复请求错误只出现一次',
        repeatedError: '重复请求错误',
        repeatedErrorMsg1: '自定义请求错误 1',
        repeatedErrorMsg2: '自定义请求错误 2'
      }
    },
    manage: {
      common: {
        status: {
          enable: '启用',
          disable: '禁用'
        }
      },
      role: {
        title: '角色列表',
        roleName: '角色名称',
        roleCode: '角色编码',
        roleStatus: '角色状态',
        roleDesc: '角色描述',
        menuAuth: '菜单权限',
        buttonAuth: '按钮权限',
        form: {
          roleName: '请输入角色名称',
          roleCode: '请输入角色编码',
          roleStatus: '请选择角色状态',
          roleDesc: '请输入角色描述'
        },
        addRole: '新增角色',
        editRole: '编辑角色'
      },
      user: {
        title: '用户列表',
        username: '用户名',
        userGender: '性别',
        nickName: '昵称',
        userPhone: '手机号',
        userEmail: '邮箱',
        userStatus: '用户状态',
        userRole: '用户角色',
        form: {
          username: '请输入用户名',
          userGender: '请选择性别',
          nickName: '请输入昵称',
          userPhone: '请输入手机号',
          userEmail: '请输入邮箱',
          userStatus: '请选择用户状态',
          userRole: '请选择用户角色'
        },
        addUser: '新增用户',
        editUser: '编辑用户',
        gender: {
          male: '男',
          female: '女'
        }
      },
      menu: {
        home: '首页',
        title: '菜单列表',
        id: 'ID',
        parentId: '父级菜单ID',
        menuType: '菜单类型',
        menuName: '菜单名称',
        routeName: '路由名称',
        routePath: '路由路径',
        pathParam: '路径参数',
        layout: '布局',
        page: '页面组件',
        i18nKey: '国际化key',
        icon: '图标',
        localIcon: '本地图标',
        iconTypeTitle: '图标类型',
        order: '排序',
        constant: '常量路由',
        keepAlive: '缓存路由',
        href: '外链',
        hideInMenu: '隐藏菜单',
        activeMenu: '高亮的菜单',
        multiTab: '支持多页签',
        fixedIndexInTab: '固定在页签中的序号',
        query: '路由参数',
        button: '按钮',
        buttonCode: '按钮编码',
        buttonDesc: '按钮描述',
        menuStatus: '菜单状态',
        form: {
          home: '请选择首页',
          menuType: '请选择菜单类型',
          menuName: '请输入菜单名称',
          routeName: '请输入路由名称',
          routePath: '请输入路由路径',
          pathParam: '请输入路径参数',
          page: '请选择页面组件',
          layout: '请选择布局组件',
          i18nKey: '请输入国际化key',
          icon: '请输入图标',
          localIcon: '请选择本地图标',
          order: '请输入排序',
          keepAlive: '请选择是否缓存路由',
          href: '请输入外链',
          hideInMenu: '请选择是否隐藏菜单',
          activeMenu: '请选择高亮的菜单的路由名称',
          multiTab: '请选择是否支持多标签',
          fixedInTab: '请选择是否固定在页签中',
          fixedIndexInTab: '请输入固定在页签中的序号',
          queryKey: '请输入路由参数Key',
          queryValue: '请输入路由参数Value',
          button: '请选择是否按钮',
          buttonCode: '请输入按钮编码',
          buttonDesc: '请输入按钮描述',
          menuStatus: '请选择菜单状态'
        },
        addMenu: '新增菜单',
        editMenu: '编辑菜单',
        addChildMenu: '新增子菜单',
        type: {
          directory: '目录',
          menu: '菜单'
        },
        iconType: {
          iconify: 'iconify图标',
          local: '本地图标'
        }
      }
    },
    dashboard: {
      title: '仪表盘 - 今日',
      config: '配置图表',
      name: '名称',
      eventTypeId: '事件类型',
      aggregateType: {
        title: '聚合类型',
        COUNT: '计数',
        SUM: '求和',
        GROUP_BY: '分组',
        HISTOGRAM: '直方图'
      },
      groupByField: '分组字段',
      aggregateField: '聚合字段',
      chart1: '图表1',
      chart2: '图表2',
      chart3: '图表3',
      chart4: '图表4',
      chart5: '图表5',
      chart6: '图表6'
    },
    detection: {
      rule: {
        title: '规则',
        id: '规则ID',
        name: '规则名称',
        description: '规则描述',
        criteriaType: '条件类型',
        protocol: '日志协议',
        criteria: {
          title: '条件',
          filter: {
            title: '过滤条件',
            groupOperator: {
              AND: 'AND',
              OR: 'OR'
            },
            fieldOperator: {
              EQUALS: '=',
              NOT_EQUALS: '!=',
              GREATER_THAN: '>',
              GREATER_THAN_OR_EQUALS: '>=',
              LESS_THAN: '<',
              LESS_THAN_OR_EQUALS: '<=',
              EXISTS: '存在',
              CONTAINS: '包含',
              STARTS_WITH: '以...开始',
              ENDS_WITH: '以...结束',
              IN: '在...中',
              NOT_IN: '不在...中',
              REGEX_MATCH: '正则匹配',
              EXPRESS: '表达式'
            }
          },
          statistics: {
            title: '统计条件',
            enabled: '启用',
            disabled: '禁用',
            eager: '尽早触发',
            windowSize: '窗口大小',
            windowSlide: '窗口步长',
            groupBy: '分组字段',
            field: '统计字段',
            aggregate: '统计方式',
            windowUnit: {
              SECOND: '秒',
              MINUTE: '分钟',
              HOUR: '小时',
              DAY: '天'
            },
            windowAggregate: {
              SUM: '求和',
              COUNT: '计数',
              COUNT_UNIQUE: '唯一计数',
              MAX: '最大值',
              MIN: '最小值'
            },
            windowOperator: {
              EQUALS: '=',
              NOT_EQUALS: '!=',
              GREATER_THAN: '>',
              GREATER_THAN_OR_EQUALS: '>=',
              LESS_THAN: '<',
              LESS_THAN_OR_EQUALS: '<='
            }
          }
        },
        input: '输入',
        output: {
          title: '输出',
          eventTypeId: '事件类型',
          messageTempalte: '事件描述',
          propertyFields: '事件字段'
        },
        frequency: '频率',
        aggregation: '聚合',
        outputEventTypeId: '事件类型',
        outputEventTypeName: '事件类型',
        createTime: '创建时间',
        lastUpdateTime: '更新时间',
        test: {
          input: '输入日志',
          output: '输出事件'
        },
        form: {
          addRule: '新增规则',
          editRule: '编辑规则',
          testRule: '测试规则',
          name: '请输入规则名称',
          description: '请输入规则描述',
          protocol: '请选择日志协议',
          outputEventTypeId: '请选择输出事件类型',
          addCriteriaFileterNodeGroup: '新增组',
          addCriteriaFileterNodeField: '新增字段',
          criteriaFilterNodeField: '请选择日志字段',
          criteriaFilterNodeValue: '请输入值',
          windowUnit: '单位',
          windowSize: '大小',
          groupBy: '请选择分组字段',
          field: '请选择字段',
          value: '请输入值',
          operator: '操作符',
          aggregate: '聚合',
          eventTypeId: '请选择事件类型',
          eventMessageTemplate: '请输入事件描述',
          eventPropertyFiled: '请选择事件字段',
          eventPropertyValue: '请输入日志字段映射',
          badJson: '请输入正确的JSON格式'
        }
      },
      task: {
        title: '任务',
        id: '任务ID',
        name: '任务名称',
        description: '任务描述',
        srcDataSourceId: '输入数据源',
        startingOffsetStrategy: '起始位置',
        startingOffset: {
          EARLIEST: '最早位置',
          LATEST: '最新位置'
        },
        rules: '应用规则',
        jobStatus: {
          title: '运行状态',
          NOT_CREATED: '未创建',
          CREATING: '创建中',
          CREATED: '已创建',
          RUNNING: '运行中',
          FAILING: '失败中',
          FAILED: '已失败',
          CANCELLING: '取消中',
          CANCELED: '已取消',
          FINISHED: '已完成',
          RESTARTING: '重启中',
          SUSPENDING: '暂停中',
          SUSPENDED: '已暂停',
          RECONCILING: '协调中'
        },
        jobNumLogIn: '日志输入数',
        jobNumEventOut: '事件输出数',
        jobLastUpdateTime: '运行更新时间',
        createTime: '创建时间',
        lastUpdateTime: '更新时间',
        form: {
          addTask: '添加任务',
          editTask: '编辑任务',
          name: '请输入任务名称',
          description: '请输入任务描述',
          srcDataSourceId: '请选择输入数据源',
          startingOffsetStrategy: '起始位置'
        }
      },
      protocol: {
        title: '协议',
        code: '编码',
        fieldSchema: '日志格式',
        form: {
          addProtocol: '新增协议',
          editProtocol: '编辑协议',
          code: '请输入编码',
          fieldSchema: '请输入日志格式',
          fieldPath: '日志字段',
          fieldName: '显示名称'
        }
      },
      datasource: {
        title: 'Kafka 数据源',
        id: '数据源ID',
        name: '名称',
        bootstrapServers: '服务器地址',
        topic: '队列名',
        numPartition: '分区数',
        form: {
          addDatasource: '新增数据源',
          editDatasource: '编辑数据源',
          name: '请输入名称',
          bootstrapServers: '请输入服务器地址',
          topic: '请输入队列名',
          numPartition: '请输入分区数'
        }
      }
    },
    event: {
      eventList: {
        id: '事件ID',
        title: '事件列表',
        detail: '事件详情',
        eventTypeId: '事件类型',
        eventTypeName: '事件类型',
        level: '事件级别',
        query: '事件属性',
        message: '事件内容',
        taskName: '来源任务',
        ruleName: '匹配规则',
        originalLog: '来源日志',
        property: '事件属性',
        timestamp: '创建时间',
        beginTime: '开始时间',
        endTime: '结束时间',
        form: {
          beginTime: '请输入开始时间',
          endTime: '请输入结束时间',
          level: '请选择事件级别',
          eventTypeId: '请选择事件类型',
          query: '请输入 Lucene 查询语句（选中事件类型时生效）'
        }
      },
      eventType: {
        id: '类型ID',
        parentId: '上级类型ID',
        name: '类型名称',
        level: {
          title: '事件级别',
          NA: 'N/A',
          LOG: '记录',
          ALERT: '告警'
        },
        title: '事件类型',
        numEvent: '事件数量（近1个月）',
        form: {
          addType: '新增类型',
          addChildType: '新增子类型',
          editType: '编辑类型',
          name: '请输入类型名称',
          level: '请选择事件级别',
          fieldSchema: '请输入事件格式',
          fieldPath: '事件字段',
          fieldName: '显示名称'
        },
        fieldSchema: '事件格式'
      }
    },
    statistics: {
      eventCount: '事件数',
      attackCount: '攻击入侵',
      malwareCount: '恶意程序',
      spamCount: '垃圾信息',
      httpCount: 'HTTP请求（今日）',
      httpHistogram: 'HTTP请求',
      alertCount: '告警数',
      tcpCount: 'TCP连接（今日）',
      loginFailureCount: '登录失败（今日）',
      sshCount: 'SSH连接（今日）',
      news: {
        title: '告警动态',
        more: '更多动态',
        chart: '告警分布'
      }
    }
  },
  form: {
    required: '不能为空',
    username: {
      required: '请输入用户名',
      invalid: '用户名格式不正确'
    },
    phone: {
      required: '请输入手机号',
      invalid: '手机号格式不正确'
    },
    pwd: {
      required: '请输入密码',
      invalid: '密码格式不正确，8-16个字符，至少包含大写字母、小写字母、数字、特殊符号'
    },
    confirmPwd: {
      required: '请输入确认密码',
      invalid: '两次输入密码不一致'
    },
    code: {
      required: '请输入验证码',
      invalid: '验证码格式不正确'
    },
    email: {
      required: '请输入邮箱',
      invalid: '邮箱格式不正确'
    }
  },
  dropdown: {
    closeCurrent: '关闭',
    closeOther: '关闭其它',
    closeLeft: '关闭左侧',
    closeRight: '关闭右侧',
    closeAll: '关闭所有'
  },
  icon: {
    themeConfig: '主题配置',
    themeSchema: '主题模式',
    lang: '切换语言',
    fullscreen: '全屏',
    fullscreenExit: '退出全屏',
    reload: '刷新页面',
    collapse: '折叠菜单',
    expand: '展开菜单',
    pin: '固定',
    unpin: '取消固定'
  },
  datatable: {
    itemCount: '共 {total} 条'
  }
};

export default local;
