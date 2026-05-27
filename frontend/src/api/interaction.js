import request from './request'

export function toggleLike(noteId) {
  return request.post(`/api/interactions/like/${noteId}`)
}

export function toggleFavorite(noteId) {
  return request.post(`/api/interactions/favorite/${noteId}`)
}

export function toggleFollow(userId) {
  return request.post(`/api/interactions/follow/${userId}`)
}

export function getInteractionStatus(noteId) {
  return request.get(`/api/interactions/status/${noteId}`)
}

export function getFollowStatus(userId) {
  return request.get(`/api/interactions/follow-status/${userId}`)
}
