<template>
  <el-menu
    :default-active="route.path"
    :collapse="collapse"
    :collapse-transition="false"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
    router
    @select="emit('select')"
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
</template>

<script setup>
import { useRoute } from 'vue-router'
import { useAppStore } from '../stores/app'

defineProps({ collapse: Boolean })
const emit = defineEmits(['select'])

const route = useRoute()
const store = useAppStore()
</script>

<style scoped>
.el-menu {
  border-right: none;
}
</style>
