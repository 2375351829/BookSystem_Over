<template>
  <router-view />
</template>

<script setup lang="ts">
// 根组件 - 仅渲染路由视图
</script>

<style>
:root {
  --color-primary: #409eff;
  --color-bg-page: #f5f7fa;
  --color-bg-card: #ffffff;
  --color-border-light: #e4e7ed;
  --color-text-regular: #606266;
  --header-height: 56px;
  --sidebar-width: 200px;
  --sidebar-collapsed-width: 64px;
  --content-padding: 20px;
  --shadow-sm: 0 1px 4px rgba(0, 0, 0, 0.08);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #303133;
  background-color: var(--color-bg-page);
}

#app {
  width: 100%;
  height: 100vh;
}
</style>
