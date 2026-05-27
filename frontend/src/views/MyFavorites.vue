<template>
  <div class="favorites-page page-container">
    <h2 class="page-title">我的收藏</h2>

    <div class="waterfall" v-loading="loading">
      <NoteCard v-for="note in notes" :key="note.id" :note="note" />
    </div>

    <el-empty v-if="!loading && notes.length === 0" description="还没有收藏任何笔记哦~">
      <el-button type="primary" round @click="$router.push('/')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavoriteNotes } from '@/api/note'
import NoteCard from '@/components/NoteCard.vue'

const notes = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await getFavoriteNotes()
    notes.value = res.data?.records || res.data || []
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--text-primary);
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
</style>
