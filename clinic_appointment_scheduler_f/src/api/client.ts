import axios from 'axios'

const api = axios.create({
    // with proxy we can call relative /api
    baseURL: '/api',
})

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers = config.headers ?? {}
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

export default api