export default defineNuxtPlugin((nuxtApp) => {
    // retrieve auth cookie directly in plugin context to support SSR
    const authCookie = useCookie<{ token?: string; username?: string } | null>('auth')

    const config = useRuntimeConfig()

    // custom fetch instance
    const api = $fetch.create({
        baseURL: config.public.apiBase,

        // before every request, check if token exists
        onRequest({options}) {
            const token = authCookie.value?.token
            if (token) {
                options.headers = options.headers || {}

                // convert objects to headers instance.
                const headers = (options.headers instanceof Headers)
                    ? options.headers
                    : new Headers(options.headers as Record<string, string>)

                // ** inject auth header with the bearer token **
                headers.set('Authorization', `Bearer ${token}`)
                options.headers = headers
            }
        },

        // if token is expired, clear auth cookie and move to login page
        onResponseError({response}) {
            if (response.status === 401) {
                authCookie.value = null
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
