import { defineConfig } from 'vitepress';
import { qqSvg } from './icon.js';

export default defineConfig({
  head: [
    ['meta', { name: 'author', content: 'airxiechao' }],
    [
      'meta',
      {
        name: 'keywords',
        content: 'airxiechao'
      }
    ],
    ['link', { rel: 'icon', type: 'image/svg+xml', href: '/logo.svg' }],
    [
      'meta',
      {
        name: 'viewport',
        content: 'width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no'
      }
    ],
    ['link', { rel: 'icon', href: '/favicon.ico' }]
  ],
  assetsDir: 'public',
  srcDir: 'src',
  title: 'airxiechao',
  description: '为小微企业提供安全、便捷的信息系统',
  themeConfig: {
    logo: '/logo.svg',
    socialLinks: [{ icon: 'github', link: 'https://github.com/airxiechao' }],
    footer: {
      message:
        '蜀ICP备20023213号-3 <img src="/icon-gongan.png" style="display:inline;vertical-align:top;margin-left:8px" /> 川公网安备 51019002006461号',
      copyright: '© 2024 成都鲲擎软件科技有限公司'
    },
    nav: [
      { text: '产品介绍', link: '/guide/intro', activeMatch: '/guide/' },
      {
        text: '演示',
        items: [
          {
            text: '鲲擎运维 Y20',
            link: 'https://y20.work'
          },
          {
            text: '鲲擎安全 J20',
            link: 'https://j20.airxiechao.com'
          }
        ]
      },
      { text: '常见问题', link: '/faq/', activeMatch: '/faq/' },
      {
        text: '博客',
        link: '/blogs/',
        activeMatch: '/blogs/'
      }
    ],
    sidebar: {
      '/guide/': [
        {
          text: '全部',
          items: [
            {
              text: '产品目录',
              link: '/guide/intro'
            }
          ]
        },
        {
          text: '鲲擎运维',
          items: [
            {
              text: 'Y20 简介',
              link: '/guide/Y20/intro'
            },
            {
              text: '快速开始',
              link: '/guide/Y20/quick-start'
            }
          ]
        },
        {
          text: '鲲擎安全',
          items: [
            {
              text: 'J20 简介',
              link: '/guide/J20/intro'
            },
            {
              text: '快速开始',
              link: '/guide/J20/quick-start'
            }
          ]
        }
      ],
      '/blogs/': [
        {
          text: '博客',
          link: '/blogs/',
          items: [
            {
              text: '2024-06-18',
              link: '/blogs/2024-06-18'
            }
          ]
        }
      ]
    }
  }
});
