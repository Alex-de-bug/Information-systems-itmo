import React from 'react';
import { useNavigate } from 'react-router-dom'; 
import { useDispatch } from 'react-redux';
import axios from 'axios';
import { logout } from '../redux/slices/userSlice';
import NewVehicleForm from '../components/NewVehicleForm';

const Home = () => {

  // const handleSend = async (e) => {
  //       e.preventDefault();
  //       try {
  //           const response = await axios.get('http://localhost:8080/admin', {
  //               headers: {
  //                   Authorization: `Bearer ${localStorage.getItem("token")}`, 
  //               },
  //             });
  //           console.log('Успешный ответ:', response.data);
  //       } catch (error) {
  //           console.error('Ошибка при отправке данных:', error);
  //       }
  //   };

  return (
    <div>
      <div>
        <h1>Create a New Vehicle</h1>
        <NewVehicleForm />
      </div>
    </div>
  );
};

export default Home;
