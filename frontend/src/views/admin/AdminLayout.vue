<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <span class="sidebar-logo">🌿</span>
        <span class="sidebar-title">管理后台</span>
      </div>

      <nav class="sidebar-menu">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ active: $route.path === item.path }"
        >
          <el-icon :size="18"><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="footer-item" @click="$router.push('/')">
          <el-icon :size="16"><Back /></el-icon>
          <span>返回前台</span>
        </div>
        <div class="footer-item" @click="handleLogout">
          <el-icon :size="16"><SwitchButton /></el-icon>
          <span>退出登录</span>
        </div>
      </div>
    </aside>

    <!-- 内容区 -->
    <div class="admin-main">
      <header class="admin-topbar">
        <div class="topbar-title">{{ currentTitle }}</div>
        <div class="topbar-user">
          <el-avatar :size="32" :src="userStore.userInfo?.avatar || defaultAvatar" />
          <span>{{ userStore.userInfo?.nickname || '管理员' }}</span>
        </div>
      </header>
      <div class="admin-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { DataAnalysis, User, Document, Menu, Back, SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const menuItems = [
  { path: '/admin/dashboard', label: '数据看板', icon: DataAnalysis },
  { path: '/admin/users', label: '用户管理', icon: User },
  { path: '/admin/notes', label: '内容审核', icon: Document },
  { path: '/admin/categories', label: '分类管理', icon: Menu }
]

const currentTitle = computed(() => {
  const item = menuItems.find(m => m.path === route.path)
  return item?.label || '管理后台'
})

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 220px;
  background: #2d3436;
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.sidebar-logo {
  font-size: 28px;
}

.sidebar-title {
  font-size: 17px;
  font-weight: 700;
  color: #fff;
}

.sidebar-menu {
  flex: 1;
  padding: 12px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.65);
  transition: all 0.3s;
  cursor: pointer;
  text-decoration: none;
}

.menu-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.06);
}

.menu-item.active {
  color: #fff;
  background: var(--primary-dark);
  border-right: 3px solid var(--primary);
}

.sidebar-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding: 12px 0;
}

.footer-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 24px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: color 0.3s;
}

.footer-item:hover {
  color: #fff;
}

.admin-main {
  flex: 1;
  margin-left: 220px;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.admin-topbar {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.topbar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--text-secondary);
}

.admin-content {
  padding: 24px 28px;
  flex: 1;
}
</style>
