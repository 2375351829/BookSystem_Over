<template>
  <div class="profile-page">
    <el-row :gutter="24">
      <el-col :span="7">
        <el-card class="profile-sidebar" shadow="hover">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleUpload"
              accept="image/*"
            >
              <el-avatar :size="100" :src="user?.avatar || ''">
                {{ user?.username?.charAt(0)?.toUpperCase() || 'U' }}
              </el-avatar>
            </el-upload>
            <p class="upload-tip">{{ t('profile.clickUpload') }}</p>
          </div>
          <h3 class="user-name">{{ user?.username }}</h3>
          <el-tag :type="typeTagType" size="small" effect="dark">{{ typeLabel }}</el-tag>
          <div class="identity-info">
            <span class="identity-label">身份标识:</span>
            <span class="identity-value">{{ identityDisplay }}</span>
          </div>

          <div class="quota-section">
            <div class="quota-header">
              <span>{{ t('profile.borrowQuota') }}</span>
              <span>{{ user?.borrowedCount || 0 }} / {{ user?.borrowLimit || 10 }}</span>
            </div>
            <el-progress :percentage="quotaPercent" :color="quotaColor" :stroke-width="8" />
          </div>

          <div class="quick-links">
            <router-link to="/borrows" class="quick-link"><el-icon><Reading /></el-icon> {{ t('nav.myBorrows') }}</router-link>
            <router-link to="/reservations" class="quick-link"><el-icon><Clock /></el-icon> {{ t('nav.myReservations') }}</router-link>
            <router-link to="/favorites" class="quick-link"><el-icon><Star /></el-icon> {{ t('nav.favorites') }}</router-link>
            <router-link to="/notifications" class="quick-link"><el-icon><Bell /></el-icon> {{ t('nav.notifications') }}</router-link>
          </div>
        </el-card>
      </el-col>

      <el-col :span="17">
        <el-tabs v-model="activeTab" type="border-card" class="profile-tabs">
          <el-tab-pane :label="t('profile.basicInfo')" name="basic">
            <el-form ref="basicFormRef" :model="basicForm" :rules="basicRules" label-width="100px" style="max-width:560px">
              <el-form-item label="身份标识">
                <el-input :model-value="identityDisplay" disabled />
              </el-form-item>
              <el-form-item :label="t('profile.realName')" prop="realName">
                <el-input v-model="basicForm.realName" />
              </el-form-item>
              <el-form-item :label="t('profile.email')" prop="email">
                <el-input v-model="basicForm.email" />
              </el-form-item>
              <el-form-item :label="t('profile.phone')" prop="phone">
                <el-input v-model="basicForm.phone" />
              </el-form-item>
              <el-form-item label="校区" prop="campus">
                <el-input v-model="basicForm.campus" disabled />
              </el-form-item>
              <el-form-item label="院系" prop="college">
                <el-input v-model="basicForm.college" disabled />
              </el-form-item>
              <el-form-item label="年级" prop="grade" v-if="user?.userType === 'STUDENT'">
                <el-input v-model="basicForm.grade" disabled />
              </el-form-item>
              <el-form-item label="班级" prop="className" v-if="user?.userType === 'STUDENT'">
                <el-input v-model="basicForm.className" disabled />
              </el-form-item>
              <el-form-item label="辅导员" prop="counselor" v-if="user?.userType === 'STUDENT'">
                <el-input v-model="basicForm.counselor" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingBasic" @click="saveBasic">{{ t('common.save') }}</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane :label="t('profile.security')" name="security">
            <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="120px" style="max-width:480px">
              <el-form-item :label="t('profile.oldPassword')" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item :label="t('profile.newPassword')" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password @input="checkStrength" />
                <div class="strength-bar">
                  <div class="strength-segment" :class="{ active: strength >= 1, weak: strength === 1 }"></div>
                  <div class="strength-segment" :class="{ active: strength >= 2, medium: strength === 2 }"></div>
                  <div class="strength-segment" :class="{ active: strength >= 3, strong: strength === 3 }"></div>
                  <span class="strength-text">{{ strengthLabel }}</span>
                </div>
              </el-form-item>
              <el-form-item :label="t('profile.confirmNewPwd')" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingPwd" @click="changePassword">{{ t('common.save') }}</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane :label="t('profile.preferences')" name="preferences">
            <el-form label-position="top" style="max-width:560px">
              <el-form-item :label="t('profile.interestCategories')">
                <el-checkbox-group v-model="prefForm.categories">
                  <el-checkbox v-for="c in categoryOptions" :key="c.value" :value="c.value">{{ c.label }}</el-checkbox>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item :label="t('profile.preferenceTags')">
                <div class="tag-input-area">
                  <el-tag v-for="tag in prefForm.tags" :key="tag" closable @close="removeTag(tag)" style="margin:2px 4px 2px 0">{{ tag }}</el-tag>
                  <el-input
                    v-if="inputVisible"
                    ref="tagInputRef"
                    v-model="inputValue"
                    size="small"
                    style="width:100px"
                    @keyup.enter="addTag"
                    @blur="addTag"
                  />
                  <el-button v-else size="small" @click="showInput">+ {{ t('profile.addTag') }}</el-button>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingPref" @click="savePreferences">{{ t('common.save') }}</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane :label="t('profile.notifications')" name="notifications">
            <div class="notification-settings">
              <div class="notif-item" v-for="(item, key) in notifSettings" :key="key">
                <div class="notif-info">
                  <span class="notif-label">{{ item.label }}</span>
                  <span class="notif-desc">{{ item.desc }}</span>
                </div>
                <el-switch v-model="item.value" @change="saveNotifSettings" />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane :label="t('profile.stats')" name="stats">
            <div id="stats-chart-bar" style="width:100%;height:320px;margin-bottom:24px"></div>
            <div id="stats-chart-pie" style="width:100%;height:300px"></div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading, Clock, Star, Bell } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/authStore'
