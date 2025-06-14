import React, { useState } from 'react';
import { api_endpoint } from './Universals';

const ValidateToken = () => {
  const [token, setToken] = useState('');
  const [responseMessage, setResponseMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await fetch(api_endpoint+'/register/validate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({ token })
      });

      const message = await response.text();
      setResponseMessage(message);
    } catch (error) {
      console.error('Validation error:', error);
      setResponseMessage('An error occurred while validating the token.');
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <h2 className="text-center mb-4">Validate Your Token</h2>
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="token" className="form-label">Token</label>
              <input
                type="text"
                className="form-control"
                id="token"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary w-100">Validate</button>
          </form>
          {responseMessage && (
            <div className="mt-3 text-center">
              <strong>{responseMessage}</strong>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ValidateToken;