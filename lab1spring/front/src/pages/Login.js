import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { loginUser } from '../redux/actions/userActions';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';


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
        const response = await axios.post('http://localhost:8080/auth', userData);
        console.log('Успешный ответ:', response.data);
        

        localStorage.setItem('token', response.data.token);
        localStorage.setItem('roles', JSON.stringify(response.data.roles));
        localStorage.setItem('name', response.data.name);

        dispatch(loginUser(response.data.name, response.data.roles, response.data.token));

        navigate('/home');
    } catch (error) {
        console.error('Ошибка при отправке данных:', error);
    }
    
  };

  return (
    <form onSubmit={handleLogin}>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button type="submit">Login</button>
    </form>
  );
};

export default Login;
