<template>
  <div class="app-container">
    <div class="page-header">
      <h2>烈士数据展示</h2>
      <div>
        <el-button type="primary" @click="$router.push('/register')" v-if="store.isAdmin">
          <el-icon><Plus /></el-icon> 新增烈士
        </el-button>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="list" stripe v-loading="loading" @row-click="goToDetail" style="cursor: pointer">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="60" />
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column prop="deathDate" label="牺牲日期" width="120" />
        <el-table-column prop="birthplace" label="籍贯" show-overflow-tooltip />
        <el-table-column prop="militaryUnit" label="所属部队" show-overflow-tooltip />
        <el-table-column prop="rank" label="职务/军衔" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click.stop="$router.push(`/martyrs/${row.id}`)">查看</el-button>
            <el-button size="small" type="primary" @click.stop="handleEdit(row)" v-if="store.isAdmin">编辑</el-button>
            <el-button size="small" type="danger" @click.stop="handleDelete(row)" v-if="store.isAdmin">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="display: flex; justify-content: center; margin-top: 20px">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          @current-change="loadData"
          layout="prev, pager, next, total"
        />
      </div>
    </el-card>

    <el-dialog v-model="editDialog" :title="editForm.id ? '编辑烈士信息' : '新增烈士信息'" width="700px">
      <el-form :model="editForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名"><el-input v-model="editForm.name" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="editForm.gender">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期"><el-date-picker v-model="editForm.birthDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="牺牲日期"><el-date-picker v-model="editForm.deathDate" type="date" style="width: 100%" value-format="YYYY-MM-DD" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="籍贯"><el-input v-model="editForm.birthplace" /></el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="民族"><el-input v-model="editForm.ethnicity" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="政治面貌"><el-input v-model="editForm.politicalStatus" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部队"><el-input v-model="editForm.militaryUnit" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职务/军衔"><el-input v-model="editForm.rank" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="牺牲地点"><el-input v-model="editForm.sacrificeLocation" /></el-form-item>
        <el-form-item label="牺牲原因"><el-input v-model="editForm.sacrificeReason" type="textarea" /></el-form-item>
        <el-form-item label="安葬地点"><el-input v-model="editForm.burialLocation" /></el-form-item>
        <el-form-item label="生平简介"><el-input v-model="editForm.description" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { martyrApi } from '../../api'
import { useAppStore } from '../../stores/app'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const store = useAppStore()
const list = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const editDialog = ref(false)
const saving = ref(false)
const editForm = ref({})

async function loadData() {
  loading.value = true
  try {
    const res = await martyrApi.list({ page: page.value - 1, size: size.value })
    list.value = res.content
    total.value = res.totalElements
  } finally {
    loading.value = false
  }
}

function goToDetail(row) {
  router.push(`/martyrs/${row.id}`)
}

function handleEdit(row) {
  editForm.value = { ...row }
  editDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除烈士「${row.name}」吗？相关数据也将被删除。`, '确认删除', { type: 'warning' })
    .then(async () => {
      await martyrApi.delete(row.id)
      ElMessage.success('已移入回收站')
      loadData()
    })
    .catch(() => {})
}

async function handleSave() {
  saving.value = true
  try {
    if (editForm.value.id) {
      await martyrApi.update(editForm.value.id, editForm.value)
      ElMessage.success('更新成功')
    } else {
      await martyrApi.create(editForm.value)
      ElMessage.success('创建成功')
    }
    editDialog.value = false
    loadData()
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>
