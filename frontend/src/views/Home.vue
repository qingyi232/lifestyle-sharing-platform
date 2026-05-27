<template>
  <div class="home-page">
    <div class="page-container">
      <!-- 分类标签栏 -->
      <div class="category-bar">
        <div class="category-scroll">
          <span
            class="category-tag"
            :class="{ active: activeCategoryId === null }"
            @click="selectCategory(null)"
          >
            全部
          </span>
          <span
            v-for="cat in categories"
            :key="cat.id"
            class="category-tag"
            :class="{ active: activeCategoryId === cat.id }"
            @click="selectCategory(cat.id)"
          >
            {{ cat.icon }} {{ cat.name }}
          </span>
        </div>
      </div>

      <!-- 排序选项 -->
      <div class="sort-bar">
        <span
          class="sort-item"
          :class="{ active: orderBy === 'latest' }"
          @click="changeOrder('latest')"
        >
          最新
        </span>
        <span
          class="sort-item"
          :class="{ active: orderBy === 'hot' }"
          @click="changeOrder('hot')"
        >
          最热
        </span>
        <span
          v-if="userStore.isLoggedIn"
          class="sort-item"
          :class="{ active: orderBy === 'following' }"
          @click="changeOrder('following')"
        >
          关注
        </span>
      </div>

      <!-- 笔记瀑布流 -->
      <div class="waterfall" v-loading="loading">
        <NoteCard v-for="note in notes" :key="note.id" :note="note" />
      </div>

      <el-empty v-if="!loading && notes.length === 0" description="暂无笔记，快来发布第一篇吧~" />

      <!-- 分页 -->
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NoteCard from '@/components/NoteCard.vue'
import { getNotes, getFollowingNotes } from '@/api/note'
import { getCategories } from '@/api/category'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const notes = ref([])
const categories = ref([])
const activeCategoryId = ref(null)
const orderBy = ref('latest')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const loading = ref(false)

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch {
    /* ignore */
  }
}

const fetchNotes = async () => {
  loading.value = true
  try {
    if (orderBy.value === 'following') {
      const res = await getFollowingNotes({ page: currentPage.value, size: pageSize.value })
      const allNotes = res.data?.records || res.data || []
      if (activeCategoryId.value) {
        notes.value = allNotes.filter(n => n.categoryId === activeCategoryId.value)
      } else {
        notes.value = allNotes
      }
      total.value = res.data?.total || notes.value.length
    } else {
      const params = {
        page: currentPage.value,
        size: pageSize.value,
        orderBy: orderBy.value
      }
      if (activeCategoryId.value) params.categoryId = activeCategoryId.value
      const res = await getNotes(params)
      notes.value = res.data?.records || res.data || []
      total.value = res.data?.total || notes.value.length
    }
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  activeCategoryId.value = id
  currentPage.value = 1
  fetchNotes()
}

const changeOrder = (order) => {
  orderBy.value = order
  currentPage.value = 1
  fetchNotes()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchNotes()
}

onMounted(() => {
  fetchCategories()
  fetchNotes()
})
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 60px);
}

.category-bar {
  margin-bottom: 20px;
  overflow-x: auto;
}

.category-scroll {
  display: flex;
  gap: 10px;
  padding: 4px 0;
  white-space: nowrap;
}

.category-tag {
  display: inline-block;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  color: var(--text-secondary);
  background: var(--card-bg);
  border: 1px solid var(--border);
  cursor: pointer;
  transition: all 0.3s;
  user-select: none;
}

.category-tag:hover {
  color: var(--primary);
  border-color: var(--primary-light);
}

.category-tag.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}

.sort-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.sort-item {
  font-size: 15px;
  color: var(--text-secondary);
  cursor: pointer;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.sort-item.active {
  color: var(--primary);
  font-weight: 600;
  border-bottom-color: var(--primary);
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
