import React from 'react';
import { Navigate } from 'react-router-dom';
import { useDispatch } from 'react-redux'; 
import { logout } from '../redux/slices/userSlice';



const ProtectedRoute = ({ children, requiredRole, startEndpoint }) => {
  const token = localStorage.getItem('token');
  const roles = JSON.parse(localStorage.getItem('roles')) || [];
  const dispatch = useDispatch();

  const hasAccess = roles.includes(requiredRole);

  if (!token || !hasAccess) {
    dispatch(logout()); 
    return <Navigate to = {startEndpoint} />;
  }

  return children; 
};

export default ProtectedRoute;
