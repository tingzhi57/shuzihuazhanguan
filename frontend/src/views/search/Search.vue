<template>
  <div class="app-container">
    <div class="page-header">
      <h2>资源查询</h2>
    </div>

    <el-card shadow="never">
      <el-row :gutter="16">
        <el-col :span="8">
          <el-input v-model="keyword" placeholder="输入关键词搜索..." size="large" clearable @keyup.enter="handleSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="searchType" placeholder="搜索范围" size="large" clearable style="width: 100%">
            <el-option label="全部" value="" />
            <el-option label="烈士信息" value="martyrs" />
            <el-option label="事迹史料" value="deeds" />
            <el-option label="影像音视频" value="media" />
            <el-option label="文物实物" value="relics" />
            <el-option label="荣誉纪念" value="honors" />
          </el-select>
        </el-col>
        <el-col :span="2">
          <el-button type="primary" size="large" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px" v-if="keyword">
      <template #header>搜索结果：共 {{ totalCount }} 条</template>

      <div v-if="filteredMartyrs.length && (!searchType || searchType === 'martyrs')">
        <h4 style="margin: 12px 0 8px; color: #409eff">烈士信息</h4>
        <el-table :data="filteredMartyrs" stripe @row-click="goToMartyr" style="cursor: pointer">
          <el-table-column prop="name" label="姓名" width="120" />
          <el-table-column prop="gender" label="性别" width="60" />
          <el-table-column prop="birthDate" label="出生日期" width="120" />
          <el-table-column prop="birthplace" label="籍贯" />
          <el-table-column prop="militaryUnit" label="所属部队" />
          <el-table-column prop="rank" label="职务" />
        </el-table>
      </div>

      <div v-if="filteredDeeds.length && (!searchType || searchType === 'deeds')">
        <h4 style="margin: 16px 0 8px; color: #67c23a">事迹史料</h4>
        <el-table :data="filteredDeeds" stripe>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="deedType" label="类型" width="80" />
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="source" label="来源" />
        </el-table>
      </div>

      <div v-if="filteredMedia.length && (!searchType || searchType === 'media')">
        <h4 style="margin: 16px 0 8px; color: #e6a23c">影像音视频</h4>
        <el-table :data="filteredMedia" stripe>
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="type" label="类型" width="80" />
        </el-table>
      </div>

      <div v-if="filteredRelics.length && (!searchType || searchType === 'relics')">
        <h4 style="margin: 16px 0 8px; color: #f56c6c">文物实物</h4>
        <el-table :data="filteredRelics" stripe>
          <el-table-column prop="name" label="名称" />
          <el-table-column prop="category" label="类别" width="100" />
          <el-table-column prop="era" label="年代" width="100" />
          <el-table-column prop="material" label="材质" width="80" />
        </el-table>
      </div>

      <div v-if="filteredHonors.length && (!searchType || searchType === 'honors')">
        <h4 style="margin: 16px 0 8px; color: #909399">荣誉纪念</h4>
        <el-table :data="filteredHonors" stripe>
          <el-table-column prop="honorName" label="荣誉名称" />
          <el-table-column prop="honorType" label="类型" width="100" />
          <el-table-column prop="issuingAuthority" label="颁发单位" />
        </el-table>
      </div>

      <el-empty v-if="totalCount === 0" description="未找到相关资源" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { martyrApi, deedApi, mediaApi, relicApi, honorApi } from '../../api'

const router = useRouter()
const keyword = ref('')
const searchType = ref('')
const loading = ref(false)

const martyrs = ref([])
const deeds = ref([])
const mediaList = ref([])
const relics = ref([])
const honors = ref([])

const totalCount = computed(() =>
  filteredMartyrs.value.length + filteredDeeds.value.length + filteredMedia.value.length +
  filteredRelics.value.length + filteredHonors.value.length
)

const filteredMartyrs = computed(() => searchType.value && searchType.value !== 'martyrs' ? [] : martyrs.value)
const filteredDeeds = computed(() => searchType.value && searchType.value !== 'deeds' ? [] : deeds.value)
const filteredMedia = computed(() => searchType.value && searchType.value !== 'media' ? [] : mediaList.value)
const filteredRelics = computed(() => searchType.value && searchType.value !== 'relics' ? [] : relics.value)
const filteredHonors = computed(() => searchType.value && searchType.value !== 'honors' ? [] : honors.value)

async function handleSearch() {
  if (!keyword.value.trim()) return
  loading.value = true
  try {
    const params = { page: 0, size: 100, keyword: keyword.value }
    if (!searchType.value || searchType.value === 'martyrs') {
      martyrs.value = (await martyrApi.list(params)).content
    } else { martyrs.value = [] }
    if (!searchType.value || searchType.value === 'deeds') {
      deeds.value = (await deedApi.list(params)).content
    } else { deeds.value = [] }
    if (!searchType.value || searchType.value === 'media') {
      mediaList.value = (await mediaApi.list(params)).content
    } else { mediaList.value = [] }
    if (!searchType.value || searchType.value === 'relics') {
      relics.value = (await relicApi.list(params)).content
    } else { relics.value = [] }
    if (!searchType.value || searchType.value === 'honors') {
      honors.value = (await honorApi.list(params)).content
    } else { honors.value = [] }
  } finally {
    loading.value = false
  }
}

function goToMartyr(row) {
  router.push(`/martyrs/${row.id}`)
}
</script>
