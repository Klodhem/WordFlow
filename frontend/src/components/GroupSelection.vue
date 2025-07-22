<script setup>
import {onMounted, onUnmounted, ref} from "vue";
import apiClient from '@/axios.js';
import {useRouter} from 'vue-router'

const router = useRouter();
const groups = ref([])
const intervalId = ref(null);


const selectGroup = async group => {
  await router.push({
    name: 'DisplayingGroup',
    params: {groupId: group.groupId}
  })
}

const getGroups = async () => {
  const typeUser = localStorage.getItem('typeUser')
  try {
    if (typeUser === "ROLE_STUDENT") {
      const response = await apiClient.get('/groups')
      groups.value = response.data
    } else if (typeUser === "ROLE_TEACHER") {
      const response = await apiClient.get('/groups/managed')
      groups.value = response.data
    }
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

onMounted(() => {
  getGroups()
  intervalId.value = setInterval(getGroups, 180000)
})
onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value)
    intervalId.value = null
  }
})
</script>

<template>
  <div class="bg-gray-100 rounded overflow-y-auto">
    <ul>
      <li
        v-for="(group, index) in groups"
        :key="index"
        @click="selectGroup(group)"
        class="p-2 hover:bg-gray-200 rounded text-sm md:text-base flex justify-between items-center"
      >
        <span class="flex-1">{{ group.groupName }}</span>
      </li>
    </ul>
  </div>
</template>

<style scoped>

</style>
