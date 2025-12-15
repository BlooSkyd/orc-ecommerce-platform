import React from 'react'

export default function Drawer({open, onClose, title, children}){
  return (
    <div className={open ? 'drawer open' : 'drawer'}>
      <div style={{display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:12}}>
        <h3 style={{margin:0}}>{title}</h3>
        <button className="btn" onClick={onClose}>Close</button>
      </div>
      {children}
    </div>
  )
}
