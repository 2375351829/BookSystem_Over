<template>
  <div class="not-found-view">
    <div class="not-found-content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 404 页面
</script>

<style scoped>
.not-found-view {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 400px;
}

.not-found-content {
  text-align: center;
}

.not-found-content h1 {
  font-size: 120px;
  color: #409eff;
  margin-bottom: 16px;
  line-height: 1;
}

.not-found-content p {
  font-size: 18px;
  color: #909399;
  margin-bottom: 24px;
}
</style>
