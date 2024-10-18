import React, { useEffect, useState } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import { useNavigate } from 'react-router-dom'; 
import { useDispatch } from 'react-redux';
import { logoutUser } from '../redux/actions/userActions';


const darkTheme = createTheme({
  palette: {
    mode: 'dark',
  },
});

const NavBar = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate(); 
  const dispatch = useDispatch();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    localStorage.removeItem('name'); 
    setIsLoggedIn(false); 
    dispatch(logoutUser()); 
    //navigate('/login');
  };

  return (
    <ThemeProvider theme={darkTheme}>
      <Box sx={{ flexGrow: 1 }}>
        <AppBar position="static">
          <Toolbar>
            {!isLoggedIn ? (
              <>
                <Button onClick={() => navigate('/login')} color="inherit" sx={{ marginRight: 'auto' }}>Вход</Button>
                <Typography variant="h6" sx={{ flexGrow: 1, textAlign: 'center' }}>
                  Добро пожаловать на сайт!
                </Typography>
                <Button onClick={() => navigate('/register')} color="inherit" sx={{ marginLeft: 'auto' }}>Регистрация</Button>
              </>
            ) : (
              <>
                <Typography variant="h6" sx={{ flexGrow: 1, textAlign: 'center' }}>
                  Вы залогинены!
                </Typography>
                <Button color="inherit" sx={{ marginLeft: 'auto' }} onClick={handleLogout}>
                  Выйти
                </Button>
              </>
            )}
          </Toolbar>
        </AppBar>
      </Box>
    </ThemeProvider>
  );
};

export default NavBar;
