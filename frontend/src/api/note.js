import request from './request'

export function getNotes(params) {
  return request.get('/api/notes', { params })
}

export function getNoteById(id) {
  return request.get(`/api/notes/${id}`)
}

export function createNote(data) {
  return request.post('/api/notes', data)
}

export function updateNote(id, data) {
  return request.put(`/api/notes/${id}`, data)
}

export function deleteNote(id) {
  return request.delete(`/api/notes/${id}`)
}

export function getMyNotes() {
  return request.get('/api/notes/my')
}

export function getFavoriteNotes() {
  return request.get('/api/notes/favorites')
}

export function getFollowingNotes(params) {
  return request.get('/api/notes/following', { params })
}

export function getHotNotes() {
  return request.get('/api/notes/hot')
}
