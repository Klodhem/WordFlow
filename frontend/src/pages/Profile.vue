<script setup>
import {getCurrentInstance, onMounted, ref} from "vue";
import { useRouter } from 'vue-router'

const { proxy } = getCurrentInstance();
const user = ref({});
const router = useRouter()

const logout = () => {
  localStorage.removeItem('authToken')
  router.push({ name: 'Login' })
}
const getUser = async () => {
  try {
    const response = await proxy.$axios.get('http://localhost:8080/user/getUser')
    user.value = response.data
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

onMounted(() => {
  getUser()
})
</script>

<template>
  <div class="mt-8 gap-10 p-4">
    <div class="mb-6">
      <p>Имя пользователя: {{user.username}}</p>
      <p>Почта: {{user.email}}</p>
    </div>

    <button @click="logout" class="px-4 py-2 bg-red-500 text-white rounded">
      Выйти
    </button>
  </div>
</template>
