<template>
  <el-container style="height: 100vh">
    <el-aside :width="store.sidebarCollapsed ? '64px' : '220px'" style="background: #304156; transition: width 0.3s">
      <div class="logo" :style="{ width: store.sidebarCollapsed ? '64px' : '220px' }">
        <span v-if="!store.sidebarCollapsed">烈士数字展厅</span>
        <span v-else>展</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="store.sidebarCollapsed"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>控制台</span>
        </el-menu-item>
        <el-menu-item index="/martyrs">
          <el-icon><User /></el-icon>
          <span>烈士数据展示</span>
        </el-menu-item>
        <el-menu-item index="/relics">
          <el-icon><Collection /></el-icon>
          <span>文物数据展示</span>
        </el-menu-item>
        <el-menu-item index="/deeds">
          <el-icon><Document /></el-icon>
          <span>事迹史料</span>
        </el-menu-item>
        <el-menu-item index="/media">
          <el-icon><VideoCamera /></el-icon>
          <span>影像音视频</span>
        </el-menu-item>
        <el-menu-item index="/honors">
          <el-icon><Medal /></el-icon>
          <span>荣誉纪念</span>
        </el-menu-item>
        <el-menu-item index="/register" v-if="store.isAdmin">
          <el-icon><Edit /></el-icon>
          <span>资源登记</span>
        </el-menu-item>
        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>资源统计</span>
        </el-menu-item>
        <el-menu-item index="/search">
          <el-icon><Search /></el-icon>
          <span>资源查询</span>
        </el-menu-item>
        <el-menu-item index="/recycle" v-if="store.isAdmin">
          <el-icon><Delete /></el-icon>
          <span>回收站</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background: #fff; border-bottom: 1px solid #e6e6e6; display: flex; align-items: center; justify-content: space-between; height: 60px; padding: 0 20px">
        <div style="display: flex; align-items: center">
          <el-button @click="store.toggleSidebar" text>
            <el-icon><Fold v-if="!store.sidebarCollapsed" /><Expand v-else /></el-icon>
          </el-button>
        </div>
        <div style="display: flex; align-items: center; gap: 12px">
          <el-tag v-if="store.isAdmin" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="info" size="small">游客</el-tag>
          <span>{{ store.nickname }}</span>
          <el-button @click="handleLogout" text type="primary" size="small">退出</el-button>
        </div>
      </el-header>
      <el-main style="background: #f5f7fa; padding: 20px; overflow-y: auto">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const route = useRoute()
const router = useRouter()
const store = useAppStore()

function handleLogout() {
  store.logout()
  router.push('/login')
}
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
</style>
