<script setup>
import {reactive, ref} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const router = useRouter()

const username = ref('')
const password = ref('')

const errors = reactive({
  username: '',
  password: ''
});

const loginForm = async () => {
  errors.username = username.value ? '' : 'Введите имя пользователя';
  errors.password = password.value ? '' : 'Введите пароль';

  if (errors.username || errors.password) return;

  try {
    const response = await axios.post('http://localhost:8080/auth/login', {
      username: username.value,
      password: password.value
    })
    const token = response.data['jwt-token'];
    if (!token) {
      console.error('Токен отсутствует в ответе сервера');
      return;
    }

    const payload = JSON.parse(atob(token.split('.')[1]));
    const expirationTime = payload.exp * 1000;
    const typeUser = payload.role;

    localStorage.setItem('authToken', token);
    localStorage.setItem('tokenExpiration', expirationTime);
    localStorage.setItem('typeUser', typeUser);
    await router.push({name: 'Home'})
  } catch (err) {
    console.error(err)
    alert('Ошибка авторизации')
  }
}
</script>

<template>
  <section class="mb-16">
    <div class="flex flex-col items-center justify-center px-6 py-8 mx-auto lg:py-0">
      <div class="w-full bg-white rounded-lg shadow md:mt-0 sm:max-w-md xl:p-0 ">
        <div class="p-6 space-y-4 md:space-y-6 sm:p-8">
          <h1 class="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl ">
            Авторизация
          </h1>
          <form novalidate class="space-y-4 md:space-y-6" @submit.prevent="loginForm">
            <div>
              <label for="username" class="block mb-2 text-sm font-medium text-gray-900 ">Имя пользователя</label>
              <input type="text" name="username" id="username" v-model="username"
                     :class="errors.username ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " placeholder=". . ." required="">
              <p v-if="errors.username" class="text-red-500 text-sm mt-1">
                {{ errors.username }}
              </p>
            </div>
            <div>
              <label for="password" class="block mb-2 text-sm font-medium text-gray-900 ">Пароль</label>
              <input type="password" name="password" id="password" v-model="password" placeholder="••••••••"
                     :class="errors.password ? 'border-red-500' : 'border-gray-300'"
                     class="bg-gray-50 border border-gray-300 text-gray-900 rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 " required="">
              <p v-if="errors.password" class="text-red-500 text-sm mt-1">
                {{ errors.password }}
              </p>
            </div>
            <button type="submit" class="w-full text-gray-50 border-gray-700 bg-gray-700 hover:bg-gray-800 focus:ring-4 focus:outline-none focus:ring-gray-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center">Войти</button>
            <p class="text-sm font-light text-gray-500">
              Вы не зарегистрированы? <a href="/registration" class="font-medium text-primary-600 hover:underline">Регистрация</a>
            </p>
          </form>
        </div>
      </div>
    </div>
  </section>
</template>
