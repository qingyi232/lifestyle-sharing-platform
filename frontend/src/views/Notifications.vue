<template>
  <div class="notify-page page-container">
    <div class="notify-header flex-between">
      <h2 class="page-title">通知</h2>
      <el-button v-if="notifications.length" text type="primary" @click="handleMarkAllRead">
        全部标记已读
      </el-button>
    </div>

    <div class="notify-list" v-loading="loading">
      <div
        v-for="n in notifications"
        :key="n.id"
        class="notify-item card"
        :class="{ unread: !n.read && !n.isRead }"
        @click="handleRead(n)"
      >
        <div class="notify-dot" v-if="!n.read && !n.isRead"></div>
        <el-avatar :size="40" :src="n.fromUserAvatar || defaultAvatar" />
        <div class="notify-body">
          <p class="notify-content">
            <span class="from-user">{{ n.fromUserName || '用户' }}</span>
            {{ n.content || getNotifyText(n.type) }}
          </p>
          <span class="notify-time">{{ formatTime(n.createdAt) }}</span>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && notifications.length === 0" description="暂无通知" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotifications, markAsRead, markAllAsRead } from '@/api/notification'

const notifications = ref([])
const loading = ref(true)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const getNotifyText = (type) => {
  const map = {
    like: '赞了你的笔记',
    comment: '评论了你的笔记',
    follow: '关注了你',
    favorite: '收藏了你的笔记'
  }
  return map[type] || '给你发送了通知'
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  return d.toLocaleDateString('zh-CN')
}

const handleRead = async (n) => {
  if (!n.read && !n.isRead) {
    try {
      await markAsRead(n.id)
      n.read = true
      n.isRead = true
    } catch {
      /* ignore */
    }
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(n => { n.read = true; n.isRead = true })
    ElMessage.success('已全部标记为已读')
  } catch {
    /* ignore */
  }
}

onMounted(async () => {
  try {
    const res = await getNotifications()
    notifications.value = res.data || []
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.notify-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.notify-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notify-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  cursor: pointer;
  position: relative;
  transition: all 0.3s;
}

.notify-item:hover {
  box-shadow: var(--shadow-hover);
}

.notify-item.unread {
  background: #f0faf4;
}

.notify-dot {
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
}

.notify-body {
  flex: 1;
}

.notify-content {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.5;
  margin-bottom: 4px;
}

.from-user {
  font-weight: 600;
  margin-right: 4px;
}

.notify-time {
  font-size: 12px;
  color: #b0b0b0;
}
</style>
