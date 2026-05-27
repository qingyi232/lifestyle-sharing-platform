<template>
  <header class="navbar">
    <div class="navbar-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">🌿</span>
        <span class="logo-text">生活方式分享</span>
      </router-link>

      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="搜索你感兴趣的内容..."
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
        />
      </div>

      <div class="nav-right">
        <template v-if="!userStore.isLoggedIn">
          <el-button round @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" round @click="$router.push('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-button type="primary" round :icon="EditPen" @click="$router.push('/note/edit')">
            发布笔记
          </el-button>

          <div class="notification-wrapper" @click="$router.push('/notifications')">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
              <el-icon :size="22"><Bell /></el-icon>
            </el-badge>
          </div>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-wrapper">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar || defaultAvatar" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user-center">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="my-notes">
                  <el-icon><Document /></el-icon> 我的笔记
                </el-dropdown-item>
                <el-dropdown-item command="my-favorites">
                  <el-icon><Star /></el-icon> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon> 账号设置
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isAdmin" command="admin" divided>
                  <el-icon><Monitor /></el-icon> 后台管理
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadCount } from '@/api/notification'
import { Search, EditPen, Bell, User, Document, Star, Setting, Monitor, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')
const unreadCount = ref(0)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

let timer = null

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: keyword.value.trim() } })
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
  } else if (command === 'admin') {
    router.push('/admin')
  } else {
    router.push(`/${command}`)
  }
}

const fetchUnreadCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data?.count || 0
  } catch {
    /* ignore */
  }
}

onMounted(() => {
  fetchUnreadCount()
  timer = setInterval(fetchUnreadCount, 30000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff;
  border-bottom: 1px solid var(--border);
}

.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  gap: 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
  white-space: nowrap;
}

.search-box {
  flex: 1;
  max-width: 420px;
}

.search-box :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: var(--bg-light);
  box-shadow: none !important;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.search-box :deep(.el-input__wrapper:hover),
.search-box :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-light);
  background: #fff;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.notification-wrapper {
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  color: var(--text-secondary);
  transition: color 0.3s;
}

.notification-wrapper:hover {
  color: var(--primary);
}

.avatar-wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.avatar-wrapper :deep(.el-avatar) {
  border: 2px solid var(--primary-light);
  transition: border-color 0.3s;
}

.avatar-wrapper:hover :deep(.el-avatar) {
  border-color: var(--primary);
}
</style>
