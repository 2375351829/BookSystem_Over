<template>
  <div class="login-page">
    <div class="login-left">
      <div class="left-content">
        <el-icon :size="80" class="welcome-icon"><Reading /></el-icon>
        <h1 class="welcome-title">{{ t('login.welcome') }}</h1>
        <p class="welcome-subtitle">{{ t('login.subtitle') }}</p>
        <div class="feature-list">
          <div class="feature-item"><el-icon><Collection /></el-icon><span>{{ t('login.feature1') }}</span></div>
          <div class="feature-item"><el-icon><Search /></el-icon><span>{{ t('login.feature2') }}</span></div>
          <div class="feature-item"><el-icon><Timer /></el-icon><span>{{ t('login.feature3') }}</span></div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="lang-switch">
        <el-button text @click="toggleLocale">
          {{ locale === 'zh-CN' ? 'EN' : '中文' }}
        </el-button>
      </div>
      <div class="login-card">
        <h2 class="card-title">{{ t('login.title') }}</h2>
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" :placeholder="t('login.username')" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" :placeholder="t('login.password')" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item prop="captcha">
            <div class="captcha-row">
              <el-input v-model="form.captcha" :placeholder="t('login.captcha')" prefix-icon="Key" @keyup.enter="handleLogin" />
              <img :src="captchaUrl" class="captcha-img" @click="refreshCaptcha" :alt="t('login.refreshCaptcha')" />
            </div>
          </el-form-item>
          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="form.remember">{{ t('login.rememberMe') }}</el-checkbox>
              <router-link to="/forgot-password" class="link">{{ t('login.forgotPassword') }}</router-link>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">{{ t('login.loginBtn') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
      <p class="copyright">&copy; {{ new Date().getFullYear() }} {{ t('login.copyright') }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Reading, Collection, Search, Timer } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/authStore'
import { authApi } from '@/api/auth'

const { t, locale } = useI18n()
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const captchaUrl = ref('')
const captchaId = ref('')

const form = reactive({
  username: '',
  password: '',
  captcha: '',
  remember: false
})

const rules: FormRules = {
  username: [{ required: true, message: () => t('login.usernameRequired'), trigger: 'blur' }],
  password: [{ required: true, message: () => t('login.passwordRequired'), trigger: 'blur' }, { min: 6, message: () => t('login.passwordMin'), trigger: 'blur' }],
  captcha: [{ required: true, message: () => t('login.captchaRequired'), trigger: 'blur' }]
}

function toggleLocale() {
  locale.value = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
}

async function refreshCaptcha() {
  try {
    const res: any = await authApi.getCaptcha()
    if (res.code === 200) {
      captchaUrl.value = res.captchaImage
      captchaId.value = res.captchaId
    }
  } catch (e) {
    console.error('Failed to get captcha:', e)
  }
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login({ username: form.username, password: form.password, captcha: form.captcha, remember: form.remember })
    ElMessage.success(t('login.success'))
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    ElMessage.error(e?.message || t('login.failed'))
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: 100vh;
}
.login-left {
  width: 40%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}
.login-left::before {
  content: '';
  position: absolute;
  width: 300px; height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.08);
  top: -60px; right: -60px;
}
.login-left::after {
  content: '';
  position: absolute;
  width: 200px; height: 200px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  bottom: -40px; left: -40px;
}
.left-content {
  text-align: center;
  z-index: 1;
  padding: 40px;
}
.welcome-icon { margin-bottom: 24px; opacity: 0.95; }
.welcome-title { font-size: 32px; font-weight: 700; margin: 0 0 12px; }
.welcome-subtitle { font-size: 16px; opacity: 0.85; margin: 0 0 40px; }
.feature-list { display: flex; flex-direction: column; gap: 16px; }
.feature-item { display: flex; align-items: center; justify-content: center; gap: 10px; font-size: 15px; opacity: 0.9; }

.login-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-page);
  padding: 40px;
  position: relative;
}
.lang-switch { position: absolute; top: 20px; right: 24px; }
.login-card {
  width: 420px;
  background: var(--color-bg-card);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  padding: 40px;
}
.card-title { font-size: 24px; font-weight: 700; text-align: center; margin: 0 0 30px; color: var(--color-text-primary); }
.captcha-row { display: flex; width: 100%; gap: 12px; align-items: center; }
.captcha-row .el-input { flex: 1; }
.captcha-img { height: 40px; cursor: pointer; border-radius: var(--border-radius-sm); border: 1px solid var(--color-border); flex-shrink: 0; }
.form-options { display: flex; justify-content: space-between; align-items: center; width: 100%; }
.link { color: var(--color-primary); text-decoration: none; font-size: 13px; }
.link:hover { text-decoration: underline; }
.login-btn { width: 100%; }
.card-footer { text-align: center; font-size: 13px; color: var(--color-text-secondary); margin-top: 8px; }
.card-footer .link { margin-left: 4px; font-weight: 500; }
.third-party-login { display: flex; justify-content: center; gap: 16px; }
.copyright { font-size: 12px; color: var(--color-text-placeholder); margin-top: 24px; }
</style>
