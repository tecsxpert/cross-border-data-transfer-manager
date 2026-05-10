import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// Attach JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Handle 401 globally
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(err)
  }
)

// ── Auth ──────────────────────────────────────────────────────────────────────
export const authApi = {
  login:    (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
}

// ── Transfers ─────────────────────────────────────────────────────────────────
export const transferApi = {
  getAll:   (params) => api.get('/transfers', { params }),
  getById:  (id)     => api.get(`/transfers/${id}`),
  create:   (data)   => api.post('/transfers', data),
  update:   (id, d)  => api.put(`/transfers/${id}`, d),
  delete:   (id)     => api.delete(`/transfers/${id}`),
  search:   (params) => api.get('/transfers/search', { params }),
  exportCsv: ()      => api.get('/transfers/export', { responseType: 'blob' }),
}

// ── Stats ─────────────────────────────────────────────────────────────────────
export const statsApi = {
  get:     () => api.get('/stats'),
  auditLog:(params) => api.get('/audit-log', { params }),
}

// ── AI ────────────────────────────────────────────────────────────────────────
export const aiApi = {
  describe:       (id) => api.post(`/ai/describe/${id}`),
  recommend:      (id) => api.post(`/ai/recommend/${id}`),
  generateReport: (id) => api.post(`/ai/generate-report/${id}`),
  health:         ()   => api.get('/ai/health'),
}

export default api
