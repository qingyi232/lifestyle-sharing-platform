<template>
  <div class="settings-page page-container">
    <h2 class="page-title">账号设置</h2>

    <div class="settings-sections">
      <!-- 基本信息 -->
      <div class="settings-card card">
        <h3 class="card-title">基本信息</h3>

        <div class="avatar-section">
          <el-avatar :key="profileForm.avatar" :size="72" :src="profileForm.avatar || defaultAvatar" />
          <el-upload :show-file-list="false" :http-request="handleAvatarUpload" accept="image/*">
            <el-button size="small" round>更换头像</el-button>
          </el-upload>
        </div>

        <el-form :model="profileForm" label-width="80px" label-position="left" size="large">
          <el-form-item label="昵称">
            <el-input v-model="profileForm.nickname" placeholder="输入新昵称" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="profileForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己..." resize="none" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" round :loading="saving" @click="handleSaveProfile">保存修改</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 修改密码 -->
      <div class="settings-card card">
        <h3 class="card-title">修改密码</h3>
        <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" label-position="left" size="large">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" round :loading="changingPwd" @click="handleChangePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 绑定信息 -->
      <div class="settings-card card">
        <h3 class="card-title">绑定信息</h3>
        <el-form :model="bindForm" label-width="80px" label-position="left" size="large">
          <el-form-item label="邮箱">
            <el-input v-model="bindForm.email" placeholder="绑定邮箱地址" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="bindForm.phone" placeholder="绑定手机号码" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" round :loading="savingBind" @click="handleSaveBind">保存绑定</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 危险区域 -->
      <div class="settings-card card danger-card">
        <h3 class="card-title danger">危险操作</h3>
        <p class="danger-desc">注销账号后，你的所有数据将被永久删除，此操作不可撤销。</p>
        <el-button type="danger" round @click="handleDeleteAccount">注销账号</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getProfile, updateProfile, updatePassword, deleteAccount } from '@/api/user'
import { uploadFile, uploadUrlFromResult } from '@/api/upload'

const router = useRouter()
const userStore = useUserStore()
const saving = ref(false)
const changingPwd = ref(false)
const savingBind = ref(false)
const pwdFormRef = ref(null)
const defaultAvatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop'

const profileForm = reactive({
  nickname: '',
  bio: '',
  avatar: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const bindForm = reactive({
  email: '',
  phone: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const handleAvatarUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file)
    const url = uploadUrlFromResult(res)
    if (!url) {
      ElMessage.error('上传成功但未返回图片地址')
      return
    }
    profileForm.avatar = url
    const upd = await updateProfile({ avatar: url })
    userStore.setUserInfo(upd.data)
    ElMessage.success('头像已更新')
  } catch {
    ElMessage.error('头像上传失败，请确认已登录后重试')
  }
}

const handleSaveProfile = async () => {
  saving.value = true
  try {
    await updateProfile({
      nickname: profileForm.nickname,
      bio: profileForm.bio,
      avatar: profileForm.avatar
    })
    ElMessage.success('保存成功')
    const res = await getProfile()
    userStore.setUserInfo(res.data)
  } catch {
    /* ignore */
  } finally {
    saving.value = false
  }
}

const handleChangePassword = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    router.push('/login')
  } catch {
    /* ignore */
  } finally {
    changingPwd.value = false
  }
}

const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '注销账号后，你的所有数据将被永久删除，此操作不可撤销！确定要注销吗？',
      '⚠️ 注销账号',
      { type: 'error', confirmButtonText: '确定注销', cancelButtonText: '取消' }
    )
    await deleteAccount()
    ElMessage.success('账号已注销')
    userStore.logout()
    router.push('/')
  } catch {
    /* ignore */
  }
}

const handleSaveBind = async () => {
  savingBind.value = true
  try {
    await updateProfile({
      email: bindForm.email || '',
      phone: bindForm.phone || ''
    })
    ElMessage.success('绑定信息已更新')
    const res = await getProfile()
    userStore.setUserInfo(res.data)
  } catch {
    /* ignore */
  } finally {
    savingBind.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getProfile()
    const data = res.data
    profileForm.nickname = data.nickname || ''
    profileForm.bio = data.bio || ''
    profileForm.avatar = data.avatar || ''
    bindForm.email = data.email || ''
    bindForm.phone = data.phone || ''
  } catch {
    /* ignore */
  }
})
</script>

<style scoped>
.settings-page {
  max-width: 720px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 28px;
  color: var(--text-primary);
  text-align: center;
}

.settings-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.settings-card {
  padding: 32px 36px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title.danger {
  color: #e07a5f;
  border-bottom-color: #fdd;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
  padding: 16px 20px;
  background: var(--bg-light, #f9f9f7);
  border-radius: 12px;
}

.settings-card :deep(.el-form-item__label) {
  font-weight: 500;
}

.settings-card :deep(.el-input__wrapper),
.settings-card :deep(.el-textarea__inner) {
  border-radius: 10px;
}

.settings-card :deep(.el-form-item) {
  margin-bottom: 22px;
}

.danger-card {
  border: 1px solid #fdd;
  background: #fffbfa;
}

.danger-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
  line-height: 1.6;
}
</style>
