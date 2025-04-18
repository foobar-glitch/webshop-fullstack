import React, { useState } from 'react';
import { api_endpoint } from './Universals';

const AddInventoryItem = () => {
  const [formData, setFormData] = useState({
    productName: '',
    description: '',
    price: '',
    quantity: '',
    category: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Example POST request:
    const res = await fetch(api_endpoint+'/inventory', {
       method: 'POST',
       credentials: 'include',
       headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
        
      },
      body: new URLSearchParams({
        productName: formData.productName,
        description: formData.description,
        price: formData.price, 
        quantity: formData.quantity,
        category: formData.category
      })
     })

     console.log(await res.text())
  };

  const formStyle = {
    maxWidth: 'fit-content',
    marginLeft: 'auto',
    marginRight: 'auto',
    margin: '40px',
    fontFamily: 'Arial, sans-serif'
  };

  const labelStyle = {
    display: 'block',
    marginTop: '10px',
    fontWeight: 'bold'
  };

  const inputStyle = {
    width: '100%',
    padding: '8px',
    marginTop: '4px'
  };

  const buttonStyle = {
    marginTop: '20px',
    padding: '10px 20px'
  };

  return (
    <div style={formStyle} className='add-inventory'>
      <h2>Add New Inventory Item</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="productName" style={labelStyle}>Product Name:</label>
        <input
          type="text"
          id="productName"
          name="productName"
          value={formData.productName}
          onChange={handleChange}
          style={inputStyle}
          required
        />

        <label htmlFor="description" style={labelStyle}>Description:</label>
        <textarea
          id="description"
          name="description"
          rows="4"
          value={formData.description}
          onChange={handleChange}
          style={inputStyle}
        />

        <label htmlFor="price" style={labelStyle}>Price ($):</label>
        <input
          type="number"
          id="price"
          name="price"
          step="0.01"
          value={formData.price}
          onChange={handleChange}
          style={inputStyle}
          required
        />

        <label htmlFor="quantity" style={labelStyle}>Quantity:</label>
        <input
          type="number"
          id="quantity"
          name="quantity"
          value={formData.quantity}
          onChange={handleChange}
          style={inputStyle}
          required
        />

        <label htmlFor="category" style={labelStyle}>Category:</label>
        <input
          type="text"
          id="category"
          name="category"
          value={formData.category}
          onChange={handleChange}
          style={inputStyle}
        />

        <button type="submit" style={buttonStyle}>Add Item</button>
      </form>
    </div>
  );
};

export default AddInventoryItem;
