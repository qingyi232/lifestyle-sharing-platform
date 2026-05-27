<template>
  <div class="category-manage">
    <div class="manage-toolbar card">
      <span class="toolbar-title">分类列表</span>
      <el-button type="primary" :icon="Plus" @click="openDialog(null)">添加分类</el-button>
    </div>

    <div class="card" style="padding: 0;">
      <el-table :data="categories" v-loading="loading" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="图标" width="80">
          <template #default="{ row }">
            <span style="font-size: 20px;">{{ row.icon || '📁' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingCategory ? '编辑分类' : '添加分类'" width="460px">
      <el-form :model="form" label-width="80px" size="large">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="输入 emoji 图标，如 🍕" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getCategories } from '@/api/category'
import { createCategory, updateCategory, deleteCategory } from '@/api/admin'

const categories = ref([])
const loading = ref(true)
const dialogVisible = ref(false)
const editingCategory = ref(null)
const saving = ref(false)

const form = reactive({
  name: '',
  icon: '',
  sortOrder: 0
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const openDialog = (cat) => {
  editingCategory.value = cat
  if (cat) {
    form.name = cat.name
    form.icon = cat.icon || ''
    form.sortOrder = cat.sortOrder || 0
  } else {
    form.name = ''
    form.icon = ''
    form.sortOrder = 0
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  saving.value = true
  try {
    const data = { name: form.name, icon: form.icon, sortOrder: form.sortOrder }
    if (editingCategory.value) {
      await updateCategory(editingCategory.value.id, data)
      ElMessage.success('修改成功')
    } else {
      await createCategory(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchCategories()
  } catch {
    /* ignore */
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除分类 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch {
    /* ignore */
  }
}

onMounted(fetchCategories)
</script>

<style scoped>
.manage-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  margin-bottom: 16px;
}

.toolbar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
</style>
