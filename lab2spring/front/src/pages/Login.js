import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import { loginSuccess, setNotification } from '../redux/slices/userSlice';
import AuthGuard from '../utils/AuthGuard';




const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const dispatch = useDispatch();
  const navigate = useNavigate();



  const handleLogin = async (e) => {
    e.preventDefault();

    const userData = {
        username: username,
        password: password,
    };
      
    try {
        const response = await axios.post(`http://${process.env.REACT_APP_SERVER}/api/auth`, userData);
        console.log('Успешный ответ:', response.data);

        dispatch(loginSuccess({
          user: response.data.name, 
          roles: response.data.roles, 
          token: response.data.token
        }));
        
        navigate('/home');

        dispatch(setNotification({
          color: 'success', 
          message: 'Вы успешно вошли!'
        }));

    }catch (error) {
        console.error('Ошибка при отправке данных:', error);
        dispatch(setNotification({
          color: 'error', 
          message: error.response ? error.response.data.message : 'Сервер недоступен. Попробуйте позже.'
        }));
    }
    
  };

  return (
    <AuthGuard>
      <Box
        component="main"
        sx={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          flexGrow: 1,
          pb: "15%",
        }}
      >
        <Box
          component="form"
          onSubmit={handleLogin}
          sx={{
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            padding: 4,
            borderRadius: 2,
            display: 'flex',
            flexDirection: 'column',
            width: '300px',
          }}
        >
          <TextField
            label="Username"
            variant="outlined"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            sx={{ marginBottom: 2 }}
            fullWidth
          />
          <TextField
            label="Password"
            variant="outlined"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            sx={{ marginBottom: 2 }}
            fullWidth
          />
          <Button variant="contained" color="primary" type="submit" sx={{ marginBottom: 4 }} fullWidth>
            Login
          </Button>
          <Button variant="outlined" color="info" onClick={() => navigate('/register')}>
            Not registered?
          </Button>
        </Box>
      </Box>
    </AuthGuard>
  );
};

export default Login;
