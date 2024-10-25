import React, { useState } from 'react';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';
import { Typography, TextField, Select, MenuItem, FormControl, InputLabel, Checkbox, Button, Box, FormControlLabel, Container } from '@mui/material';



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
      dispatch(setNotification({
        color: 'error',
        message: error.response.data.message ? error.response.data.message : 'Ошибка при отправке данных'
      }));
      console.error('Error sending data', error);
    }
  };

  return (
    <Container 
        sx={{ 
          backgroundColor: 'rgba(0, 0, 0, 0.87)', 
          padding: 1, 
          borderRadius: 2, 
          color: 'white',
          '& .MuiInputLabel-root': { 
            color: 'white',
          },
          '& .MuiOutlinedInput-root': { 
            color: 'white',
            '& fieldset': {
              borderColor: 'rgba(255, 255, 255, 0.23)',
            },
            '&:hover fieldset': {
              borderColor: 'white',
            },
          },
          '& .MuiSelect-icon': { 
            color: 'white',
          },
          '& .MuiFormControlLabel-label': { 
            color: 'white',
          }
        }}
      >
        <Typography variant="h4">
          Новая тачила
        </Typography>
        <Box component="form" onSubmit={handleSubmit} sx={{ '& .MuiTextField-root': { m: 1, width: '30%' }, mt: 3 }}>
          <TextField
            label="Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            helperText="type: string"
            fullWidth
          />
          <TextField
            label="X Coordinate"
            name="x"
            type="number"
            value={formData.x}
            onChange={handleChange}
            fullWidth
            helperText="type: long"
          />
          <TextField
            label="Y Coordinate"
            name="y"
            type="number"
            value={formData.y}
            onChange={handleChange}
            fullWidth
            helperText="type: double"
          />
          <FormControl sx={{ m: 1 }}>
            <InputLabel>Type</InputLabel>
            <Select
              name="type"
              value={formData.type}
              onChange={handleChange}
              label="Type"
            >
              <MenuItem value="PLANE">PLANE</MenuItem>
              <MenuItem value="BOAT">BOAT</MenuItem>
              <MenuItem value="BICYCLE">BICYCLE</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="Engine Power"
            name="enginePower"
            type="number"
            value={formData.enginePower}
            onChange={handleChange}
            fullWidth
            helperText="type: double"
          />
          <TextField
            label="Number of Wheels"
            name="numberOfWheels"
            type="number"
            value={formData.numberOfWheels}
            onChange={handleChange}
            fullWidth
            helperText="type: long"
          />
          <TextField
            label="Capacity"
            name="capacity"
            type="number"
            value={formData.capacity}
            onChange={handleChange}
            fullWidth
            helperText="type: long"
          />
          <TextField
            label="Distance Travelled"
            name="distanceTravelled"
            type="number"
            value={formData.distanceTravelled}
            onChange={handleChange}
            fullWidth
            helperText="type: double"
          />
          <TextField
            label="Fuel Consumption"
            name="fuelConsumption"
            type="number"
            value={formData.fuelConsumption}
            onChange={handleChange}
            fullWidth
            helperText="type: float"
          />
          <FormControl sx={{ m: 1 }}>
            <InputLabel>Fuel Type</InputLabel>
            <Select
              name="fuelType"
              value={formData.fuelType}
              onChange={handleChange}
              label="Fuel Type"
            >
              <MenuItem value="KEROSENE">KEROSENE</MenuItem>
              <MenuItem value="ELECTRICITY">ELECTRICITY</MenuItem>
              <MenuItem value="DIESEL">DIESEL</MenuItem>
              <MenuItem value="MANPOWER">MANPOWER</MenuItem>
              <MenuItem value="PLASMA">PLASMA</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="Names of Owners"
            name="namesOfOwners"
            value={formData.namesOfOwners.join(', ')}
            onChange={handleListChange}
            helperText="exampte: alex, tom"
            fullWidth
          />
          <FormControlLabel
            control={
              <Checkbox
                name="permissionToEdit"
                checked={formData.permissionToEdit}
                onChange={handleCheckboxChange}
              />
            }
            label="Разрешение на редактирование"
            sx={{ m: 1.3 }}
          />
          <Box sx={{ mt: 2, mb: 1 }}>
            <Button 
              type="submit" 
              variant="contained" 
              color="primary"
              fullWidth
            >
              Создать 
            </Button>
          </Box>
        </Box>
  </Container>
  );
};

export default NewVehicleForm;
