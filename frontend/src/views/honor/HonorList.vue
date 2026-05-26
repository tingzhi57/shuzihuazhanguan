<template>
  <div class="app-container">
    <div class="page-header">
      <h2>荣誉纪念</h2>
      <div v-if="store.isAdmin">
        <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon> 新增荣誉</el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" stripe v-loading="loading">
        <el-table-column prop="honorName" label="荣誉名称" show-overflow-tooltip />
        <el-table-column prop="honorType" label="类型" width="120" />
        <el-table-column prop="issuingAuthority" label="颁发单位" show-overflow-tooltip />
        <el-table-column prop="issueDate" label="颁发日期" width="120" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleEdit(row)" v-if="store.isAdmin">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)" v-if="store.isAdmin">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: center; margin-top: 20px">
        <el-pagination v-model:current-page="page" :page-size="size" :total="total" @current-change="loadData" layout="prev, pager, next, total" />
      </div>
    </el-card>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑荣誉' : '新增荣誉'" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="荣誉名称"><el-input v-model="editForm.honorName" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="荣誉类型"><el-input v-model="editForm.honorType" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="颁发单位"><el-input v-model="editForm.issuingAuthority" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="颁发日期"><el-date-picker v-model="editForm.issueDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
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
import { ref, onMounted } from 'vue'
import { honorApi } from '../../api'
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
    const res = await honorApi.list({ page: page.value - 1, size: size.value })
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
  ElMessageBox.confirm('确定删除该荣誉吗？', '确认删除', { type: 'warning' })
    .then(async () => {
      await honorApi.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

async function handleSave() {
  saving.value = true
  try {
    if (editForm.value.id) {
      await honorApi.update(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await honorApi.create(editForm.value)
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
