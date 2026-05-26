import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', name: 'Login', component: () => import('../views/login/Login.vue') },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('../views/dashboard/Dashboard.vue') },
      { path: 'martyrs', name: 'MartyrList', component: () => import('../views/martyr/MartyrList.vue') },
      { path: 'martyrs/:id', name: 'MartyrDetail', component: () => import('../views/martyr/MartyrDetail.vue') },
      { path: 'register', name: 'ResourceRegister', component: () => import('../views/register/ResourceRegister.vue') },
      { path: 'statistics', name: 'Statistics', component: () => import('../views/statistics/Statistics.vue') },
      { path: 'search', name: 'Search', component: () => import('../views/search/Search.vue') },
      { path: 'recycle', name: 'RecycleBin', component: () => import('../views/recycle/RecycleBin.vue') },
      { path: 'relics', name: 'RelicDisplay', component: () => import('../views/relic/RelicDisplay.vue') },
      { path: 'deeds', name: 'DeedList', component: () => import('../views/deeds/DeedList.vue') },
      { path: 'media', name: 'MediaList', component: () => import('../views/media/MediaList.vue') },
      { path: 'honors', name: 'HonorList', component: () => import('../views/honor/HonorList.vue') },
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.name !== 'Login' && !token) {
    next({ name: 'Login' })
  } else if (to.name === 'Login' && token) {
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router
