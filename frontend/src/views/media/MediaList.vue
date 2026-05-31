<template>
  <div class="app-container">
    <div class="page-header">
      <h2>影像音视频</h2>
      <div v-if="store.isAdmin">
        <el-button type="primary" @click="$router.push('/register?tab=media')"><el-icon><Plus /></el-icon> 新增媒体</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column label="预览" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.type === 'image' && row.filePath"
              :src="row.filePath"
              style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer"
              fit="cover"
              :preview-src-list="[row.filePath]"
              preview-teleported
            >
              <template #error>
                <div style="width: 60px; height: 60px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
                  <el-icon :size="24"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else-if="row.type === 'video'" style="width: 60px; height: 60px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
              <el-icon :size="24" style="color: #e6a23c"><VideoCamera /></el-icon>
            </div>
            <div v-else style="width: 60px; height: 60px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
              <el-icon :size="24" style="color: #409eff"><Headset /></el-icon>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column label="所属类型" width="90">
          <template #default="{ row }">
            <el-tag :type="row.ownerType === 'MARTYR' ? 'danger' : 'warning'" size="small">
              {{ row.ownerType === 'MARTYR' ? '烈士' : '文物' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属对象" width="120">
          <template #default="{ row }">
            {{ row.ownerType === 'MARTYR' ? (martyrMap[row.ownerId] || '-') : (relicMap[row.ownerId] || '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'image' ? 'success' : row.type === 'video' ? 'warning' : 'info'" size="small">
              {{ row.type === 'image' ? '图片' : row.type === 'video' ? '视频' : '音频' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ formatSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="设为头像" width="100" v-if="store.isAdmin">
          <template #default="{ row }">
            <el-checkbox
              v-if="row.type === 'image' && row.ownerType === 'MARTYR' && row.ownerId"
              :model-value="row.isAvatar"
              @change="handleAvatarChange(row, $event)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handlePreview(row)" v-if="row.filePath">
              <el-icon><View /></el-icon> 查看
            </el-button>
            <el-button size="small" type="primary" @click="handleEdit(row)" v-if="store.isAdmin">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="store.isAdmin">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: center; margin-top: 20px">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" @current-change="loadData" layout="prev, pager, next, total" />
      </div>
    </el-card>

    <el-dialog v-model="previewDialog" :title="previewItem?.title || '预览'" width="700px" destroy-on-close>
      <div style="display: flex; flex-direction: column; align-items: center">
        <img v-if="previewItem?.type === 'image'" :src="previewItem.filePath" style="max-width: 100%; max-height: 500px; border-radius: 4px" />
        <video v-else-if="previewItem?.type === 'video'" :src="previewItem.filePath" controls style="max-width: 100%; max-height: 500px; border-radius: 4px" />
        <audio v-else-if="previewItem?.type === 'audio'" :src="previewItem.filePath" controls style="width: 100%" />
        <div style="margin-top: 16px; color: #606266; width: 100%">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="标题">{{ previewItem?.title }}</el-descriptions-item>
            <el-descriptions-item label="大小">{{ formatSize(previewItem?.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ previewItem?.uploadDate?.replace('T', ' ') }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ previewItem?.description || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      <template #footer>
        <el-button type="primary" @click="handleDownload" v-if="previewItem?.filePath">
          <el-icon><Download /></el-icon> 下载文件
        </el-button>
        <el-button @click="previewDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑媒体' : '新增媒体'" width="600px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="所属类型">
          <el-select v-model="editForm.ownerType" style="width: 100%">
            <el-option label="烈士" value="MARTYR" />
            <el-option label="文物" value="RELIC" />
          </el-select>
        </el-form-item>
        <el-form-item :label="editForm.ownerType === 'MARTYR' ? '所属烈士' : '所属文物'">
          <el-select v-model="editForm.ownerId" filterable placeholder="请搜索选择" clearable style="width: 100%">
            <el-option v-for="m in (editForm.ownerType === 'MARTYR' ? martyrOptions : relicOptions)" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.type">
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { mediaApi, martyrApi, relicApi } from '../../api'
import { useAppStore } from '../../stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useAppStore()
const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const showDialog = ref(false)
const saving = ref(false)
const editForm = ref({})
const previewDialog = ref(false)
const previewItem = ref(null)
const martyrOptions = ref([])
const martyrMap = reactive({})
const relicOptions = ref([])
const relicMap = reactive({})

function formatSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function loadData() {
  loading.value = true
  try {
    const res = await mediaApi.list({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function handlePreview(row) {
  previewItem.value = row
  previewDialog.value = true
}

function handleDownload() {
  if (previewItem.value?.filePath) {
    window.open(previewItem.value.filePath, '_blank')
  }
}

async function handleAvatarChange(row, checked) {
  try {
    if (checked) {
      await mediaApi.setAvatar(row.id)
      ElMessage.success('已设为头像')
    } else {
      await mediaApi.clearAvatar(row.id)
      ElMessage.success('已取消头像')
    }
    loadData()
  } catch {}
}

function handleEdit(row) {
  editForm.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该媒体资源吗？', '确认删除', { type: 'warning' })
    .then(async () => {
      await mediaApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

async function handleSave() {
  saving.value = true
  try {
    if (editForm.value.id) {
      await mediaApi.update(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await mediaApi.create(editForm.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

async function loadMartyrOptions() {
  try {
    const res = await martyrApi.list({ page: 0, size: 1000 })
    martyrOptions.value = res.content
    res.content.forEach(m => { martyrMap[m.id] = m.name })
  } catch {}
}

async function loadRelicOptions() {
  try {
    const res = await relicApi.list({ page: 0, size: 1000 })
    relicOptions.value = res.content
    res.content.forEach(r => { relicMap[r.id] = r.name })
  } catch {}
}

onMounted(() => {
  loadData()
  loadMartyrOptions()
  loadRelicOptions()
})
</script>
