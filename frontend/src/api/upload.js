import request from './request'

/**
 * 上传图片。不要手动设置 Content-Type，否则缺少 boundary，服务端无法解析文件。
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/upload', formData, { timeout: 120000 })
}

/**
 * 从后端 Result 中取出图片 URL（兼容 data 为字符串或 { url }）
 */
export function uploadUrlFromResult(res) {
  const d = res?.data
  if (typeof d === 'string' && d.trim()) return d.trim()
  if (d && typeof d === 'object' && typeof d.url === 'string') return d.url.trim()
  return ''
}
