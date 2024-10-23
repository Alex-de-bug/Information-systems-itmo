import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { Button, Table, TableBody, TableCell, TableHead, TableRow, Paper } from '@mui/material';

const AdminPanel = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);


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
        setError('Ошибка при загрузке заявок');
      } finally {
        setLoading(false);
      }
    };
    fetchRequests();
  }, []);

  const handleApprove = async (username) => {
    try {
        const response = await axios.post('http://localhost:8080/admin/requests', {username: username, rulling: true}, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        });
        setRequests(response.data);
      } catch (err) {
        setError('Ошибка при загрузке заявок');
      } finally {
        setLoading(false);
      }
  };

  const handleReject = async (username) => {
    try {
        const response = await axios.post('http://localhost:8080/admin/requests', {username: username, rulling: false}, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
            }
        });
        setRequests(response.data);
      } catch (err) {
        setError('Ошибка при загрузке заявок');
      } finally {
        setLoading(false);
      }
  };

  return (
    <Paper sx={{ padding: 2 }}>
      <h1>Админ панель</h1>
      {loading ? (
        <p>Загрузка заявок...</p>
      ) : error ? (
        <p>{error}</p>
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
                    onClick={() => handleApprove(request.username)}
                    sx={{ marginRight: 1 }}
                  >
                    Принять
                  </Button>
                  <Button
                    variant="contained"
                    color="secondary"
                    onClick={() => handleReject(request.username)}
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
