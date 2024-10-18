import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import ProtectedRoute from './protect_routes/ProtectedRoute';
import Admin from './pages/Admin';


function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route 
          path="/home" 
          element={
            <ProtectedRoute requiredRole="ROLE_USER" startEndpoint="/login"> 
              <Home />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/admin" 
          element={
            <ProtectedRoute requiredRole="ROLE_ADMIN" startEndpoint="/home"> 
              <Admin />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </Router>
  );
}

export default App;
