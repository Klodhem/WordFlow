<script setup>
import apiClient from "@/axios.js";
import {onMounted, onUnmounted, ref} from "vue";

const invites = ref([])
const intervalId = ref(null);

const getInvite = async () => {
  try {
    const response = await apiClient.get('/groups/invite')
    invites.value = response.data
    console.log(invites)
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const accept = async invite => {
  try {
    const response = await apiClient.post(`/groups/accept`, null, {
      params: {
        inviteId: invite.inviteId
      }
    })
    if (response.data === true) {
      await getInvite()
      invites.value = invites.value.filter(s => s.inviteId !== invite.inviteId);
    }
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const decline = async invite => {
  try {
    const response = await apiClient.post(`/groups/decline`, null, {
      params: {
        inviteId: invite.inviteId
      }
    })
    if (response.data === true) {
      await getInvite()
      invites.value = invites.value.filter(s => s.inviteId !== invite.inviteId);
    }
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

onMounted(() => {
  getInvite()
  intervalId.value = setInterval(getInvite, 180000)
})
onUnmounted(() => {
  if (intervalId.value) {
    clearInterval(intervalId.value)
    intervalId.value = null
  }
})
</script>

<template>
  <div class="bg-gray-100 rounded overflow-y-auto flex justify-center items-center max-h-full">
    <ul v-if="!invites.value" class="shadow w-full px-4">
      <li
        v-for="(invite, index) in invites"
        :key="index"
        class="p-4 hover:bg-gray-200 rounded text-sm md:text-base flex flex-wrap justify-between items-center"
      >
        <span class="flex-1">Пользователь "{{
            invite.nameTeacher
          }}" пригласил вас в группу "{{ invite.groupName }}"</span>
        <div class="flex gap-2 shrink-0">
          <button
            @click="accept(invite)"
            class="px-3 py-1 text-white disabled:bg-slate-600 bg-green-600 hover:bg-green-700 rounded-lg text-sm"
          >
            Принять
          </button>
          <button
            @click="decline(invite)"
            class="px-3 py-1 text-white disabled:bg-slate-600 bg-red-600 hover:bg-red-700 rounded-lg text-sm"
          >
            Отклонить
          </button>
        </div>
      </li>
    </ul>
    <div v-if="invites.value" class="flex justify-center items-center h-full">
      <h3 class="text-base md:text-lg font-medium text-slate-600">У вас пока нет приглашений :(</h3>
    </div>
  </div>
</template>

<style scoped>

</style>
