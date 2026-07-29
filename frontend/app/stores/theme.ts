import {defineStore} from 'pinia'
import {ref} from 'vue'

export const useThemeStore = defineStore(
    'theme',
    () => {
        const activeTheme = ref('quiverly')

        function toggleTheme(isChecked?: boolean) {
            if (typeof isChecked === 'boolean') {
                activeTheme.value = isChecked ? 'quiverly-dark' : 'quiverly'
            } else {
                activeTheme.value = activeTheme.value === 'quiverly-dark' ? 'quiverly' : 'quiverly-dark'
            }
        }

        return {activeTheme, toggleTheme}
    },
    {
        persist: {
            storage: piniaPluginPersistedstate.cookies({
                maxAge: 365 * 24 * 60 * 60, // keep the theme preference for whole ah year
            }),
        },
    }
)