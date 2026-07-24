export default defineNuxtPlugin((nuxtApp) => {
    // retrieve cookies directly in plugin context to support SSR
    const token = useCookie('token')
    const username = useCookie('username')

    // custom fetch instance
    const api = $fetch.create({
        baseURL: 'http://localhost:8080',

        // before every request, check if token exists or create empty obj
        onRequest({options}) {
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
        },

        // if token is expired, clear cookies and move to login page
        onResponseError({response}) {
            if (response.status === 401) {
                token.value = null
                username.value = null
                nuxtApp.runWithContext(() => navigateTo('/login'))
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
