import request from './request'

export function getAdminUsers(params) {
  return request.get('/api/admin/users', { params })
}

export function updateUserStatus(id, data) {
  return request.put(`/api/admin/users/${id}/status`, data)
}

export function resetUserPassword(id) {
  return request.put(`/api/admin/users/${id}/reset-password`)
}

export function updateUserRole(id, data) {
  return request.put(`/api/admin/users/${id}/role`, data)
}

export function getPendingNotes(params) {
  return request.get('/api/admin/notes', { params })
}

export function getAllAdminNotes(params) {
  return request.get('/api/admin/notes/all', { params })
}

export function approveNote(id) {
  return request.put(`/api/admin/notes/${id}/approve`)
}

export function rejectNote(id, data) {
  return request.put(`/api/admin/notes/${id}/reject`, data)
}

export function createCategory(data) {
  return request.post('/api/admin/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/api/admin/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/api/admin/categories/${id}`)
}

export function getAdminStats() {
  return request.get('/api/admin/stats')
}
