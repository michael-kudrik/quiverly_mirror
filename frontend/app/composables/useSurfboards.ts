//https://nuxt.com/docs/4.x/guide/recipes/custom-usefetch#custom-usefetch-with-createusefetch

// use ts interfaces to define what types SurfBoard can receive
export interface SurfboardImage {
    id: number
    url: string
    imageWidth: number
    imageHeight: number
    cover: boolean
}

export interface SurfBoard {
    model: string
    shaper: string
    length: number
    width: number
    volume: number
    boardType: string
    owner: {
        username: string
        email: string
        createdAt: string
        id: number
        role: string
    }
    id: number
    purchasedAt: string
    images?: SurfboardImage[]
}


export type CreateSurfboardInput = Omit<SurfBoard, 'id' | 'owner'>

export const SURFBOARD_TYPES = [
    'Longboard',
    'Shortboard',
    'Mid-Length',
    'Funboard',
    'Fish',
    'Hybrid',
    'Gun',
    'Foamie',
    'Egg',
    'Groveler',
    'Asymmetrical',
    'SUP',
    'Step-Up'
] as const

const DEFAULT_FALLBACK_IMAGE = "https://images.unsplash.com/photo-1502680390469-be75c86b636f?w=600&auto=format&fit=crop&q=80"

export const getImageUrl = (rawUrl?: string | null): string => {
    if (!rawUrl) return DEFAULT_FALLBACK_IMAGE
    const config = useRuntimeConfig()
    const apiBase = config.public.apiBase || 'http://localhost:8080'
    return rawUrl.startsWith('/') ? `${apiBase}${rawUrl}` : rawUrl
}

export const useSurfboards = () => {
    const api = useApi() // authenticated $fetch

    // fetching boards uses the useApiFetch composable (automatically attaches Authorization headers)
    const getMyBoards = () => {

        // return the result of useApiFetch. This should return a Res, Pending, and Error object
        return useApiFetch<SurfBoard[]>('/api/v1/surfboard/my-boards', {
            key: 'my-boards',
            default: () => []
        })
    }

    // api calls for write operations.
    const addBoard = async (boardData: CreateSurfboardInput) => {

        // uses custom $fetch to handle the auth headers
        // tells the server to do something
        return await api<SurfBoard>('/api/v1/surfboard', {
            method: 'POST',
            body: boardData
        })
    }

    /**
     * @todo have a user form that allows the submission of new surfboards
     * do so inside an event handler or sumn idk bro
     */

    return {
        getMyBoards,
        addBoard
    }
}
