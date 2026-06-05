import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const nickname = ref(localStorage.getItem('nickname') || '')
  const sidebarCollapsed = ref(localStorage.getItem('sidebarCollapsed') !== 'false')

  const isAdmin = computed(() => role.value === 'ADMIN')
  const isLoggedIn = computed(() => !!token.value)

  function setUser(tokenVal, roleVal, nicknameVal) {
    token.value = tokenVal
    role.value = roleVal
    nickname.value = nicknameVal
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('role', roleVal)
    localStorage.setItem('nickname', nicknameVal)
  }

  function logout() {
    token.value = ''
    role.value = ''
    nickname.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('nickname')
    localStorage.removeItem('sidebarCollapsed')
  }

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
    localStorage.setItem('sidebarCollapsed', sidebarCollapsed.value)
  }

  return { token, role, nickname, sidebarCollapsed, isAdmin, isLoggedIn, setUser, logout, toggleSidebar }
})
