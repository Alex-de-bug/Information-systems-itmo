import React, { useState } from 'react';
import axios from 'axios';
import { Box, Button, TextField } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';
import AuthGuard from '../utils/AuthGuard';


const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const dispatch = useDispatch();

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    const userData = {
      username: username,
      password: password,
      confirmPassword: confirmPassword,
    };
    
    try {
      const response = await axios.post('http://195.58.48.101/api/reg', userData);
      console.log('Успешный ответ:', response.data);
      navigate('/login');

      dispatch(setNotification({
        color: 'success', 
        message: 'Вы успешно зарегистрировались!'
      }));
    } catch (error) {
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
          onSubmit={handleRegister}
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
          <TextField
            label="Confirm Password"
            variant="outlined"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            sx={{ marginBottom: 2 }}
            fullWidth
          />
          <Button variant="contained" color="primary" type="submit" sx={{ marginBottom: 4 }} fullWidth>
            Register
          </Button>

          <Button variant="outlined" color="info" onClick={() => navigate('/login')}>
            Already registered?
          </Button>
        </Box>
      </Box>
    </AuthGuard>
  );
};

export default Register;
