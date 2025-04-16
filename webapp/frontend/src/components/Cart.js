import React, { useState, useEffect } from 'react';
import { api_endpoint } from './Universals';
import { Link } from 'react-router-dom';
const ShoppingCart = () => {
  const [cartItems, setCartItems] = useState([]);
  const [totalCost, setTotalCost] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch cart data
  const fetchCart = async () => {
    try {
      const cartRes = await fetch(api_endpoint + `/cart`, {
        credentials: 'include',
      });

      if (!cartRes.ok) {
        setError('Failed to load cart. Are you logged in?');
        setLoading(false);
        return;
      }

      const cartData = await cartRes.json();

      const updatedCartItems = await Promise.all(
        cartData.map(async (item) => {
          const productDetails = await fetchProductDetails(item.inventoryId);
          return { ...item, productDetails };
        })
      );

      setCartItems(updatedCartItems);
      setLoading(false);

      if (updatedCartItems.length === 0) {
        setError('Your cart is empty.');
      }

      // Fetch total cost after setting items
      fetchTotalCost();
    } catch (err) {
      setLoading(false);
      setError('Something went wrong while fetching the cart.');
    }
  };

  // Fetch product details
  const fetchProductDetails = async (inventoryId) => {
    try {
      const res = await fetch(api_endpoint + `/inventory/${inventoryId}`, {
        credentials: 'include',
      });
      if (res.ok) {
        return await res.json();
      }
      return { productName: 'Unknown Product', price: 0 };
    } catch (err) {
      console.warn('Could not fetch product details for inventoryId:', inventoryId);
      return { productName: 'Unknown Product', price: 0 };
    }
  };

  // Fetch total cost
  const fetchTotalCost = async () => {
    try {
      const res = await fetch(api_endpoint + `/cart/cost`, {
        credentials: 'include',
      });
      if (res.ok) {
        const data = await res.json(); // Assume response is like { total: 123.45 }
        setTotalCost(data || 0);
      } else {
        console.warn('Failed to fetch total cost');
      }
    } catch (err) {
      console.error('Error fetching total cost:', err);
    }
  };

  // Delete item
  const deleteCartItem = async (cartId) => {
    if (!window.confirm('Are you sure you want to remove this item from your cart?')) return;

    try {
      const formData = new URLSearchParams();
      formData.append('cartId', cartId);

      const res = await fetch(api_endpoint + '/cart', {
        method: 'DELETE',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString(),
      });

      if (res.ok) {
        alert('Item removed from cart.');
        fetchCart(); // Refetch cart & total cost
      } else {
        const text = await res.text();
        alert(`Failed to delete item: ${text}`);
      }
    } catch (err) {
      console.error('Error deleting item:', err);
      alert('Something went wrong while deleting the item.');
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  return (
    <div style={{ fontFamily: 'Arial, sans-serif', padding: '2em', backgroundColor: '#f9f9f9' }}>
      <h1 style={{ textAlign: 'center' }}>Your Shopping Cart</h1>

      <div
        style={{
          maxWidth: '800px',
          margin: '0 auto',
          backgroundColor: '#fff',
          padding: '1.5em',
          borderRadius: '10px',
          boxShadow: '0 0 10px rgba(0,0,0,0.1)',
        }}
      >
        {loading ? (
          <p>Loading cart...</p>
        ) : error ? (
          <p>{error}</p>
        ) : (
          <>
            {cartItems.map((item) => (
              <div
                key={item.cartId}
                style={{
                  padding: '1em',
                  borderBottom: '1px solid #ddd',
                  position: 'relative',
                }}
              >
                <Link to={"/entry?inventoryId="+item.inventoryId}><h3>{item.productDetails.productName || 'Unknown Product'}</h3></Link>
                <p><strong>Price:</strong> €{item.productDetails.price.toFixed(2)}</p>
                <p><strong>Quantity:</strong> {item.quantity}</p>
                <p><strong>Added:</strong> {new Date(item.createdAt).toLocaleString()}</p>
                <button
                  onClick={() => deleteCartItem(item.cartId)}
                  style={{
                    backgroundColor: 'crimson',
                    color: 'white',
                    border: 'none',
                    padding: '0.5em 1em',
                    borderRadius: '5px',
                    cursor: 'pointer',
                    position: 'absolute',
                    top: '1em',
                    right: '1em',
                  }}
                >
                  Delete
                </button>
              </div>
            ))}

            {/* Total Cost Section */}
            <div style={{ textAlign: 'right', marginTop: '1em', fontSize: '1.2em' }}>
              <strong>Total Cost:</strong> €{totalCost.toFixed(2)}
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ShoppingCart;