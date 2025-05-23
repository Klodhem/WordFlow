<script setup>

</script>

<template>
  <header class="flex justify-between border-b border-slate-200 px-10 py-8">
    <router-link to="/">
      <div class="flex items-center gap-4">
        <img src="/logo.png" alt="Logo" class="w-10"/>
        <div>
          <h2 class="text-xl font-bold">WordFlow</h2>
          <p class="text-slate-400">Платформа для изучения языков</p>
        </div>
      </div>
    </router-link>

    <ul class="flex items-center gap-10">
      <li class="flex items-center cursor-pointer gap-3 text-gray-500 hover:text-black">
        <router-link to="/Profile" class="flex items-center gap-3">
          <!-- <img src="/profile.svg" alt="Profile"> -->
          <span>Профиль</span>
        </router-link>
      </li>
    </ul>
  </header>
</template>

<style scoped>

</style>
