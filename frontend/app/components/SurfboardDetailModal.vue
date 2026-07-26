<script setup lang="ts">
import {ref, computed, watch, reactive} from 'vue'
import {CloseMediumFill} from '@mingcute/vue'
import type {SurfBoard, SurfboardImage} from '~/composables/useSurfboards'
import {getImageUrl, SURFBOARD_TYPES} from '~/composables/useSurfboards'
import Compressor from 'compressorjs'

const props = defineProps<{
  board: SurfBoard | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'updated'): void
  (e: 'deleted'): void
}>()

const api = useApi()

const activeTab = ref<'overview' | 'edit' | 'images'>('overview')

const selectedImageIndex = ref(0)

// edit form reactive state
const editForm = reactive({
  model: '',
  shaper: '',
  boardType: '',
  length: null as number | null,
  width: null as number | null,
  volume: null as number | null,
  purchasedAt: ''
})

const submittingEdit = ref(false)
const editError = ref('')

// image upload state
const uploadFiles = ref<File[]>([])
const uploadingImages = ref(false)
const uploadError = ref('')

// delete board state
const deletingBoard = ref(false)
const showDeleteConfirm = ref(false)

// populate the edit form
watch(() => props.board, (newBoard) => {
  if (newBoard) {
    editForm.model = newBoard.model || ''
    editForm.shaper = newBoard.shaper || ''
    editForm.boardType = newBoard.boardType || ''
    editForm.length = newBoard.length ?? null
    editForm.width = newBoard.width ?? null
    editForm.volume = newBoard.volume ?? null
    editForm.purchasedAt = newBoard.purchasedAt || ''
    selectedImageIndex.value = 0
    activeTab.value = 'overview'
    showDeleteConfirm.value = false
  }
}, {immediate: true})

const images = computed<SurfboardImage[]>(() => {
  return props.board?.images || []
})

const currentImage = computed(() => {
  if (images.value.length === 0) return null
  return images.value[selectedImageIndex.value] || images.value[0]
})

const currentImageUrl = computed(() => getImageUrl(currentImage.value?.url))

const formattedLength = computed(() => {
  const val = props.board?.length
  if (val == null || val === 0) return 'N/A'
  const strVal = String(val)
  if (strVal.includes('.')) {
    const [feet, inches] = strVal.split('.')
    return inches && inches !== '0' ? `${feet}'${inches}"` : `${feet}'`
  }
  return `${val}'`
})

const specs = computed(() => [
  {label: 'Model', value: props.board?.model || 'N/A'},
  {label: 'Shaper', value: props.board?.shaper || 'N/A'},
  {label: 'Type', value: props.board?.boardType || 'N/A'},
  {label: 'Length', value: formattedLength.value},
  {label: 'Width', value: props.board?.width != null ? `${props.board.width}"` : 'N/A'},
  {label: 'Volume', value: props.board?.volume != null ? `${props.board.volume} L` : 'N/A'},
])

const tabs = computed(() => [
  {key: 'overview', label: 'Overview & Gallery'},
  {key: 'edit', label: 'Edit Specs'},
  {key: 'images', label: `Manage Images (${images.value.length})`}
])

async function runAction(action: () => Promise<void>, errorMsg: string) {
  try {
    await action()
    emit('updated')
  } catch (err: any) {
    console.error(errorMsg, err)
    alert(err.data?.message || errorMsg)
  }
}

// handles editing existing board properties
async function handleUpdateBoard() {
  if (!props.board) return
  if (!editForm.model.trim() || !editForm.shaper.trim()) {
    editError.value = 'Model and Shaper are required.'
    return
  }

  submittingEdit.value = true
  editError.value = ''

  try {
    const payload = {
      model: editForm.model,
      shaper: editForm.shaper,
      boardType: editForm.boardType || null,
      length: editForm.length ?? 0,
      width: editForm.width ?? 0,
      volume: editForm.volume ?? 0,
      purchasedAt: editForm.purchasedAt || null
    }

    await api(`/api/v1/surfboard/${props.board.id}`, {
      method: 'PUT',
      body: payload
    })

    emit('updated')
    activeTab.value = 'overview'
  } catch (err: any) {
    console.error('Failed to update board:', err)
    editError.value = err.data?.message || 'Failed to update board.'
  } finally {
    submittingEdit.value = false
  }
}

