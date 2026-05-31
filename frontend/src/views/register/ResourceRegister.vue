<template>
  <div class="app-container">
    <div class="page-header">
      <h2>资源登记</h2>
    </div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="烈士基本信息" name="martyr">
          <el-form :model="martyrForm" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="姓名"><el-input v-model="martyrForm.name" /></el-form-item></el-col>
              <el-col :span="8">
                <el-form-item label="性别">
                  <el-select v-model="martyrForm.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8"><el-form-item label="民族"><el-input v-model="martyrForm.ethnicity" /></el-form-item></el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="出生日期"><el-date-picker v-model="martyrForm.birthDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="牺牲日期"><el-date-picker v-model="martyrForm.deathDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="政治面貌"><el-input v-model="martyrForm.politicalStatus" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="籍贯"><el-input v-model="martyrForm.birthplace" /></el-form-item>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="所属部队"><el-input v-model="martyrForm.militaryUnit" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="职务/军衔"><el-input v-model="martyrForm.rank" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="牺牲地点"><el-input v-model="martyrForm.sacrificeLocation" /></el-form-item>
            <el-form-item label="牺牲原因"><el-input v-model="martyrForm.sacrificeReason" type="textarea" /></el-form-item>
            <el-form-item label="安葬地点"><el-input v-model="martyrForm.burialLocation" /></el-form-item>
            <el-form-item label="生平简介"><el-input v-model="martyrForm.description" type="textarea" :rows="3" /></el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveMartyr" :loading="saving">提交登记</el-button>
              <el-button @click="resetMartyr">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="事迹史料" name="deed">
          <el-form :model="deedForm" label-width="100px">
            <el-form-item label="所属烈士">
              <el-select v-model="deedForm.martyrId" filterable placeholder="请搜索选择烈士">
                <el-option v-for="m in martyrOptions" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题"><el-input v-model="deedForm.title" /></el-form-item>
            <el-form-item label="类型">
              <el-select v-model="deedForm.deedType"><el-option label="事迹" value="事迹" /><el-option label="史料" value="史料" /></el-select>
            </el-form-item>
            <el-form-item label="内容"><el-input v-model="deedForm.content" type="textarea" :rows="5" /></el-form-item>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="来源"><el-input v-model="deedForm.source" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="日期"><el-date-picker v-model="deedForm.date" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
            </el-row>
            <el-form-item><el-button type="primary" @click="saveDeed" :loading="saving">提交登记</el-button></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="影像音视频" name="media">
          <el-form :model="mediaForm" label-width="100px">
            <el-form-item label="所属烈士">
              <el-select v-model="mediaForm.martyrId" filterable placeholder="请搜索选择烈士">
                <el-option v-for="m in martyrOptions" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="标题"><el-input v-model="mediaForm.title" /></el-form-item>
            <el-form-item label="类型">
              <el-select v-model="mediaForm.type"><el-option label="图片" value="image" /><el-option label="视频" value="video" /><el-option label="音频" value="audio" /></el-select>
            </el-form-item>
            <el-form-item label="上传文件">
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :show-file-list="true"
                :limit="1"
                :on-change="handleFileChange"
                :on-remove="handleFileRemove"
              >
                <el-button type="primary" plain>
                  <el-icon><Upload /></el-icon> 选择文件
                </el-button>
                <template #tip>
                  <span style="font-size: 12px; color: #909399">支持图片、视频、音频文件</span>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item label="文件路径" v-if="mediaForm.filePath">
              <el-tag closable @close="mediaForm.filePath = ''">{{ mediaForm.filePath }}</el-tag>
            </el-form-item>
            <el-form-item label="描述"><el-input v-model="mediaForm.description" type="textarea" /></el-form-item>
            <el-form-item v-if="uploadProgress > 0" style="margin-bottom: 12px">
              <el-progress :percentage="uploadProgress" :stroke-width="16" />
            </el-form-item>
            <el-form-item v-if="mediaForm.type === 'image' && mediaForm.martyrId">
              <el-checkbox v-model="mediaForm.isAvatar">设为烈士头像</el-checkbox>
            </el-form-item>
            <el-form-item><el-button type="primary" @click="saveMedia" :loading="saving">提交登记</el-button></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="文物实物" name="relic">
          <el-form :model="relicForm" label-width="100px">
            <el-form-item label="所属烈士">
              <el-select v-model="relicForm.martyrId" filterable placeholder="请搜索选择烈士">
                <el-option v-for="m in martyrOptions" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="名称"><el-input v-model="relicForm.name" /></el-form-item>
            <el-row :gutter="20">
              <el-col :span="8"><el-form-item label="类别"><el-input v-model="relicForm.category" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="材质"><el-input v-model="relicForm.material" /></el-form-item></el-col>
              <el-col :span="8"><el-form-item label="年代"><el-input v-model="relicForm.era" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="来源"><el-input v-model="relicForm.origin" /></el-form-item>
            <el-form-item label="保存状态"><el-input v-model="relicForm.preservationState" /></el-form-item>
            <el-form-item label="存放位置"><el-input v-model="relicForm.location" /></el-form-item>
            <el-form-item label="描述"><el-input v-model="relicForm.description" type="textarea" /></el-form-item>
            <el-form-item><el-button type="primary" @click="saveRelic" :loading="saving">提交登记</el-button></el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="荣誉纪念" name="honor">
          <el-form :model="honorForm" label-width="100px">
            <el-form-item label="所属烈士">
              <el-select v-model="honorForm.martyrId" filterable placeholder="请搜索选择烈士">
                <el-option v-for="m in martyrOptions" :key="m.id" :label="m.name" :value="m.id" />
              </el-select>
            </el-form-item>
            <el-form-item label="荣誉名称"><el-input v-model="honorForm.honorName" /></el-form-item>
            <el-row :gutter="20">
              <el-col :span="12"><el-form-item label="荣誉类型"><el-input v-model="honorForm.honorType" /></el-form-item></el-col>
              <el-col :span="12"><el-form-item label="颁发单位"><el-input v-model="honorForm.issuingAuthority" /></el-form-item></el-col>
            </el-row>
            <el-form-item label="颁发日期"><el-date-picker v-model="honorForm.issueDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
            <el-form-item label="描述"><el-input v-model="honorForm.description" type="textarea" /></el-form-item>
            <el-form-item><el-button type="primary" @click="saveHonor" :loading="saving">提交登记</el-button></el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { useRoute } from 'vue-router'
