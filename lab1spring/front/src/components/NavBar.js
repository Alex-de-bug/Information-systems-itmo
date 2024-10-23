import React, { useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { useLocation, useNavigate } from 'react-router-dom'; 
import { useDispatch, useSelector } from 'react-redux'; 

import { logout } from '../redux/slices/userSlice';


const NavBar = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const location = useLocation();
  const navigate = useNavigate(); 
  const dispatch = useDispatch();
  var userName = useSelector((state) => state.user.user);
  const [onAdminPage, setOnAdminPage] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
    }
  }, [userName]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    localStorage.removeItem('name'); 
    setIsLoggedIn(false); 
    dispatch(logout()); 
    navigate('/login');
  };

  useEffect(() => {
    setOnAdminPage(location.pathname === '/admin');
  }, [location.pathname]);

  const handleHomePage = () => {
    navigate('/');
  };

  const handleAdminPage = () => {
    navigate('/admin');
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          {!isLoggedIn ? (
            <>
              <Typography variant="h6" sx={{ flexGrow: 1, textAlign: 'center' }}>
                Дениченко, ISU: 367193
              </Typography>
            </>
          ) : (
            <>
              <Typography variant="h6" sx={{ flexGrow: 1, textAlign: 'center' }}>
                LOGIN: {localStorage.getItem('name')} ROLE:{' '}
                {localStorage.getItem('roles') &&
                localStorage.getItem('roles').includes('ROLE_ADMIN')
                  ? 'ADMIN'
                  : 'USER'}
              </Typography>
              {
                onAdminPage ? (
                  <Button color="inherit" onClick={handleHomePage}>
                    Домой
                  </Button>
                ) : (
                  <Button color="inherit" onClick={handleAdminPage}>
                    Управление
                  </Button>
                )
              }
              <Button color="inherit" sx={{ marginLeft: 'auto' }} onClick={handleLogout}>
                Выйти
              </Button>
            </>
          )}
        </Toolbar>
      </AppBar>
    </Box>
  );
};

export default NavBar;
