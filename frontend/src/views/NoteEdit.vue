<template>
  <div class="edit-page page-container">
    <div class="edit-card card">
      <h2 class="edit-title">{{ isEdit ? '编辑笔记' : '发布笔记' }}</h2>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <!-- 封面图片 -->
        <el-form-item label="封面图片" prop="coverImage">
          <el-upload
            class="cover-uploader"
            :show-file-list="false"
            :http-request="handleCoverUpload"
            accept="image/*"
          >
            <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon :size="40"><Plus /></el-icon>
              <span>点击上传封面</span>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 标题 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="给你的笔记起个标题" maxlength="100" show-word-limit />
        </el-form-item>

        <!-- 富文本编辑器 -->
        <el-form-item label="正文内容" prop="content">
          <div class="editor-wrapper">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #e8e8e8;" />
            <Editor
              v-model="form.content"
              :defaultConfig="editorConfig"
              style="min-height: 350px;"
              @onCreated="handleEditorCreated"
            />
          </div>
        </el-form-item>

        <!-- 分类 -->
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id">
              {{ cat.icon }} {{ cat.name }}
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 标签 -->
        <el-form-item label="标签">
          <div class="tags-input">
            <el-tag
              v-for="tag in form.tagList"
              :key="tag"
              closable
              round
              @close="removeTag(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="tagInputVisible"
              ref="tagInputRef"
              v-model="tagInputValue"
              size="small"
              style="width: 120px"
              @keyup.enter="addTag"
              @blur="addTag"
            />
            <el-button v-else size="small" round @click="showTagInput">+ 添加标签</el-button>
          </div>
        </el-form-item>

        <!-- 更多图片 -->
        <el-form-item label="更多图片">
          <el-upload
            v-model:file-list="imageFileList"
            list-type="picture-card"
            :http-request="handleImageUpload"
            accept="image/*"
          >
            <el-icon :size="24"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <div class="submit-bar">
            <el-button @click="$router.back()">取消</el-button>
            <el-button type="primary" round :loading="submitting" @click="handleSubmit">
              {{ isEdit ? '保存修改' : '发布笔记' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onBeforeUnmount, shallowRef, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { getNoteById, createNote, updateNote } from '@/api/note'
import { getCategories } from '@/api/category'
import { uploadFile, uploadUrlFromResult } from '@/api/upload'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const formRef = ref(null)
const editorRef = shallowRef(null)
const submitting = ref(false)
const categories = ref([])
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref(null)
const imageFileList = ref([])

const form = reactive({
  title: '',
  content: '',
  coverImage: '',
  categoryId: '',
  tagList: [],
  images: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const toolbarConfig = {}
const editorConfig = {
  placeholder: '分享你的生活方式...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file, insertFn) {
        try {
          const res = await uploadFile(file)
          const url = uploadUrlFromResult(res)
          if (!url) {
            ElMessage.error('上传成功但未返回图片地址')
            return
          }
          insertFn(url, '', '')
        } catch {
          ElMessage.error('图片上传失败，请确认已登录后重试')
        }
      }
    }
  }
}

const handleEditorCreated = (editor) => {
  editorRef.value = editor
}

const handleCoverUpload = async ({ file }) => {
  try {
    const res = await uploadFile(file)
    const url = uploadUrlFromResult(res)
    if (!url) {
      ElMessage.error('上传成功但未返回图片地址')
      return
    }
    form.coverImage = url
  } catch {
    ElMessage.error('封面上传失败，请确认已登录后重试')
  }
}

const handleImageUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadFile(file)
    const url = uploadUrlFromResult(res)
    if (!url) {
      onError(new Error('no url'))
      ElMessage.error('上传成功但未返回图片地址')
      return
    }
    form.images.push(url)
    onSuccess(res)
  } catch (e) {
    onError(e)
    ElMessage.error('图片上传失败，请确认已登录后重试')
  }
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => tagInputRef.value?.input?.focus())
}

const addTag = () => {
  const val = tagInputValue.value.trim()
  if (val && !form.tagList.includes(val)) {
    form.tagList.push(val)
  }
  tagInputVisible.value = false
  tagInputValue.value = ''
}

const removeTag = (tag) => {
  form.tagList = form.tagList.filter(t => t !== tag)
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = {
      title: form.title,
      content: form.content,
      coverImage: form.coverImage,
      categoryId: form.categoryId,
      tags: form.tagList,
      images: JSON.stringify(form.images)
    }
    if (isEdit.value) {
      await updateNote(route.params.id, data)
      ElMessage.success('修改成功')
    } else {
      await createNote(data)
      ElMessage.success('发布成功')
    }
    router.push('/my-notes')
  } catch {
    /* ignore */
  } finally {
    submitting.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch {
    /* ignore */
  }
}

const fetchNote = async () => {
  if (!isEdit.value) return
  try {
    const res = await getNoteById(route.params.id)
    const n = res.data
    form.title = n.title
    form.content = n.content
    form.coverImage = n.coverImage
    form.categoryId = n.categoryId
    form.tagList = n.tags ? (typeof n.tags === 'string' ? JSON.parse(n.tags) : n.tags) : []
    form.images = n.images ? (typeof n.images === 'string' ? JSON.parse(n.images) : n.images) : []
    imageFileList.value = form.images.map((url, i) => ({ name: `image-${i}`, url }))
  } catch {
    /* ignore */
  }
}

onMounted(() => {
  fetchCategories()
  fetchNote()
})

onBeforeUnmount(() => {
  if (editorRef.value) editorRef.value.destroy()
})
</script>

<style scoped>
.edit-page {
  max-width: 800px;
}

.edit-card {
  padding: 40px;
}

.edit-title {
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 28px;
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
  border: 2px dashed var(--border);
  border-radius: var(--radius);
  cursor: pointer;
  transition: border-color 0.3s;
  overflow: hidden;
}

.cover-uploader :deep(.el-upload:hover) {
  border-color: var(--primary);
}

.cover-preview {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-secondary);
  gap: 8px;
}

.editor-wrapper {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.submit-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  width: 100%;
}

.edit-card :deep(.el-input__wrapper),
.edit-card :deep(.el-select .el-input__wrapper) {
  border-radius: 10px;
}
</style>
