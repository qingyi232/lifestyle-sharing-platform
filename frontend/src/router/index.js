import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

import Home from '@/views/Home.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import NoteDetail from '@/views/NoteDetail.vue'
import NoteEdit from '@/views/NoteEdit.vue'
import Search from '@/views/Search.vue'
import Profile from '@/views/Profile.vue'
import UserCenter from '@/views/UserCenter.vue'
import MyNotes from '@/views/MyNotes.vue'
import MyFavorites from '@/views/MyFavorites.vue'
import FollowList from '@/views/FollowList.vue'
import Settings from '@/views/Settings.vue'
import Notifications from '@/views/Notifications.vue'
import AdminLayout from '@/views/admin/AdminLayout.vue'
import Dashboard from '@/views/admin/Dashboard.vue'
import UserManage from '@/views/admin/UserManage.vue'
import NoteReview from '@/views/admin/NoteReview.vue'
import CategoryManage from '@/views/admin/CategoryManage.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/note/:id', name: 'NoteDetail', component: NoteDetail },
  { path: '/note/edit', name: 'NoteCreate', component: NoteEdit, meta: { requiresAuth: true } },
  { path: '/note/edit/:id', name: 'NoteEditById', component: NoteEdit, meta: { requiresAuth: true } },
  { path: '/search', name: 'Search', component: Search },
  { path: '/user/:id', name: 'Profile', component: Profile },
  { path: '/user-center', name: 'UserCenter', component: UserCenter, meta: { requiresAuth: true } },
  { path: '/my-notes', name: 'MyNotes', component: MyNotes, meta: { requiresAuth: true } },
  { path: '/my-favorites', name: 'MyFavorites', component: MyFavorites, meta: { requiresAuth: true } },
  { path: '/follow-list', name: 'FollowList', component: FollowList, meta: { requiresAuth: true } },
  { path: '/settings', name: 'Settings', component: Settings, meta: { requiresAuth: true } },
  { path: '/notifications', name: 'Notifications', component: Notifications, meta: { requiresAuth: true } },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: Dashboard },
      { path: 'users', name: 'AdminUsers', component: UserManage },
      { path: 'notes', name: 'AdminNotes', component: NoteReview },
      { path: 'categories', name: 'AdminCategories', component: CategoryManage }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
