import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import './App.scss';
//import { Button, Alert, Breadcrumb, Card, Form, Container, Row, Col } from "react-bootstrap"
import "bootstrap/dist/css/bootstrap.min.css"
import Home from './components/Home';
import Header from './components/Header';
import NotFound from './components/NotFound';
import ProductEntry from './components/ProductEntry';
import ShoppingCart from './components/Cart';

function App() {
  return (
    <BrowserRouter>
      <div className="App">
        {/* This Header will appear on every route */}
        <Header />
        <div className="content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/entry" element={<ProductEntry />}/>
            <Route path="/cart" element={<ShoppingCart />}></Route>
            <Route path="*" element={<NotFound />}/>
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
