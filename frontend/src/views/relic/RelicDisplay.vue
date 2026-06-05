<template>
  <div class="app-container">
    <div class="page-header">
      <h2>文物数据展示</h2>
      <div v-if="store.isAdmin">
        <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon> 新增文物</el-button>
      </div>
    </div>

    <div class="relic-gallery">
      <div class="relic-card" v-for="item in list" :key="item.id">
        <div class="relic-media" @click="handleView(item)">
          <template v-if="relicMediaMap[item.id]?.length">
            <img v-if="relicMediaMap[item.id][0].type === 'image'" :src="relicMediaMap[item.id][0].filePath" @error="onMediaError(item.id, 0)" />
            <div v-else-if="relicMediaMap[item.id][0].type === 'video'" class="media-type-overlay">
              <el-icon :size="36" style="color: #fff"><VideoCamera /></el-icon>
            </div>
            <div v-else class="media-type-overlay">
              <el-icon :size="36" style="color: #fff"><Headset /></el-icon>
            </div>
            <span class="media-count" v-if="relicMediaMap[item.id].length > 1">{{ relicMediaMap[item.id].length }}个媒体</span>
          </template>
          <el-icon v-else :size="48" style="color: #c0c4cc"><Collection /></el-icon>
        </div>
        <div class="relic-info">
          <h4>{{ item.name }}</h4>
          <p><strong>类别：</strong>{{ item.category || '-' }}</p>
          <p><strong>年代：</strong>{{ item.era || '-' }}</p>
          <p><strong>材质：</strong>{{ item.material || '-' }}</p>
          <p><strong>保存状态：</strong>
            <el-tag :type="item.preservationState === '完好' ? 'success' : item.preservationState === '破损' ? 'danger' : 'info'" size="small">
              {{ item.preservationState || '未知' }}
            </el-tag>
          </p>
          <p style="color: #909399; font-size: 12px; margin-top: 8px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">{{ item.description }}</p>
          <div class="media-thumbs" v-if="relicMediaMap[item.id]?.length > 1" @click="handleView(item)">
            <div v-for="(m, idx) in relicMediaMap[item.id].slice(1, 5)" :key="m.id" class="media-thumb">
              <img v-if="m.type === 'image'" :src="m.filePath" @error="onMediaThumbError($event)" />
              <el-icon v-else-if="m.type === 'video'" style="color: #e6a23c" :size="18"><VideoCamera /></el-icon>
              <el-icon v-else style="color: #409eff" :size="18"><Headset /></el-icon>
            </div>
            <span v-if="relicMediaMap[item.id].length > 5" class="media-more">+{{ relicMediaMap[item.id].length - 5 }}</span>
          </div>
          <div style="margin-top: 10px; display: flex; gap: 8px">
            <el-button size="small" type="primary" @click="handleView(item)">查看</el-button>
            <el-button size="small" @click="handleEdit(item)" v-if="store.isAdmin">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item)" v-if="store.isAdmin">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div style="display: flex; justify-content: center; margin-top: 20px">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" @current-change="loadData" layout="prev, pager, next, total" />
    </div>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑文物' : '新增文物'" width="600px" class="mobile-dialog">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-row :gutter="20" class="form-row">
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="类别"><el-input v-model="editForm.category" /></el-form-item></el-col>
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="材质"><el-input v-model="editForm.material" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20" class="form-row">
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="年代"><el-input v-model="editForm.era" /></el-form-item></el-col>
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="来源"><el-input v-model="editForm.origin" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="保存状态">
          <el-select v-model="editForm.preservationState">
            <el-option label="完好" value="完好" />
            <el-option label="较好" value="较好" />
            <el-option label="一般" value="一般" />
            <el-option label="破损" value="破损" />
          </el-select>
        </el-form-item>
        <el-form-item label="存放位置"><el-input v-model="editForm.location" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editForm.description" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialog" :title="detailItem?.name || '文物详情'" width="700px" destroy-on-close>
      <el-tabs v-model="detailTab">
        <el-tab-pane label="基本信息" name="info">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="名称">{{ detailItem?.name }}</el-descriptions-item>
            <el-descriptions-item label="类别">{{ detailItem?.category || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年代">{{ detailItem?.era || '-' }}</el-descriptions-item>
            <el-descriptions-item label="材质">{{ detailItem?.material || '-' }}</el-descriptions-item>
            <el-descriptions-item label="来源">{{ detailItem?.origin || '-' }}</el-descriptions-item>
            <el-descriptions-item label="保存状态">
              <el-tag :type="detailItem?.preservationState === '完好' ? 'success' : detailItem?.preservationState === '破损' ? 'danger' : 'info'" size="small">
                {{ detailItem?.preservationState || '未知' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="存放位置">{{ detailItem?.location || '-' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ detailItem?.description || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="影像音视频" name="media">
          <el-table :data="relicMedia" stripe v-loading="mediaLoading" v-if="relicMedia.length">
            <el-table-column label="预览" width="80">
              <template #default="{ row }">
                <el-image
                  v-if="row.type === 'image' && row.filePath"
                  :src="row.filePath"
                  style="width: 50px; height: 50px; border-radius: 4px"
                  fit="cover"
                  :preview-src-list="[row.filePath]"
                  preview-teleported
                >
                  <template #error>
                    <div style="width: 50px; height: 50px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
                      <el-icon :size="20"><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <div v-else-if="row.type === 'video'" style="width: 50px; height: 50px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
                  <el-icon :size="20" style="color: #e6a23c"><VideoCamera /></el-icon>
                </div>
                <div v-else style="width: 50px; height: 50px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; border-radius: 4px">
                  <el-icon :size="20" style="color: #409eff"><Headset /></el-icon>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="70">
              <template #default="{ row }">
                <el-tag :type="row.type === 'image' ? 'success' : row.type === 'video' ? 'warning' : 'info'" size="small">
                  {{ row.type === 'image' ? '图片' : row.type === 'video' ? '视频' : '音频' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="handleMediaPreview(row)" v-if="row.filePath">
                  <el-icon><View /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无关联的影像音视频" />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <el-dialog v-model="mediaPreviewDialog" :title="mediaPreviewItem?.title || '预览'" width="700px" destroy-on-close>
      <div style="display: flex; flex-direction: column; align-items: center">
        <img v-if="mediaPreviewItem?.type === 'image'" :src="mediaPreviewItem.filePath" style="max-width: 100%; max-height: 500px; border-radius: 4px" />
        <video v-else-if="mediaPreviewItem?.type === 'video'" :src="mediaPreviewItem.filePath" controls style="max-width: 100%; max-height: 500px; border-radius: 4px" />
        <audio v-else-if="mediaPreviewItem?.type === 'audio'" :src="mediaPreviewItem.filePath" controls style="width: 100%" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { relicApi, mediaApi } from '../../api'
import { useAppStore } from '../../stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'

const store = useAppStore()
const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(12)
const total = ref(0)
const showDialog = ref(false)
const saving = ref(false)
const editForm = ref({})
const detailDialog = ref(false)
const detailTab = ref('info')
const detailItem = ref(null)
const relicMedia = ref([])
const mediaLoading = ref(false)
const mediaPreviewDialog = ref(false)
const mediaPreviewItem = ref(null)
const relicMediaMap = reactive({})

async function loadData() {
  loading.value = true
  try {
    const res = await relicApi.list({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
    // Load media for all relics in parallel
    const ids = res.content.map(r => r.id)
    const results = await Promise.allSettled(ids.map(id => mediaApi.getByOwner('RELIC', id)))
    ids.forEach((id, i) => {
      if (results[i].status === 'fulfilled') {
        relicMediaMap[id] = results[i].value
      } else {
        relicMediaMap[id] = []
      }
    })
  } finally {
    loading.value = false
  }
}

function onMediaError(relicId, idx) {
  const media = relicMediaMap[relicId]
  if (media && media[idx]) {
    // Remove broken media and try next
    media.splice(idx, 1)
  }
}

function onMediaThumbError(e) {
  e.target.style.display = 'none'
}

function handleView(row) {
  detailItem.value = row
  detailTab.value = 'info'
  detailDialog.value = true
  relicMedia.value = []
  loadRelicMedia(row.id)
}

async function loadRelicMedia(relicId) {
  mediaLoading.value = true
  try {
    const res = await mediaApi.getByOwner('RELIC', relicId)
    relicMedia.value = res
  } finally {
    mediaLoading.value = false
  }
}

function handleMediaPreview(row) {
  mediaPreviewItem.value = row
  mediaPreviewDialog.value = true
}

function handleEdit(row) {
  editForm.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定删除文物「${row.name}」吗？`, '确认删除', { type: 'warning' })
    .then(async () => {
      await relicApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

async function handleSave() {
  saving.value = true
  try {
    if (editForm.value.id) {
      await relicApi.update(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await relicApi.create(editForm.value)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.relic-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.relic-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: box-shadow 0.2s;
  background: #fff;
}

.relic-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.relic-media {
  height: 180px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

@media (max-width: 768px) {
  .relic-media {
    height: 160px;
  }

  .relic-info {
    padding: 12px;
  }

  .relic-info h4 {
    font-size: 14px;
  }

  .relic-info p {
    font-size: 12px;
  }
}

.relic-media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.media-type-overlay {
  background: #303133;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-count {
  position: absolute;
  bottom: 6px;
  right: 6px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}

.relic-info {
  padding: 14px;
}

.relic-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.relic-info p {
  margin: 4px 0;
  font-size: 13px;
  color: #606266;
}

.media-thumbs {
  display: flex;
  gap: 4px;
  margin-top: 8px;
  cursor: pointer;
  align-items: center;
}

.media-thumb {
  width: 36px;
  height: 36px;
  border-radius: 4px;
  overflow: hidden;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e4e7ed;
}

.media-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.media-more {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

@media (max-width: 768px) {
  .form-row :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }

  .form-row {
    display: flex;
    flex-direction: column;
  }

  .form-row .el-form-item {
    margin-bottom: 0;
  }
}
</style>
