import React, { useEffect, useState } from 'react'
import { users, products, orders } from '../api'

export default function Home(){
  const [loading, setLoading] = useState(true)
  const [usersList, setUsersList] = useState([])
  const [productsList, setProductsList] = useState([])
  const [ordersList, setOrdersList] = useState([])

  useEffect(()=>{
    setLoading(true)
    Promise.all([
      users.list().catch(()=>[]),
      products.list().catch(()=>[]),
      orders.list().catch(()=>[])
    ])
    .then(([u,p,o]) => {
      setUsersList(u || [])
      setProductsList(p || [])
      setOrdersList(o || [])
    })
    .finally(()=>setLoading(false))
  },[])

  // Users stats
  const usersCount = usersList.length
  const usersActive = usersList.filter(u => u.active === true).length
  const usersActivePct = usersCount ? Math.round((usersActive / usersCount) * 100) : 0

  // Products stats
  const productsCount = productsList.length
  const productsByCategory = productsList.reduce((acc, p) => {
    const c = p.category || 'UNKNOWN'
    acc[c] = (acc[c] || 0) + 1
    return acc
  }, {})
  const lowStock = productsList.filter(p => typeof p.stock === 'number' && p.stock <= 10)

  // Orders stats
  const ordersCount = ordersList.length
  const ordersByStatus = ordersList.reduce((acc, o) => {
    const s = o.orderStatus || o.status || 'UNKNOWN'
    acc[s] = (acc[s] || 0) + 1
    return acc
  }, {})
  const avgOrderPrice = ordersCount ? (ordersList.reduce((s,o) => s + (parseFloat(o.totalAmount) || 0),0) / ordersCount) : 0
  const avgItemsPerOrder = ordersCount ? (ordersList.reduce((s,o) => {
    const items = o.orderItems || o.items || []
    const qty = items.reduce((q,it) => q + (it.quantity || 0), 0)
    return s + qty
  },0) / ordersCount) : 0

  return (
    <div>
      <div className="header"><h2>Accueil - Statistiques</h2></div>

      <div className="card">
        <h3>Utilisateurs</h3>
        {loading ? <div>Loading...</div> : (
          <div>
            <div>Nombre d'utilisateurs: <strong>{usersCount}</strong></div>
            <div>Actifs: <strong>{usersActive}</strong> ({usersActivePct}%)</div>
          </div>
        )}
      </div>

      <div className="card">
        <h3>Produits</h3>
        {loading ? <div>Loading...</div> : (
          <div>
            <div>Nombre d'articles différents: <strong>{productsCount}</strong></div>
            <div style={{marginTop:6}}>
              <strong>Nombre par catégorie:</strong>
              <ul>
                {Object.keys(productsByCategory).map(cat => (
                  <li key={cat}>{cat}: {productsByCategory[cat]}</li>
                ))}
              </ul>
            </div>

            <div style={{marginTop:6}}>
              {lowStock.length > 0 ? (
                <div>
                  <strong>Articles avec 10 unités ou moins (à surveiller):</strong>
                  <ul>
                    {lowStock.map(p => (
                      <li key={p.id}><span style={{color:'#b91c1c'}}>{p.name || p.id}</span> — stock: {p.stock}</li>
                    ))}
                  </ul>
                </div>
              ) : (
                <div>Tous les articles ont plus de 10 produits en stock.</div>
              )}
            </div>
          </div>
        )}
      </div>

      <div className="card">
        <h3>Commandes</h3>
        {loading ? <div>Loading...</div> : (
          <div>
            <div>Nombre de commandes: <strong>{ordersCount}</strong></div>
            <div style={{marginTop:6}}>
              <strong>Nombre par statut:</strong>
              <ul>
                {Object.keys(ordersByStatus).map(s => <li key={s}>{s}: {ordersByStatus[s]}</li>)}
              </ul>
            </div>
            <div>Prix moyen des commandes: <strong>{avgOrderPrice.toFixed(2)}</strong></div>
          </div>
        )}
      </div>
    </div>
  )
}
