<script setup lang="ts">
import {ref, onMounted} from 'vue'
import {useAuthStore} from '~/stores/auth'
import {useUser} from '~/composables/useUser'

const authStore = useAuthStore()
const {getProfile, uploadAvatar, removeAvatar} = useUser()

const avatarUrl = ref<string | null>(null)
const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const isUploading = ref(false)
const message = ref<{ text: string; type: 'success' | 'error' } | null>(null)

onMounted(async () => {
  if (authStore.isLoggedIn) {
    try {
      const profile = await getProfile()
      if (profile.avatarUrl) {
        avatarUrl.value = profile.avatarUrl
      }
    } catch (e) {
      console.error('Failed to load profile:', e)
    }
  }
})

function clearSelection() {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = null
  }
  selectedFile.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
    }
    const file = target.files[0]
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
    message.value = null
  }
}

async function handleUpload() {
  if (!selectedFile.value) return
  isUploading.value = true
  message.value = null

  try {
    const res = await uploadAvatar(selectedFile.value)
    avatarUrl.value = res.avatarUrl
    authStore.avatarUrl = res.avatarUrl
    message.value = {text: 'Profile picture updated successfully! \\(^o^)/', type: 'success'}
    clearSelection()
  } catch (err: any) {
    message.value = {
      text: err?.data?.message || 'Failed to upload profile picture. Please try again.',
      type: 'error'
    }
  } finally {
    isUploading.value = false
  }
}

async function handleRemoveAvatar() {
  isUploading.value = true
  message.value = null
  try {
    await removeAvatar()
    avatarUrl.value = null
    authStore.avatarUrl = null
    clearSelection()
    message.value = {text: 'Profile picture removed successfully.', type: 'success'}
  } catch (err: any) {
    message.value = {
      text: err?.data?.message || 'Failed to remove profile picture. Please try again.',
      type: 'error'
    }
  } finally {
    isUploading.value = false
  }
}


const currentPassword = ref("")
const newPassword = ref("")
const confirmNewPassword = ref("")
const {changePassword} = useUser()
const isLoading = ref(false)

async function handlePasswordChange() {
  isLoading.value = true

  if (currentPassword.value === newPassword.value) {
    isLoading.value = false
    message.value = {text: 'Passwords cannot be the same!', type: 'error'}
    return
  } else if (newPassword.value !== confirmNewPassword.value) {
    isLoading.value = false
    message.value = {text: 'Both fields must have matching new passwords!', type: 'error'}
    return
  }
  try {
    await changePassword(currentPassword.value, newPassword.value)
    currentPassword.value = ""
    newPassword.value = ""
    message.value = {text: 'Password changed successfully.', type: 'success'}
  } catch (err: any) {
    message.value = {text: err?.data.message || 'Failed to change password.', type: 'error'}
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="max-w-4xl mx-auto py-10 px-4">
    <div class="mb-8">
      <h1 class="text-3xl font-bold">Settings</h1>
      <p class="text-base-content/70 pt-1">Manage your account preferences and personal details</p>
    </div>

    <!-- Alert Message -->
    <div v-if="message" :class="['alert mb-6 shadow-sm', message.type === 'success' ? 'alert-success' : 'alert-error']">
      <span>{{ message.text }}</span>
    </div>

    <!-- Profile Picture Card -->
    <div class="card bg-base-100 shadow-md border border-base-200">
      <div class="card-body">
        <h2 class="card-title text-xl mb-4">Profile Picture</h2>

        <div class="flex flex-col sm:flex-row items-center gap-6">
          <!-- Avatar Preview -->
          <UserAvatar :avatar-url="previewUrl || avatarUrl" class="w-24 h-24 text-3xl ring-4 ring-primary/20"/>

          <!-- Controls -->
          <div class="flex-1 flex flex-col gap-3 text-center sm:text-left">
            <div>
              <p class="font-medium text-sm">Upload a new avatar</p>
              <p class="text-xs text-base-content/60">Supports PNG, JPG, or WebP format up to 5MB.</p>
            </div>

            <div class="flex flex-wrap items-center gap-3 justify-center sm:justify-start">
              <input
                  ref="fileInput"
                  type="file"
                  accept="image/png, image/jpeg, image/webp"
                  class="file-input file-input-bordered file-input-sm w-full max-w-xs"
                  @change="handleFileChange"
              />

              <template v-if="selectedFile">
                <button
                    class="btn btn-primary btn-sm"
                    :disabled="isUploading"
                    @click="handleUpload"
                >
                  <span v-if="isUploading" class="loading loading-spinner loading-xs"></span>
                  <span v-else>Save Avatar</span>
                </button>

                <button
                    class="btn btn-ghost btn-sm"
                    :disabled="isUploading"
                    @click="clearSelection"
                >
                  Cancel
                </button>
              </template>

              <button
                  v-else-if="avatarUrl"
                  class="btn btn-outline btn-error btn-sm"
                  :disabled="isUploading"
                  @click="handleRemoveAvatar"
              >
                Remove Avatar
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="card bg-base-100 shadow-md border border-base-200 mt-6">
      <div class="card-body">
        <h2 class="card-title text-xl mb-4">Change Password</h2>
        <form @submit.prevent="handlePasswordChange" class="flex flex-col gap-4 max-w-md">
          <div class="form-control">
            <label class="label pb-1">
              <span class="label-text font-medium text-sm">Current Password</span>
            </label>
            <input
                v-model="currentPassword"
                type="password"
                required
                class="input input-bordered w-full"
            />
          </div>

          <div class="form-control">
            <label class="label pb-1">
              <span class="label-text font-medium text-sm">New Password</span>
            </label>
            <input
                v-model="newPassword"
                type="password"
                required
                minlength="8"
                pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
                title="Must be more than 8 characters, including number, lowercase letter, uppercase letter"
                class="input input-bordered w-full"
            />
          </div>

          <div class="form-control">
            <label class="label pb-1">
              <span class="label-text font-medium text-sm">Confirm New Password</span>
            </label>
            <input
                v-model="confirmNewPassword"
                type="password"
                required
                minlength="8"
                pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
                title="Must be more than 8 characters, including number, lowercase letter, uppercase letter"
                class="input input-bordered w-full"
            />
          </div>

          <div class="pt-2">
            <button type="submit" class="btn btn-primary sm:w-auto w-full" :disabled="isLoading">
              <span v-if="isLoading" class="loading loading-spinner loading-xs"></span>
              <span>Update Password</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>