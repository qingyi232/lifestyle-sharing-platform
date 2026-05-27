import request from './request'

export function getNotifications() {
  return request.get('/api/notifications')
}

export function markAsRead(id) {
  return request.put(`/api/notifications/${id}/read`)
}

export function markAllAsRead() {
  return request.put('/api/notifications/read-all')
}

export function getUnreadCount() {
  return request.get('/api/notifications/unread-count')
}