import { userApi } from '@/api/user'
import { authApi } from '@/api/auth'
import { statsApi } from '@/api/stats'
import type { FormInstance, FormRules, UploadRequestOptions } from 'element-plus'

const { t } = useI18n()
const authStore = useAuthStore()

const user = computed(() => authStore.user)
const activeTab = ref('basic')

const basicFormRef = ref<FormInstance>()
const savingBasic = ref(false)
const basicForm = reactive({ realName: '', email: '', phone: '', campus: '', college: '', grade: '', className: '', counselor: '' })
const basicRules: FormRules = {
  realName: [{ required: true, message: () => t('profile.inputRealName'), trigger: 'blur' }],
  email: [{ required: true, type: 'email', message: () => t('profile.emailInvalid'), trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: () => t('register.phoneInvalid'), trigger: 'blur' }]
}

const identityDisplay = computed(() => {
  const u = user.value
  if (!u) return '-'
  return u.studentId || u.facultyId || u.userId || '-'
})

const pwdFormRef = ref<FormInstance>()
const savingPwd = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const strength = ref(0)
const strengthLabel = ref('')
const validateConfirmPass = (_rule: any, value: string, cb: any) => {
  if (value !== pwdForm.newPassword) cb(new Error(t('register.passNotMatch')))
  else cb()
}
const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: () => t('profile.inputOldPwd'), trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: () => t('register.passwordMin'), trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPass, trigger: 'blur' }]
}

const savingPref = ref(false)
const prefForm = reactive({ categories: [] as string[], tags: [] as string[] })
const inputVisible = ref(false)
const inputValue = ref('')
const tagInputRef = ref<any>()

const categoryOptions = [
  { label: '文学', value: 'literature' }, { label: '科技', value: 'technology' },
  { label: '历史', value: 'history' }, { label: '艺术', value: 'art' },
  { label: '经济', value: 'economics' }, { label: '哲学', value: 'philosophy' }
]

const notifSettings = reactive<Record<string, any>>({
  borrowReminder: { label: t('profile.notifBorrow'), desc: t('profile.notifBorrowDesc'), value: true },
  dueWarning: { label: t('profile.notifDue'), desc: t('profile.notifDueDesc'), value: true },
  reservationNotice: { label: t('profile.notifReserve'), desc: t('profile.notifReserveDesc'), value: true },
  systemAnnounce: { label: t('profile.notifSystem'), desc: t('profile.notifSystemDesc'), value: true }
})

const typeTagType = computed(() => {
  const userType = (user.value?.userType || user.value?.type || 'READER').toUpperCase()
  const m: Record<string, string> = { READER: 'info', VIP: 'warning', TEACHER: 'success', STUDENT: '', ADMIN: 'danger' }
  return m[userType] || 'info'
})
const typeLabel = computed(() => {
  const userType = (user.value?.userType || user.value?.type || 'READER').toUpperCase()
  const m: Record<string, string> = { READER: '普通读者', VIP: 'VIP读者', TEACHER: '教师', STUDENT: '学生', ADMIN: '管理员' }
  return m[userType] || '普通读者'
})
const quotaPercent = computed(() => {
  if (!user.value) return 0
  const borrowed = user.value.borrowedCount ?? 0
  const limit = user.value.borrowLimit ?? 10
  if (limit <= 0) return 0
  return Math.min(Math.round((borrowed / limit) * 100), 100)
})
const quotaColor = computed(() => {
  if (quotaPercent.value >= 90) return '#F56C6C'
  if (quotaPercent.value >= 70) return '#E6A23C'
  return '#67C23A'
})

