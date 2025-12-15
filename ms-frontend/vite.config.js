import { defineConfig } from 'vite'

// Dev proxy so Vite serves API requests to local microservices (avoids CORS during dev)
export default defineConfig({
  server: {
    proxy: {
      '/api/v1/products': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      },
      '/api/v1/users': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/api/v1/orders': {
        target: 'http://localhost:8083',
        changeOrigin: true,
        secure: false
      },
      '/metrics': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
