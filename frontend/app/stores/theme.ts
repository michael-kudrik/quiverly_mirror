import { defineStore } from 'pinia'
import { ref } from 'vue'

// this is used to toggle the daisyUI theme

export const useThemeStore = defineStore('theme', () => {
    const activeTheme = ref('quiverly')

    function initTheme() {
        const savedTheme = localStorage.getItem('quiverly-theme')
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

        if (savedTheme) {
            activeTheme.value = savedTheme
        } else if (prefersDark) {
            activeTheme.value = 'quiverly-dark'
        }
    }

    function toggleTheme(isChecked: boolean) {
        const newTheme = isChecked ? 'quiverly-dark' : 'quiverly'
        activeTheme.value = newTheme
        localStorage.setItem('quiverly-theme', newTheme)
    }

    return { activeTheme, initTheme, toggleTheme }
})