// board deletion
async function handleDeleteBoard() {
  if (!props.board) return

  deletingBoard.value = true
  try {
    await api(`/api/v1/surfboard/${props.board.id}`, {
      method: 'DELETE'
    })
    emit('deleted')
    emit('close')
  } catch (err: any) {
    console.error('Failed to delete board:', err)
    alert(err.data?.message || 'Failed to delete board.')
  } finally {
    deletingBoard.value = false
    showDeleteConfirm.value = false
  }
}

// file select
function handleMultipleFilesChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files) {
    uploadFiles.value = Array.from(target.files)
  }
}

// compress uploaded images
function compressImage(file: File): Promise<Blob | File> {
  return new Promise((resolve, reject) => {
    new Compressor(file, {
      quality: 0.7,
      mimeType: 'image/webp',
      success(result) {
        resolve(result)
      },
      error(err) {
        reject(err)
      }
    })
  })
}

// upload images
async function handleUploadImages() {
  if (!props.board || uploadFiles.value.length === 0) return

  uploadingImages.value = true
  uploadError.value = ''

  try {
    let index = 0
    for (const file of uploadFiles.value) {
      if (!file) continue
      const compressedBlob = await compressImage(file)
      const fileToUpload = new File([compressedBlob], `extra_${Date.now()}_${index++}.webp`, {type: 'image/webp'})

      const formData = new FormData()
      formData.append('file', fileToUpload)
      formData.append('isCover', 'false')

      await api(`/api/v1/surfboard/${props.board.id}/images`, {
        method: 'POST',
        body: formData
      })
    }

    uploadFiles.value = []
    emit('updated')
  } catch (err: any) {
    console.error('Failed to upload image:', err)
    uploadError.value = err.data?.message || 'Failed to upload one or more images.'
  } finally {
    uploadingImages.value = false
  }
}

// set the cover image
function handleSetCover(imageId: number) {
  if (!props.board) return
  runAction(
      () => api(`/api/v1/surfboard/${props.board!.id}/images/${imageId}/cover`, {method: 'PUT'}),
      'Failed to set cover image.'
  )
}

// delete image
function handleDeleteImage(imageId: number) {
  if (!props.board) return
  if (!confirm('Are you sure you want to delete this photo?')) return
  runAction(
      () => api(`/api/v1/surfboard/${props.board!.id}/images/${imageId}`, {method: 'DELETE'}),
      'Failed to delete image.'
  )
}
</script>

