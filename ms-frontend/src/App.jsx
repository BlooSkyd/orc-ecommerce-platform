import React, { useState } from 'react'
import Home from './pages/Home'
import Products from './pages/Products'
import Users from './pages/Users'
import Orders from './pages/Orders'

export default function App() {
  const [tab, setTab] = useState('home')
  return (
    <div className="app">
      <aside className="sidebar">
        <h2>Platform Admin</h2>
        <nav>
          <button onClick={() => setTab('home')} className={tab === 'home' ? 'active' : ''}>Accueil</button>
          <button onClick={() => setTab('users')} className={tab === 'users' ? 'active' : ''}>Users</button>
          <button onClick={() => setTab('products')} className={tab === 'products' ? 'active' : ''}>Products</button>
          <button onClick={() => setTab('orders')} className={tab === 'orders' ? 'active' : ''}>Orders</button>
        </nav>
      </aside>
      <main className="main">
        {tab === 'home' && <Home />}
        {tab === 'users' && <Users />}
        {tab === 'products' && <Products />}
        {tab === 'orders' && <Orders />}
      </main>
    </div>
  )
}
