import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole, startEndpoint }) => {
  const token = localStorage.getItem('token');
  const roles = JSON.parse(localStorage.getItem('roles')) || [];

  const hasAccess = roles.includes(requiredRole);

  if (!token || !hasAccess) {
    return <Navigate to = {startEndpoint} />;
  }

  return children; 
};

export default ProtectedRoute;
