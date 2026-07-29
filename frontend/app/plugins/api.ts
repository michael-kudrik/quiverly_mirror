import { useAuthStore } from '~/stores/auth'

export default defineNuxtPlugin((nuxtApp) => {
    const authCookie = useCookie<any>('auth')
    const config = useRuntimeConfig()

    // custom fetch instance
    const api = $fetch.create({
        baseURL: config.public.apiBase,

        // before every request, check if token exists
        onRequest({options}) {
            let token: string | null | undefined = null

            // 1. Try Pinia store state
            try {
                const authStore = useAuthStore(nuxtApp.$pinia)
                token = authStore.token
            } catch (e) {}

            // 2. Fallback to cookie if pinia state isn't initialized yet
            if (!token && authCookie.value) {
                try {
                    const parsed = typeof authCookie.value === 'string'
                        ? JSON.parse(decodeURIComponent(authCookie.value))
                        : authCookie.value
                    token = parsed?.token
                } catch (e) {
                    if (typeof authCookie.value === 'object') {
                        token = authCookie.value?.token
                    }
                }
            }

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
                try {
                    const authStore = useAuthStore(nuxtApp.$pinia)
                    authStore.token = null
                    authStore.username = null
                    authStore.avatarUrl = null
                } catch (e) {}
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
