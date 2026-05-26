<template>
  <div class="app-container">
    <div class="page-header">
      <h2>控制台</h2>
    </div>

    <div class="stat-cards">
      <div class="stat-card" v-for="item in statItems" :key="item.label" @click="handleNavigate(item.route)" style="cursor: pointer">
        <div class="stat-icon" :style="{ background: item.color }">
          <el-icon :size="24"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ item.count }}</div>
        </div>
      </div>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px">
      <div class="martyr-detail-section">
        <h3>最新登记的烈士</h3>
        <el-table :data="recentMartyrs" style="width: 100%" @row-click="goToMartyr" stripe>
          <el-table-column prop="name" label="姓名" />
          <el-table-column prop="gender" label="性别" width="60" />
          <el-table-column prop="birthDate" label="出生日期" width="120" />
          <el-table-column prop="militaryUnit" label="部队" show-overflow-tooltip />
        </el-table>
      </div>
      <div class="martyr-detail-section">
        <h3>快速操作</h3>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px">
          <el-button type="primary" size="large" @click="$router.push('/register')" v-if="store.isAdmin">
            <el-icon><Edit /></el-icon> 资源登记
          </el-button>
          <el-button type="success" size="large" @click="$router.push('/martyrs')">
            <el-icon><User /></el-icon> 烈士数据
          </el-button>
          <el-button type="warning" size="large" @click="$router.push('/relics')">
            <el-icon><Collection /></el-icon> 文物展示
          </el-button>
          <el-button type="info" size="large" @click="$router.push('/search')">
            <el-icon><Search /></el-icon> 资源查询
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { statisticsApi, martyrApi } from '../../api'
import { useAppStore } from '../../stores/app'

const router = useRouter()
const store = useAppStore()

const stats = ref({})
const recentMartyrs = ref([])

const statItems = ref([])

async function loadStats() {
  try {
    const res = await statisticsApi.get()
    stats.value = res
    statItems.value = [
      { label: '烈士人数', count: res.martyrCount, icon: 'User', color: '#409eff', route: '/martyrs' },
      { label: '事迹史料', count: res.deedCount, icon: 'Document', color: '#67c23a', route: '/deeds' },
      { label: '影像音视频', count: res.mediaCount, icon: 'VideoCamera', color: '#e6a23c', route: '/media' },
      { label: '文物实物', count: res.relicCount, icon: 'Collection', color: '#f56c6c', route: '/relics' },
      { label: '荣誉纪念', count: res.honorCount, icon: 'Medal', color: '#909399', route: '/honors' },
    ]
  } catch {}
}

async function loadRecent() {
  try {
    const res = await martyrApi.list({ page: 0, size: 5 })
    recentMartyrs.value = res.content
  } catch {}
}

function goToMartyr(row) {
  router.push(`/martyrs/${row.id}`)
}

function handleNavigate(route) {
  router.push(route)
}

onMounted(() => {
  loadStats()
  loadRecent()
})
</script>
