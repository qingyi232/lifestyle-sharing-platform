<template>
  <div class="my-notes page-container">
    <h2 class="page-title">我的笔记</h2>

    <div class="notes-list" v-loading="loading">
      <div v-for="note in notes" :key="note.id" class="note-item card">
        <div class="note-left" @click="$router.push(`/note/${note.id}`)">
          <img
            :src="getCoverUrl(note)"
            class="note-thumb"
            @error="(e) => { if (e.target.src !== defaultCover) e.target.src = defaultCover }"
          />
        </div>
        <div class="note-body">
          <h3 class="note-title" @click="$router.push(`/note/${note.id}`)">{{ note.title }}</h3>
          <p class="note-summary">{{ stripHtml(note.content) }}</p>
          <div class="note-meta">
            <el-tag
              :type="statusType(note.status)"
              size="small"
              effect="plain"
              round
            >
              {{ statusText(note.status) }}
            </el-tag>
            <span class="note-time">{{ formatTime(note.createdAt) }}</span>
          </div>
          <div v-if="note.status === 2 && note.rejectReason" class="reject-reason">
            驳回原因：{{ note.rejectReason }}
          </div>
        </div>
        <div class="note-actions">
          <el-button size="small" :icon="Edit" circle @click="$router.push(`/note/edit/${note.id}`)" />
          <el-button size="small" :icon="Delete" type="danger" circle @click="handleDelete(note.id)" />
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && notes.length === 0" description="还没有发布笔记，快去创作吧~">
      <el-button type="primary" round @click="$router.push('/note/edit')">发布笔记</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import { getMyNotes, deleteNote } from '@/api/note'

const notes = ref([])
const loading = ref(true)
const defaultCover = 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800&q=80'

const getCoverUrl = (note) => {
  const url = note?.coverImage
  if (url && typeof url === 'string' && url.trim()) return url
  return defaultCover
}

const statusText = (s) => {
  const map = { 0: '待审核', 1: '已发布', 2: '已驳回' }
  return map[s] || '未知'
}

const statusType = (s) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[s] || 'info'
}

const stripHtml = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, '').substring(0, 100)
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const fetchNotes = async () => {
  loading.value = true
  try {
    const res = await getMyNotes()
    notes.value = res.data?.records || res.data || []
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇笔记吗？', '提示', { type: 'warning' })
    await deleteNote(id)
    ElMessage.success('删除成功')
    fetchNotes()
  } catch {
    /* ignore */
  }
}

onMounted(fetchNotes)
</script>

<style scoped>
.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--text-primary);
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.note-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  align-items: flex-start;
  transition: box-shadow 0.3s;
}

.note-item:hover {
  box-shadow: var(--shadow-hover);
}

.note-left {
  background: linear-gradient(135deg, #f0f7f4 0%, #e8f0ec 100%);
  border-radius: 8px;
}

.note-thumb {
  width: 140px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
  flex-shrink: 0;
  display: block;
}

.note-body {
  flex: 1;
  min-width: 0;
}

.note-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
  cursor: pointer;
}

.note-title:hover {
  color: var(--primary);
}

.note-summary {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.note-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.note-time {
  font-size: 12px;
  color: #b0b0b0;
}

.reject-reason {
  margin-top: 8px;
  padding: 8px 12px;
  background: #fff3f0;
  border-radius: 6px;
  font-size: 13px;
  color: var(--accent);
}

.note-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>
