import React, { useState } from 'react';
import axios from 'axios';
import NavBar from '../components/NavBar';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleRegister = async (e) => {
    e.preventDefault();

    const userData = {
      username: username,
      password: password,
      confirmPassword: confirmPassword,
    };
    
    try {
      const response = await axios.post('http://localhost:8080/reg', userData);
      console.log('Успешный ответ:', response.data);
    } catch (error) {
      console.error('Ошибка при отправке данных:', error);
    }
  };

  return (

    <div>
      <NavBar />
      <main>
        <form onSubmit={handleRegister}>
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
          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          <button type="submit">Register</button>
        </form>
      </main>
    </div>
    
  );
};

export default Register;
