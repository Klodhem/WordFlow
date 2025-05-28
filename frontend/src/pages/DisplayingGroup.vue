<script setup>
import {computed, onMounted, onUnmounted, ref} from 'vue';
import TeacherGroup from "@/components/TeacherGroup.vue";
import StudentGroup from "@/components/StudentGroup.vue";
import store from "@/store/store.js";

const typeUser = ref('')

const componentMap = {
  ROLE_TEACHER: TeacherGroup,
  ROLE_STUDENT: StudentGroup,
};

const currentComponent = computed(() => componentMap[typeUser.value]);
onMounted(() => {
  typeUser.value = localStorage.getItem('typeUser')
})

onUnmounted(()=>{
  store.state.selectedGroupId=null;
})
</script>

<template>
  <component :is="currentComponent" />
</template>

<style scoped>

</style>
