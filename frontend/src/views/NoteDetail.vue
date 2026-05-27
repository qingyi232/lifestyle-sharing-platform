<template>
  <div class="detail-page page-container" v-loading="loading">
    <div v-if="note" class="detail-content">
      <!-- 图片展示 -->
      <div class="detail-images" v-if="allImages.length">
        <el-carousel v-if="allImages.length > 1" :autoplay="false" height="480px" indicator-position="outside">
          <el-carousel-item v-for="(img, i) in allImages" :key="i">
            <img :src="img" class="carousel-img" />
          </el-carousel-item>
        </el-carousel>
        <img v-else :src="allImages[0]" class="single-img" />
      </div>

      <!-- 文章信息 -->
      <div class="detail-info">
        <h1 class="detail-title">{{ note.title }}</h1>

        <!-- 作者信息 -->
        <div class="author-bar">
          <div class="author-left" @click="$router.push(`/user/${note.userId}`)">
            <el-avatar :size="44" :src="note.authorAvatar || defaultAvatar" />
            <div class="author-meta">
              <span class="author-name">{{ note.authorName || '用户' }}</span>
              <span class="publish-time">{{ formatTime(note.createdAt) }}</span>
            </div>
          </div>
          <el-button
            v-if="userStore.isLoggedIn && note.userId !== userStore.userInfo?.id"
            :type="followed ? 'default' : 'primary'"
            size="small"
            round
            @click="handleFollow"
          >
            {{ followed ? '已关注' : '+ 关注' }}
          </el-button>
        </div>

        <!-- 正文 -->
        <div class="article-body" v-html="note.content"></div>

        <!-- 分类和标签 -->
        <div class="tags-area">
          <el-tag v-if="note.categoryName" type="success" effect="plain" round>
            {{ note.categoryName }}
          </el-tag>
          <el-tag
            v-for="(tag, i) in parseTags(note.tags)"
            :key="i"
            effect="plain"
            round
          >
            {{ tag }}
          </el-tag>
        </div>

        <!-- 互动栏 -->
        <div class="interaction-bar">
          <div class="interact-btn" :class="{ active: liked }" @click="handleLike">
            <span class="interact-icon">{{ liked ? '❤️' : '🤍' }}</span>
            <span>{{ note.likeCount || 0 }}</span>
          </div>
          <div class="interact-btn" :class="{ active: favorited }" @click="handleFavorite">
            <span class="interact-icon">{{ favorited ? '⭐' : '☆' }}</span>
            <span>{{ note.favoriteCount || 0 }}</span>
          </div>
          <div class="interact-btn">
            <span class="interact-icon">💬</span>
            <span>{{ note.commentCount || 0 }}</span>
          </div>
        </div>

        <!-- 评论区 -->
        <CommentSection :noteId="noteId" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getNoteById } from '@/api/note'
import { toggleLike, toggleFavorite, getInteractionStatus, toggleFollow, getFollowStatus } from '@/api/interaction'
import CommentSection from '@/components/CommentSection.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const noteId = computed(() => route.params.id)
const note = ref(null)
const loading = ref(true)
const liked = ref(false)
const favorited = ref(false)
const followed = ref(false)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const allImages = computed(() => {
  const imgs = []
  if (note.value?.coverImage) imgs.push(note.value.coverImage)
  if (note.value?.images) {
    const parsed = typeof note.value.images === 'string'
      ? JSON.parse(note.value.images || '[]')
      : (note.value.images || [])
    parsed.forEach(img => { if (!imgs.includes(img)) imgs.push(img) })
  }
  return imgs
})

const parseTags = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  try { return JSON.parse(tags) } catch { return tags.split(',').filter(Boolean) }
}

const fetchNote = async () => {
  loading.value = true
  try {
    const res = await getNoteById(noteId.value)
    note.value = res.data
    if (userStore.isLoggedIn) {
      fetchInteractionStatus()
      if (note.value.userId) fetchFollowStatus()
    }
  } catch {
    router.push('/')
  } finally {
    loading.value = false
  }
}

const fetchInteractionStatus = async () => {
  try {
    const res = await getInteractionStatus(noteId.value)
    liked.value = res.data?.liked || false
    favorited.value = res.data?.favorited || false
  } catch {
    /* ignore */
  }
}

const fetchFollowStatus = async () => {
  try {
    const res = await getFollowStatus(note.value.userId)
    followed.value = res.data?.followed || false
  } catch {
    /* ignore */
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    const res = await toggleLike(noteId.value)
    liked.value = res.data?.liked
    note.value.likeCount = liked.value
      ? (note.value.likeCount || 0) + 1
      : Math.max((note.value.likeCount || 0) - 1, 0)
  } catch {
    /* ignore */
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) return router.push('/login')
  try {
    const res = await toggleFavorite(noteId.value)
    favorited.value = res.data?.favorited
    note.value.favoriteCount = favorited.value
      ? (note.value.favoriteCount || 0) + 1
      : Math.max((note.value.favoriteCount || 0) - 1, 0)
  } catch {
    /* ignore */
  }
}

const handleFollow = async () => {
  try {
    const res = await toggleFollow(note.value.userId)
    followed.value = res.data?.followed
    ElMessage.success(followed.value ? '关注成功' : '已取消关注')
  } catch {
    /* ignore */
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(fetchNote)
</script>

<style scoped>
.detail-page {
  max-width: 800px;
}

.detail-images {
  margin-bottom: 24px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: #f0f0f0;
}

.carousel-img,
.single-img {
  width: 100%;
  max-height: 480px;
  object-fit: contain;
  background: #f5f5f3;
}

.single-img {
  border-radius: var(--radius-lg);
}

.detail-title {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.4;
  color: var(--text-primary);
  margin-bottom: 20px;
}

.author-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.author-left {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.author-meta {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.publish-time {
  font-size: 13px;
  color: var(--text-secondary);
}

.article-body {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
  margin-bottom: 24px;
}

.article-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}

.tags-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.interaction-bar {
  display: flex;
  gap: 32px;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.interact-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
  user-select: none;
}

.interact-btn:hover {
  background: var(--bg-light);
}

.interact-btn.active {
  color: var(--accent);
}

.interact-icon {
  font-size: 18px;
}
</style>
