<script setup lang="ts">
import {ref} from 'vue';
import {SurfboardLine} from '@mingcute/vue';

const authStore = useAuthStore();
const username = ref('');
const password = ref('');
const isLoading = ref(false);
const errorMessage = ref<string | null>(null);

async function handleLogin() {
  isLoading.value = true;
  errorMessage.value = null;
  try {
    await authStore.login(username.value, password.value);
  } catch (error: any) {
    errorMessage.value = error.data?.message || 'Invalid username or password';
  } finally {
    isLoading.value = false;
  }
}
</script>

<template>
  <div class="flex h-screen items-center justify-center w-full bg-neutral">
    <div class="aura aura-lg text-primary">
      <div class="card card-border text-base-content bg-base-100 w-96">
        <div class="card-body">
          <div class="flex items-center justify-center">
            <SurfboardLine size="32" color="oklch(39% 0.07 227.392)"/>
          </div>
          <h2 class="card-title justify-center font-logo text-3xl">Quiverly</h2>

          <form @submit.prevent="handleLogin" class="card-actions justify-center w-full">
            <div class="w-full">
              <label class="mt-4 fieldset-label">Username</label>
              <label class="input validator w-full">
                <svg class="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                  <g
                      stroke-linejoin="round"
                      stroke-linecap="round"
                      stroke-width="2.5"
                      fill="none"
                      stroke="currentColor"
                  >
                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </g>
                </svg>
                <input
                    v-model="username"
                    type="text"
                    required
                    placeholder="Username"
                    pattern="[A-Za-z][A-Za-z0-9\-]*"
                    minlength="3"
                    maxlength="30"
                    title="Only letters, numbers or dash"
                />
              </label>
              <p class="validator-hint hidden">
                Must be 3 to 30 characters
                <br/>containing only letters, numbers or dash
              </p>

              <label class="fieldset-label mt-4">Password</label>
              <label class="input validator w-full">
                <svg class="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                  <g
                      stroke-linejoin="round"
                      stroke-linecap="round"
                      stroke-width="2.5"
                      fill="none"
                      stroke="currentColor"
                  >
                    <path
                        d="M2.586 17.414A2 2 0 0 0 2 18.828V21a1 1 0 0 0 1 1h3a1 1 0 0 0 1-1v-1a1 1 0 0 1 1-1h1a1 1 0 0 0 1-1v-1a1 1 0 0 1 1-1h.172a2 2 0 0 0 1.414-.586l.814-.814a6.5 6.5 0 1 0-4-4z"
                    ></path>
                    <circle cx="16.5" cy="7.5" r=".5" fill="currentColor"></circle>
                  </g>
                </svg>
                <input
                    v-model="password"
                    type="password"
                    required
                    placeholder="Password"
                    minlength="8"
                    pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
                    title="Must be more than 8 characters, including number, lowercase letter, uppercase letter"
                />
              </label>
              <p class="validator-hint hidden">
                Must be more than 8 characters, including
                <br/>At least one number <br/>At least one lowercase letter <br/>At least one uppercase letter
              </p>

              <div class="tooltip tooltip-right tooltip-secondary" data-tip="Minimal cookies used">
                <button data-cuelume-toggle type="submit" class="btn btn-primary w-full mt-6" :disabled="isLoading">
                  <span v-if="isLoading" class="loading loading-spinner"></span>
                  Login
                </button>
              </div>

              <p v-if="errorMessage" class="text-error text-sm mt-2 text-center">
                {{ errorMessage }}
              </p>
            </div>
          </form>
        </div>
        <div class="divider m-4">OR</div>
        <div class="flex mb-2 justify-center items-center">
          <p class="text-xs pr-2">Not a local?</p>
          <NuxtLink to="/register" class="link text-sm text-primary font-bold">Sign Up</NuxtLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>