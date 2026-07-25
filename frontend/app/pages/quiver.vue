<script setup lang="ts">
import {AddLine, CloseMediumFill} from "@mingcute/vue"
import {useSurfboards} from "~/composables/useSurfboards"
import Compressor from "compressorjs"

const {addBoard} = useSurfboards();
const modalRef = ref<HTMLElement | null>(null);
const submitting = ref(false);
const error = ref('');
const api = useApi();

const form = reactive({
  model: '',
  shaper: '',
  length: null as number | null,
  width: null as number | null,
  volume: null as number | null,
  purchasedAt: '',
  imageFile: null as File | null
})

function resetForm() {
  form.model = ''
  form.shaper = ''
  form.length = null
  form.width = null
  form.volume = null
  form.purchasedAt = ''
  form.imageFile = null
  error.value = ''
}

async function handleSubmit() {
  console.log('handleSubmit triggered, validating form...', form)
  if (!validateForm()) {
    console.warn('Form validation failed:', error.value)
    return
  }
  submitting.value = true

  try {
    const payload = {
      model: form.model,
      shaper: form.shaper,
      length: form.length || 0,
      width: form.width || 0,
      volume: form.volume || 0,
      purchasedAt: form.purchasedAt ? form.purchasedAt : null
    }
    console.log('Sending surfboard payload:', payload)

    const createdBoard = await addBoard(payload as any)
    console.log('Created surfboard response:', createdBoard)

    if (form.imageFile && createdBoard?.id) {
      try {
        const compressedBlob = await compressImage(form.imageFile)
        const fileToUpload = new File([compressedBlob], `${createdBoard.id}.webp`, {type: 'image/webp'})
        const formData = new FormData()
        formData.append('file', fileToUpload)
        formData.append('isCover', 'true')

        await api(`/api/v1/surfboard/${createdBoard.id}/images`, {
          method: 'POST',
          body: formData
        })
        console.log('Uploaded surfboard image successfully')
      } catch (imgErr: any) {
        console.error('Image upload error:', imgErr)
        error.value = imgErr.data?.message || "Board saved, but image upload failed."
      }
    }

    refreshNuxtData('my-boards')
    modalRef.value?.hidePopover()
    resetForm()
  } catch (err: any) {
    console.error('Submit error:', err)
    error.value = err.data?.message || "Failed to create board :/"
  } finally {
    submitting.value = false
  }
}


function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    form.imageFile = target.files[0]
    console.log('File selected:', form.imageFile.name, `${(form.imageFile.size / 1024 / 1024).toFixed(2)} MB`)
  } else {
    form.imageFile = null
  }
}


function validateForm(): boolean {
  if (!form.model.trim() || !form.shaper.trim()) {
    error.value = 'Model and Shaper are required.'
    return false
  }
  if (form.imageFile && form.imageFile.size > 20 * 1024 * 1024) { // 20MB upload limit before we compress
    error.value = 'Image file is too large (max 20MB).'
    return false
  }
  error.value = ''
  return true
}

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

</script>

<template>
  <SurfboardGrid/>
  <div ref="modalRef" class="modal" id="add-board-modal" popover>
    <div class="modal-box relative">
      <div class="absolute right-2 top-2">
        <button class="cursor-pointer" popovertarget="add-board-modal" popovertargetaction="hide">
          <CloseMediumFill/>
        </button>
      </div>
      <h3 class="font-bold text-lg text-center">New Board</h3>
      <div class="flex justify-center items-center">
        <fieldset class="fieldset bg-base-200 border-base-300 rounded-box w-xs border p-4">
          <legend class="fieldset-legend">Specs</legend>
          <div v-if="error" class="alert alert-error mb-3 p-2 text-sm">
            <span>{{ error }}</span>
          </div>
          <form @submit.prevent="handleSubmit">
            <input v-model="form.model" type="text" class="input validator mb-2" placeholder="Model"/>
            <input v-model="form.shaper" type="text" class="input mb-2" placeholder="Shaper"/>
            <input v-model="form.volume" min="10" max="150" type="number" class="input mb-2"
                   placeholder="Volume (Liters)"/>
            <div class="flex mb-1">
              <input v-model="form.length" type="number" class="input" placeholder="Length"/>
              <input v-model="form.width" type="number" class="input" placeholder="Width"/>
            </div>
            <label>Date Purchased</label>
            <input v-model="form.purchasedAt" type="date" class="input"/>

            <fieldset class="fieldset">
              <legend class="fieldset-legend">Image</legend>
              <input type="file" accept="image/*" class="file-input" @change="handleFileChange"/>
              <label class="label">Max size 10MB</label>
            </fieldset>

            <button class="btn btn-primary" type="submit" :disabled="submitting">{{
                submitting ? "Saving..." : "Submit"
              }}
            </button>
          </form>
        </fieldset>
      </div>
    </div>
  </div>
  <div class="modal-backdrop">
    <button popovertarget="my-modal-2" popovertargetaction="hide">close</button>
  </div>
  <div class="fixed bottom-10 right-6 z-50 tooltip tooltip-left tooltip-accent" data-tip="New Board">
    <button data-cuelume-toggle class="btn btn-circle btn-accent drop-shadow-lg drop-shadow-accent/50"
            popovertarget="add-board-modal">
      <AddLine></AddLine>
    </button>
  </div>
</template>

<style scoped>

</style>