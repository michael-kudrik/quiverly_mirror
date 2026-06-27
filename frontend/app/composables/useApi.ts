export const useApi = () => {
  const token = useCookie('token')

  return $fetch.create({
    baseURL: 'http://localhost:8080',
    onRequest({ options }) {
      if (token.value) {
        options.headers = options.headers || {}
        
        // Ensure headers are handled as a Headers object
        const headers = (options.headers instanceof Headers)
          ? options.headers
          : new Headers(options.headers as Record<string, string>)

        headers.set('Authorization', `Bearer ${token.value}`)
        options.headers = headers
      }
    }
  })
}