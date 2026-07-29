import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore(
    'auth',
    () => {
        const api = useApi()
        const token = ref<string | null>(null)
        const username = ref<string | null>(null)

        const isLoggedIn = computed(() => !!token.value)

        function logout() {
            token.value = null
            username.value = null
            return navigateTo('/login')
        }

        async function login(u: string, p: string) {
            const data = await api<{ token: string; username: string }>('/api/auth/login', {
                method: 'POST',
                body: { username: u, password: p }
            })
            token.value = data.token
            username.value = data.username
            return navigateTo('/')
        }

        async function register(u: string, p: string, e: string) {
            await api('/api/v1/user', {
                method: 'POST',
                body: { username: u, password: p, email: e }
            })
            return navigateTo('/login')
        }

        return { token, username, isLoggedIn, login, logout, register }
    },
    {
        persist: {
            storage: piniaPluginPersistedstate.cookies({
                maxAge: 86400,
            }),
            pick: ['token', 'username'],
        },
    }
)