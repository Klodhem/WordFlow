<script setup>

import store from "@/store/store.js";
import { vAutoAnimate } from '@formkit/auto-animate'
import {computed, ref} from "vue";
import apiClient from "@/axios.js";
import {useRoute} from "vue-router";
const route = useRoute()

const groupId = Number(useRoute().params.groupId)
const showTest = ref(false)
const showHistory = ref(false)
const solutionsHistory = ref([])
const resultTest = ref([])
const solutionSend = ref(false)
const formKey = ref(0)
const openAttempts = ref([])
const isDisplaying = computed(() => route.path.includes('/displaying-group'))

const submitTest = async () => {
  const questions = store.state.test.map(q => {
    let sel = store.state.userAnswers[q.questionId]
    if (q.type === 'MULTIPLE') {
      sel = Array.isArray(sel) ? sel : []
    }
    const answers = q.answers.map(ans => ({
      ...ans,
      correct:
        q.type === 'SINGLE'
          ? sel === ans.answerId
          : sel.includes(ans.answerId)
    }))
    return {...q, answers}
  })

  try {
    const resp = await apiClient.post('/test/solution', questions,
      {
        params: {
          videoId: store.state.selectedVideo.videoId,
          groupId: groupId
        },
        headers: {'Content-Type': 'application/json'}
      }
    )
    resultTest.value = resp.data;
    solutionSend.value = true;
  } catch (err) {
    console.error(err)
    alert('Не удалось отправить тест')
  }
  const responseHistorySolution = await apiClient.get('/test/history',
    {
      params: {videoId: store.state.selectedVideo.videoId},
    }
  )
  solutionsHistory.value = responseHistorySolution.data;

};

function toggle(id) {
  const idx = openAttempts.value.indexOf(id)
  if (idx === -1) openAttempts.value.push(id)
  else openAttempts.value.splice(idx, 1)
}

function isOpen(id) {
  return openAttempts.value.includes(id)
}

const questionMap = computed(() => {
  const map = new Map()
  store.state.test.forEach(q => map.set(q.questionId, q))
  return map
})

function formatDate(dateTime) {
  const [date, time] = dateTime.split('T')
  return `${date} ${time.substring(0, 8)}`
}

function tryTestAgain() {
  solutionSend.value = false;
  resultTest.value = null;

  const currentQuestionIds = store.state.test.map(q => q.questionId);
  currentQuestionIds.forEach(id => delete store.state.userAnswers[id]);

  store.state.test.forEach(q => {
    if (q.type === 'SINGLE') {
      store.state.userAnswers[q.questionId] = null;
    } else {
      store.state.userAnswers[q.questionId] = [];
    }
  });

  formKey.value++;
}

const generateTest = async () => {
  try {
    await apiClient.post('/test/generate',{},
      {
        params: {videoId: store.state.selectedVideo.videoId},
      });

  } catch (err) {
    console.log('Ошибка запроса:', err.message)
  }
}

const mergedResults = computed(() =>
  store.state.test.map(q => {
    const sheet = resultTest.value.userAnswerSheetList
      .find(s => s.questionId === q.questionId)
    return {
      ...q,
      userMark: sheet ? sheet.mark : 0
    }
  })
)

</script>

<template>
  <div class="bg-gray-200 rounded shadow mb-5">
    <h2
      class="cursor-pointer select-none block text-2xl font-medium text-gray-900 ml-4"
      @click="showTest = !showTest">
      Тест
      <span class="ml-2">{{ showTest ? '▾' : '▸' }}</span>
    </h2>

    <div v-auto-animate class="overflow-x-hidden">
      <div
        v-if="showTest"
        class="rounded-lg shadow-lg overflow-hidden bg-white">
        <div v-if="store.state.test.length !== 0&&!solutionSend" class="ml-4">
          <form id="testForm" :key="formKey" @submit.prevent="submitTest">
            <fieldset v-for="(q, i) in store.state.test" :key="q.questionId" style="margin-bottom:1em;">
              <legend>{{ i + 1 }}. {{ q.text }}</legend>

              <div v-if="q.type === 'SINGLE'">
                <label v-for="ans in q.answers" :key="ans.answerId" style="display:block;">
                  <input
                    type="radio"
                    :name="'q' + q.questionId"
                    :value="ans.answerId"
                    v-model="store.state.userAnswers[q.questionId]"
                  />
                  {{ ans.text }}
                </label>
              </div>

              <div v-else-if="q.type === 'MULTIPLE'">
                <label v-for="ans in q.answers" :key="ans.answerId" style="display:block;">
                  <input
                    type="checkbox"
                    :value="ans.answerId"
                    v-model="store.state.userAnswers[q.questionId]"
                  />
                  {{ ans.text }}
                </label>
              </div>
            </fieldset>
            <button type="submit" class="px-3 py-2 font-semibold text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">Завершить тест</button>
          </form>
        </div>
        <div v-if="store.state.test.length !== 0&&solutionSend" class="gap-20">
          <div class="w-full p-4 border border-gray-200 rounded-lg shadow-sm bg-gray-700">
            <h5 class="mb-2 text-xl font-semibold tracking-tight text-gray-900 dark:text-white">Общая оценка: {{resultTest.mark}}/100</h5>
          </div>
          <table class="table-auto border border-collapse w-full mt-4">
            <thead>
            <tr class="bg-gray-200">
              <th class="border px-4 py-2 text-left">Вопрос</th>
              <th class="border px-4 py-2 text-left">Оценка</th>
            </tr>
            </thead>
            <tbody class="">
            <tr v-for="item in mergedResults" :key="item.questionId">
              <td class="border px-4 py-2">{{ item.text }}</td>
              <td class="border px-4 py-2">{{ item.userMark }}</td>
            </tr>
            </tbody>
          </table>
          <button type="button" @click="tryTestAgain" class="mt-4 mb-4 font-semibold px-3 py-3 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10"
          >Повторить попытку</button>
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
                        <span>Оценка: {{ attempt.mark }} (дата и время: {{ formatDate(attempt.dateTime) }})</span>
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
                            <td class="border px-4 py-2">{{ questionMap.get(sheet.questionId)?.text || 'Вопрос не найден' }}</td>
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
        <div v-if="store.state.test.length === 0" class="ml-4">
          <div v-if="!isDisplaying">
            <p class="mt-2 text-lg font-semibold tracking-tight text-gray-900 ">По данному видео еще не сгенерирован тест.</p>
            <button @click="generateTest" class="mt-3 mb-4 font-semibold px-3 py-3 text-white disabled:bg-slate-600 bg-gray-700 hover:bg-gray-800 rounded-lg text-sm relative z-10">Сгенерировать</button>
          </div>
          <div v-if="isDisplaying">
            <p class="mt-2 text-lg font-semibold tracking-tight text-gray-900 ">Преподаватель еще не создал тест на выбранное видео.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>
