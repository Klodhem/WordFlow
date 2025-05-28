<script setup>
import {onMounted, ref} from "vue";

import store from "@/store/store.js";

const videoPlayer = ref(null);

onMounted(async () => {
  store.commit('SET_VIDEO_PLAYER', videoPlayer)
});
</script>

<template>
  <div class="mx-auto" v-if="store.state.selectedVideo">
    <video ref="videoPlayer" class="w-full aspect-video bg-gray-900 rounded-lg" controls crossOrigin="anonymous">
      <source :src="store.state.videoUrl" type="video/mp4" />
      <track
        label="original"
        kind="subtitles"
        srclang="or"
        :src="store.state.originalSubtitleUrl"
        default />
      <track
        label="translate"
        kind="subtitles"
        srclang="tr"
        :src="store.state.translatedSubtitleUrl"
      />
    </video>
  </div>
</template>

<style scoped>

</style>
