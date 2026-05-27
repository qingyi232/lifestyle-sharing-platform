<template>
  <div class="comment-section">
    <h3 class="section-title">评论 ({{ comments.length }})</h3>

    <div v-if="userStore.isLoggedIn" class="comment-input-area">
      <el-avatar :size="36" :src="userStore.userInfo?.avatar || defaultAvatar" />
      <div class="input-wrapper">
        <el-input
          v-model="newComment"
          type="textarea"
          :rows="2"
          placeholder="写下你的评论..."
          resize="none"
        />
        <el-button type="primary" size="small" round :disabled="!newComment.trim()" @click="submitComment(null)">
          发表评论
        </el-button>
      </div>
    </div>
    <div v-else class="login-hint">
      <router-link to="/login">登录</router-link> 后参与评论
    </div>

    <div class="comment-list">
      <div v-for="comment in topLevelComments" :key="comment.id" class="comment-item">
        <div class="comment-main">
          <router-link :to="`/user/${comment.userId}`">
            <el-avatar :size="36" :src="comment.userAvatar || defaultAvatar" />
          </router-link>
          <div class="comment-content">
            <div class="comment-header">
              <router-link :to="`/user/${comment.userId}`" class="comment-author">
                {{ comment.userNickname || comment.username || '用户' }}
              </router-link>
              <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
            <div class="comment-actions">
              <span class="action-btn" @click="toggleReply(comment.id)">回复</span>
              <span
                v-if="comment.userId === userStore.userInfo?.id"
                class="action-btn delete-btn"
                @click="handleDelete(comment.id)"
              >
                删除
              </span>
            </div>

            <div v-if="replyingTo === comment.id" class="reply-input-area">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                :placeholder="`回复 ${comment.userNickname || comment.username || '用户'}...`"
                resize="none"
              />
              <div class="reply-actions">
                <el-button size="small" @click="replyingTo = null">取消</el-button>
                <el-button type="primary" size="small" round :disabled="!replyContent.trim()" @click="submitComment(comment.id)">
                  回复
                </el-button>
              </div>
            </div>

            <div v-if="getChildren(comment.id).length" class="child-comments">
              <div v-for="child in getChildren(comment.id)" :key="child.id" class="comment-item child">
                <router-link :to="`/user/${child.userId}`">
                  <el-avatar :size="28" :src="child.userAvatar || defaultAvatar" />
                </router-link>
                <div class="comment-content">
                  <div class="comment-header">
                    <router-link :to="`/user/${child.userId}`" class="comment-author">
                      {{ child.userNickname || child.username || '用户' }}
                    </router-link>
                    <span class="comment-time">{{ formatTime(child.createdAt) }}</span>
                  </div>
                  <p class="comment-text">
                    <span v-if="child.replyToNickname" class="reply-to">
                      回复 <em>@{{ child.replyToNickname }}</em>:
                    </span>
                    {{ child.content }}
                  </p>
                  <div class="comment-actions">
                    <span class="action-btn" @click="toggleReply(child.id)">回复</span>
                    <span
                      v-if="child.userId === userStore.userInfo?.id"
                      class="action-btn delete-btn"
                      @click="handleDelete(child.id)"
                    >
                      删除
                    </span>
                  </div>

                  <div v-if="replyingTo === child.id" class="reply-input-area">
                    <el-input
                      v-model="replyContent"
                      type="textarea"
                      :rows="2"
                      :placeholder="`回复 ${child.userNickname || child.username || '用户'}...`"
                      resize="none"
                    />
                    <div class="reply-actions">
                      <el-button size="small" @click="replyingTo = null">取消</el-button>
                      <el-button type="primary" size="small" round :disabled="!replyContent.trim()" @click="submitComment(comment.id)">
                        回复
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无评论，来发表第一条评论吧" :image-size="80" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getCommentsByNoteId, createComment, deleteComment } from '@/api/comment'

const props = defineProps({
  noteId: { type: [Number, String], required: true }
})

const userStore = useUserStore()
const comments = ref([])
const newComment = ref('')
const replyingTo = ref(null)
const replyContent = ref('')
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const topLevelComments = computed(() => comments.value.filter(c => !c.parentId))

const getChildren = (parentId) => comments.value.filter(c => c.parentId === parentId)

const toggleReply = (id) => {
  replyingTo.value = replyingTo.value === id ? null : id
  replyContent.value = ''
}

const fetchComments = async () => {
  try {
    const res = await getCommentsByNoteId(props.noteId)
    comments.value = res.data || []
  } catch {
    /* ignore */
  }
}

const submitComment = async (parentId) => {
  const content = parentId ? replyContent.value.trim() : newComment.value.trim()
  if (!content) return
  try {
    await createComment({ noteId: props.noteId, content, parentId })
    ElMessage.success('评论成功')
    newComment.value = ''
    replyContent.value = ''
    replyingTo.value = null
    fetchComments()
  } catch {
    /* ignore */
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' })
    await deleteComment(id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch {
    /* ignore */
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = (now - d) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)}天前`
  return d.toLocaleDateString()
}

fetchComments()

defineExpose({ fetchComments })
</script>

<style scoped>
.comment-section {
  margin-top: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: var(--text-primary);
}

.comment-input-area {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.input-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: 10px;
  background: var(--bg-light);
  border: 1px solid transparent;
}

.input-wrapper :deep(.el-textarea__inner:focus) {
  border-color: var(--primary-light);
  background: #fff;
}

.login-hint {
  padding: 16px;
  text-align: center;
  color: var(--text-secondary);
  background: var(--bg-light);
  border-radius: var(--radius);
  margin-bottom: 24px;
}

.login-hint a {
  color: var(--primary);
  font-weight: 500;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.comment-author:hover {
  color: var(--primary);
}

.comment-time {
  font-size: 12px;
  color: #b0b0b0;
}

.comment-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 8px;
}

.reply-to {
  color: var(--primary);
  margin-right: 4px;
}

.reply-to em {
  font-style: normal;
  font-weight: 500;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-btn {
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover {
  color: var(--primary);
}

.delete-btn:hover {
  color: var(--accent) !important;
}

.reply-input-area {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reply-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.child-comments {
  margin-top: 12px;
  padding-left: 12px;
  border-left: 2px solid var(--primary-light);
}

.child-comments .comment-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
}

.child-comments .comment-item:last-child {
  border-bottom: none;
}
</style>