function checkStrength(val: string) {
  let s = 0
  if (val.length >= 6) s++
  if (/[A-Z]/.test(val) && /[a-z]/.test(val)) s++
  if (/\d/.test(val) && /[^A-Za-z0-9]/.test(val)) s++
  strength.value = s
  const labels = ['', t('profile.weak'), t('profile.medium'), t('profile.strong')]
  strengthLabel.value = labels[s] || ''
}

async function saveBasic() {
  const valid = await basicFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingBasic.value = true
  try {
    await userApi.updateProfile(basicForm)
    ElMessage.success(t('profile.saveSuccess'))
    authStore.getUserInfo()
  } catch (e: any) { ElMessage.error(e?.message || '') }
  finally { savingBasic.value = false }
}

async function changePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  savingPwd.value = true
  try {
    await authApi.changePassword({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    ElMessage.success(t('profile.pwdChanged'))
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
    strength.value = 0
    strengthLabel.value = ''
  } catch (e: any) { ElMessage.error(e?.message || '') }
  finally { savingPwd.value = false }
}

function showInput() {
  inputVisible.value = true
  nextTick(() => tagInputRef.value?.focus())
}
function addTag() {
  const val = inputValue.value.trim()
  if (val && !prefForm.tags.includes(val)) {
    prefForm.tags.push(val)
  }
  inputVisible.value = false
  inputValue.value = ''
}
function removeTag(tag: string) {
  prefForm.tags = prefForm.tags.filter(t => t !== tag)
}

async function savePreferences() {
  savingPref.value = true
  try {
    await userApi.updatePreferences(prefForm)
    ElMessage.success(t('profile.saveSuccess'))
  } catch (e: any) { ElMessage.error(e?.message || '') }
  finally { savingPref.value = false }
}

async function saveNotifSettings() {
  const payload: Record<string, boolean> = {}
  Object.keys(notifSettings).forEach(k => { payload[k] = notifSettings[k].value })
  try {
    await userApi.updateNotificationSettings(payload)
  } catch {}
}

function beforeAvatarUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) ElMessage.error(t('profile.imageOnly'))
  if (!isLt2M) ElMessage.error(t('profile.imageSize'))
  return isImage && isLt2M
}
async function handleUpload(options: UploadRequestOptions) {
  try {
    const res: any = await userApi.uploadAvatar(options.file)
    const avatarUrl = res.url || res.data?.url || ''
    if (avatarUrl && authStore.user) {
      authStore.user.avatar = avatarUrl
    }
    ElMessage.success(t('profile.avatarUploaded'))
    authStore.getUserInfo()
  } catch (e: any) { ElMessage.error(e?.message || '头像上传失败') }
}

async function loadStatsCharts() {
  if (activeTab.value !== 'stats') return
  await nextTick()
  try {
    const [trendRes, catRes] = await Promise.all([
      statsApi.getBorrowTrends().catch(() => ({ data: { months: [], values: [] } })),
      statsApi.getCategoryStats().catch(() => ({ data: [] }))
    ])
    renderBarChart(trendRes.data.months || [], trendRes.data.values || [])
    renderPieChart(catRes.data || [])
  } catch {}
}

function renderBarChart(months: string[], values: number[]) {
  const el = document.getElementById('stats-chart-bar')
  if (!el) return
  el.innerHTML = ''
  const maxVal = Math.max(...values, 1)
  months.forEach((m, i) => {
    const barWrap = document.createElement('div')
    barWrap.style.cssText = 'display:flex;flex-direction:column;align-items:center;flex:1;gap:4px'
    const h = Math.max((values[i] / maxVal) * 240, 4)
    const bar = document.createElement('div')
    bar.style.cssText = `width:32px;height:${h}px;background:linear-gradient(180deg,#409EFF,#66B1FF);border-radius:4px 4px 0 0;transition:height 0.5s`
    const lbl = document.createElement('span')
    lbl.style.cssText = 'font-size:11px;color:#909399'
    lbl.textContent = m.slice(5)
    const valLbl = document.createElement('span')
    valLbl.style.cssText = 'font-size:11px;font-weight:600;color:#303133'
    valLbl.textContent = String(values[i])
    barWrap.appendChild(valLbl)
    barWrap.appendChild(bar)
    barWrap.appendChild(lbl)
    el.appendChild(barWrap)
  })
  el.style.display = 'flex'
  el.style.alignItems = 'flex-end'
  el.style.gap = '8px'
  el.style.paddingTop = '20px'
}

