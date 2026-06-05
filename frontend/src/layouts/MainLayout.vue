<template>
  <el-container style="height: 100vh">
    <el-aside v-if="!isMobile" :width="store.sidebarCollapsed ? '64px' : '220px'" style="background: #304156; transition: width 0.3s">
      <div class="logo" :style="{ width: store.sidebarCollapsed ? '64px' : '220px' }">
        <span v-if="!store.sidebarCollapsed">烈士数字展厅</span>
        <span v-else>展</span>
      </div>
      <SidebarMenu :collapse="store.sidebarCollapsed" />
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; height: 60px; padding: 0 20px">
        <div style="display: flex; align-items: center">
          <el-button v-if="isMobile" @click="drawerVisible = true" text>
            <el-icon><Operation /></el-icon>
          </el-button>
          <el-button v-else @click="store.toggleSidebar" text>
            <el-icon><Fold v-if="!store.sidebarCollapsed" /><Expand v-else /></el-icon>
          </el-button>
          <span class="header-title">烈士数字展厅</span>
        </div>
        <div style="display: flex; align-items: center; gap: 12px">
          <el-tag v-if="store.isAdmin" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="info" size="small">游客</el-tag>
          <span class="header-nickname">{{ store.nickname }}</span>
          <el-button @click="handleLogout" text type="primary" size="small">退出</el-button>
        </div>
      </el-header>
      <el-main class="main-content" style="background: #f5f7fa; padding: 20px; overflow-y: auto">
        <router-view />
      </el-main>
    </el-container>

    <el-drawer v-model="drawerVisible" :size="240" direction="ltr" :with-header="false" style="background: #304156">
      <div class="logo" style="width: 100%">
        <span>烈士数字展厅</span>
      </div>
      <SidebarMenu :collapse="false" @select="drawerVisible = false" />
    </el-drawer>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'
import { Operation } from '@element-plus/icons-vue'
import SidebarMenu from './SidebarMenu.vue'

const route = useRoute()
const router = useRouter()
const store = useAppStore()
const drawerVisible = ref(false)
const isMobile = ref(false)

function checkMobile() {
  isMobile.value = window.innerWidth < 768
}

function handleLogout() {
  store.logout()
  router.push('/login')
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #1f2d3d;
  overflow: hidden;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
}

.header-title {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

@media (max-width: 768px) {
  .header-title {
    font-size: 14px;
  }
  .header-nickname {
    display: none;
  }
  .main-content {
    padding: 12px !important;
  }
}
</style>
