import request from './request'

export function getProfile() {
  return request.get('/api/users/profile')
}

export function updateProfile(data) {
  return request.put('/api/users/profile', data)
}

export function updatePassword(data) {
  return request.put('/api/users/password', data)
}

export function deleteAccount() {
  return request.delete('/api/users/account')
}

export function getUserById(id) {
  return request.get(`/api/users/${id}`)
}

export function getUserNotes(id, params) {
  return request.get(`/api/users/${id}/notes`, { params })
}

export function getUserFollowers(id) {
  return request.get(`/api/users/${id}/followers`)
}

export function getUserFollowing(id) {
  return request.get(`/api/users/${id}/following`)
}
