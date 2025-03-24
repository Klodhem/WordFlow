<script setup>
import Header from "@/components/Header.vue";
</script>

<template>
  <div class="mb-32">
    <Header />
    <div class="bg-white rounded-xl shadow-xl mt-8 mx-4 md:mx-12 gap-10 p-4">
      <keep-alive><router-view></router-view></keep-alive>
    </div>
  </div>
</template>
