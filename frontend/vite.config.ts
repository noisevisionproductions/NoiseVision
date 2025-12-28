import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import * as path from "node:path";

export default defineConfig({
    plugins: [react()],
    define: {
        __API_URL__: JSON.stringify(process.env.VITE_API_URL || 'https://backend-service-dot-noisevision.appspot.com'),
    },
    base: '/',
    build: {
        outDir: 'dist',
        assetsDir: 'assets',
        sourcemap: false,
        emptyOutDir: true
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    server: {
        port: Number(process.env.PORT) || 3000,
        host: '0.0.0.0',
        open: true,
        strictPort: true,
        proxy: {
            '/api': {
                target: process.env.VITE_API_URL || 'http://localhost:8080',
                changeOrigin: true,
                secure: false
            }
        }
    }
});