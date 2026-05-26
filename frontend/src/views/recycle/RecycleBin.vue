<template>
  <div class="app-container">
    <div class="page-header">
      <h2>回收站</h2>
      <el-button type="danger" @click="refreshList"><el-icon><Refresh /></el-icon> 刷新</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="birthplace" label="籍贯" show-overflow-tooltip />
        <el-table-column prop="deletedAt" label="删除时间" width="180">
          <template #default="{ row }">{{ row.deletedAt?.replace('T', ' ') || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleRestore(row)">恢复</el-button>
            <el-button size="small" type="danger" @click="handlePermanentDelete(row)">永久删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!list.length && !loading" description="回收站为空" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { recycleApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    list.value = await recycleApi.list()
  } finally {
    loading.value = false
  }
}

function handleRestore(row) {
  ElMessageBox.confirm(`确定恢复烈士「${row.name}」及其相关数据吗？`, '确认恢复')
    .then(async () => {
      await recycleApi.restore(row.id)
      ElMessage.success('恢复成功')
      loadData()
    })
    .catch(() => {})
}

function handlePermanentDelete(row) {
  ElMessageBox.confirm(`永久删除后将无法恢复，确定删除「${row.name}」吗？`, '确认永久删除', { type: 'warning', confirmButtonText: '确认删除', confirmButtonClass: 'el-button--danger' })
    .then(async () => {
      await recycleApi.delete(row.id)
      ElMessage.success('已永久删除')
      loadData()
    })
    .catch(() => {})
}

function refreshList() {
  loadData()
}

onMounted(loadData)
</script>
