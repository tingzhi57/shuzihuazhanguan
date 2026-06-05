<template>
  <div class="app-container">
    <el-button text @click="$router.back()" style="margin-bottom: 16px">
      <el-icon><ArrowLeft /></el-icon> 返回
    </el-button>

    <el-card shadow="never" v-loading="loading">
      <div class="martyr-header">
        <div class="martyr-avatar">
          <el-image v-if="martyr.photo" :src="martyr.photo" style="width: 100%; height: 100%" fit="cover" />
          <el-icon v-else :size="64" style="color: #c0c4cc"><User /></el-icon>
        </div>
        <div style="flex: 1; min-width: 0">
          <h2 class="martyr-name">{{ martyr.name }}</h2>
          <el-descriptions :column="3" border class="martyr-descriptions">
            <el-descriptions-item label="性别">{{ martyr.gender }}</el-descriptions-item>
            <el-descriptions-item label="民族">{{ martyr.ethnicity || '-' }}</el-descriptions-item>
            <el-descriptions-item label="政治面貌">{{ martyr.politicalStatus || '-' }}</el-descriptions-item>
            <el-descriptions-item label="出生日期">{{ martyr.birthDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="牺牲日期">{{ martyr.deathDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="籍贯">{{ martyr.birthplace || '-' }}</el-descriptions-item>
            <el-descriptions-item label="所属部队">{{ martyr.militaryUnit || '-' }}</el-descriptions-item>
            <el-descriptions-item label="职务/军衔">{{ martyr.rank || '-' }}</el-descriptions-item>
            <el-descriptions-item label="安葬地点">{{ martyr.burialLocation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="牺牲地点" :span="3">{{ martyr.sacrificeLocation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="牺牲原因" :span="3">{{ martyr.sacrificeReason || '-' }}</el-descriptions-item>
            <el-descriptions-item label="生平简介" :span="3">{{ martyr.description || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>

    <div style="margin-top: 16px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="事迹史料" name="deeds">
          <el-table :data="deeds" stripe v-if="deeds.length">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="deedType" label="类型" width="100" />
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="source" label="来源" />
          </el-table>
          <el-empty v-else description="暂无事迹史料" />
        </el-tab-pane>
        <el-tab-pane label="影像音视频" name="media">
          <el-table :data="mediaList" stripe v-if="mediaList.length">
            <el-table-column prop="title" label="标题" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="handlePreview(row)" v-if="row.filePath">
                  <el-icon><View /></el-icon> 查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无影像音视频" />
        </el-tab-pane>
        <el-tab-pane label="文物实物" name="relics">
          <el-table :data="relics" stripe v-if="relics.length">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="category" label="类别" width="100" />
            <el-table-column prop="era" label="年代" width="100" />
            <el-table-column prop="material" label="材质" width="80" />
          </el-table>
          <el-empty v-else description="暂无文物实物" />
        </el-tab-pane>
        <el-tab-pane label="荣誉纪念" name="honors">
          <el-table :data="honors" stripe v-if="honors.length">
            <el-table-column prop="honorName" label="荣誉名称" />
            <el-table-column prop="honorType" label="类型" width="100" />
            <el-table-column prop="issuingAuthority" label="颁发单位" />
            <el-table-column prop="issueDate" label="颁发日期" width="120" />
          </el-table>
          <el-empty v-else description="暂无荣誉纪念" />
        </el-tab-pane>
      </el-tabs>
    </div>

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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { martyrApi, deedApi, mediaApi, relicApi, honorApi } from '../../api'

const route = useRoute()
const loading = ref(false)
const martyr = ref({})
const activeTab = ref('deeds')
const deeds = ref([])
const mediaList = ref([])
const relics = ref([])
const honors = ref([])
const previewDialog = ref(false)
const previewItem = ref(null)

function formatSize(bytes) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
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

async function loadData() {
  loading.value = true
  try {
    const id = route.params.id
    martyr.value = await martyrApi.get(id)
    deeds.value = await deedApi.getByMartyr(id)
    mediaList.value = await mediaApi.getByMartyr(id)
    relics.value = await relicApi.getByMartyr(id)
    honors.value = await honorApi.getByMartyr(id)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
watch(() => route.params.id, loadData)
</script>

<style scoped>
.martyr-header {
  display: flex;
  gap: 24px;
}

.martyr-avatar {
  width: 160px;
  height: 200px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
  background: #f0f2f5;
}

.martyr-name {
  font-size: 24px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .martyr-header {
    flex-direction: column;
    align-items: center;
  }

  .martyr-avatar {
    width: 120px;
    height: 150px;
  }

  .martyr-name {
    font-size: 20px;
    text-align: center;
  }

  .martyr-descriptions :deep(.el-descriptions__table) {
    table-layout: auto;
  }
}
</style>
