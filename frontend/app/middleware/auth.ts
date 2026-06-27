// middleware/auth.ts
import { useAuthStore } from '~/stores/auth'

export default defineNuxtRouteMiddleware((to) => {
    const auth = useAuthStore()

    const publicRoutes = ['/login', '/register']
    const requireAuth = !publicRoutes.includes(to.path)

    // anyone attempting to access non-public pages gets bumped to login

    if (!auth.isLoggedIn && requireAuth){
        return navigateTo('/login')
    }

    if (auth.isLoggedIn && publicRoutes.includes(to.path)) {
        return navigateTo('/')
    }
})