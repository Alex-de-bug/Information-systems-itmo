import { Button, Select, MenuItem, FormControl, InputLabel, Box } from '@mui/material';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';
import { useState } from 'react';

const DeleteByFuelTypeButton = ({ vehicles, currentUser, userRoles }) => {
    const dispatch = useDispatch();
    const [selectedFuelType, setSelectedFuelType] = useState('');

    const availableFuelTypes = [...new Set(vehicles.map(v => v.fuelType))];

    const handleMassDelete = async () => {
        if (!selectedFuelType) {
            dispatch(setNotification({
                color: 'warning',
                message: 'Выберите тип топлива'
            }));
            return;
        }

        const vehiclesToDelete = vehicles.filter(vehicle => 
            vehicle.fuelType === selectedFuelType && 
            ((vehicle.permissionToEdit && userRoles.includes('ADMIN')) || 
             vehicle.namesUsers.includes(currentUser))
        );

        if (vehiclesToDelete.length === 0) {
            dispatch(setNotification({
                color: 'warning',
                message: 'Нет доступных машин для удаления с указанным типом топлива'
            }));
            return;
        }

        try {
            for (const vehicle of vehiclesToDelete) {
                await axios.delete(`http://localhost:8081/api/user/vehicles/${vehicle.id}`, {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`,
                        'Reassign-Vehicle-Id': ''
                    }
                });
            }
            
            dispatch(setNotification({
                color: 'success',
                message: `Удалено ${vehiclesToDelete.length} машин с типом топлива ${selectedFuelType}`
            }));
            setSelectedFuelType('');
        } catch (error) {
            dispatch(setNotification({
                color: 'error',
                message: error.response?.data?.message || 'Произошла ошибка при удалении'
            }));
        }
    };

    return (
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', my: 2 }}>
            <FormControl sx={{ minWidth: 200 }}>
                <InputLabel>Тип топлива</InputLabel>
                <Select
                    value={selectedFuelType}
                    label="Тип топлива"
                    onChange={(e) => setSelectedFuelType(e.target.value)}
                >
                    {availableFuelTypes.map(fuelType => (
                        <MenuItem key={fuelType} value={fuelType}>
                            {fuelType === 'MANPOWER' ? 'Человек' : fuelType === 'DIESEL' ? 'Дизель' : fuelType === 'KEROSENE' ? 'Керосин' : fuelType === 'ELECTRICITY' ? 'Электричество' : fuelType === 'PLASMA' ? 'Плазма' : 'Неизвестно'}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
            <Button 
                variant="contained" 
                color="error" 
                onClick={handleMassDelete}
                disabled={!selectedFuelType}
            >
                Удалить все машины с выбранным топливом
            </Button>
        </Box>
    );
};

export default DeleteByFuelTypeButton;