<template>
  <footer class="app-footer">
    <div class="footer-inner">
      <div class="footer-brand">
        <span class="footer-logo">🌿</span>
        <span class="footer-name">生活方式分享</span>
      </div>
      <p class="footer-desc">发现美好生活，分享真实体验</p>
      <div class="footer-links">
        <a href="#">关于我们</a>
        <span class="divider">·</span>
        <a href="#">帮助中心</a>
        <span class="divider">·</span>
        <a href="#">用户协议</a>
        <span class="divider">·</span>
        <a href="#">隐私政策</a>
      </div>
      <p class="footer-copy">&copy; 2026 生活方式分享平台 · 让生活更美好</p>
    </div>
  </footer>
</template>

<style scoped>
.app-footer {
  background: var(--card-bg);
  border-top: 1px solid var(--border);
  padding: 40px 20px;
  margin-top: auto;
  text-align: center;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.footer-logo {
  font-size: 24px;
}

.footer-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
}

.footer-desc {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 16px;
}

.footer-links a {
  font-size: 13px;
  color: var(--text-secondary);
  transition: color 0.2s;
}

.footer-links a:hover {
  color: var(--primary);
}

.divider {
  color: #d0d0d0;
}

.footer-copy {
  font-size: 12px;
  color: #b0b0b0;
}
</style>
