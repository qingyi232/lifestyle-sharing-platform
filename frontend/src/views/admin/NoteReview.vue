<template>
  <div class="note-review">
    <!-- 标签页 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="待审核" name="pending" />
      <el-tab-pane label="全部笔记" name="all" />
    </el-tabs>

    <!-- 笔记列表 -->
    <div class="card" style="padding: 0;">
      <el-table :data="notes" v-loading="loading" style="width: 100%" stripe>
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <img :src="row.coverImage || defaultCover" class="note-cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180">
          <template #default="{ row }">
            <span class="note-title-link" @click="previewNote(row)">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column label="作者" width="120">
          <template #default="{ row }">
            {{ row.authorName || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="状态" width="90" v-if="activeTab === 'all'">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small" effect="plain">
              {{ statusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="previewNote(row)">预览</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" text @click="handleApprove(row)">通过</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" text @click="handleReject(row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="previewData?.title" width="680px" top="5vh">
      <div v-if="previewData" class="preview-content">
        <img v-if="previewData.coverImage" :src="previewData.coverImage" class="preview-cover" />
        <div class="preview-meta">
          <span>作者：{{ previewData.authorName }}</span>
          <span>分类：{{ previewData.categoryName }}</span>
        </div>
        <div class="preview-body" v-html="previewData.content"></div>
      </div>
    </el-dialog>

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectVisible" title="驳回原因" width="480px">
      <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入驳回原因..." />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="rejecting" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingNotes, getAllAdminNotes, approveNote, rejectNote } from '@/api/admin'

const activeTab = ref('pending')
const notes = ref([])
const loading = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const previewVisible = ref(false)
const previewData = ref(null)
const rejectVisible = ref(false)
const rejectReason = ref('')
const rejectingNoteId = ref(null)
const rejecting = ref(false)
const defaultCover = 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?w=800'

const statusText = (s) => {
  const map = { 0: '待审核', 1: '已发布', 2: '已驳回' }
  return map[s] || '未知'
}

const statusType = (s) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[s] || 'info'
}

const formatDate = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const fetchNotes = async () => {
  loading.value = true
  try {
    const params = { page: currentPage.value, size: pageSize.value }
    const res = activeTab.value === 'pending'
      ? await getPendingNotes(params)
      : await getAllAdminNotes(params)
    notes.value = res.data?.records || res.data || []
    total.value = res.data?.total || notes.value.length
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  fetchNotes()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotes()
}

const previewNote = (row) => {
  previewData.value = row
  previewVisible.value = true
}

const handleApprove = async (row) => {
  try {
    await approveNote(row.id)
    ElMessage.success('审核通过')
    fetchNotes()
  } catch {
    /* ignore */
  }
}

const handleReject = (row) => {
  rejectingNoteId.value = row.id
  rejectReason.value = ''
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  rejecting.value = true
  try {
    await rejectNote(rejectingNoteId.value, { reason: rejectReason.value })
    ElMessage.success('已驳回')
    rejectVisible.value = false
    fetchNotes()
  } catch {
    /* ignore */
  } finally {
    rejecting.value = false
  }
}

onMounted(fetchNotes)
</script>

<style scoped>
.note-cover {
  width: 72px;
  height: 50px;
  object-fit: cover;
  border-radius: 6px;
}

.note-title-link {
  color: var(--primary);
  cursor: pointer;
}

.note-title-link:hover {
  text-decoration: underline;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}

.preview-content {
  max-height: 70vh;
  overflow-y: auto;
}

.preview-cover {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 16px;
}

.preview-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.preview-body {
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-primary);
}

.preview-body :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
</style>
