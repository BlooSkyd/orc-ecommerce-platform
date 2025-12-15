import React, { useEffect, useState } from 'react'
import { orders, users, products } from '../api'
import Drawer from '../components/Drawer'

export default function Orders(){
  const [list, setList] = useState([])
  const [q, setQ] = useState('')
  const [selected, setSelected] = useState(null)
  const [open, setOpen] = useState(false)
  const [usersList, setUsersList] = useState([])
  const [productsList, setProductsList] = useState([])

  async function load(){
    try {
      const data = await orders.list()
      setList(data || [])
    } catch(e) {
      console.error(e)
    }
  }

  useEffect(() => {
    load()
    users.list().then(u => setUsersList(u || [])).catch(() => {})
    products.list().then(p => setProductsList(p || [])).catch(() => {})
  }, [])

  function openNew(){ setSelected({items:[]}); setOpen(true) }
  async function openEdit(it){
    try {
      const full = await orders.get(it.id)
      setSelected(full || it)
      setOpen(true)
    } catch(e) {
      alert(e.message)
    }
  }
  
  async function remove(id){
    if(!confirm('Delete?')) return
    try {
      await orders.remove(id)
      await load()
    } catch(e) {
      alert(e.message)
    }
  }

  const filtered = list.filter(o => (o.id || '').toString().includes(q))

  return (
    <div>
      <div className="header">
        <h2>Orders</h2>
        <div>
          <button className="btn" onClick={openNew}>New order</button>
        </div>
      </div>
      <div className="toolbar">
        <input className="search" placeholder="Search by id" value={q} onChange={e => setQ(e.target.value)} />
        <button className="btn" onClick={() => { 
          if(!q) return load()
          orders.byUser(q).then(r => setList(r || [])).catch(e => alert(e.message))
        }}>Search</button>
        <button className="btn" onClick={() => { setQ(''); load() }}>Reset</button>
      </div>
      <div className="list">
        <div className="list-header">
          <div className="col">Order / Address</div>
          <div className="col">Customer</div>
          <div className="col">Status</div>
          <div className="col" style={{flex:'0 0 120px'}}>Total</div>
          <div className="col" style={{flex:'0 0 140px',textAlign:'right'}}>Actions</div>
        </div>
        {filtered.map(it => {
          const disabled = it.orderStatus === 'DELIVERED' || it.orderStatus === 'CANCELLED'
          return (
          <div key={it.id} className="row">
            <div className="meta" style={{flex:1}}>
              <div><strong>Order #{it.id}</strong></div>
              <div className="small">{it.shippingAddress}</div>
            </div>
            <div style={{flex:1}}>{it.userName || it.userId}</div>
            <div style={{flex:1}}>{it.orderStatus}</div>
            <div style={{flex:'0 0 120px'}}>{it.totalAmount || ''}</div>
            <div style={{flex:'0 0 140px',textAlign:'right'}}>
              <button className="btn" onClick={() => openEdit(it)}>Edit</button>
              <button className={disabled ? 'btn secondary' : 'btn danger'} onClick={() => remove(it.id)} disabled={disabled} title={disabled ? 'Cannot delete delivered or cancelled order' : ''}>Delete</button>
            </div>
          </div>
        )})}
      </div>

      <Drawer open={open} onClose={() => setOpen(false)} title={selected && selected.id ? 'Edit order' : 'New order'}>
        {selected && (
          <OrderForm 
            data={selected} 
            users={usersList} 
            products={productsList} 
            onSaved={async () => { setOpen(false); await load() }} 
          />
        )}
      </Drawer>
    </div>
  )
}

