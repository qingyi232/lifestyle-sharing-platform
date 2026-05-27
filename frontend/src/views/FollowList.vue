<template>
  <div class="follow-page page-container">
    <div class="tabs-header">
      <span
        class="tab-item"
        :class="{ active: type === 'following' }"
        @click="switchType('following')"
      >
        关注
      </span>
      <span
        class="tab-item"
        :class="{ active: type === 'followers' }"
        @click="switchType('followers')"
      >
        粉丝
      </span>
    </div>

    <div class="user-list" v-loading="loading">
      <UserCard
        v-for="user in users"
        :key="user.id"
        :user="user"
        @update="fetchUsers"
      />
    </div>

    <el-empty
      v-if="!loading && users.length === 0"
      :description="type === 'following' ? '还没有关注任何人' : '还没有粉丝'"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUserFollowers, getUserFollowing } from '@/api/user'
import UserCard from '@/components/UserCard.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const type = ref(route.query.type || 'following')
const users = ref([])
const loading = ref(true)

const switchType = (t) => {
  type.value = t
  router.replace({ query: { type: t } })
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const userId = userStore.userInfo?.id
    if (!userId) return
    const res = type.value === 'following'
      ? await getUserFollowing(userId)
      : await getUserFollowers(userId)
    users.value = res.data || []
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

watch(() => route.query.type, (val) => {
  type.value = val || 'following'
  fetchUsers()
})

onMounted(fetchUsers)
</script>

<style scoped>
.tabs-header {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  border-bottom: 2px solid #f0f0f0;
}

.tab-item {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-secondary);
  padding-bottom: 12px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.3s;
}

.tab-item.active {
  color: var(--primary);
  border-bottom-color: var(--primary);
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
