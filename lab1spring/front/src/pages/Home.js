import React from 'react';
import { useNavigate } from 'react-router-dom'; 
import { useDispatch } from 'react-redux';
import { logoutUser } from '../redux/actions/userActions';
import axios from 'axios';

const Home = () => {
  const name = localStorage.getItem('name');
  const roles = JSON.parse(localStorage.getItem('roles')) || []; 
  const navigate = useNavigate(); 
  const dispatch = useDispatch();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    localStorage.removeItem('name'); 
    dispatch(logoutUser()); 
    navigate('/login');
  };


  const handleAdminPage = () => {
    navigate('/admin'); 
  };

  const handleSend = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.get('http://localhost:8080/secured', {
                headers: {
                    authorization: `Bearer ${localStorage.getItem("token")}`, 
                },
              });
            console.log('Успешный ответ:', response.data);
        } catch (error) {
            console.error('Ошибка при отправке данных:', error);
        }
    };

  return (
    <div>
      <h1>Добро пожаловать, {name}!</h1>

      {roles.includes('ROLE_ADMIN') && (
        <button onClick={handleAdminPage}>Перейти на страницу администратора</button>
      )}
      <button onClick={handleSend}>пробовать</button>

        <button onClick={handleLogout}>Выйти</button>
    </div>
  );
};

export default Home;
