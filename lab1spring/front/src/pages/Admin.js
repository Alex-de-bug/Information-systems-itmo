import axios from 'axios';
import React from 'react';
import { useNavigate } from 'react-router-dom'; 


const Admin = () => {
    const navigate = useNavigate(); 


    const handleHomePage = () => {
        navigate('/home'); 
        };

    const handleSend = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.get('http://localhost:8080/admin');
            console.log('Успешный ответ:', response.data);
        } catch (error) {
            console.error('Ошибка при отправке данных:', error);
        }
    };

  return (
    <div>
      <h1>Страница администратора</h1>
      <button onClick={handleHomePage}>Домой</button>
      <button onClick={handleSend}>пробовать</button>
    </div>
  );
};

export default Admin;
