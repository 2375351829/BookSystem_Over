<template>
  <div class="log-monitor">
    <div class="log-header">
      <div class="header-left">
        <h3>实时日志监控</h3>
        <el-tag :type="connectionStatus === 'connected' ? 'success' : connectionStatus === 'connecting' ? 'warning' : 'danger'" size="small">
          {{ statusText }}
        </el-tag>
      </div>
      <div class="header-right">
        <el-input
          v-model="filterText"
          placeholder="过滤日志..."
          clearable
          style="width: 200px; margin-right: 12px"
          @input="handleFilterChange"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="logLevel" placeholder="日志级别" clearable style="width: 120px; margin-right: 12px" @change="handleFilterChange">
          <el-option label="全部" value="" />
          <el-option label="ERROR" value="ERROR" />
          <el-option label="WARN" value="WARN" />
          <el-option label="INFO" value="INFO" />
          <el-option label="DEBUG" value="DEBUG" />
        </el-select>
        <el-button-group>
          <el-button :type="isPaused ? 'primary' : 'default'" @click="togglePause">
            <el-icon><component :is="isPaused ? 'VideoPlay' : 'VideoPause'" /></el-icon>
            {{ isPaused ? '恢复' : '暂停' }}
          </el-button>
          <el-button @click="clearLogs">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
          <el-button :type="autoScroll ? 'primary' : 'default'" @click="autoScroll = !autoScroll">
            <el-icon><Bottom /></el-icon>
            自动滚动
          </el-button>
        </el-button-group>
      </div>
    </div>

    <div class="log-stats">
      <span>总行数: {{ totalLines }}</span>
      <span>过滤后: {{ filteredLines.length }}</span>
      <span v-if="logInfo.path">日志文件: {{ logInfo.path }}</span>
      <span v-if="logInfo.size">文件大小: {{ formatSize(logInfo.size) }}</span>
    </div>

    <div ref="logContainer" class="log-container">
      <div v-if="filteredLines.length === 0" class="log-empty">
        <el-empty description="暂无日志数据" :image-size="80" />
      </div>
      <div v-else class="log-lines">
        <div
          v-for="(line, index) in filteredLines"
          :key="index"
          class="log-line"
          :class="getLogClass(line)"
        >
          <span class="line-number">{{ index + 1 }}</span>
          <span class="line-content" v-html="highlightText(line)"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Delete, Bottom, VideoPlay, VideoPause } from '@element-plus/icons-vue'
import { logApi } from '@/api/log'

interface LogLine {
  text: string
  timestamp?: string
}

const logContainer = ref<HTMLElement>()
const eventSource = ref<EventSource>()
const sessionId = ref<string>('')
const connectionStatus = ref<'disconnected' | 'connecting' | 'connected'>('disconnected')
const isPaused = ref(false)
const autoScroll = ref(true)
const filterText = ref('')
const logLevel = ref('')
const allLogs = ref<LogLine[]>([])
const totalLines = ref(0)
const logInfo = ref<any>({})

const statusText = computed(() => {
  switch (connectionStatus.value) {
    case 'connected':
      return isPaused.value ? '已暂停' : '已连接'
    case 'connecting':
      return '连接中...'
    default:
      return '未连接'
  }
})

const filteredLines = computed(() => {
  let lines = allLogs.value.map(log => log.text)
  
  if (logLevel.value) {
    lines = lines.filter(line => line.includes(logLevel.value))
  }
  
  if (filterText.value) {
    const filter = filterText.value.toLowerCase()
    lines = lines.filter(line => line.toLowerCase().includes(filter))
  }
  
  return lines
})

function getLogClass(line: string): string {
  if (line.includes('ERROR') || line.includes('Exception')) return 'log-error'
  if (line.includes('WARN')) return 'log-warn'
  if (line.includes('DEBUG')) return 'log-debug'
  if (line.includes('INFO')) return 'log-info'
  return ''
}

function highlightText(text: string): string {
  if (!filterText.value) return escapeHtml(text)
  
  const escaped = escapeHtml(text)
  const regex = new RegExp(`(${escapeRegExp(filterText.value)})`, 'gi')
  return escaped.replace(regex, '<mark class="highlight">$1</mark>')
}

function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

function escapeRegExp(string: string): string {
  return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
}

function formatSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