function renderPieChart(data: { name: string; value: number }[]) {
  const el = document.getElementById('stats-chart-pie')
  if (!el) return
  el.innerHTML = ''
  const colors = ['#409EFF','#67C23A','#E6A23C','#F56C6C','#909399','#00CED1']
  const total = data.reduce((s, d) => s + d.value, 0) || 1
  data.forEach((d, idx) => {
    const pct = ((d.value / total) * 100).toFixed(1)
    const row = document.createElement('div')
    row.style.cssText = 'display:flex;align-items:center;gap:12px;padding:8px 0;border-bottom:1px solid #f0f0f0'
    const dot = document.createElement('span')
    dot.style.cssText = `width:14px;height:14px;border-radius:50%;background:${colors[idx % colors.length]};flex-shrink:0`
    const nameEl = document.createElement('span')
    nameEl.style.cssText = 'flex:1;font-size:13px;color:#303133'
    nameEl.textContent = d.name
    const valEl = document.createElement('span')
    valEl.style.cssText = 'font-size:13px;font-weight:600;color:#303133;width:50px;text-align:right'
    valEl.textContent = String(d.value)
    const pctEl = document.createElement('span')
    pctEl.style.cssText = 'font-size:12px;color:#909399;width:52px;text-align:right'
    pctEl.textContent = `${pct}%`
    row.append(dot, nameEl, valEl, pctEl)
    el.appendChild(row)
  })
}

watch(activeTab, (val) => {
  if (val === 'stats') loadStatsCharts()
})

onMounted(async () => {
  if (authStore.user) {
    basicForm.realName = authStore.user.realName || ''
    basicForm.email = authStore.user.email || ''
    basicForm.phone = authStore.user.phone || ''
    basicForm.campus = authStore.user.campus || ''
    basicForm.college = authStore.user.college || ''
    basicForm.grade = authStore.user.grade || ''
    basicForm.className = authStore.user.className || ''
    basicForm.counselor = authStore.user.counselor || ''
  }
})
</script>

<style scoped>
.profile-page { padding-top: 4px; }

.profile-sidebar { border-radius: var(--border-radius-base); text-align: center; }
.avatar-section { margin-bottom: 16px; }
.avatar-uploader { cursor: pointer; display: inline-block; }
.upload-tip { font-size: 12px; color: var(--color-text-placeholder); margin: 8px 0 0; }
.user-name { font-size: 20px; font-weight: 700; margin: 12px 0 8px; color: var(--color-text-primary); }

.identity-info { display: flex; flex-direction: column; align-items: center; gap: 4px; margin: 12px 0; padding: 10px; background: var(--color-bg-page); border-radius: var(--border-radius-sm); }
.identity-label { font-size: 11px; color: var(--color-text-secondary); }
.identity-value { font-size: 14px; font-weight: 600; color: var(--color-primary); }

.quota-section { text-align: left; margin: 20px 0; padding: 16px; background: var(--color-bg-page); border-radius: var(--border-radius-sm); }
.quota-header { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 10px; color: var(--color-text-regular); }

.quick-links { display: flex; flex-direction: column; gap: 4px; margin-top: 20px; text-align: left; }
.quick-link { display: flex; align-items: center; gap: 8px; padding: 10px 12px; border-radius: var(--border-radius-sm); color: var(--color-text-regular); text-decoration: none; font-size: 13px; transition: all 0.2s; }
.quick-link:hover { background: var(--color-bg-page); color: var(--color-primary); }

.profile-tabs { border-radius: var(--border-radius-base); }
.profile-tabs :deep(.el-tabs__content) { padding: 24px; }

.strength-bar { display: flex; align-items: center; gap: 4px; margin-top: 8px; }
.strength-segment { flex: 1; height: 4px; background: #e4e7ed; border-radius: 2px; transition: all 0.3s; }
.strength-segment.active.weak { background: #F56C6C; }
.strength-segment.active.medium { background: #E6A23C; }
.strength-segment.active.strong { background: #67C23A; }
.strength-text { font-size: 12px; color: var(--color-text-secondary); margin-left: 8px; }

.tag-input-area { display: flex; flex-wrap: wrap; gap: 4px; align-items: center; line-height: 28px; }

.notification-settings { display: flex; flex-direction: column; gap: 0; }
.notif-item { display: flex; justify-content: space-between; align-items: center; padding: 16px 0; border-bottom: 1px solid var(--color-border-light); }
.notif-item:last-child { border-bottom: none; }
.notif-label { font-size: 14px; color: var(--color-text-primary); font-weight: 500; display: block; }
.notif-desc { font-size: 12px; color: var(--color-text-secondary); margin-top: 2px; display: block; }
</style>
