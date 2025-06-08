<script setup>
import GroupSelection from "@/components/GroupSelection.vue";
import {onMounted, ref} from "vue";
import apiClient from "@/axios.js";
import InvitingStudents from "@/components/InvitingStudents.vue";

const typeUser = ref('')
const groupName = ref('')
const error = ref(null)

const createGroup = () => {
  error.value = null

  if (!groupName.value.trim()) {
    error.value = 'Введите название группы'
    return
  }

  try {
    const response = apiClient.post('/groups/create', {
      groupName: groupName.value,
    })
    groupName.value = '';
    console.log(response);
  } catch (err) {
    console.error(err)
    alert('Ошибка при попытке пригласить пользователя')
  }
}


onMounted(() => {
  typeUser.value = localStorage.getItem('typeUser')
})
</script>

<template>
  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div class="bg-gray-100 rounded shadow flex flex-col p-2 gap-4">
      <h3 class="text-base md:text-lg font-medium">Выберите группу</h3>
      <GroupSelection></GroupSelection>
    </div>

    <div class="bg-gray-100 rounded shadow p-2 flex flex-col gap-4">
      <div v-if="typeUser === 'ROLE_TEACHER'">
        <h3 class="block text-lg font-medium text-gray-900 ">Создать новую группу</h3>
        <form novalidate class="space-y-4 md:space-y-6" @submit.prevent="createGroup">
          <div>
            <label for="groupName"
                   class="block mb-2 text-sm font-medium text-gray-900 ">Название</label>
            <input type="text" name="groupName" id="groupName" v-model="groupName"
                   :class="error ? 'border-red-500' : 'border-gray-300'"
                   class="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 "
                   placeholder=". . ." required="">
            <p v-if="error" class="text-red-500 text-sm mt-1">
              {{ error }}
            </p>
          </div>
          <button type="submit"
                  class="w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
            Создать
          </button>
        </form>
      </div>

      <div v-if="typeUser === 'ROLE_STUDENT'" class="h-[60vh] overflow-hidden flex flex-col">
        <h3 class="block text-lg font-medium text-gray-900 ">Приглашения</h3>
        <InvitingStudents></InvitingStudents>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>
