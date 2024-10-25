import { Dialog, DialogTitle, DialogContent, FormControl, Select, MenuItem, DialogActions, Button } from '@mui/material';
import { useState } from 'react';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';

const DeleteVehicleDialog = ({ 
    open, 
    onClose, 
    vehicle, 
    vehicles, 
    currentUser, 
    userRoles 
}) => {
    const [selectedVehicleId, setSelectedVehicleId] = useState('');
    const dispatch = useDispatch();

    const handleDeleteRequest = async () => {
        try {        
            await axios.delete(`http://localhost:8080/user/vehicles/${vehicle.id}`, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`,
                    'Reassign-Vehicle-Id': selectedVehicleId
                }
            });
            
            dispatch(setNotification({
                color: 'success',
                message: 'Вы удалили машину'
            }));
            onClose();
        } catch (error) {
            dispatch(setNotification({
                color: 'error',
                message: error.response.data.message
            }));
        }
    };

    const handleSkipReassign = async () => {
        setSelectedVehicleId('');
        handleDeleteRequest();
    };

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Перевязать координаты?</DialogTitle>
            <DialogContent>
                <FormControl fullWidth sx={{ mt: 2 }}>
                    <Select
                        value={selectedVehicleId}
                        onChange={(e) => setSelectedVehicleId(e.target.value)}
                        displayEmpty
                    >
                        <MenuItem value="">
                            <em>Выберите машину</em>
                        </MenuItem>
                        {vehicles
                            .filter(v => v.id !== vehicle.id && 
                                ((v.permissionToEdit && userRoles.includes('ADMIN')) || 
                                v.namesUsers.includes(currentUser)))
                            .map(v => (
                                <MenuItem key={v.id} value={v.id}>
                                    {v.name}
                                </MenuItem>
                            ))}
                    </Select>
                </FormControl>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSkipReassign} color="primary" disabled={selectedVehicleId}>
                    Пропустить
                </Button>
                <Button 
                    onClick={handleDeleteRequest}
                    color="primary"
                    disabled={!selectedVehicleId}
                >
                    Перевязать
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default DeleteVehicleDialog;