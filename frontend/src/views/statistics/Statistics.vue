<template>
  <div class="app-container">
    <div class="page-header">
      <h2>资源统计</h2>
    </div>

    <div class="stat-cards">
      <div class="stat-card" v-for="item in statItems" :key="item.label">
        <div class="stat-icon" :style="{ background: item.color }">
          <el-icon :size="24"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="label">{{ item.label }}</div>
          <div class="value">{{ item.count }}</div>
        </div>
      </div>
    </div>

    <div class="chart-grid">
      <el-card shadow="never">
        <template #header>各类资源数量统计</template>
        <div ref="barChartRef" style="height: 350px"></div>
      </el-card>
      <el-card shadow="never">
        <template #header>资源占比分析</template>
        <div ref="pieChartRef" style="height: 350px"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { statisticsApi } from '../../api'
import * as echarts from 'echarts'

const stats = ref({})
const barChartRef = ref(null)
const pieChartRef = ref(null)

const statItems = ref([])

async function loadStats() {
  const res = await statisticsApi.get()
  stats.value = res
  statItems.value = [
    { label: '烈士人数', count: res.martyrCount, icon: 'User', color: '#409eff' },
    { label: '事迹史料', count: res.deedCount, icon: 'Document', color: '#67c23a' },
    { label: '影像音视频', count: res.mediaCount, icon: 'VideoCamera', color: '#e6a23c' },
    { label: '文物实物', count: res.relicCount, icon: 'Collection', color: '#f56c6c' },
    { label: '荣誉纪念', count: res.honorCount, icon: 'Medal', color: '#909399' },
  ]
  nextTick(() => {
    renderCharts()
  })
}

function renderCharts() {
  const categories = ['烈士人数', '事迹史料', '影像音视频', '文物实物', '荣誉纪念']
  const values = [stats.value.martyrCount, stats.value.deedCount, stats.value.mediaCount, stats.value.relicCount, stats.value.honorCount]

  const barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: categories },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: values, itemStyle: { borderRadius: [4, 4, 0, 0], color: '#409eff' } }]
  })

  const pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: categories.map((name, i) => ({ name, value: values[i] })),
      label: { show: true, formatter: '{b}\n{d}%' },
      itemStyle: { borderRadius: 4 }
    }]
  })
}

onMounted(loadStats)
</script>

<style scoped>
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

@media (max-width: 768px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}
</style>
