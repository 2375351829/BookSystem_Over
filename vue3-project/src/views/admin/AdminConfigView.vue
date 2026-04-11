<template>
  <div class="admin-config-view">
    <div class="config-layout">
      <div class="left-menu">
        <el-menu :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="borrow">
            <el-icon><Reading /></el-icon>
            <span>借阅规则</span>
          </el-menu-item>
          <el-menu-item index="fine">
            <el-icon><Wallet /></el-icon>
            <span>罚款规则</span>
          </el-menu-item>
          <el-menu-item index="reservation">
            <el-icon><Calendar /></el-icon>
            <span>预约规则</span>
          </el-menu-item>
          <el-menu-item index="system">
            <el-icon><Setting /></el-icon>
            <span>系统参数</span>
          </el-menu-item>
          <el-menu-item index="logs">
            <el-icon><Monitor /></el-icon>
            <span>日志监控</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div class="right-content">
        <!-- 借阅规则 -->
        <div v-if="activeMenu === 'borrow'" class="config-form-section">
          <h3 class="section-title">借阅规则配置</h3>

          <div class="form-group-title">各用户类型借阅上限</div>
          <el-table :data="borrowRules.userLimits" border size="small" style="max-width: 600px; margin-bottom: 24px">
            <el-table-column prop="type" label="用户类型" width="120" />
            <el-table-column label="借阅上限（本）" width="160">
              <template #default="{ row }">
                <el-input-number v-model="row.limit" :min="1" :max="50" size="small" />
              </template>
            </el-table-column>
          </el-table>

          <el-form label-width="180px" style="max-width: 560px">
            <el-form-item label="默认借阅期限（天）">
              <el-input-number v-model="borrowRules.defaultDays" :min="1" :max="90" />
            </el-form-item>
            <el-form-item label="续借次数限制">
              <el-input-number v-model="borrowRules.maxRenewCount" :min="0" :max="10" />
            </el-form-item>
            <el-form-item label="续借期限（天）">
              <el-input-number v-model="borrowRules.renewDays" :min="1" :max="60" />
            </el-form-item>
          </el-form>
        </div>

        <!-- 罚款规则 -->
        <div v-if="activeMenu === 'fine'" class="config-form-section">
          <h3 class="section-title">罚款规则配置</h3>
          <el-alert title="罚款规则将影响所有逾期图书的计算，请谨慎修改" type="warning" :closable="false" show-icon style="margin-bottom: 20px" />

          <el-form label-width="180px" style="max-width: 560px">
            <el-form-item label="每日罚款金额（元）">
              <el-input-number v-model="fineRules.dailyFine" :min="0" :max="100" :step="0.1" :precision="2" />
            </el-form-item>
            <el-form-item label="罚款上限（元）">
              <el-input-number v-model="fineRules.maxFine" :min="0" :max="9999" :step="0.1" :precision="2" />
            </el-form-item>
            <el-form-item label="免罚天数（宽限期）">
              <el-input-number v-model="fineRules.graceDays" :min="0" :max="30" />
            </el-form-item>
          </el-form>
        </div>

        <!-- 预约规则 -->
        <div v-if="activeMenu === 'reservation'" class="config-form-section">
          <h3 class="section-title">预约规则配置</h3>
          <el-form label-width="180px" style="max-width: 560px">
            <el-form-item label="每人预约上限">
              <el-input-number v-model="reservationRules.maxReservations" :min="1" :max="50" />
            </el-form-item>
            <el-form-item label="预约有效天数">
              <el-input-number v-model="reservationRules.validDays" :min="1" :max="90" />
            </el-form-item>
            <el-form-item label="排队等候上限">
              <el-input-number v-model="reservationRules.queueLimit" :min="1" :max="30" />
            </el-form-item>
          </el-form>
        </div>

        <!-- 系统参数 -->
        <div v-if="activeMenu === 'system'" class="config-form-section">
          <h3 class="section-title">系统参数配置</h3>
          <el-form label-width="180px" style="max-width: 640px">
            <el-form-item label="系统名称">
              <el-input v-model="systemConfig.systemName" placeholder="城市图书馆管理系统" style="max-width: 360px" />
            </el-form-item>
            <el-form-item label="开放时间">
              <el-time-picker
                v-model="systemConfig.openHours"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
              />
            </el-form-item>
            <el-form-item label="最大并发借阅数">
              <el-input-number v-model="systemConfig.maxConcurrentBorrow" :min="1" :max="99" />
            </el-form-item>
            <el-divider content-position="left">通知开关</el-divider>
            <el-form-item label="到期提醒通知">
              <el-switch v-model="systemConfig.notifyDueReminder" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="逾期提醒通知">
              <el-switch v-model="systemConfig.notifyOverdue" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="预约到馆通知">
              <el-switch v-model="systemConfig.notifyReservationReady" active-text="开启" inactive-text="关闭" />
            </el-form-item>
            <el-form-item label="新书上架通知">
              <el-switch v-model="systemConfig.notifyNewBook" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-form>
        </div>

        <!-- 日志监控 -->
        <div v-if="activeMenu === 'logs'" class="config-form-section log-section">
          <LogMonitor />
        </div>

        <div class="action-bar" v-if="activeMenu && activeMenu !== 'logs'">
          <el-button type="primary" :loading="saveLoading" @click="handleSave">保存配置</el-button>
          <el-button plain @click="handleReset">恢复默认</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Reading, Wallet, Calendar, Setting, Monitor } from '@element-plus/icons-vue'
