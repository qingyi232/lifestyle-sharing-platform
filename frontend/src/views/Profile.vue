<template>
  <div class="profile-page page-container" v-loading="loading">
    <div v-if="user" class="profile-content">
      <!-- 用户信息卡 -->
      <div class="profile-header card">
        <div class="profile-info">
          <el-avatar :size="80" :src="user.avatar || defaultAvatar" />
          <div class="profile-meta">
            <h2 class="profile-name">{{ user.nickname || user.username }}</h2>
            <p class="profile-bio">{{ user.bio || '这个人很懒，什么都没写~' }}</p>
            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-num">{{ user.notesCount || 0 }}</span>
                <span class="stat-label">笔记</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ user.followersCount || 0 }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item">
                <span class="stat-num">{{ user.followingCount || 0 }}</span>
                <span class="stat-label">关注</span>
              </div>
            </div>
          </div>
          <el-button
            v-if="userStore.isLoggedIn && user.id !== userStore.userInfo?.id"
            :type="followed ? 'default' : 'primary'"
            round
            @click="handleFollow"
          >
            {{ followed ? '已关注' : '+ 关注' }}
          </el-button>
        </div>
      </div>

      <!-- 用户笔记 -->
      <div class="profile-notes">
        <h3 class="section-title">Ta 的笔记</h3>
        <div class="waterfall" v-loading="notesLoading">
          <NoteCard v-for="note in notes" :key="note.id" :note="note" />
        </div>
        <el-empty v-if="!notesLoading && notes.length === 0" description="暂无笔记" />
        <div class="pagination-wrapper" v-if="total > pageSize">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserById, getUserNotes } from '@/api/user'
import { toggleFollow, getFollowStatus } from '@/api/interaction'
import NoteCard from '@/components/NoteCard.vue'

const route = useRoute()
const userStore = useUserStore()
const user = ref(null)
const notes = ref([])
const loading = ref(true)
const notesLoading = ref(false)
const followed = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const fetchUser = async () => {
  loading.value = true
  try {
    const res = await getUserById(route.params.id)
    user.value = res.data
    if (userStore.isLoggedIn && user.value.id !== userStore.userInfo?.id) {
      const statusRes = await getFollowStatus(user.value.id)
      followed.value = statusRes.data?.followed || false
    }
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const fetchNotes = async () => {
  notesLoading.value = true
  try {
    const res = await getUserNotes(route.params.id, { page: currentPage.value, size: pageSize.value })
    notes.value = res.data?.records || res.data || []
    total.value = res.data?.total || notes.value.length
  } catch {
    /* ignore */
  } finally {
    notesLoading.value = false
  }
}

const handleFollow = async () => {
  try {
    const res = await toggleFollow(user.value.id)
    followed.value = res.data?.followed
    ElMessage.success(followed.value ? '关注成功' : '已取消关注')
    fetchUser()
  } catch {
    /* ignore */
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotes()
}

watch(() => route.params.id, () => {
  fetchUser()
  fetchNotes()
})

onMounted(() => {
  fetchUser()
  fetchNotes()
})
</script>

<style scoped>
.profile-header {
  padding: 32px;
  margin-bottom: 28px;
}

.profile-info {
  display: flex;
  align-items: flex-start;
  gap: 24px;
}

.profile-meta {
  flex: 1;
}

.profile-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.profile-bio {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.profile-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.waterfall {
  columns: 4;
  column-gap: 20px;
}

@media (max-width: 1200px) {
  .waterfall { columns: 3; }
}

@media (max-width: 900px) {
  .waterfall { columns: 2; }
}

@media (max-width: 600px) {
  .waterfall { columns: 1; }
  .profile-info { flex-direction: column; align-items: center; text-align: center; }
  .profile-stats { justify-content: center; }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}
</style>
