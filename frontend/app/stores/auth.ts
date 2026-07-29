import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore(
    'auth',
    () => {
        const api = useApi()
        const token = ref<string | null>(null)
        const username = ref<string | null>(null)
        const avatarUrl = ref<string | null>(null)

        const isLoggedIn = computed(() => !!token.value)

        function logout() {
            token.value = null
            username.value = null
            avatarUrl.value = null
            useCookie('auth').value = null
            return navigateTo('/login')
        }

        async function login(u: string, p: string) {
            const data = await api<{ token: string; username: string }>('/api/auth/login', {
                method: 'POST',
                body: { username: u, password: p }
            })
            token.value = data.token
            username.value = data.username

            try {
                const profile = await api<{ avatarUrl?: string }>('/api/v1/user/me')
                avatarUrl.value = profile.avatarUrl || null
            } catch (e) {}

            return navigateTo('/')
        }

        async function register(u: string, p: string, e: string) {
            await api('/api/v1/user', {
                method: 'POST',
                body: { username: u, password: p, email: e }
            })
            return navigateTo('/login')
        }

        return { token, username, avatarUrl, isLoggedIn, login, logout, register }
    },
    {
        persist: {
            storage: piniaPluginPersistedstate.cookies({
                maxAge: 86400,
            }),
            pick: ['token', 'username', 'avatarUrl'],
        },
    }
)