import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'
import {createRouter, createWebHistory} from "vue-router";
import store from './store/store.js'
import Aura from '@primeuix/themes/aura';
import PrimeVue from 'primevue/config';
import MultiSelect from 'primevue/multiselect'
import Home from "@/pages/Home.vue";
import Login from "@/pages/Login.vue";
import Registration from "@/pages/Registration.vue";
import apiClient from './axios.js';
import {autoAnimatePlugin} from '@formkit/auto-animate/vue'
import '@mdi/font/css/materialdesignicons.css'
import Profile from "@/pages/Profile.vue";
import Groups from "@/pages/Groups.vue";
import DisplayingGroup from "@/pages/DisplayingGroup.vue";

const app = createApp(App)


const routes = [
  {path: '/', name: 'Home', component: Home, meta: {requiresAuth: true}},
  {path: '/groups', name: 'Groups', component: Groups, meta: {requiresAuth: true}},
  {path: '/profile', name: 'Profile', component: Profile, meta: {requiresAuth: true}},
  {path: '/displaying-group/:groupId', name: 'DisplayingGroup', component: DisplayingGroup, meta: {requiresAuth: true}},
  {path: '/login', name: 'Login', component: Login},
  {path: '/registration', name: 'Registration', component: Registration}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('authToken')
  if (to.meta.requiresAuth && !token) {
    return next({name: 'Login'})
  }
  next()
})


app.config.globalProperties.$axios = apiClient;

app.use(router)
app.use(store)
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      primary: 'purple',
      darkModeSelector: '.p-light'
    }
  }
});
app.component('MultiSelect', MultiSelect)
app.use(autoAnimatePlugin)

app.mount('#app')
