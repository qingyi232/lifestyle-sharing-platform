<template>
  <div class="search-page page-container">
    <div class="search-header">
      <el-input
        v-model="keyword"
        placeholder="搜索笔记..."
        size="large"
        clearable
        :prefix-icon="SearchIcon"
        @keyup.enter="handleSearch"
        class="search-input"
      />
    </div>

    <div v-if="keyword" class="search-info">
      搜索 "<span class="text-primary">{{ keyword }}</span>" 的结果，共 {{ total }} 条
    </div>

    <div class="waterfall" v-loading="loading">
      <NoteCard v-for="note in notes" :key="note.id" :note="note" />
    </div>

    <el-empty v-if="!loading && notes.length === 0 && keyword" description="没有找到相关笔记" />

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
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search as SearchIcon } from '@element-plus/icons-vue'
import NoteCard from '@/components/NoteCard.vue'
import { getNotes } from '@/api/note'

const route = useRoute()
const router = useRouter()
const keyword = ref(route.query.keyword || '')
const notes = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const fetchNotes = async () => {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const res = await getNotes({ keyword: keyword.value, page: currentPage.value, size: pageSize.value })
    notes.value = res.data?.records || res.data || []
    total.value = res.data?.total || notes.value.length
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  router.replace({ query: { keyword: keyword.value } })
  fetchNotes()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotes()
}

watch(() => route.query.keyword, (val) => {
  if (val) {
    keyword.value = val
    fetchNotes()
  }
})

onMounted(() => {
  if (keyword.value) fetchNotes()
})
</script>

<style scoped>
.search-header {
  max-width: 600px;
  margin: 0 auto 28px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 24px;
  box-shadow: var(--shadow) !important;
  padding: 4px 16px;
}

.search-info {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 20px;
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
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 32px 0;
}
</style>
