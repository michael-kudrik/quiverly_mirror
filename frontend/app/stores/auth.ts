export const useAuthStore = defineStore('auth', () => {
    const api = useApi()
    // nuxt built in useCookie syncs token state to the cookie
    const token = useCookie('token')
    //create a reference to store logged-in usernames. defaults to null and accepts string or null
    const username = ref<string | null>(null)

    const isLoggedIn = computed(() => !!token.value)

    async function login(u: string, p: string){
        const data = await api<{ token: string; username: string }>('/api/auth/login', {
            method: 'POST',
            body: {username: u, password: p}
        })
        token.value = data.token
        username.value = data.username
        return navigateTo('/')
    }

    async function register(u: string, p: string, e: string){
        await api('/api/v1/user', {
            method: 'POST',
            body: {username: u, password: p, email: e}
        })
        return navigateTo('/login')
    }
    return {token, username, isLoggedIn, login, register}
})