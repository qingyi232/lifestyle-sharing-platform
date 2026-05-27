<template>
  <div class="dashboard" v-loading="loading">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card" style="--accent: #52b788; --accent-bg: #e8f5ec;">
        <div class="stat-icon"><el-icon :size="28"><User /></el-icon></div>
        <div class="stat-data">
          <span class="stat-num">{{ stats.totalUsers || 0 }}</span>
          <span class="stat-label">总用户数</span>
        </div>
      </div>
      <div class="stat-card" style="--accent: #e07a5f; --accent-bg: #fdf0ec;">
        <div class="stat-icon"><el-icon :size="28"><Document /></el-icon></div>
        <div class="stat-data">
          <span class="stat-num">{{ stats.totalNotes || 0 }}</span>
          <span class="stat-label">总笔记数</span>
        </div>
      </div>
      <div class="stat-card" style="--accent: #636e72; --accent-bg: #f0f0f0;">
        <div class="stat-icon"><el-icon :size="28"><ChatDotRound /></el-icon></div>
        <div class="stat-data">
          <span class="stat-num">{{ stats.totalComments || 0 }}</span>
          <span class="stat-label">总评论数</span>
        </div>
      </div>
      <div class="stat-card" style="--accent: #2d6a4f; --accent-bg: #d5efdf;">
        <div class="stat-icon"><el-icon :size="28"><TrendCharts /></el-icon></div>
        <div class="stat-data">
          <span class="stat-num">{{ stats.dailyActiveUsers || 0 }}</span>
          <span class="stat-label">日活用户</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-row">
      <div class="chart-card card">
        <h3 class="chart-title">近7日新增用户</h3>
        <v-chart v-if="userChartOption" :option="userChartOption" style="height: 300px;" autoresize />
      </div>
      <div class="chart-card card">
        <h3 class="chart-title">近7日新增笔记</h3>
        <v-chart v-if="noteChartOption" :option="noteChartOption" style="height: 300px;" autoresize />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { User, Document, ChatDotRound, TrendCharts } from '@element-plus/icons-vue'
import { getAdminStats } from '@/api/admin'

use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent])

const stats = ref({})
const loading = ref(true)
const userChartOption = ref(null)
const noteChartOption = ref(null)

const buildChartOption = (data, color, areaColor) => {
  const dates = (data || []).map(item => item.date || item.name || '')
  const values = (data || []).map(item => item.count || item.value || 0)
  return {
    tooltip: { trigger: 'axis' },
    grid: { top: 20, right: 20, bottom: 30, left: 50 },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: { lineStyle: { color: '#e8e8e8' } },
      axisLabel: { color: '#636e72', fontSize: 12 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#636e72' }
    },
    series: [{
      type: 'line',
      data: values,
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { color, width: 3 },
      itemStyle: { color },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: areaColor },
            { offset: 1, color: 'rgba(255,255,255,0)' }
          ]
        }
      }
    }]
  }
}

onMounted(async () => {
  try {
    const res = await getAdminStats()
    stats.value = res.data || {}
    userChartOption.value = buildChartOption(res.data?.userTrend, '#52b788', 'rgba(82,183,136,0.2)')
    noteChartOption.value = buildChartOption(res.data?.noteTrend, '#e07a5f', 'rgba(224,122,95,0.2)')
  } catch {
    userChartOption.value = buildChartOption([], '#52b788', 'rgba(82,183,136,0.2)')
    noteChartOption.value = buildChartOption([], '#e07a5f', 'rgba(224,122,95,0.2)')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 28px;
}

.stat-card {
  background: var(--accent-bg);
  border-radius: var(--radius);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: var(--accent);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-data {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.chart-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  padding: 24px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

@media (max-width: 1000px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .chart-row { grid-template-columns: 1fr; }
}
</style>
