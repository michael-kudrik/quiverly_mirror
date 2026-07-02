//https://nuxt.com/docs/4.x/guide/recipes/custom-usefetch#custom-usefetch-with-createusefetch

// use ts interfaces to define what types SurfBoard can receive
export interface SurfBoard {
  model: string
  shaper: string
  length: number
  width: number
  volume: number
  owner: {
    username: string
    email: string
    createdAt: string
    id: number
    role: string
  }
  id: number
  purchasedAt: string
}

export type CreateSurfboardInput = Omit<SurfBoard, 'id' | 'owner'>

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
