<template>
  <div class="user-manage">
    <!-- 搜索 -->
    <div class="manage-toolbar card">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名、昵称..."
        clearable
        style="width: 300px"
        :prefix-icon="Search"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 表格 -->
    <div class="card" style="padding: 0;">
      <el-table :data="users" v-loading="loading" style="width: 100%" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar || defaultAvatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" min-width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="110" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'info'" size="small" effect="plain">
              {{ row.role === 1 ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              text
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="primary" text @click="handleResetPwd(row)">
              重置密码
            </el-button>
            <el-button
              v-if="row.username !== 'admin'"
              size="small"
              :type="row.role === 1 ? 'info' : 'danger'"
              text
              @click="handleToggleRole(row)"
            >
              {{ row.role === 1 ? '取消管理员' : '设为管理员' }}
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getAdminUsers, updateUserStatus, resetUserPassword, updateUserRole } from '@/api/admin'

const users = ref([])
const keyword = ref('')
const loading = ref(true)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const formatDate = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getAdminUsers({ page: currentPage.value, size: pageSize.value, keyword: keyword.value })
    users.value = res.data?.records || res.data || []
    total.value = res.data?.total || users.value.length
  } catch {
    /* ignore */
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 "${row.username}" 吗？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch {
    /* ignore */
  }
}

const handleToggleRole = async (row) => {
  const newRole = row.role === 1 ? 0 : 1
  const action = newRole === 1 ? '设为管理员' : '取消管理员'
  try {
    await ElMessageBox.confirm(`确定要将用户 "${row.username}" ${action}吗？`, '提示', { type: 'warning' })
    await updateUserRole(row.id, { role: newRole })
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch {
    /* ignore */
  }
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', { type: 'warning' })
    await resetUserPassword(row.id)
    ElMessage.success('密码重置成功')
  } catch {
    /* ignore */
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.manage-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 20px 0;
}
</style>
