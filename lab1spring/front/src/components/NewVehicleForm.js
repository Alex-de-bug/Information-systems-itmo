import React, { useState } from 'react';
import axios from 'axios';
import { useDispatch } from 'react-redux';

const NewVehicleForm = () => {
    const dispatch = useDispatch();
    const [formData, setFormData] = useState({
        name: '',
        x: '',
        y: '',
        type: 'PLANE',
        enginePower: '',
        numberOfWheels: '',
        capacity: '',
        distanceTravelled: '',
        fuelConsumption: '',
        fuelType: 'KEROSENE',
        namesOfOwners: [],
        permissionToEdit: false
    });

    const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
        ...formData,
        [name]: value
    });
    };

    const handleListChange = (e) => {
      setFormData({
          ...formData,
          namesOfOwners: e.target.value.split(',')
      });
    };

    const handleCheckboxChange = (e) => {
    setFormData({
        ...formData,
        permissionToEdit: e.target.checked
    });
    };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post('http://localhost:8080/user/vehicles', formData, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`, 
            },
        });
      console.log('Response:', response.data);
    } catch (error) {
      console.error('Error sending data', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Name:</label>
        <input type="text" name="name" value={formData.name} onChange={handleChange} required />
      </div>
      <div>
        <label>X Coordinate (Long):</label>
        <input type="number" name="x" value={formData.x} onChange={handleChange} required />
      </div>
      <div>
        <label>Y Coordinate (Double):</label>
        <input type="number" step="any" name="y" value={formData.y} onChange={handleChange} required />
      </div>
      <div>
        <label>Type:</label>
        <select name="type" value={formData.type} onChange={handleChange} required>
          <option value="PLANE">PLANE</option>
          <option value="BOAT">BOAT</option>
          <option value="BICYCLE">BICYCLE</option>
        </select>
      </div>
      <div>
        <label>Engine Power (Double):</label>
        <input type="number" step="any" name="enginePower" value={formData.enginePower} onChange={handleChange} required />
      </div>
      <div>
        <label>Number of Wheels (Long):</label>
        <input type="number" name="numberOfWheels" value={formData.numberOfWheels} onChange={handleChange} required />
      </div>
      <div>
        <label>Capacity (Long):</label>
        <input type="number" name="capacity" value={formData.capacity} onChange={handleChange} required />
      </div>
      <div>
        <label>Distance Travelled (Double):</label>
        <input type="number" step="any" name="distanceTravelled" value={formData.distanceTravelled} onChange={handleChange} required />
      </div>
      <div>
        <label>Fuel Consumption (Float):</label>
        <input type="number" step="any" name="fuelConsumption" value={formData.fuelConsumption} onChange={handleChange} required />
      </div>
      <div>
        <label>Fuel Type:</label>
        <select name="fuelType" value={formData.fuelType} onChange={handleChange} required>
          <option value="KEROSENE">KEROSENE</option>
          <option value="ELECTRICITY">ELECTRICITY</option>
          <option value="DIESEL">DIESEL</option>
          <option value="MANPOWER">MANPOWER</option>
          <option value="PLASMA">PLASMA</option>
        </select>
      </div>
      <div>
        <label>Names of Owners (comma-separated):</label>
        <input type="text" name="namesOfOwners" value={formData.namesOfOwners.join(',')} onChange={handleListChange} />
      </div>
      <div>
        <label>Permission to Edit:</label>
        <input type="checkbox" name="permissionToEdit" checked={formData.permissionToEdit} onChange={handleCheckboxChange} />
      </div>
      <button type="submit">Submit</button>
    </form>
  );
};

export default NewVehicleForm;