async function connectSSE() {
  if (eventSource.value) {
    eventSource.value.close()
  }

  connectionStatus.value = 'connecting'
  
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const params = new URLSearchParams()
  if (token) params.append('token', token)
  if (filterText.value) params.append('filter', filterText.value)
  params.append('initialLines', '100')
  
  const url = `http://127.0.0.1:8080/api/logs/stream?${params.toString()}`
  
  eventSource.value = new EventSource(url, {
    withCredentials: false
  })

  eventSource.value.addEventListener('connected', (event: MessageEvent) => {
    try {
      const data = JSON.parse(event.data)
      sessionId.value = data.sessionId
      connectionStatus.value = 'connected'
      ElMessage.success('日志流已连接')
    } catch (e) {
      connectionStatus.value = 'connected'
    }
  })

  eventSource.value.addEventListener('log', (event: MessageEvent) => {
    if (!isPaused.value) {
      allLogs.value.push({ text: event.data })
      totalLines.value++
      
      if (allLogs.value.length > 5000) {
        allLogs.value = allLogs.value.slice(-4000)
      }
      
      if (autoScroll.value) {
        scrollToBottom()
      }
    }
  })

  eventSource.value.addEventListener('error', (event: MessageEvent) => {
    if (event.data) {
      ElMessage.error(event.data)
    }
    connectionStatus.value = 'disconnected'
  })

  eventSource.value.onerror = () => {
    connectionStatus.value = 'disconnected'
    ElMessage.error('日志流连接失败')
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (logContainer.value) {
      logContainer.value.scrollTop = logContainer.value.scrollHeight
    }
  })
}

async function togglePause() {
  if (!sessionId.value) return
  
  try {
    if (isPaused.value) {
      await logApi.resumeStream(sessionId.value)
      ElMessage.success('日志流已恢复')
    } else {
      await logApi.pauseStream(sessionId.value)
      ElMessage.success('日志流已暂停')
    }
    isPaused.value = !isPaused.value
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function clearLogs() {
  allLogs.value = []
  totalLines.value = 0
}

async function handleFilterChange() {
  if (connectionStatus.value === 'connected') {
    await disconnectSSE()
    await connectSSE()
  }
}

async function disconnectSSE() {
  if (eventSource.value) {
    eventSource.value.close()
    eventSource.value = undefined
  }
  
  if (sessionId.value) {
    try {
      await logApi.closeStream(sessionId.value)
    } catch (error) {
      // ignore
    }
  }
  
  connectionStatus.value = 'disconnected'
  sessionId.value = ''
}

async function fetchLogStatus() {
  try {
    const res: any = await logApi.getLogStatus()
    logInfo.value = res.data || res
  } catch (error) {
    // ignore
  }
}

onMounted(async () => {
  await fetchLogStatus()
  await connectSSE()
})

onUnmounted(async () => {
  await disconnectSSE()
})

watch(autoScroll, (newVal) => {
  if (newVal) {
    scrollToBottom()
  }
})
</script>

<style scoped>
.log-monitor {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border-light);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.header-right {
  display: flex;
  align-items: center;
}

.log-stats {
  display: flex;
  gap: 16px;
  padding: 8px 20px;
  background: var(--color-bg-secondary);
  font-size: 12px;
  color: var(--color-text-secondary);
  border-bottom: 1px solid var(--color-border-light);
}

.log-container {
  flex: 1;
  overflow-y: auto;
  background: #1e1e1e;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.6;
}

.log-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--color-text-secondary);
}

.log-lines {
  padding: 8px 0;
}

.log-line {
  display: flex;
  padding: 2px 16px;
  color: #d4d4d4;
  transition: background-color 0.2s;
}

.log-line:hover {
  background: rgba(255, 255, 255, 0.05);
}

.line-number {
  min-width: 50px;
  color: #858585;
  text-align: right;
  padding-right: 16px;
  user-select: none;
}

.line-content {
  flex: 1;
  white-space: pre-wrap;
  word-break: break-all;
}

.log-error {
  background: rgba(255, 0, 0, 0.1);
  color: #f48771;
}

.log-error .line-number {
  color: #f48771;
}

.log-warn {
  background: rgba(255, 200, 0, 0.1);
  color: #cca700;
}

.log-warn .line-number {
  color: #cca700;
}

.log-info {
  color: #75beff;
}

.log-debug {
  color: #75beff;
  opacity: 0.7;
}

:deep(.highlight) {
  background: #ffc107;
  color: #000;
  padding: 0 2px;
  border-radius: 2px;
}

:deep(.el-button-group) {
  display: flex;
}

:deep(.el-empty__description p) {
  color: #666;
}
</style>
