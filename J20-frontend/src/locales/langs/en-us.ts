const local: App.I18n.Schema = {
  system: {
    title: 'Security Awareness'
  },
  common: {
    action: 'Action',
    view: 'View',
    save: 'Save',
    add: 'Add',
    test: 'Test',
    start: 'Start',
    stop: 'Stop',
    choosed: 'Choosed',
    startSuccess: 'Start Success',
    stopSuccess: 'Stop Success',
    addSuccess: 'Add Success',
    backToHome: 'Back to home',
    batchDelete: 'Batch Delete',
    cancel: 'Cancel',
    close: 'Close',
    check: 'Check',
    expandColumn: 'Expand Column',
    columnSetting: 'Column Setting',
    config: 'Config',
    confirm: 'Confirm',
    delete: 'Delete',
    deleteSuccess: 'Delete Success',
    confirmDelete: 'Are you sure you want to delete?',
    edit: 'Edit',
    index: 'Index',
    keywordSearch: 'Please enter keyword',
    logout: 'Logout',
    logoutConfirm: 'Are you sure you want to log out?',
    lookForward: 'Coming soon',
    modify: 'Modify',
    modifySuccess: 'Modify Success',
    noData: 'No Data',
    operate: 'Operate',
    pleaseCheckValue: 'Please check whether the value is valid',
    refresh: 'Refresh',
    reset: 'Reset',
    search: 'Search',
    switch: 'Switch',
    tip: 'Tip',
    trigger: 'Trigger',
    update: 'Update',
    updateSuccess: 'Update Success',
    userCenter: 'User Center',
    yesOrNo: {
      yes: 'Yes',
      no: 'No'
    }
  },
  request: {
    logout: 'Logout user after request failed',
    logoutMsg: 'User status is invalid, please log in again',
    logoutWithModal: 'Pop up modal after request failed and then log out user',
    logoutWithModalMsg: 'User status is invalid, please log in again',
    refreshToken: 'The requested token has expired, refresh the token',
    tokenExpired: 'The requested token has expired'
  },
  theme: {
    themeSchema: {
      title: 'Theme Schema',
      light: 'Light',
      dark: 'Dark',
      auto: 'Follow System'
    },
    grayscale: 'Grayscale',
    layoutMode: {
      title: 'Layout Mode',
      vertical: 'Vertical Menu Mode',
      horizontal: 'Horizontal Menu Mode',
      'vertical-mix': 'Vertical Mix Menu Mode',
      'horizontal-mix': 'Horizontal Mix menu Mode'
    },
    recommendColor: 'Apply Recommended Color Algorithm',
    recommendColorDesc: 'The recommended color algorithm refers to',
    themeColor: {
      title: 'Theme Color',
      primary: 'Primary',
      info: 'Info',
      success: 'Success',
      warning: 'Warning',
      error: 'Error',
      followPrimary: 'Follow Primary'
    },
    scrollMode: {
      title: 'Scroll Mode',
      wrapper: 'Wrapper',
      content: 'Content'
    },
    page: {
      animate: 'Page Animate',
      mode: {
        title: 'Page Animate Mode',
        fade: 'Fade',
        'fade-slide': 'Slide',
        'fade-bottom': 'Fade Zoom',
        'fade-scale': 'Fade Scale',
        'zoom-fade': 'Zoom Fade',
        'zoom-out': 'Zoom Out',
        none: 'None'
      }
    },
    fixedHeaderAndTab: 'Fixed Header And Tab',
    header: {
      height: 'Header Height',
      breadcrumb: {
        visible: 'Breadcrumb Visible',
        showIcon: 'Breadcrumb Icon Visible'
      }
    },
    tab: {
      visible: 'Tab Visible',
      cache: 'Tab Cache',
      height: 'Tab Height',
      mode: {
        title: 'Tab Mode',
        chrome: 'Chrome',
        button: 'Button'
      }
    },
    sider: {
      inverted: 'Dark Sider',
      width: 'Sider Width',
      collapsedWidth: 'Sider Collapsed Width',
      mixWidth: 'Mix Sider Width',
      mixCollapsedWidth: 'Mix Sider Collapse Width',
      mixChildMenuWidth: 'Mix Child Menu Width'
    },
    footer: {
      visible: 'Footer Visible',
      fixed: 'Fixed Footer',
      height: 'Footer Height',
      right: 'Right Footer'
    },
    themeDrawerTitle: 'Theme Configuration',
    pageFunTitle: 'Page Function',
    configOperation: {
      copyConfig: 'Copy Config',
      copySuccessMsg: 'Copy Success, Please replace the variable "themeSettings" in "src/theme/settings.ts"',
      resetConfig: 'Reset Config',
      resetSuccessMsg: 'Reset Success'
    }
  },
  route: {
    login: 'Login',
    403: 'No Permission',
    404: 'Page Not Found',
    500: 'Server Error',
    'iframe-page': 'Iframe',
    home: 'Home',
    document: 'Document',
    document_project: 'Project Document',
    'document_project-link': 'Project Document(External Link)',
    document_vue: 'Vue Document',
    document_vite: 'Vite Document',
    document_unocss: 'UnoCSS Document',
    document_naive: 'Naive UI Document',
    document_antd: 'Ant Design Vue Document',
    'user-center': 'User Center',
    about: 'About',
    manage: 'System Manage',
    exception: 'Exception',
    exception_403: '403',
    exception_404: '404',
    exception_500: '500',
    event: 'Event',
    'event_event-list': 'Event List',
    'detection_event-type': 'Event Type',
    detection: 'Network Security',
    detection_datasource: 'Log Datasource',
    detection_protocol: 'Log Protocol',
    detection_rule: 'Rule',
    detection_task: 'Task',
    event_attck: 'ATT&CK'
  },
  page: {
    login: {
      common: {
        loginOrRegister: 'Login / Register',
        usernamePlaceholder: 'Please enter user name',
        phonePlaceholder: 'Please enter phone number',
        codePlaceholder: 'Please enter verification code',
        passwordPlaceholder: 'Please enter password',
        confirmPasswordPlaceholder: 'Please enter password again',
        codeLogin: 'Verification code login',
        confirm: 'Confirm',
        back: 'Back',
        validateSuccess: 'Verification passed',
        loginSuccess: 'Login successfully',
        registerSuccess: 'Register successfully',
        welcomeBack: 'Welcome back, {username} !'
      },
      pwdLogin: {
        title: 'Login',
        rememberMe: 'Remember me',
        forgetPassword: 'Forget password?',
        register: 'Register',
        noAccount: 'No account?',
        otherAccountLogin: 'Other Account Login',
        otherLoginMode: 'Other Login Mode',
        superAdmin: 'Super Admin',
        admin: 'Admin',
        user: 'User'
      },
      codeLogin: {
        title: 'Verification Code Login',
        getCode: 'Get verification code',
        reGetCode: 'Reacquire after {time}s',
        sendCodeSuccess: 'Verification code sent successfully',
        imageCodePlaceholder: 'Please enter image verification code'
      },
      register: {
        title: 'Register',
        agreement: 'I have read and agree to',
        protocol: '《User Agreement》',
        policy: '《Privacy Policy》'
      },
      resetPwd: {
        title: 'Reset Password'
      },
      bindWeChat: {
        title: 'Bind WeChat'
      }
    },
    about: {
      title: 'About',
      introduction: `AXC-J20 is a security detection and awareness system.`,
      projectInfo: {
        title: 'Project Info',
        version: 'Version',
        latestBuildTime: 'Latest Build Time',
        githubLink: 'Github Link',
        previewLink: 'Preview Link'
      },
      prdDep: 'Production Dependency',
      devDep: 'Development Dependency'
    },
    home: {
      projectCount: 'Project Count',
      todo: 'Todo',
      message: 'Message',
      downloadCount: 'Download Count',
      registerCount: 'Register Count',
      schedule: 'Work and rest Schedule',
      study: 'Study',
      work: 'Work',
      rest: 'Rest',
      entertainment: 'Entertainment',
      visitCount: 'Visit Count',
      turnover: 'Turnover',
      dealCount: 'Deal Count',
      projectNews: {
        title: 'Project News',
        moreNews: 'More News'
      },
      creativity: 'Creativity'
    },
    function: {
      tab: {
        tabOperate: {
          title: 'Tab Operation',
          addTab: 'Add Tab',
          addTabDesc: 'To about page',
          closeTab: 'Close Tab',
          closeCurrentTab: 'Close Current Tab',
          closeAboutTab: 'Close "About" Tab',
          addMultiTab: 'Add Multi Tab',
          addMultiTabDesc1: 'To MultiTab page',
          addMultiTabDesc2: 'To MultiTab page(with query params)'
        },
        tabTitle: {
          title: 'Tab Title',
          changeTitle: 'Change Title',
          change: 'Change',
          resetTitle: 'Reset Title',
          reset: 'Reset'
        }
      },
      multiTab: {
        routeParam: 'Route Param',
        backTab: 'Back function_tab'
      },
      toggleAuth: {
        toggleAccount: 'Toggle Account',
        authHook: 'Auth Hook Function `hasAuth`',
        superAdminVisible: 'Super Admin Visible',
        adminVisible: 'Admin Visible',
        adminOrUserVisible: 'Admin and User Visible'
      },
      request: {
        repeatedErrorOccurOnce: 'Repeated Request Error Occurs Once',
        repeatedError: 'Repeated Request Error',
        repeatedErrorMsg1: 'Custom Request Error 1',
        repeatedErrorMsg2: 'Custom Request Error 2'
      }
    },
    manage: {
      common: {
        status: {
          enable: 'Enable',
          disable: 'Disable'
        }
      },
      role: {
        title: 'Role List',
        roleName: 'Role Name',
        roleCode: 'Role Code',
        roleStatus: 'Role Status',
        roleDesc: 'Role Description',
        menuAuth: 'Menu Auth',
        buttonAuth: 'Button Auth',
        form: {
          roleName: 'Please enter role name',
          roleCode: 'Please enter role code',
          roleStatus: 'Please select role status',
          roleDesc: 'Please enter role description'
        },
        addRole: 'Add Role',
        editRole: 'Edit Role'
      },
      user: {
        title: 'User List',
        username: 'User Name',
        userGender: 'Gender',
        nickName: 'Nick Name',
        userPhone: 'Phone Number',
        userEmail: 'Email',
        userStatus: 'User Status',
        userRole: 'User Role',
        form: {
          username: 'Please enter user name',
          userGender: 'Please select gender',
          nickName: 'Please enter nick name',
          userPhone: 'Please enter phone number',
          userEmail: 'Please enter email',
          userStatus: 'Please select user status',
          userRole: 'Please select user role'
        },
        addUser: 'Add User',
        editUser: 'Edit User',
        gender: {
          male: 'Male',
          female: 'Female'
        }
      },
      menu: {
        home: 'Home',
        title: 'Menu List',
        id: 'ID',
        parentId: 'Parent ID',
        menuType: 'Menu Type',
        menuName: 'Menu Name',
        routeName: 'Route Name',
        routePath: 'Route Path',
        pathParam: 'Path Param',
        layout: 'Layout Component',
        page: 'Page Component',
        i18nKey: 'I18n Key',
        icon: 'Icon',
        localIcon: 'Local Icon',
        iconTypeTitle: 'Icon Type',
        order: 'Order',
        constant: 'Constant',
        keepAlive: 'Keep Alive',
        href: 'Href',
        hideInMenu: 'Hide In Menu',
        activeMenu: 'Active Menu',
        multiTab: 'Multi Tab',
        fixedIndexInTab: 'Fixed Index In Tab',
        query: 'Query Params',
        button: 'Button',
        buttonCode: 'Button Code',
        buttonDesc: 'Button Desc',
        menuStatus: 'Menu Status',
        form: {
          home: 'Please select home',
          menuType: 'Please select menu type',
          menuName: 'Please enter menu name',
          routeName: 'Please enter route name',
          routePath: 'Please enter route path',
          pathParam: 'Please enter path param',
          page: 'Please select page component',
          layout: 'Please select layout component',
          i18nKey: 'Please enter i18n key',
          icon: 'Please enter iconify name',
          localIcon: 'Please enter local icon name',
          order: 'Please enter order',
          keepAlive: 'Please select whether to cache route',
          href: 'Please enter href',
          hideInMenu: 'Please select whether to hide menu',
          activeMenu: 'Please select route name of the highlighted menu',
          multiTab: 'Please select whether to support multiple tabs',
          fixedInTab: 'Please select whether to fix in the tab',
          fixedIndexInTab: 'Please enter the index fixed in the tab',
          queryKey: 'Please enter route parameter Key',
          queryValue: 'Please enter route parameter Value',
          button: 'Please select whether it is a button',
          buttonCode: 'Please enter button code',
          buttonDesc: 'Please enter button description',
          menuStatus: 'Please select menu status'
        },
        addMenu: 'Add Menu',
        editMenu: 'Edit Menu',
        addChildMenu: 'Add Child Menu',
        type: {
          directory: 'Directory',
          menu: 'Menu'
        },
        iconType: {
          iconify: 'Iconify Icon',
          local: 'Local Icon'
        }
      }
    },
    dashboard: {
      title: 'Dashboard - Today',
      config: 'Config Charts',
      name: 'Name',
      eventTypeId: 'Event Type',
      aggregateType: {
        title: 'Aggregate Type',
        COUNT: 'COUNT',
        SUM: 'SUM',
        GROUP_BY: 'GROUP_BY',
        HISTOGRAM: 'HISTOGRAM'
      },
      groupByField: 'Group By Field',
      aggregateField: 'Aggregate Field',
      chart1: 'Chart 1',
      chart2: 'Chart 2',
      chart3: 'Chart 3',
      chart4: 'Chart 4',
      chart5: 'Chart 5',
      chart6: 'Chart 6'
    },
    detection: {
      rule: {
        title: 'Rule',
        id: 'Rule ID',
        name: 'Rule Name',
        description: 'Rule Description',
        protocol: 'Log Protocol',
        criteriaType: 'Criteria Type',
        criteria: {
          title: 'Criteria',
          filter: {
            title: 'Filter',
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
              EXISTS: 'EXISTS',
              CONTAINS: 'CONTAINS',
              STARTS_WITH: 'STATS WITH',
              ENDS_WITH: 'END WITH',
              IN: 'IN',
              NOT_IN: 'NOT IN',
              REGEX_MATCH: 'REGEX MATCH',
              EXPRESS: 'EXPRESS'
            }
          },
          statistics: {
            title: 'Statistics',
            enabled: 'Enabled',
            disabled: 'Disabled',
            eager: 'Eager Trigger',
            windowSize: 'Window Size',
            windowSlide: 'Window Slide',
            groupBy: 'Group By',
            field: 'Field',
            aggregate: 'Aggregate',
            windowUnit: {
              SECOND: 'SECOND',
              MINUTE: 'MINUTE',
              HOUR: 'HOUR',
              DAY: 'DAY'
            },
            windowAggregate: {
              SUM: 'SUM',
              COUNT: 'COUNT',
              COUNT_UNIQUE: 'COUNT UNIQUE',
              MAX: 'MAX',
              MIN: 'MIN'
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
        input: 'Input',
        output: {
          title: 'Output',
          eventTypeId: 'Event Type',
          messageTempalte: 'Event Message',
          propertyFields: 'Event Fields'
        },
        frequency: 'Frequncy',
        aggregation: 'Aggregation',
        outputEventTypeId: 'Event Type',
        outputEventTypeName: 'Event Type',
        createTime: 'Create Time',
        lastUpdateTime: 'Update Time',
        test: {
          input: 'Input Log',
          output: 'Output Event'
        },
        form: {
          addRule: 'Add Rule',
          editRule: 'Edit Rule',
          testRule: 'Test Rule',
          name: 'Please enter rule name',
          description: 'Please enter rule description',
          protocol: 'Please choose log protocol',
          outputEventTypeId: 'Please choose output event type',
          addCriteriaFileterNodeGroup: 'Add Group',
          addCriteriaFileterNodeField: 'Add Field',
          criteriaFilterNodeField: 'Please choose log field',
          criteriaFilterNodeValue: 'Please input value',
          windowUnit: 'Unit',
          windowSize: 'Size',
          groupBy: 'Please choose group fields',
          field: 'Please choose field',
          value: 'Please enter value',
          operator: 'Operator',
          aggregate: 'Aggregate',
          eventTypeId: 'Please choose event type',
          eventMessageTemplate: 'Please enter event message',
          eventPropertyFiled: 'Please choose event field',
          eventPropertyValue: 'Please enter log field mapping',
          badJson: 'Please input correct JSON format'
        }
      },
      task: {
        title: 'Task',
        id: 'Task ID',
        name: 'Task Name',
        description: 'Task Description',
        srcDataSourceId: 'Input Datasouce',
        startingOffsetStrategy: 'Starting Offset',
        startingOffset: {
          EARLIEST: 'EARLIES',
          LATEST: 'LATEST'
        },
        rules: 'Apply Rules',
        jobStatus: {
          title: 'Job Status',
          NOT_CREATED: 'NOT_CREATED',
          CREATING: 'CREATING',
          CREATED: 'CREATED',
          RUNNING: 'RUNNING',
          FAILING: 'FAILING',
          FAILED: 'FAILED',
          CANCELLING: 'CANCELLING',
          CANCELED: 'CANCELED',
          FINISHED: 'FINISHED',
          RESTARTING: 'RESTARTING',
          SUSPENDING: 'SUSPENDING',
          SUSPENDED: 'SUSPENDED',
          RECONCILING: 'RECONCILING'
        },
        jobNumLogIn: 'Number of Log Input',
        jobNumEventOut: 'Number of Event Output',
        jobLastUpdateTime: 'Job Update Time',
        createTime: 'Create Time',
        lastUpdateTime: 'Update Time',
        form: {
          addTask: 'Add Task',
          editTask: 'Edit Task',
          name: 'Please enter task name',
          description: 'Please enter task description',
          srcDataSourceId: 'Please choose input datasource',
          startingOffsetStrategy: 'Starting Offset'
        }
      },
      protocol: {
        title: 'Protocol',
        code: 'Code',
        fieldSchema: 'Log Schema',
        form: {
          addProtocol: 'Add Protocol',
          editProtocol: 'Edit Protocol',
          code: 'Please enter code',
          fieldSchema: 'Please enter log shcema',
          fieldPath: 'Log Filed',
          fieldName: 'Dispaly Name'
        }
      },
      datasource: {
        title: 'Kafka Datasource',
        id: 'Datasource ID',
        name: 'Name',
        bootstrapServers: 'Bootstrap Servers',
        topic: 'Topic',
        numPartition: 'Number of Partitions',
        form: {
          addDatasource: 'Add Datasource',
          editDatasource: 'Edit Datasource',
          name: 'Please enter name',
          bootstrapServers: 'Please enter bootstrap servers',
          topic: 'Please enter topic',
          numPartition: 'Please enter number of partitions'
        }
      }
    },
    event: {
      eventList: {
        id: 'Event ID',
        title: 'Event List',
        detail: 'Event Detail',
        eventTypeId: 'Event Type',
        eventTypeName: 'Event Type',
        level: 'Event Level',
        query: 'Property',
        message: 'Event Message',
        taskName: 'Source Task',
        ruleName: 'Matched Rule',
        originalLog: 'Original Log',
        property: 'Property',
        timestamp: 'Create Time',
        beginTime: 'Begin Time',
        endTime: 'End Time',
        form: {
          beginTime: 'Please enter begin Time',
          endTime: 'Please enter end time',
          level: 'Please choose event level',
          eventTypeId: 'Please choose event type',
          query: 'Please enter lucene query (Takes effect when the event type is selected)'
        }
      },
      eventType: {
        id: 'Type ID',
        parentId: 'Parent Type ID',
        name: 'Type Name',
        level: {
          title: 'Event Level',
          NA: 'N/A',
          LOG: 'LOG',
          ALERT: 'ALERT'
        },
        title: 'Event Type',
        numEvent: 'Number of Events(Last Month)',
        form: {
          addChildType: 'Add Child Type',
          addType: 'Add Type',
          editType: 'Edit Type',
          name: 'Please enter type name',
          level: 'Please choose event level',
          fieldSchema: 'Please enter event schema',
          fieldPath: 'Event Filed',
          fieldName: 'Dispaly Name'
        },
        fieldSchema: 'Event Schema'
      }
    },
    statistics: {
      eventCount: 'Event Count',
      attackCount: 'Attack',
      malwareCount: 'Malware',
      spamCount: 'Spam',
      httpCount: 'HTTP Request(Today)',
      httpHistogram: 'HTTP Request',
      alertCount: 'Alert Count',
      tcpCount: 'TCP Connection(Today)',
      loginFailureCount: 'Login Failure(Today)）',
      sshCount: 'SSH Connect(Today)',
      news: {
        title: 'Alert News',
        more: 'More News',
        chart: 'Alert Distribution'
      }
    }
  },
  form: {
    required: 'Cannot be empty',
    username: {
      required: 'Please enter user name',
      invalid: 'User name format is incorrect'
    },
    phone: {
      required: 'Please enter phone number',
      invalid: 'Phone number format is incorrect'
    },
    pwd: {
      required: 'Please enter password',
      invalid: '8-16 characters, at least one uppercase letter, lowercase letter, digit, special character'
    },
    confirmPwd: {
      required: 'Please enter password again',
      invalid: 'The two passwords are inconsistent'
    },
    code: {
      required: 'Please enter verification code',
      invalid: 'Verification code format is incorrect'
    },
    email: {
      required: 'Please enter email',
      invalid: 'Email format is incorrect'
    }
  },
  dropdown: {
    closeCurrent: 'Close Current',
    closeOther: 'Close Other',
    closeLeft: 'Close Left',
    closeRight: 'Close Right',
    closeAll: 'Close All'
  },
  icon: {
    themeConfig: 'Theme Configuration',
    themeSchema: 'Theme Schema',
    lang: 'Switch Language',
    fullscreen: 'Fullscreen',
    fullscreenExit: 'Exit Fullscreen',
    reload: 'Reload Page',
    collapse: 'Collapse Menu',
    expand: 'Expand Menu',
    pin: 'Pin',
    unpin: 'Unpin'
  },
  datatable: {
    itemCount: 'Total {total} items'
  }
};

export default local;
