import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import {createRouter, createWebHistory} from "vue-router";
import Home from "@/pages/Home.vue";
import Login from "@/pages/Login.vue";
import Registration from "@/pages/Registration.vue";
import apiClient from './axios.js';
import '@mdi/font/css/materialdesignicons.css'

const app = createApp(App)


const routes =[
  {path: '/', name: 'Home',component: Home, meta: { requiresAuth: true }},
  {path: '/Login', name: 'Login', component: Login},
  {path: '/Registration', name: 'Registration', component: Registration}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken')
  if (to.meta.requiresAuth && !token) {
    return next({ name: 'Login' })
  }
  next()
})

app.config.globalProperties.$axios = apiClient;

app.use(router)

app.mount('#app')
