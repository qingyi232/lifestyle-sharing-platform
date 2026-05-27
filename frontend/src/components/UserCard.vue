<template>
  <div class="user-card card" :class="{ clickable: clickable }" @click="handleClick">
    <el-avatar :size="48" :src="user.avatar || defaultAvatar" />
    <div class="user-info">
      <div class="user-name">{{ user.nickname || user.username || '用户' }}</div>
      <div class="user-bio">{{ user.bio || '这个人很懒，什么都没写~' }}</div>
    </div>
    <el-button
      v-if="showFollow && userStore.isLoggedIn && user.id !== userStore.userInfo?.id"
      :type="user.followed ? 'default' : 'primary'"
      size="small"
      round
      @click.stop="handleFollow"
    >
      {{ user.followed ? '已关注' : '关注' }}
    </el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { toggleFollow } from '@/api/interaction'

const props = defineProps({
  user: { type: Object, required: true },
  showFollow: { type: Boolean, default: true },
  clickable: { type: Boolean, default: true }
})

const emit = defineEmits(['update'])
const router = useRouter()
const userStore = useUserStore()
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const handleClick = () => {
  if (props.clickable && props.user.id) {
    router.push(`/user/${props.user.id}`)
  }
}

const handleFollow = async () => {
  try {
    const res = await toggleFollow(props.user.id)
    props.user.followed = res.data?.followed
    ElMessage.success(res.data?.followed ? '关注成功' : '已取消关注')
    emit('update')
  } catch {
    /* ignore */
  }
}
</script>

<style scoped>
.user-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  transition: all 0.3s;
}

.user-card.clickable {
  cursor: pointer;
}

.user-card.clickable:hover {
  box-shadow: var(--shadow-hover);
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.user-bio {
  font-size: 13px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