<template>
  <dialog class="modal" :class="{ 'modal-open': !!board }" :open="!!board" @close="$emit('close')">
    <div v-if="board"
         class="modal-box relative max-w-3xl w-full max-h-[85vh] my-auto p-6 bg-base-100 rounded-box shadow-xl">
      <!--      close button-->
      <button class="btn btn-sm btn-circle btn-ghost absolute right-3 top-3" @click="$emit('close')">
        <CloseMediumFill/>
      </button>

      <!--      modal-->
      <div>
        <div class="flex items-center justify-between">
          <h2 class="text-2xl font-bold flex items-center gap-2">
            <span>{{ board.model }}</span>
            <span class="text-base font-normal text-base-content/70">by {{ board.shaper }}</span>
          </h2>
        </div>
        <div class="flex gap-2 mt-2">
          <span v-if="board.boardType" class="badge badge-neutral">{{ board.boardType }}</span>
        </div>
      </div>

      <div class="divider my-3"></div>

      <!--      tabs to navigate-->
      <div class="tabs tabs-lift mb-6 bg-base-200">
        <button
            v-for="tab in tabs"
            :key="tab.key"
            class="tab flex-1"
            :class="{ 'tab-active': activeTab === tab.key }"
            @click="activeTab = tab.key as any"
        >
          {{ tab.label }}
        </button>
      </div>

      <!--      tab 1 overview-->
      <div v-if="activeTab === 'overview'" class="space-y-6">
        <!--        display image-->
        <div
            class="relative w-full h-80 bg-base-300 rounded-box overflow-hidden shadow-inner flex items-center justify-center">
          <img
              :src="currentImageUrl"
              :alt="board.model"
              class="w-full h-full object-contain"
          />
          <span v-if="currentImage?.cover" class="badge badge-primary absolute top-3 left-3 shadow">
            Cover Photo
          </span>
        </div>

        <!--        display different images as carousel-->
        <div v-if="images.length > 1" class="flex gap-2 overflow-x-auto pl-2 py-2">
          <button
              v-for="(img, idx) in images"
              :key="img.id"
              class="relative w-20 h-20 rounded-lg overflow-hidden border-2 transition-all flex-shrink-0"
              :class="idx === selectedImageIndex ? 'border-primary scale-105 shadow' : 'border-base-300 opacity-70 hover:opacity-100'"
              @click="selectedImageIndex = idx"
          >
            <img :src="getImageUrl(img.url)" class="w-full h-full object-cover" alt="surfboard image"/>
            <span v-if="img.cover"
                  class="absolute bottom-0 inset-x-0 bg-primary/80 text-[10px] text-white text-center py-0.5">
              Cover
            </span>
          </button>
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 gap-4 bg-base-200 p-4 rounded-box">
          <div v-for="spec in specs" :key="spec.label">
            <span class="text-xs text-base-content/60 font-semibold block uppercase">{{ spec.label }}</span>
            <span class="font-medium text-base">{{ spec.value }}</span>
          </div>
          <div v-if="board.purchasedAt" class="col-span-2 sm:col-span-3 border-t border-base-300 pt-2 mt-1">
            <span class="text-xs text-base-content/60 font-semibold block uppercase">Purchased Date</span>
            <span class="font-medium text-base">{{ board.purchasedAt }}</span>
          </div>
        </div>
      </div>

      <!--      tab 2 edit-->
      <div v-if="activeTab === 'edit'">
        <div v-if="editError" class="alert alert-error mb-4 text-sm">
          <span>{{ editError }}</span>
        </div>

        <form @submit.prevent="handleUpdateBoard" class="space-y-4">
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label for="edit-model" class="label text-sm font-semibold">Model Name</label>
              <input id="edit-model" v-model="editForm.model" type="text" class="input w-full" required/>
            </div>
            <div>
              <label for="edit-shaper" class="label text-sm font-semibold">Shaper</label>
              <input id="edit-shaper" v-model="editForm.shaper" type="text" class="input bordered w-full"
                     required/>
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label for="edit-boardType" class="label text-sm font-semibold">Board Type</label>
              <select id="edit-boardType" v-model="editForm.boardType" class="select select-bordered w-full">
                <option value="" disabled>Select Type</option>
                <option v-for="type in SURFBOARD_TYPES" :key="type" :value="type">{{ type }}</option>
              </select>
            </div>
            <div>
              <label for="edit-volume" class="label text-sm font-semibold">Volume (Liters)</label>
              <input id="edit-volume" v-model="editForm.volume" type="number" step="any"
                     class="input w-full [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"/>
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label for="edit-length" class="label text-sm font-semibold">Length (e.g. 9.4)</label>
              <input id="edit-length" v-model="editForm.length" type="number" step="any"
                     class="input w-full [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"/>
            </div>
            <div>
              <label for="edit-width" class="label text-sm font-semibold">Width (Inches)</label>
              <input id="edit-width" v-model="editForm.width" type="number" step="any"
                     class="input w-full [appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none"/>
            </div>
          </div>

          <div>
            <label for="edit-purchasedAt" class="label text-sm font-semibold">Purchased Date</label>
            <input id="edit-purchasedAt" v-model="editForm.purchasedAt" type="date"
                   class="input w-full"/>
          </div>

          <div class="flex justify-between items-center ">
            <button
                type="button"
                class="btn btn-error btn-outline"
                :disabled="deletingBoard"
                @click="showDeleteConfirm = true"
            >
              {{ deletingBoard ? 'Deleting...' : 'Delete Board' }}
            </button>
            <button type="submit" class="btn btn-primary" :disabled="submittingEdit">
              {{ submittingEdit ? 'Saving...' : 'Save Changes' }}
            </button>
          </div>
        </form>
      </div>

      <!--      tab 3 images-->
      <div v-if="activeTab === 'images'" class="space-y-6">
        <div class="bg-base-200 p-4 rounded-box border border-base-300">
          <h3 class="font-bold text-base mb-2">Upload Additional Photos</h3>
          <div v-if="uploadError" class="alert alert-error mb-3 text-sm">
            <span>{{ uploadError }}</span>
          </div>

          <div class="flex flex-col sm:flex-row gap-3 items-center">
            <input
                type="file"
                accept="image/*"
                multiple
                class="file-input file-input-bordered w-full"
                @change="handleMultipleFilesChange"
            />
            <button
                class="btn btn-primary whitespace-nowrap w-full sm:w-auto"
                :disabled="uploadFiles.length === 0 || uploadingImages"
                @click="handleUploadImages"
            >
              {{ uploadingImages ? 'Uploading...' : `Upload (${uploadFiles.length})` }}
            </button>
          </div>
          <p class="text-xs text-base-content/60 mt-1">Select one or more photos. Images are automatically
            compressed.</p>
        </div>

        <!--        images-->
        <div>
          <h3 class="font-bold text-base mb-3">Current Photos</h3>
          <div v-if="images.length === 0" class="text-center py-8 text-base-content/60">
            No photos uploaded yet for this surfboard.
          </div>
          <div v-else class="grid grid-cols-2 sm:grid-cols-3 gap-4">
            <div
                v-for="img in images"
                :key="img.id"
                class="relative group bg-base-200 rounded-box overflow-hidden border border-base-300 shadow-sm"
            >
              <img :src="getImageUrl(img.url)" class="w-full h-36 object-cover" alt="board image"/>

              <div v-if="img.cover" class="absolute top-2 left-2 badge badge-primary text-xs shadow-md">
                Cover Photo
              </div>

              <div
                  class="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex flex-col justify-center items-center gap-2 p-2">
                <button
                    v-if="!img.cover"
                    class="btn btn-xs btn-accent w-full"
                    @click="handleSetCover(img.id)"
                >
                  Make Cover
                </button>
                <button
                    class="btn btn-xs btn-error w-full"
                    @click="handleDeleteImage(img.id)"
                >
                  Delete Photo
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!--      delete confirmation-->
      <dialog class="modal" :class="{ 'modal-open': showDeleteConfirm }">
        <div class="modal-box text-center p-6 max-w-sm">
          <h3 class="font-bold text-lg">Delete Surfboard</h3>
          <p class="py-3 text-sm text-base-content/70">Are you sure you want to delete <span
              class="font-bold font-mono">{{
              board?.model || ''
            }}</span>?
          </p>
          <div class="modal-action justify-center gap-3 mt-4">
            <button class="btn btn-ghost btn-sm" @click="showDeleteConfirm = false">Cancel</button>
            <button class="btn btn-error btn-sm" :disabled="deletingBoard" @click="handleDeleteBoard">
              {{ deletingBoard ? 'Deleting...' : 'Delete' }}
            </button>
          </div>
        </div>
      </dialog>
    </div>

    <form method="dialog" class="modal-backdrop bg-black/50">
      <button @click="$emit('close')">close</button>
    </form>
  </dialog>
</template>
