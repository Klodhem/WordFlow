<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  localStorage.removeItem('token')
  router.push({ name: 'Login' })
}
</script>

<template>
  <header class="flex justify-between border-b border-slate-200 px-10 py-8">
    <router-link to="/">
      <div class="flex items-center gap-4">
<!--        <img src="/logo.png" alt="Logo" class="w-10"/>-->
        <div>
          <h2 class="text-xl font-bold">WordFlow</h2>
          <p class="text-slate-400">Платформа для изучения языков</p>
        </div>
      </div>
    </router-link>

    <ul class="flex items-center gap-10">
<!--      <li @click="() => emit('openDrawer')" class="flex items-center cursor-pointer gap-3 text-gray-500 hover:text-black">-->
<!--        <img src="/cart.svg" alt="Cart">-->
<!--        <b>{{ totalPrice }} руб.</b>-->
<!--      </li>-->

<!--      <router-link to="/favorites">-->
<!--        <li class="flex items-center cursor-pointer gap-3 text-gray-500 hover:text-black">-->
<!--          <img src="/heart.svg" alt="Heart">-->
<!--          <span>Закладки</span>-->
<!--        </li>-->
<!--      </router-link>-->
      <button @click="logout" class="px-4 py-2 bg-red-500 text-white rounded">
        Выйти
      </button>
      <li class="flex items-center cursor-pointer gap-3 text-gray-500 hover:text-black">
<!--        <img src="/profile.svg" alt="Profile">-->
        <span>Профиль</span>
      </li>
    </ul>
  </header>
</template>

<style scoped>

</style>