function OrderForm({ data, users, products, onSaved }) {
  const [form, setForm] = useState({
    userId: data.userId || '', 
    shippingAddress: data.shippingAddress || '', 
    items: data.orderItems || data.items || [],
    status: data.orderStatus || 'PENDING'
  })
  const [line, setLine] = useState({ productId: '', quantity: 1 })

  function addLine() {
    const p = products.find(x => String(x.id) === String(line.productId))
    if(!p) { alert('Select a product'); return }
    if(!(parseInt(line.quantity) > 0)) { alert('Quantity > 0'); return }
    
    const item = { productId: p.id, quantity: parseInt(line.quantity) }
    setForm({ ...form, items: [...(form.items || []), item] })
    setLine({ productId: '', quantity: 1 })
  }

  function removeLine(idx) {
    setForm({ ...form, items: form.items.filter((_, i) => i !== idx) })
  }

  async function save() {
    try {
      if (data.id) {
        // Only status update is supported for existing orders
        const payload = { status: form.status }
        await orders.update(data.id, payload)
        onSaved()
        return
      }

      // New order validation
      if(!form.userId) { alert('Choose user'); return }
      if(!form.shippingAddress || form.shippingAddress.length < 5) { alert('Valid shipping address required'); return }
      if(!form.items || form.items.length === 0) { alert('Add at least one item'); return }

      const payload = { userId: parseInt(form.userId), shippingAddress: form.shippingAddress, items: form.items }
      await orders.create(payload)
      onSaved()
    } catch(e) {
      alert(e.message)
    }
  }

  return (
    <div>
      {/* User Selection */}
      <div className="form-row">
        <label className="small">Customer</label>
        {data.id ? (
          <div className="input" style={{padding:'8px',background:'#f9fafb'}}>{(users.find(u=>String(u.id)===String(form.userId))?.firstName || '') + ' ' + (users.find(u=>String(u.id)===String(form.userId))?.lastName || '')}</div>
        ) : (
          <select className="input" value={form.userId} onChange={e => setForm({...form, userId: e.target.value})}>
            <option value="">-- choose --</option>
            {users.map(u => <option key={u.id} value={u.id}>{u.firstName} {u.lastName}</option>)}
          </select>
        )}
      </div>

      {/* Address Input */}
      <div className="form-row">
        <label className="small">Shipping Address</label>
        {data.id ? (
          <div className="input" style={{padding:'8px',background:'#f9fafb'}}>{form.shippingAddress}</div>
        ) : (
          <input 
            className="input" 
            placeholder="Shipping address" 
            value={form.shippingAddress} 
            onChange={e => setForm({...form, shippingAddress: e.target.value})} 
          />
        )}
      </div>
      
      <hr />
      
      {/* Add Line Item Section (only for new orders) */}
      {!data.id && (
        <>
          <div className="form-row">
            <label className="small">Add Product</label>
            <div style={{display: 'flex', gap: '5px'}}>
              <select className="input" value={line.productId} onChange={e => setLine({...line, productId: e.target.value})}>
                <option value="">-- product --</option>
                {products.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
              </select>
              <input 
                className="input" 
                type="number" 
                style={{width: '60px'}} 
                value={line.quantity} 
                onChange={e => setLine({...line, quantity: e.target.value})} 
                min="1"
              />
              <button className="btn" onClick={addLine}>Add</button>
            </div>
          </div>

          {/* List of Added Items */}
          <div className="items-list" style={{marginTop: '10px', marginBottom: '20px'}}>
            <label className="small">Items</label>
            {(form.items || []).map((it, idx) => {
              const p = products.find(x => String(x.id) === String(it.productId))
              return (
                <div key={idx} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom:'1px solid #eee'}}>
                  <div>{p ? p.name : it.productId} x {it.quantity}</div>
                  <div><button className="btn danger small" onClick={() => removeLine(idx)}>Remove</button></div>
                </div>
              )
            })}
            {(!form.items || form.items.length === 0) && <div className="small text-muted">No items added yet</div>}
          </div>
        </>
      )}

      {/* For existing orders show read-only items with qty, unit price and subtotal */}
      {data.id && (
        <div className="items-list" style={{marginTop: '10px', marginBottom: '20px'}}>
          <label className="small">Items (read-only)</label>
          {(form.items || []).map((it, idx) => (
            <div key={idx} style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '6px 0', borderBottom:'1px solid #eee'}}>
              <div style={{flex:1}}>{it.productName || it.productId}</div>
              <div style={{width:120,textAlign:'right'}}>Qty: {it.quantity}</div>
              <div style={{width:140,textAlign:'right'}}>Unit: {it.unitPrice != null ? it.unitPrice : ''}</div>
              <div style={{width:140,textAlign:'right'}}>Subtotal: {it.subtotal != null ? it.subtotal : ''}</div>
            </div>
          ))}
          {(!form.items || form.items.length === 0) && <div className="small text-muted">No items</div>}
        </div>
      )}

      <div className="form-row">
        <label className="small">Status</label>
        {data.id ? (
          <select className="input" value={form.status} onChange={e=>setForm({...form,status:e.target.value})}>
            <option value="PENDING">PENDING</option>
            <option value="CONFIRMED">CONFIRMED</option>
            <option value="SHIPPED">SHIPPED</option>
            <option value="DELIVERED">DELIVERED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        ) : <div className="small">(status will be assigned after creation)</div>}
      </div>

      <div style={{display: 'flex', justifyContent: 'flex-end'}}>
        <button className="btn primary" onClick={save}>{data.id ? 'Update status' : 'Save Order'}</button>
      </div>
    </div>
  )
}