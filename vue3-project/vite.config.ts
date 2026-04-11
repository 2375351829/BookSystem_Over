import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    host: '0.0.0.0',
    port: 8081,
    strictPort: false,
    proxy: {
      '/api': {
        target: 'http://10.5.150.205:8080',
        changeOrigin: true,
        secure: false
      },
      '/uploads': {
        target: 'http://10.5.150.205:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    target: 'esnext',
    sourcemap: false,
    chunkSizeWarningLimit: 1000
  }
})
