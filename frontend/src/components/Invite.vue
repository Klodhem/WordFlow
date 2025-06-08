<script setup>
import {onMounted, onUnmounted, reactive, ref} from "vue";
import apiClient from '@/axios.js';
import {useRoute} from "vue-router";

const username = ref('')
const groupId = Number(useRoute().params.groupId)
const intervalId = ref(null);
const students = ref([])

const errors = reactive({
  username: '',
});

const inviteStudent = async () => {
  errors.username = username.value ? '' : 'Введите имя пользователя';
  if (errors.username || errors.password) return;

  try {
    await apiClient.post(`/groups/${groupId}/invite`, null, {
      params: {username: username.value}
    })
    await getMembers()
  } catch (err) {
    console.error(err)
    alert('Ошибка при попытке пригласить пользователя')
  }
}

const getMembers = async () => {
  try {
    const response = await apiClient.get(`/groups/${groupId}/members`)
    students.value = response.data
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const removeMember = async student => {
  try {
    const response = await apiClient.delete(`/groups/${groupId}/member`, {
      params: {studentId: student.userId}
    })
    if (response.data === true) {
      students.value = students.value.filter(s => s.username !== student.username);
    }
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

onMounted(() => {
  getMembers()
  intervalId.value = setInterval(getMembers, 120000)
})
onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value)
    intervalId.value = null
  }
})
</script>

<template>
  <form novalidate class="space-y-4 md:space-y-6" @submit.prevent="inviteStudent">
    <div>
      <label for="username" class="block mb-2 text-sm font-medium text-gray-900 ">Имя
        пользователя</label>
      <input type="text" name="username" id="username" v-model="username"
             :class="errors.username ? 'border-red-500' : 'border-gray-300'"
             class="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 "
             placeholder=". . ." required="">
      <p v-if="errors.username" class="text-red-500 text-sm mt-1">
        {{ errors.username }}
      </p>
    </div>
    <button type="submit"
            class="w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
      Пригласить
    </button>
  </form>

  <div class="bg-gray-100 rounded shadow overflow-y-auto" :key="groupId">
    <ul>
      <li
        v-for="(student, index) in students"
        :key="index"
        class="p-2 hover:bg-gray-200 rounded text-sm md:text-base flex justify-between items-center"
      >
        <span class="flex-1">Пользователь: {{ student.username }}</span>
        <button @click="removeMember(student)"
                class="px-3 py-1 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">
          {{
            student.status === 'EXPECTED' ? 'Отозвать приглашение' : (student.status === 'DECLINED' ? 'Удалить запись' : 'Исключить из группы')
          }}
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped>

</style>
