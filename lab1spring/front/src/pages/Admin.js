import axios from 'axios';
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AdminPanel from '../components/AdminPanel';
import { loginSuccess, setNotification } from '../redux/slices/userSlice';
import { useDispatch } from 'react-redux';


const Admin = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const admin = (JSON.parse(localStorage.getItem('roles')) || []).includes('ROLE_ADMIN');
  const [loading, setLoading] = useState(false);

  const handleRequestAdminRights = async () => {
    setLoading(true);
    try {

      const response = await axios.post('http://localhost:8080/user/rights', {username: localStorage.getItem('name')}, {
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
      {admin ? (
        <AdminPanel/>
      ) : (
        <div>
          <p>У вас нет прав администратора.</p>
          <button onClick={handleRequestAdminRights} disabled={loading}>
            {loading ? 'Отправка запроса...' : 'Запросить права администратора'}
          </button>
        </div>
      )}
    </div>
  );
};

export default Admin;
