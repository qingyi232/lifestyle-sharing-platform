<template>
  <div class="user-center page-container">
    <!-- 个人信息卡 -->
    <div class="info-card card">
      <div class="info-top">
        <el-avatar :size="80" :src="userStore.userInfo?.avatar || defaultAvatar" />
        <div class="info-meta">
          <h2>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h2>
          <p class="bio">{{ userStore.userInfo?.bio || '这个人很懒，什么都没写~' }}</p>
        </div>
      </div>
      <div class="stats-row">
        <div class="stat-item" @click="$router.push('/my-notes')">
          <span class="stat-num">{{ stats.notesCount || 0 }}</span>
          <span class="stat-label">笔记</span>
        </div>
        <div class="stat-item" @click="$router.push('/follow-list?type=followers')">
          <span class="stat-num">{{ stats.followersCount || 0 }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-item" @click="$router.push('/follow-list?type=following')">
          <span class="stat-num">{{ stats.followingCount || 0 }}</span>
          <span class="stat-label">关注</span>
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-links">
      <div class="link-card card card-hover" @click="$router.push('/my-notes')">
        <el-icon :size="28" color="#52b788"><Document /></el-icon>
        <span>我的笔记</span>
      </div>
      <div class="link-card card card-hover" @click="$router.push('/my-favorites')">
        <el-icon :size="28" color="#e07a5f"><Star /></el-icon>
        <span>我的收藏</span>
      </div>
      <div class="link-card card card-hover" @click="$router.push('/follow-list?type=following')">
        <el-icon :size="28" color="#52b788"><UserFilled /></el-icon>
        <span>关注列表</span>
      </div>
      <div class="link-card card card-hover" @click="$router.push('/follow-list?type=followers')">
        <el-icon :size="28" color="#636e72"><User /></el-icon>
        <span>粉丝列表</span>
      </div>
      <div class="link-card card card-hover" @click="$router.push('/settings')">
        <el-icon :size="28" color="#636e72"><Setting /></el-icon>
        <span>账号设置</span>
      </div>
      <div class="link-card card card-hover" @click="$router.push('/notifications')">
        <el-icon :size="28" color="#e07a5f"><Bell /></el-icon>
        <span>我的通知</span>
      </div>
    </div>

    <!-- 最近笔记 -->
    <div class="recent-notes" v-if="recentNotes.length">
      <h3 class="section-title">最近笔记</h3>
      <div class="waterfall">
        <NoteCard v-for="note in recentNotes" :key="note.id" :note="note" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getProfile } from '@/api/user'
import { getMyNotes } from '@/api/note'
import { Document, Star, UserFilled, User, Setting, Bell } from '@element-plus/icons-vue'
import NoteCard from '@/components/NoteCard.vue'

const userStore = useUserStore()
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'
const stats = ref({})
const recentNotes = ref([])

onMounted(async () => {
  try {
    const res = await getProfile()
    stats.value = res.data || {}
    userStore.setUserInfo(res.data)
  } catch {
    /* ignore */
  }
  try {
    const res = await getMyNotes()
    const allNotes = res.data?.records || res.data || []
    recentNotes.value = allNotes.slice(0, 8)
  } catch {
    /* ignore */
  }
})
</script>

<style scoped>
.info-card {
  padding: 32px;
  margin-bottom: 24px;
}

.info-top {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.info-meta h2 {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.bio {
  font-size: 14px;
  color: var(--text-secondary);
}

.stats-row {
  display: flex;
  gap: 48px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: color 0.3s;
}

.stat-item:hover .stat-num {
  color: var(--primary);
}

.stat-num {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 32px;
}

.link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.waterfall {
  columns: 4;
  column-gap: 20px;
}

@media (max-width: 900px) {
  .waterfall { columns: 2; }
  .quick-links { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .waterfall { columns: 1; }
}
</style>
