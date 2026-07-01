//
// Use this for mutations such as posting data.

// We need to use the raw $fetch since we don't need nuxt to wrap it in any refs.
// Also don't want SSR to bother with it.
//     It's basically like a single bullet, one off http request.


export const useApi = () => {
  return useNuxtApp().$api
}