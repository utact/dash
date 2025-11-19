import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/oauth2': 'http://localhost:8080',
      '/api': 'http://localhost:8080'
    }
  }
})
