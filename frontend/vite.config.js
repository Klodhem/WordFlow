import { fileURLToPath, URL } from 'node:url'

import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'


// https://vite.dev/config/
export default ({ mode }) => {
  // Загружаем env переменные вручную
  const env = loadEnv(mode, process.cwd(), 'VITE_')

  const proxyTarget = env.VITE_PROXY_TARGET || 'http://localhost:8181'

  return defineConfig({
    plugins: [vue(), vueJsx(), vueDevTools()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      proxy: {
        '^/(auth|groups|speech|tests|users|videos)': {
          target: proxyTarget,
          changeOrigin: true,
          secure: false,
          bypass: (req) => {
            if (req.headers.accept?.includes('text/html')) {
              return req.url
            }
          },
        },
      },
      historyApiFallback: true,
    },
  })
}
