import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      window.location.href = '/#/login'
    }
    ElMessage.error(error.response?.data?.error || '请求失败')
    return Promise.reject(error)
  }
)

export const authApi = {
  login(data) { return request.post('/auth/login', data) }
}

export const martyrApi = {
  list(params) { return request.get('/martyrs', { params }) },
  get(id) { return request.get(`/martyrs/${id}`) },
  create(data) { return request.post('/martyrs', data) },
  update(id, data) { return request.put(`/martyrs/${id}`, data) },
  delete(id) { return request.delete(`/martyrs/${id}`) }
}

export const deedApi = {
  list(params) { return request.get('/deeds', { params }) },
  getByMartyr(martyrId) { return request.get(`/deeds/martyr/${martyrId}`) },
  create(data) { return request.post('/deeds', data) },
  update(id, data) { return request.put(`/deeds/${id}`, data) },
  delete(id) { return request.delete(`/deeds/${id}`) }
}

export const mediaApi = {
  list(params) { return request.get('/media', { params }) },
  getByMartyr(martyrId) { return request.get(`/media/martyr/${martyrId}`) },
  create(data) { return request.post('/media', data) },
  upload(formData) { return request.post('/media/upload', formData) },
  update(id, data) { return request.put(`/media/${id}`, data) },
  setAvatar(id) { return request.put(`/media/${id}/avatar`) },
  clearAvatar(id) { return request.delete(`/media/${id}/avatar`) },
  delete(id) { return request.delete(`/media/${id}`) }
}

export const relicApi = {
  list(params) { return request.get('/relics', { params }) },
  getByMartyr(martyrId) { return request.get(`/relics/martyr/${martyrId}`) },
  create(data) { return request.post('/relics', data) },
  update(id, data) { return request.put(`/relics/${id}`, data) },
  delete(id) { return request.delete(`/relics/${id}`) }
}

export const honorApi = {
  list(params) { return request.get('/honors', { params }) },
  getByMartyr(martyrId) { return request.get(`/honors/martyr/${martyrId}`) },
  create(data) { return request.post('/honors', data) },
  update(id, data) { return request.put(`/honors/${id}`, data) },
  delete(id) { return request.delete(`/honors/${id}`) }
}

export const statisticsApi = {
  get() { return request.get('/statistics') }
}

export const recycleApi = {
  list() { return request.get('/recycle') },
  restore(id) { return request.put(`/recycle/restore/${id}`) },
  delete(id) { return request.delete(`/recycle/${id}`) }
}

export const searchApi = {
  search(params) { return request.get('/search', { params }) }
}
