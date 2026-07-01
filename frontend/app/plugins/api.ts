export default defineNuxtPlugin(() => {
 // retrieve the token directly from the cookie to support SSR
  const token = useCookie('token')

  // custom fetch instance
  const api = $fetch.create({
    baseURL: 'http://localhost:8080',

    // before every request, check if token exists or create empty obj
    onRequest({ options }) {
      if (token.value) {
        options.headers = options.headers || {}

        // convert objects to headers instance.
        const headers = (options.headers instanceof Headers)
          ? options.headers
          : new Headers(options.headers as Record<string, string>)

        // ** inject auth header with the bearer token **
        headers.set('Authorization', `Bearer ${token.value}`)
        options.headers = headers
      }
    }
  })

  // this is what allows us to call useNuxtApp().$api
  // see composables/useApi.ts
  return {
    provide: {
      api
    }
  }
})
