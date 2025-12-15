import React, { useEffect, useState } from 'react'
import { users } from '../api'
import Drawer from '../components/Drawer'

export default function Users(){
  const [list, setList] = useState([])
  const [q, setQ] = useState('')
  const [selected, setSelected] = useState(null)
  const [open, setOpen] = useState(false)

  async function load(){
    try{const data = await users.list(); setList(data || [])}catch(e){console.error(e)}
  }
  useEffect(()=>{load()},[])
  function openNew(){setSelected({});setOpen(true)}
  function openEdit(it){setSelected(it);setOpen(true)}
  async function remove(id){if(!confirm('Delete?')) return; try{await users.remove(id); await load()}catch(e){alert(e.message)}}
  async function doSearch(){ if(!q) return load(); try{ const res = await users.search(q); setList(res || []) }catch(e){alert(e.message)} }

  const filtered = list.filter(u => ((u.firstName||'') + ' ' + (u.lastName||'')).toLowerCase().includes(q.toLowerCase()))

  return (
    <div>
      <div className="header">
        <h2>Users</h2>
        <div>
          <button className="btn" onClick={openNew}>New user</button>
        </div>
      </div>
      <div className="toolbar">
        <input className="search" placeholder="Search (last name)" value={q} onChange={e=>setQ(e.target.value)} />
        <button className="btn" onClick={doSearch}>Search</button>
        <button className="btn" onClick={()=>{setQ(''); load()}}>Reset</button>
      </div>
      <div className="list">
        <div className="list-header">
          <div className="col">Full name</div>
          <div className="col" style={{flex:'0 0 220px'}}>Email</div>
          <div className="col" style={{flex:'0 0 120px',textAlign:'right'}}>Actions</div>
        </div>
        {filtered.map(it=> (
          <div key={it.id} className="row">
            <div className="meta"><strong>{it.firstName} {it.lastName}</strong><div className="small">{it.email}</div></div>
            <div style={{flex:'0 0 220px'}}>{it.email}</div>
            <div style={{flex:'0 0 120px',textAlign:'right'}}>
              <button className="btn" onClick={()=>openEdit(it)}>Edit</button>
              <button className="btn danger" onClick={()=>remove(it.id)}>Delete</button>
            </div>
          </div>
        ))}
      </div>

      <Drawer open={open} onClose={()=>setOpen(false)} title={selected && selected.id ? 'Edit user' : 'New user'}>
        {selected && <UserForm data={selected} onSaved={async ()=>{setOpen(false);await load()}} />}
      </Drawer>
    </div>
  )
}

function UserForm({data,onSaved}){
  const [form,setForm] = useState({firstName:data.firstName||'', lastName:data.lastName||'', email:data.email||''})
  const [errors,setErrors] = useState(null)
  function validate(){
    const e = []
    if(!form.firstName || form.firstName.length<2) e.push('FirstName >=2 chars')
    if(!form.lastName || form.lastName.length<2) e.push('LastName >=2 chars')
    if(!form.email || !/.+@.+\..+/.test(form.email)) e.push('Valid email required')
    return e
  }
  async function save(){
    const v = validate()
    if(v.length){ setErrors(v); return }
    try{ if(data.id) await users.update(data.id, form); else await users.create(form); onSaved() }catch(e){alert(e.message)}
  }
  return (
    <div>
      {errors && <div style={{color:'red',marginBottom:8}}>{errors.join(' · ')}</div>}
      <div className="form-row"><input className="input" placeholder="First name" value={form.firstName} onChange={e=>setForm({...form,firstName:e.target.value})} /></div>
      <div className="form-row"><input className="input" placeholder="Last name" value={form.lastName} onChange={e=>setForm({...form,lastName:e.target.value})} /></div>
      <div className="form-row"><input className="input" placeholder="Email" value={form.email} onChange={e=>setForm({...form,email:e.target.value})} /></div>
      <div style={{display:'flex',justifyContent:'flex-end'}}><button className="btn" onClick={save}>Save</button></div>
    </div>
  )
}
