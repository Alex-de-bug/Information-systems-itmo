import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Button, Table, TableBody, TableCell, TableHead, TableRow, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { loginSuccess, setNotification } from '../redux/slices/userSlice';

const AdminPanel = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();


  useEffect(() => {
    const fetchRequests = async () => {
      setLoading(true);
      try {
        const response = await axios.get('http://localhost:8080/admin/requests', {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        });
        setRequests(response.data);
      } catch (err) {
        if(err.response.status === 403) {
            try {
              const response = await axios.get('http://localhost:8080/user/token', {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem("token")}`, 
                    },
                  });
                  localStorage.setItem('token', response.data.token);
                  localStorage.setItem('roles', JSON.stringify(response.data.roles));
                  localStorage.setItem('name', response.data.name);
          
                  dispatch(loginSuccess({
                    user: response.data.name, 
                    roles: response.data.roles, 
                    token: response.data.token
                  }));
                  
                  navigate('/home');
            } catch (error) {
                console.error('Ошибка при отправке данных:', error);
            } finally{
              dispatch(setNotification({
                color: 'info', 
                message: 'У вас больше нет прав админа'
              }));
            } 
        }
        dispatch(setNotification({
          color: 'error', 
          message: err.response.data.message
        }));
      } finally {
        setLoading(false);
      }
    };
    fetchRequests();
  }, [dispatch, navigate]);

  const handleRequest = async (username, rulling) => {
    try {
        const response = await axios.post('http://localhost:8080/admin/requests', {username: username, rulling: rulling}, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        });
        setRequests(response.data);
      } catch (err) {
        dispatch(setNotification({
          color: 'error', 
          message: err.response.data.message
        }));
        setRequests([]);
      } finally {
        setLoading(false);
      }
  };

  return (
    <Paper sx={{ padding: 2 }}>
      <h1>Админ панель</h1>
      {loading ? (
        <p>Загрузка заявок...</p>
      ) : (
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Имя пользователя</TableCell>
              <TableCell align="right">Действия</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {requests.map((request) => (
              <TableRow key={request.username}>
                <TableCell>{request.username}</TableCell>
                <TableCell align="right">
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => handleRequest(request.username, true)}
                    sx={{ marginRight: 1 }}
                  >
                    Принять
                  </Button>
                  <Button
                    variant="contained"
                    color="secondary"
                    onClick={() => handleRequest(request.username, false)}
                  >
                    Отклонить
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
    </Paper>
  );
};

export default AdminPanel;
