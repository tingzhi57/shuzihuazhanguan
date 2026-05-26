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
        <div class="relic-image">
          <el-icon :size="48"><Collection /></el-icon>
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
          <p style="color: #909399; font-size: 12px; margin-top: 8px">{{ item.description }}</p>
          <div style="margin-top: 12px; display: flex; gap: 8px" v-if="store.isAdmin">
            <el-button size="small" @click="handleEdit(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div style="display: flex; justify-content: center; margin-top: 20px">
      <el-pagination v-model:current-page="page" :page-size="size" :total="total" @current-change="loadData" layout="prev, pager, next, total" />
    </div>

    <el-dialog v-model="showDialog" :title="editForm.id ? '编辑文物' : '新增文物'" width="600px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="类别"><el-input v-model="editForm.category" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="材质"><el-input v-model="editForm.material" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="年代"><el-input v-model="editForm.era" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="来源"><el-input v-model="editForm.origin" /></el-form-item></el-col>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { relicApi } from '../../api'
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

async function loadData() {
  loading.value = true
  try {
    const res = await relicApi.list({ page: page.value - 1, size: size.value })
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