import { Upload } from '@element-plus/icons-vue'
import { martyrApi, deedApi, mediaApi, relicApi, honorApi } from '../../api'
import { ElMessage } from 'element-plus'

const activeTab = ref('martyr')
const saving = ref(false)
const martyrOptions = ref([])
const uploadRef = ref(null)
const selectedFile = ref(null)
const uploadProgress = ref(0)

const defaultMartyrForm = () => ({
  name: '', gender: '男', ethnicity: '', birthDate: '', deathDate: '',
  politicalStatus: '', birthplace: '', militaryUnit: '', rank: '',
  sacrificeLocation: '', sacrificeReason: '', burialLocation: '', description: ''
})

const martyrForm = reactive(defaultMartyrForm())
const deedForm = reactive({ martyrId: null, title: '', deedType: '事迹', content: '', source: '', date: '', author: '' })
const mediaForm = reactive({ martyrId: null, title: '', type: 'image', description: '', filePath: '', isAvatar: false })
const relicForm = reactive({ martyrId: null, name: '', category: '', material: '', era: '', origin: '', preservationState: '', location: '', description: '' })
const honorForm = reactive({ martyrId: null, honorName: '', honorType: '', issuingAuthority: '', issueDate: '', description: '' })

async function loadMartyrOptions() {
  const res = await martyrApi.list({ page: 0, size: 1000 })
  martyrOptions.value = res.content
}

async function saveMartyr() {
  saving.value = true
  try {
    await martyrApi.create({ ...martyrForm })
    ElMessage.success('烈士信息登记成功')
    Object.assign(martyrForm, defaultMartyrForm())
  } finally { saving.value = false }
}

async function saveDeed() {
  saving.value = true
  try {
    await deedApi.create({ ...deedForm })
    ElMessage.success('事迹史料登记成功')
    Object.assign(deedForm, { martyrId: null, title: '', deedType: '事迹', content: '', source: '', date: '', author: '' })
  } finally { saving.value = false }
}

function handleFileChange(file) {
  selectedFile.value = file.raw
}

function handleFileRemove() {
  selectedFile.value = null
  mediaForm.filePath = ''
}

async function saveMedia() {
  saving.value = true
  uploadProgress.value = 0
  try {
    if (selectedFile.value) {
      const formData = new FormData()
      formData.append('file', selectedFile.value)
      formData.append('title', mediaForm.title || selectedFile.value.name)
      formData.append('type', mediaForm.type)
      formData.append('description', mediaForm.description || '')
      if (mediaForm.martyrId) formData.append('martyrId', mediaForm.martyrId)
      const uploadRes = await mediaApi.upload(formData, (e) => {
        uploadProgress.value = Math.round((e.loaded / e.total) * 100)
      })
      if (mediaForm.isAvatar) {
        await mediaApi.setAvatar(uploadRes.id)
      }
    } else {
      const res = await mediaApi.create({ ...mediaForm })
      if (mediaForm.isAvatar && res.id) {
        await mediaApi.setAvatar(res.id)
      }
    }
    ElMessage.success('影像音视频登记成功')
    selectedFile.value = null
    uploadRef.value?.clearFiles()
    Object.assign(mediaForm, { martyrId: null, title: '', type: 'image', description: '', filePath: '', isAvatar: false })
  } finally { saving.value = false; setTimeout(() => { uploadProgress.value = 0 }, 1000) }
}

async function saveRelic() {
  saving.value = true
  try {
    await relicApi.create({ ...relicForm })
    ElMessage.success('文物登记成功')
    Object.assign(relicForm, { martyrId: null, name: '', category: '', material: '', era: '', origin: '', preservationState: '', location: '', description: '' })
  } finally { saving.value = false }
}

async function saveHonor() {
  saving.value = true
  try {
    await honorApi.create({ ...honorForm })
    ElMessage.success('荣誉纪念登记成功')
    Object.assign(honorForm, { martyrId: null, honorName: '', honorType: '', issuingAuthority: '', issueDate: '', description: '' })
  } finally { saving.value = false }
}

function resetMartyr() {
  Object.assign(martyrForm, defaultMartyrForm())
}

const route = useRoute()

function initTab() {
  const tab = route.query.tab
  if (tab && ['media', 'deed', 'relic', 'honor'].includes(tab)) {
    activeTab.value = tab
  }
}

watch(() => route.query.tab, initTab)
onMounted(() => {
  loadMartyrOptions()
  initTab()
})
</script>
