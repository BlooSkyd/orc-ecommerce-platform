import React, { useEffect, useState } from 'react'
import { products } from '../api'
import Drawer from '../components/Drawer'

export default function Products(){
  const [list, setList] = useState([])
  const [q, setQ] = useState('')
  const [selected, setSelected] = useState(null)
  const [open, setOpen] = useState(false)

  async function load(){
    try{
      const data = await products.list()
      setList(data || [])
    }catch(e){console.error(e)}
  }
  useEffect(()=>{load()},[])

  function openNew(){setSelected({});setOpen(true)}
  function openEdit(it){setSelected(it);setOpen(true)}

  async function remove(id){
    if(!confirm('Delete?')) return
    try{await products.remove(id); await load()}catch(e){alert(e.message)}
  }

  async function doSearch(){
    if(!q) return load()
    try{const res = await products.search(q); setList(res || [])}catch(e){alert(e.message)}
  }

  const filtered = list.filter(p => (p.name || '').toLowerCase().includes(q.toLowerCase()))

  return (
    <div>
      <div className="header">
        <h2>Products</h2>
        <div>
          <button className="btn" onClick={openNew}>New product</button>
        </div>
      </div>
      <div className="toolbar">
        <input className="search" placeholder="Search" value={q} onChange={e=>setQ(e.target.value)} />
        <button className="btn" onClick={doSearch}>Search</button>
        <button className="btn" onClick={()=>{setQ(''); load()}}>Reset</button>
      </div>
      <div className="list">
        <div className="list-header">
          <div className="col">Name / Description</div>
          <div className="col" style={{flex:'0 0 110px'}}>Price</div>
          <div className="col" style={{flex:'0 0 90px'}}>Stock</div>
          <div className="col" style={{flex:'0 0 120px'}}>Category</div>
          <div className="col" style={{flex:'0 0 80px'}}>Active</div>
          <div className="col" style={{flex:'0 0 160px',textAlign:'right'}}>Actions</div>
        </div>
        {filtered.map(it=> (
          <div key={it.id} className="row">
            <div className="meta" style={{flex:1}}>
              <div><strong>{it.name}</strong></div>
              <div className="small">{it.description}</div>
            </div>
            <div style={{flex:'0 0 110px'}}>{it.price}</div>
            <div style={{flex:'0 0 90px'}}>{it.stock}</div>
            <div style={{flex:'0 0 120px'}}>{it.category}</div>
            <div style={{flex:'0 0 80px'}}>{it.active ? 'Yes' : 'No'}</div>
            <div style={{flex:'0 0 160px',textAlign:'right'}}>
              <button className="btn" onClick={()=>openEdit(it)}>Edit</button>
              <button className="btn danger" onClick={()=>remove(it.id)}>Delete</button>
            </div>
          </div>
        ))}
      </div>

      <Drawer open={open} onClose={()=>setOpen(false)} title={selected && selected.id ? 'Edit product' : 'New product'}>
        {selected && <ProductForm data={selected} onSaved={async ()=>{setOpen(false);await load()}} />}
      </Drawer>
    </div>
  )
}

function ProductForm({data,onSaved}){
  const CATS = ['ELECTRONICS','BOOKS','FOOD','OTHER']
  const [form,setForm] = useState({name:data.name||'',description:data.description||'',price: data.price!==undefined? String(data.price) : '',stock: data.stock!==undefined? String(data.stock) : '0',category:data.category||'OTHER',active: data.active===undefined? true: data.active})
  const [errors,setErrors] = useState(null)

  function validate(){
    const errs = []
    if(!form.name || form.name.length<3) errs.push('Name >=3 chars')
    if(!form.description || form.description.length<10) errs.push('Description >=10 chars')
    const p = parseFloat(form.price)
    if(!(p > 0)) errs.push('Price must be > 0')
    // two decimals
    if(!/^\d+(\.\d{1,2})?$/.test(String(form.price))) errs.push('Price must have at most 2 decimals')
    if(parseInt(form.stock) < 0) errs.push('Stock must be >= 0')
    if(!form.category) errs.push('Category required')
    return errs
  }

  async function save(){
    const v = validate()
    if(v.length){ setErrors(v); return }
    try{
      const payload = {name: form.name, description: form.description, price: parseFloat(form.price), stock: parseInt(form.stock), category: form.category, active: !!form.active}
      if(data.id) await products.update(data.id, payload)
      else await products.create(payload)
      onSaved()
    }catch(e){alert(e.message)}
  }
  return (
    <div>
      {errors && <div style={{color:'red',marginBottom:8}}>{errors.join(' · ')}</div>}
      <div className="form-row"><label className="small">Name</label><input className="input" placeholder="Name" value={form.name} onChange={e=>setForm({...form,name:e.target.value})} /></div>
      <div className="form-row"><label className="small">Description</label><textarea className="input" placeholder="Description" value={form.description} onChange={e=>setForm({...form,description:e.target.value})} /></div>
      <div className="form-row"><label className="small">Price (EUR)</label><input className="input" type="number" min="0.01" step="0.01" placeholder="Price" value={form.price} onChange={e=>setForm({...form,price:e.target.value})} /></div>
      <div className="form-row"><label className="small">Stock (units)</label><input className="input" type="number" min="0" step="1" placeholder="Stock" value={form.stock} onChange={e=>setForm({...form,stock:e.target.value})} /></div>
      <div className="form-row">
        <label className="small">Category</label>
        <select className="input" value={form.category} onChange={e=>setForm({...form,category:e.target.value})}>
          {CATS.map(c=> <option key={c} value={c}>{c}</option>)}
        </select>
      </div>
      <div className="form-row">
        <label><input type="checkbox" checked={!!form.active} onChange={e=>setForm({...form,active:e.target.checked})} /> Active</label>
      </div>
      <div style={{display:'flex',justifyContent:'flex-end'}}>
        <button className="btn" onClick={save}>Save</button>
      </div>
    </div>
  )
}
