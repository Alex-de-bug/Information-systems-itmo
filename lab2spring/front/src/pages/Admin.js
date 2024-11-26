import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminPanel from '../components/AdminPanel';
import { loginSuccess, setNotification } from '../redux/slices/userSlice';
import { useDispatch } from 'react-redux';
import { Paper, Box, Button, Typography } from '@mui/material';



const Admin = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const admin = (JSON.parse(localStorage.getItem('roles')) || []).includes('ROLE_ADMIN');
  const [loading, setLoading] = useState(false);

  const handleRequestAdminRights = async () => {
    setLoading(true);
    try {

      const response = await axios.post(`http://${process.env.REACT_APP_SERVER}/api/user/rights`, {username: localStorage.getItem('name')}, {
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`, 
        },
      });
      if(response.status === 200){
        dispatch(setNotification({
          color: 'success', 
          message: 'Вы успешно отправили запрос!'
        }));
      }else if(response.status === 202){
        try {
            const response = await axios.get(`http://${process.env.REACT_APP_SERVER}/api/user/token`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`, 
                },
              });
      
              dispatch(loginSuccess({
                user: response.data.name, 
                roles: response.data.roles, 
                token: response.data.token
              }));
              
              navigate('/admin');
              dispatch(setNotification({
                color: 'success', 
                message: 'Теперь вы являетесь гойдой'
              }));
        } catch (error) {
            console.error('Ошибка при отправке данных:', error);
        }
      }
    } catch (error) {
      dispatch(setNotification({
        color: 'error', 
        message: error.response.data.message
      }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Box
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="center"
      height="100vh"
    >
      {admin ? (
    <AdminPanel />
  ) : (
    <Paper sx={{m: 2, p: 4, borderRadius: 2, backgroundColor: 'rgba(0, 0, 0, 0.87)', }}>
      <Typography variant="h6" gutterBottom>
        У вас нет прав администратора.
      </Typography>
      <Button
        variant="contained"
        onClick={handleRequestAdminRights}
        disabled={loading}
      >
        {loading ? 'Отправка запроса...' : 'Запросить права администратора'}
      </Button>
    </Paper>
  )}
    </Box>

</div>
  );
};

export default Admin;
