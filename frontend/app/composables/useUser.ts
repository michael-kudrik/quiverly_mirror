export interface UserProfile {
    id: number
    username: string
    email: string
    avatarUrl?: string
}

export const useUser = () => {
    const api = useApi()

    // get profile details of the logged in user
    const getProfile = async () => {
        return await api<UserProfile>('/api/v1/user/me')
    }

    // upload profile avatar image
    const uploadAvatar = async (file: File) => {
        const formData = new FormData()
        formData.append('file', file)

        return await api<{ avatarUrl: string }>('/api/v1/user/me/avatar', {
            method: 'POST',
            body: formData
        })
    }

    // remove profile avatar image
    const removeAvatar = async () => {
        return await api('/api/v1/user/me/avatar', {
            method: 'DELETE'
        })
    }

    const changePassword = async (currentPassword: string, newPassword: string) => {

        return await api('/api/v1/user/me/password', {
            method: 'PUT', body: {
                currentPassword,
                newPassword
            }
        })
    }

    return {
        getProfile,
        uploadAvatar,
        removeAvatar,
        changePassword
    }
}
