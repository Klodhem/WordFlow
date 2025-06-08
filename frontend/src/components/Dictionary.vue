<script setup>

import store from "@/store/store.js";
import apiClient from "@/axios.js";
import {ref} from "vue";
import {vAutoAnimate} from '@formkit/auto-animate'

const selectedLanguage = ref(null);
const speechRate = ref(1.0);
const showTableDictionary = ref(false)


async function playSynthesizedSpeech(text) {
  try {
    const response = await apiClient.get('/speech/synthesize', {
      params: {
        text: text,
        language: selectedLanguage.value,
        speechRate: speechRate.value
      },
      responseType: 'blob'
    })
    const audioBlob = new Blob([response.data], {type: 'audio/wav'})
    const audioUrl = URL.createObjectURL(audioBlob)
    const audio = new Audio(audioUrl)
    await audio.play()
  } catch (error) {
    console.error('Ошибка синтеза речи:', error)
  }
}
</script>

<template>
  <div class="bg-gray-200 rounded shadow">
    <h2
      class="cursor-pointer select-none block text-2xl font-medium text-gray-900 ml-4"
      @click="showTableDictionary = !showTableDictionary">
      Словарь
      <span class="ml-2">
        {{ showTableDictionary ? '▾' : '▸' }}
      </span>
    </h2>

    <div v-auto-animate class="mt-2 overflow-x-hidden">
      <div
        v-if="showTableDictionary"
        class="rounded-lg shadow-lg overflow-hidden bg-white"
      >
        <table class="w-full table-fixed divide-y divide-gray-200">
          <thead class="bg-gray-500">
          <tr>
            <th class="w-1/2 px-4 py-3 text-xl font-medium uppercase text-gray-50">
              Оригинал
            </th>
            <th class="w-1/2 px-4 py-3 text-xl font-medium uppercase text-gray-50">
              Перевод
            </th>
          </tr>
          </thead>
          <tbody class="bg-gray-200 divide-y divide-gray-200">
          <tr
            v-for="(pair, idx) in store.state.testPairs"
            :key="idx"
            class="group hover:bg-gray-300 transition-colors"
          >
            <td class="px-2 py-3 align-top border-r-2 border-gray-200 break-words">
              <div class="flex items-center gap-2">
                <span class="flex-1">{{ pair.original }}</span>
                <button @click="playSynthesizedSpeech(pair.original)">
                  <span class="mdi mdi-play-circle"></span>
                </button>
              </div>
            </td>
            <td class="px-2 py-3 align-top break-words">
              <div class="flex items-center gap-2">
                <span class="flex-1">{{ pair.translated }}</span>
                <button @click="playSynthesizedSpeech(pair.translated)">
                  <span class="mdi mdi-play-circle"></span>
                </button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>


<style>
.mdi {
  font-size: 24px;
}
</style>
