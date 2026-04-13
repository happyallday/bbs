import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

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
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/post/detail.vue')
  },
  {
    path: '/post/create',
    name: 'PostCreate',
    component: () => import('@/views/post/create.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/index.vue')
  },
  {
    path: '/admin/posts',
    name: 'AdminPosts',
    component: () => import('@/views/admin/posts.vue')
  },
  {
    path: '/admin/audit',
    name: 'AdminAudit',
    component: () => import('@/views/admin/audit.vue')
  },
  {
    path: '/admin/words',
    name: 'AdminWords',
    component: () => import('@/views/admin/words.vue')
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