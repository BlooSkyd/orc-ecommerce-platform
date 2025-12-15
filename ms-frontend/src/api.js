const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

// Service specific bases (defaults to known ports). If VITE_USE_PROXY=true, use relative paths
const USE_PROXY = import.meta.env.VITE_USE_PROXY === 'true' || import.meta.env.PROD
const USERS_BASE = USE_PROXY ? '' : (import.meta.env.VITE_USERS_URL || 'http://localhost:8081')
const PRODUCTS_BASE = USE_PROXY ? '' : (import.meta.env.VITE_PRODUCTS_URL || 'http://localhost:8082')
const ORDERS_BASE = USE_PROXY ? '' : (import.meta.env.VITE_ORDERS_URL || 'http://localhost:8083')

// Backends expose APIs under /api/v1
const PREFIX = '/api/v1'

async function request(path, opts = {}){
  let url
  if (typeof path === 'string' && path.startsWith('http')) url = path
  else if (typeof path === 'string' && path.startsWith('/')) url = path
  else url = API_BASE + path
  const res = await fetch(url, opts)
  if (!res.ok) {
    const text = await res.text()
    throw new Error(text || res.statusText)
  }
  if (res.status === 204) return null
  return res.json()
}

export const products = {
  list: () => request(`${PRODUCTS_BASE}${PREFIX}/products`),
  get: (id) => request(`${PRODUCTS_BASE}${PREFIX}/products/${id}`),
  create: (body) => request(`${PRODUCTS_BASE}${PREFIX}/products`, {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  update: (id, body) => request(`${PRODUCTS_BASE}${PREFIX}/products/${id}`, {method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  remove: (id) => request(`${PRODUCTS_BASE}${PREFIX}/products/${id}`, {method:'DELETE'}),
  search: (q) => request(`${PRODUCTS_BASE}${PREFIX}/products/search?name=${encodeURIComponent(q)}`),
  byCategory: (cat) => request(`${PRODUCTS_BASE}${PREFIX}/products/category/${encodeURIComponent(cat)}`),
  active: () => request(`${PRODUCTS_BASE}${PREFIX}/products/available`)
}

export const users = {
  list: () => request(`${USERS_BASE}${PREFIX}/users`),
  get: (id) => request(`${USERS_BASE}${PREFIX}/users/${id}`),
  create: (body) => request(`${USERS_BASE}${PREFIX}/users`, {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  update: (id, body) => request(`${USERS_BASE}${PREFIX}/users/${id}`, {method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  remove: (id) => request(`${USERS_BASE}${PREFIX}/users/${id}`, {method:'DELETE'}),
  search: (lastName) => request(`${USERS_BASE}${PREFIX}/users/search?lastName=${encodeURIComponent(lastName)}`),
  active: () => request(`${USERS_BASE}${PREFIX}/users/active`)
}

export const orders = {
  list: () => request(`${ORDERS_BASE}${PREFIX}/orders`),
  get: (id) => request(`${ORDERS_BASE}${PREFIX}/orders/${id}`),
  create: (body) => request(`${ORDERS_BASE}${PREFIX}/orders`, {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  update: (id, body) => request(`${ORDERS_BASE}${PREFIX}/orders/${id}/status`, {method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)}),
  remove: (id) => request(`${ORDERS_BASE}${PREFIX}/orders/${id}`, {method:'DELETE'}),
  byUser: (userId) => request(`${ORDERS_BASE}${PREFIX}/orders/user/${encodeURIComponent(userId)}`)
}
