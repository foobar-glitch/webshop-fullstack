import React from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

import './App.scss';
import "bootstrap/dist/css/bootstrap.min.css"
import Home from './components/Home';
import Header from './components/Header';
import NotFound from './components/NotFound';
import ProductEntry from './components/ProductEntry';
import ShoppingCart from './components/Cart';
import AddInventoryItem from './components/AddInventoryItem';
import { api_endpoint } from './components/Universals';
import useFetchGET from './components/useFetchGET';
import ProtectedRoute from './components/ProtectedRoute';
import Register from './components/Register';
import ValidateToken from './components/ValidateToken';

function App() {

  const { data: profile_data, isPending, error } = useFetchGET(api_endpoint+"/authenticate");

  return (!isPending&&
    <BrowserRouter>
      <div className="App">
        {/* This Header will appear on every route */}
        <Header />
        <div className="content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/entry" element={<ProductEntry />}/>
            <Route path="/cart" element={<ShoppingCart />}></Route>
            <Route path='/add-inventory' element={
              <ProtectedRoute 
                component={AddInventoryItem}
                condition={profile_data&&profile_data.admin}
              />
              }> </Route>
            <Route path='/register' element={< Register />} />
            <Route path='/validate-token' element={<ValidateToken />}/>
            <Route path="*" element={<NotFound />}/>
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
