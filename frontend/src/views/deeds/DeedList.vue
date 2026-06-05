<template>
  <div class="app-container">
    <div class="page-header">
      <h2>事迹史料</h2>
      <div v-if="store.isAdmin">
        <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon> 新增事迹</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <div class="table-wrapper">
        <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="deedType" label="类型" width="80" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="source" label="来源" width="150" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)" v-if="store.isAdmin">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="store.isAdmin">删除</el-button>
          </template>
        </el-table-column>
        </el-table>
      </div>

      <div style="display: flex; justify-content: center; margin-top: 20px">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" @current-change="loadData" layout="prev, pager, next, total" />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑事迹' : '新增事迹'" width="700px" class="mobile-dialog">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-row :gutter="20" class="form-row">
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="类型"><el-select v-model="editForm.deedType"><el-option label="事迹" value="事迹" /><el-option label="史料" value="史料" /></el-select></el-form-item></el-col>
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="日期"><el-date-picker v-model="editForm.date" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="内容"><el-input v-model="editForm.content" type="textarea" :rows="6" /></el-form-item>
        <el-row :gutter="20" class="form-row">
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="来源"><el-input v-model="editForm.source" /></el-form-item></el-col>
          <el-col :xs="24" :sm="12" :span="12"><el-form-item label="作者"><el-input v-model="editForm.author" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { deedApi } from '../../api'
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

async function loadData() {
  loading.value = true
  try {
    const res = await deedApi.list({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function handleEdit(row) {
  editForm.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm('确定删除该事迹吗？', '确认删除', { type: 'warning' })
    .then(async () => {
      await deedApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

async function handleSave() {
  saving.value = true
  try {
    if (editForm.value.id) {
      await deedApi.update(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await deedApi.create(editForm.value)
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
