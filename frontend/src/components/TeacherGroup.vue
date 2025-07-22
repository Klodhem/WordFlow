<script setup>

import Invite from "@/components/Invite.vue";
import apiClient from '@/axios.js';
import {computed, onMounted, ref} from "vue";
import MultiSelect from 'primevue/multiselect'
import {vAutoAnimate} from '@formkit/auto-animate'
import {useRoute} from "vue-router";
import store from "@/store/store.js";

const students = ref([])
const videos = ref([])
const videosTeacher = ref([])
const selectedVideosTeacher = ref([])
const selectedStudent = ref(null);
const selectedVideo = ref(null);
const intervalId = ref(null);
const solutionsHistory = ref([])
const groupId = Number(useRoute().params.groupId)
const showHistory = ref(false)
const openAttempts = ref([])


function formatDate(dateTime) {
  const [date, time] = dateTime.split('T')
  return `${date} ${time.substring(0, 8)}`
}

function toggle(id) {
  const idx = openAttempts.value.indexOf(id)
  if (idx === -1) openAttempts.value.push(id)
  else openAttempts.value.splice(idx, 1)
}

const questionMap = computed(() => {
  const map = new Map()
  store.state.test.forEach(q => map.set(q.questionId, q))
  return map
})

const getStudentsAndVideosGroup = async () => {
  try {
    let response = await apiClient.get(`/groups/${groupId}/students`)
    students.value = response.data
    response = await apiClient.get(`/videos/group/${groupId}`)
    videos.value = response.data
    selectedVideosTeacher.value = response.data
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}
const getVideosTeacher = async () => {
  try {
    const response = await apiClient.get('/videos')
    videosTeacher.value = response.data
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

function isOpen(id) {
  return openAttempts.value.includes(id)
}

const updateGroupVideos = async () => {
  const cleanData = JSON.parse(JSON.stringify(selectedVideosTeacher.value))
  console.log(cleanData)
  try {
    const response = await apiClient.post(`/groups/${groupId}/videos`, cleanData)
    if (response.data === true) {
      alert("Видео обновлены")
    }
  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const getStudentHistory = async () => {
  const responseHistorySolution = await apiClient.get(`/tests/${selectedVideo.value.videoId}/historyStudent`,
    {
      params: {
        groupId: groupId,
        studentId: selectedStudent.value.userId
      },
    }
  )
  solutionsHistory.value = responseHistorySolution.data;

};

onMounted(() => {
  getStudentsAndVideosGroup()
  getVideosTeacher()
  intervalId.value = setInterval(getStudentsAndVideosGroup, 180000)
})
</script>

<template>
  <div :key="groupId" class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <div class="bg-gray-100 rounded shadow flex flex-col p-2 gap-4">
      <div class="card w-full">
        <MultiSelect v-model="selectedVideosTeacher" display="chip" :options="videosTeacher"
                     optionLabel="title" filter
                     placeholder="Выберите видео которые будут доступны группе" class="w-full"/>
        <button @click="updateGroupVideos"
                class="mt-2 w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
          Обновить доступные видео
        </button>
      </div>
      <h3 class="text-base md:text-lg font-medium">Просмотр результатов студента</h3>
      <div class="flex gap-4">
        <div class="flex-1">
          <label for="language" class="block mb-2 text-sm font-medium text-gray-900">Выберите
            студента</label>
          <select id="language" v-model="selectedStudent"
                  class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
            <option v-for="student in students" :key="student.userId" :value="student">
              {{ student.username }}
            </option>
          </select>
        </div>
        <div class="flex-1">
          <label for="languageTranslate" class="block mb-2 text-sm font-medium text-gray-900">Выберите
            видео</label>
          <select id="languageTranslate" v-model="selectedVideo"
                  class="w-full bg-gray-200 border border-gray-300 text-gray-700 text-sm rounded-lg focus:ring-gray-950 focus:border-gray-950 block p-2.5">
            <option v-for="video in videos" :key="video.videoId" :value="video">
              {{ video.title }}
            </option>
          </select>
        </div>
      </div>
      <button @click="getStudentHistory"
              class="w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">
        Показать результаты студента
      </button>
      <div class="bg-gray-200 rounded shadow">
        <h2
          class="cursor-pointer select-none block text-xl font-medium text-gray-900 ml-4"
          @click="showHistory = !showHistory">
          История
          <span class="ml-2">{{ showHistory ? '▾' : '▸' }}</span>
        </h2>

        <div v-auto-animate class="mt-2 overflow-x-hidden">
          <div
            v-if="showHistory"
            class="rounded-md shadow-lg overflow-hidden bg-white">
            <div v-if="solutionsHistory.length !== 0" class="max-h-96 overflow-y-auto p-2">

              <div v-auto-animate>
                <div
                  v-for="attempt in solutionsHistory.slice().reverse()"
                  :key="attempt.solutionId"
                  class="mt-4 bg-gray-100 p-4 rounded shadow"
                >
                  <div
                    class="flex justify-between items-center cursor-pointer"
                    @click="toggle(attempt.solutionId)"
                  >
                    <span>Оценка: {{ attempt.mark }} (дата и время: {{
                        formatDate(attempt.dateTime)
                      }})</span>
                    <span>{{ isOpen(attempt.solutionId) ? '▾' : '▸' }}</span>
                  </div>

                  <div
                    v-if="isOpen(attempt.solutionId)"
                    class="mt-2 bg-white p-4 rounded shadow"
                  >
                    <table class="table-auto border border-collapse w-full">
                      <thead>
                      <tr class="bg-gray-200">
                        <th class="border px-4 py-2 text-left">Вопрос</th>
                        <th class="border px-4 py-2 text-left">Оценка</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr v-for="sheet in attempt.userAnswerSheetList" :key="sheet.questionId">
                        <td class="border px-4 py-2">
                          {{sheet.questionText}}
                        </td>
                        <td class="border px-4 py-2">{{ sheet.mark }}</td>
                      </tr>
                      </tbody>
                    </table>

                  </div>
                </div>
              </div>

            </div>
            <div v-if="solutionsHistory.length === 0">
              <span>История по данному видео отсутствует</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-gray-100 rounded shadow p-2 flex flex-col gap-4">
      <Invite></Invite>


    </div>
  </div>
</template>

<style scoped>

</style>
