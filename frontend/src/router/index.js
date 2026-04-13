import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/index.vue')
  },
  {
    path: '/auth',
    name: 'Auth',
    component: () => import('@/views/auth/index.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/index.vue'),
    redirect: '/admin/boards',
    children: [
      {
        path: 'boards',
        name: 'BoardManagement',
        component: () => import('@/views/admin/board/index.vue')
      },
      {
        path: 'posts',
        name: 'PostManagement',
        component: () => import('@/views/admin/post/index.vue')
      },
      {
        path: 'sensitive-words',
        name: 'SensitiveWordManagement',
        component: () => import('@/views/admin/sensitive-word/index.vue')
      },
      {
        path: 'audit',
        name: 'AuditManagement',
        component: () => import('@/views/admin/audit/index.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/admin/user/index.vue')
      },
      {
        path: 'whitelist',
        name: 'WhitelistManagement',
        component: () => import('@/views/admin/whitelist/index.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  next()
})

export default router