import { configApi } from '@/api/config'
import LogMonitor from '@/components/LogMonitor.vue'

const activeMenu = ref('borrow')
const saveLoading = ref(false)

const borrowRules = reactive({
  userLimits: [
    { type: '普通读者', limit: 5 },
    { type: 'VIP读者', limit: 10 },
    { type: '教师', limit: 15 },
    { type: '学生', limit: 8 }
  ],
  defaultDays: 30,
  maxRenewCount: 2,
  renewDays: 15
})

const fineRules = reactive({
  dailyFine: 0.5,
  maxFine: 30.0,
  graceDays: 3
})

const reservationRules = reactive({
  maxReservations: 5,
  validDays: 7,
  queueLimit: 5
})

const systemConfig = reactive({
  systemName: '城市图书馆管理系统',
  openHours: ['08:00', '22:00'] as [string, string],
  maxConcurrentBorrow: 10,
  notifyDueReminder: true,
  notifyOverdue: true,
  notifyReservationReady: true,
  notifyNewBook: false
})

function handleMenuSelect(index: string) {
  activeMenu.value = index
}

async function fetchConfig() {
  try {
    const [borrowRes, fineRes, reservationRes] = await Promise.allSettled([
      configApi.getBorrowRules(),
      configApi.getFineRules(),
      configApi.getReservationRules()
    ])

    if (borrowRes.status === 'fulfilled') {
      const data = (borrowRes.value as any).value.data
      if (data) {
        if (Array.isArray(data.userLimits)) borrowRules.userLimits = data.userLimits
        if (data.defaultDays) borrowRules.defaultDays = data.defaultDays
        if (data.maxRenewCount !== undefined) borrowRules.maxRenewCount = data.maxRenewCount
        if (data.renewDays) borrowRules.renewDays = data.renewDays
      }
    }

    if (fineRes.status === 'fulfilled') {
      const data = (fineRes.value as any).value.data
      if (data) {
        if (data.dailyFine !== undefined) fineRules.dailyFine = data.dailyFine
        if (data.maxFine !== undefined) fineRules.maxFine = data.maxFine
        if (data.graceDays !== undefined) fineRules.graceDays = data.graceDays
      }
    }

    if (reservationRes.status === 'fulfilled') {
      const data = (reservationRes.value as any).value.data
      if (data) {
        if (data.maxReservations !== undefined) reservationRules.maxReservations = data.maxReservations
        if (data.validDays !== undefined) reservationRules.validDays = data.validDays
        if (data.queueLimit !== undefined) reservationRules.queueLimit = data.queueLimit
      }
    }

    const sysRes: any = await configApi.getConfig()
    if (sysRes.data) {
      Object.assign(systemConfig, sysRes.data)
    }
  } catch {
    // use defaults
  }
}

async function handleSave() {
  saveLoading.value = true
  try {
    const payload: Record<string, any> = {}
    if (activeMenu.value === 'borrow') payload.borrowRules = { ...borrowRules }
    else if (activeMenu.value === 'fine') payload.fineRules = { ...fineRules }
    else if (activeMenu.value === 'reservation') payload.reservationRules = { ...reservationRules }
    else if (activeMenu.value === 'system') payload.system = { ...systemConfig }

    await configApi.updateConfig(payload)
    ElMessage.success('配置保存成功')
  } catch {
    ElMessage.error('保存失败，请重试')
  } finally {
    saveLoading.value = false
  }
}

function handleReset() {
  ElMessageBox.confirm(
    `确定要恢复「${menuLabelMap[activeMenu.value]}」的默认配置吗？当前设置将被覆盖。`,
    '恢复默认确认',
    {
      confirmButtonText: '确定恢复',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await configApi.resetConfig()
      ElMessage.success('已恢复默认配置')
      fetchConfig()
    } catch {
      ElMessage.error('恢复默认配置失败')
    }
  }).catch(() => {})
}

const menuLabelMap: Record<string, string> = {
  borrow: '借阅规则',
  fine: '罚款规则',
  reservation: '预约规则',
  system: '系统参数'
}

onMounted(() => {
  fetchConfig()
})
</script>

<style scoped>
.admin-config-view {
  padding: var(--content-padding);
}

.config-layout {
  display: flex;
  gap: 24px;
  min-height: calc(100vh - 160px);
}

.left-menu {
  width: 200px;
  flex-shrink: 0;
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
}

.left-menu .el-menu {
  border-right: none;
}

.right-content {
  flex: 1;
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
}

.config-form-section {
  flex: 1;
}

.section-title {
  margin: 0 0 24px;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--color-text-primary);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border-light);
}

.form-group-title {
  font-size: var(--font-size-base);
  font-weight: 500;
  color: var(--color-text-regular);
  margin: 8px 0 12px;
}

.action-bar {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
  padding-top: 24px;
  border-top: 1px solid var(--color-border-light);
}

.log-section {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 220px);
  min-height: 500px;
}
</style>
