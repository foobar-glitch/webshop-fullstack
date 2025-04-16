import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import './css/ProductEntry.scss';
import { api_endpoint } from './Universals';

const ProductEntry = () => {
  const [searchParams] = useSearchParams();
  const inventoryId = searchParams.get('inventoryId');

  const [product, setProduct] = useState(null);
  const [averageRating, setAverageRating] = useState(null);
  const [ratings, setRatings] = useState([]);
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      if (!inventoryId) {
        setError('No inventory ID provided.');
        setLoading(false);
        return;
      }

      try {
        const [productRes, avgRes] = await Promise.all([
          fetch(`${api_endpoint}/inventory/${inventoryId}`),
          fetch(`${api_endpoint}/average-rating?inventoryId=${inventoryId}`)
        ]);

        if (!productRes.ok) throw new Error('Product not found');

        const productData = await productRes.json();
        const avg = avgRes.ok ? await avgRes.json() : null;

        setProduct(productData);
        setAverageRating(avg);
      } catch (err) {
        setError('Failed to load product details.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [inventoryId]);

  useEffect(() => {
    const fetchRatings = async () => {
      if (!inventoryId) return;

      try {
        const res = await fetch(`${api_endpoint}/rating?inventoryId=${inventoryId}`);
        const ratingsData = await res.json();

        const enrichedRatings = await Promise.all(
          ratingsData.map(async (r) => {
            try {
              const userRes = await fetch(`${api_endpoint}/username/${r.userId}`);
              const username = userRes.ok ? await userRes.text() : `User ${r.userId}`;
              return { ...r, username };
            } catch {
              return { ...r, username: `User ${r.userId}` };
            }
          })
        );

        setRatings(enrichedRatings);
      } catch (err) {
        setError('Failed to load reviews.');
      }
    };

    fetchRatings();
  }, [inventoryId]);

  const addToCart = async () => {
    try {
      const response = await fetch(`${api_endpoint}/cart`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          
        },
        body: new URLSearchParams({
          inventoryId,
          quantity: quantity.toString(),
        }),
        credentials: 'include'
      });

      if (response.ok) {
        alert('Item added to cart!');
      } else {
        const text = await response.text();
        alert(`Failed to add to cart: ${text}`);
      }
    } catch (err) {
      console.error('Add to cart error:', err);
      alert('Something went wrong while adding to cart.');
    }
  };

  if (loading) return <div className="product-details">Loading product details...</div>;
  if (error) return <div className="product-details">{error}</div>;

  return (
    <div className="product-entry">
      <div className="product-details">
        <h1>
          {product.productName}
          {averageRating !== null && (
            <span style={{ fontSize: '1rem', color: '#999' }}>
              {' '}({averageRating.toFixed(1)} ★)
            </span>
          )}
        </h1>
        <div className="price">€{product.price.toFixed(2)}</div>
        <div className="category">Category: {product.category}</div>
        <p>{product.description}</p>

        {/* Quantity and Buy */}
        <label htmlFor="quantity">Quantity:</label>
        <input
          id="quantity"
          type="number"
          min="1"
          value={quantity}
          onChange={(e) => setQuantity(Math.max(1, parseInt(e.target.value) || 1))}
        />
        <button className="buy-button" onClick={addToCart}>🛒 Buy</button>
      </div>

      {/* Rating Form */}
      <form action={`${api_endpoint}/rating`} method="POST">
        <input type="hidden" name="inventory_id" value={inventoryId} required />

        <label>Stars:</label>
        <div className="stars">
          {[5, 4, 3, 2, 1].map((star) => (
            <React.Fragment key={star}>
              <input type="radio" id={`star${star}`} name="stars" value={star} required={star === 5} />
              <label htmlFor={`star${star}`} title={`${star} stars`}>★</label>
            </React.Fragment>
          ))}
        </div>

        <label htmlFor="comment">Comment (optional):</label>
        <textarea id="comment" name="comment" rows="4" placeholder="Write your thoughts..."></textarea>

        <input type="submit" value="Submit Rating" />
      </form>

      {/* Comments */}
      <div id="commentsSection" className="comments">
        <h2>Customer Reviews</h2>
        <div id="commentsContainer">
          {ratings.length === 0 ? (
            <p>No ratings yet.</p>
          ) : (
            ratings.map((rating, index) => (
              <div className="comment" key={index}>
                <strong>@{rating.username}</strong> - <span>{rating.stars} ★</span><br />
                <em>{rating.comment || 'No comment provided.'}</em><br />
                <small>{new Date(rating.createdAt).toLocaleString()}</small>
                <hr />
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default ProductEntry;
