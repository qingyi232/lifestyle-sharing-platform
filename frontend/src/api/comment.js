import request from './request'

export function getCommentsByNoteId(noteId) {
  return request.get(`/api/comments/note/${noteId}`)
}

export function createComment(data) {
  return request.post('/api/comments', data)
}

export function deleteComment(id) {
  return request.delete(`/api/comments/${id}`)
}
