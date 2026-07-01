import type {UseFetchOptions} from "#app";

// this function wraps the default useFetch.
// since by default useFetch is unauthenticated, we pass our custom $fetch instance to it

//

export const useApiFetch: typeof useFetch = (request, opts?) => {
  // initialize custom api client
  const api = useApi()

  // call standard useFetch with the request path and options
  return useFetch(request, {
    ...opts, // also want to destructure so that we can get other options such as body and whatnot
    $fetch: api // inject the custom authenticated $fetch instance
  } as Partial<UseFetchOptions<any>>)
}
