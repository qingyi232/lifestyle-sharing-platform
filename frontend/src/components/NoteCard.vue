<template>
  <div class="note-card card card-hover" @click="goDetail">
    <div class="card-cover">
      <img
        :src="getCoverUrl(note)"
        :alt="note.title"
        loading="lazy"
        @error="onCoverError"
      />
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ note.title }}</h3>
      <p class="card-summary">{{ stripHtml(note.content) }}</p>
      <div class="card-footer">
        <div class="author-info" @click.stop="goProfile">
          <el-avatar :size="24" :src="note.authorAvatar || defaultAvatar" />
          <span class="author-name">{{ note.authorName || '匿名用户' }}</span>
        </div>
        <div class="like-info">
          <el-icon :size="14" color="#e07a5f"><i class="heart-icon">❤</i></el-icon>
          <span>{{ note.likeCount || 0 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  note: { type: Object, required: true }
})

const router = useRouter()
const defaultCover = 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800&q=80'
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const getCoverUrl = (note) => {
  const url = note?.coverImage
  if (url && typeof url === 'string' && url.trim()) return url
  return defaultCover
}

const onCoverError = (e) => {
  if (e.target.src !== defaultCover) e.target.src = defaultCover
}

const goDetail = () => {
  router.push(`/note/${props.note.id}`)
}

const goProfile = () => {
  if (props.note.authorId || props.note.userId) {
    router.push(`/user/${props.note.authorId || props.note.userId}`)
  }
}

const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').substring(0, 80)
}
</script>

<style scoped>
.note-card {
  cursor: pointer;
  transition: all 0.3s ease;
  break-inside: avoid;
  margin-bottom: 20px;
}

.card-cover {
  width: 100%;
  overflow: hidden;
  border-radius: var(--radius) var(--radius) 0 0;
}

.card-cover img {
  width: 100%;
  aspect-ratio: 4/3;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.note-card:hover .card-cover img {
  transform: scale(1.05);
}

.card-body {
  padding: 14px 16px 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.author-name {
  font-size: 13px;
  color: var(--text-secondary);
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.like-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

.heart-icon {
  font-style: normal;
  font-size: 14px;
  color: var(--accent);
}
</style>
