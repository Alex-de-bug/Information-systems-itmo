import { 
    Dialog, 
    DialogTitle, 
    DialogContent, 
    DialogActions, 
    Button, 
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Checkbox,
    FormControlLabel
} from '@mui/material';
import { useState } from 'react';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';

const EditVehicleDialog = ({ open, onClose, vehicle }) => {
    const dispatch = useDispatch();
    const [formData, setFormData] = useState({
        name: vehicle.name,
        x: vehicle.x,
        y: vehicle.y,
        enginePower: vehicle.enginePower,
        numberOfWheels: vehicle.numberOfWheels,
        capacity: vehicle.capacity,
        distanceTravelled: vehicle.distanceTravelled,
        fuelConsumption: vehicle.fuelConsumption,
        type: vehicle.type,
        fuelType: vehicle.fuelType,
        namesOfOwners: vehicle.namesUsers, 
        permissionToEdit: vehicle.permissionToEdit
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleListChange = (e) => {
        setFormData({
            ...formData,
            namesOfOwners: e.target.value.split(' ')
        });
      };

    const handlePermissionChange = (e) => {
        setFormData(prev => ({
            ...prev,
            permissionToEdit: e.target.checked
        }));
    };

    const handleSubmit = async () => {
        try {
            await axios.patch(
                `http://${process.env.REACT_APP_SERVER}/api/user/vehicles/${vehicle.id}`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`
                    }
                }
            );
    
            dispatch(setNotification({
                color: 'success',
                message: 'Машина успешно обновлена'
            }));
            onClose();
        } catch (error) {
            console.log(error);
            dispatch(setNotification({
                color: 'error',
                message: error.response?.data?.message || 'Произошла ошибка при обновлении'
            }));
        }
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Редактировать машину</DialogTitle>
            <DialogContent>
                <TextField
                    fullWidth
                    margin="dense"
                    name="name"
                    label="Название"
                    value={formData.name}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="x"
                    label="Координата X"
                    type="number"
                    value={formData.x}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="y"
                    label="Координата Y"
                    type="number"
                    value={formData.y}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="enginePower"
                    label="Мощность двигателя"
                    type="number"
                    value={formData.enginePower}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="numberOfWheels"
                    label="Количество колёс"
                    type="number"
                    value={formData.numberOfWheels}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="capacity"
                    label="Вместимость"
                    type="number"
                    value={formData.capacity}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="distanceTravelled"
                    label="Пройденное расстояние"
                    type="number"
                    value={formData.distanceTravelled}
                    onChange={handleChange}
                />
                <TextField
                    fullWidth
                    margin="dense"
                    name="fuelConsumption"
                    label="Расход топлива"
                    type="number"
                    value={formData.fuelConsumption}
                    onChange={handleChange}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Тип транспорта</InputLabel>
                    <Select
                        name="type"
                        value={formData.type}
                        onChange={handleChange}
                        label="Тип транспорта"
                    >
                        <MenuItem value="PLANE">Самолёт</MenuItem>
                        <MenuItem value="BOAT">Лодка</MenuItem>
                        <MenuItem value="BICYCLE">Велосипед</MenuItem>
                    </Select>
                </FormControl>
                <FormControl fullWidth margin="dense">
                    <InputLabel>Тип топлива</InputLabel>
                    <Select
                        name="fuelType"
                        value={formData.fuelType}
                        onChange={handleChange}
                        label="Тип топлива"
                    >
                        <MenuItem value="KEROSENE">Керосин</MenuItem>
                        <MenuItem value="ELECTRICITY">Электричество</MenuItem>
                        <MenuItem value="DIESEL">Дизель</MenuItem>
                        <MenuItem value="MANPOWER">Человек</MenuItem>
                        <MenuItem value="PLASMA">Плазма</MenuItem>
                    </Select>
                </FormControl>
                <TextField
                    fullWidth
                    margin="dense"
                    name="namesUsers"
                    label="Создатели (через пробел)"
                    value={formData.namesOfOwners.join(' ')}
                    onChange={handleListChange}
                    helperText="Введите имена пользователей через пробел"
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={formData.permissionToEdit}
                            onChange={handlePermissionChange}
                            name="permissionToEdit"
                        />
                    }
                    label="Разрешить редактирование"
                    sx={{ mt: 1 }}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} color="primary">
                    Отмена
                </Button>
                <Button onClick={handleSubmit} color="primary">
                    Сохранить
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default EditVehicleDialog;