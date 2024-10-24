import { TableRow, TableCell, IconButton, Box, MenuItem, FormControl, DialogActions, Button, Select } from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useState } from 'react';
import axios from 'axios';
import InfoIcon from '@mui/icons-material/Info';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';
import { useEffect } from 'react'; 



const VehicleTableRow = ({ vehicle, currentUser, userRoles, vehicles }) => {
  const [openInfo, setOpenInfo] = useState(false);
  const dispatch = useDispatch();
  const [openReassignDialog, setOpenReassignDialog] = useState(false);
 const [selectedVehicleId, setSelectedVehicleId] = useState('');
const [deletedCoordinates, setDeletedCoordinates] = useState(null);

useEffect(() => {
  console.log("Состояние openReassignDialog:", openReassignDialog);
}, [openReassignDialog]);

  const canModify = 
    (vehicle.permissionToEdit && (userRoles.includes('ADMIN'))) || vehicle.namesUsers.includes(currentUser);

  const handleEdit = async () => {
    try {
      console.log('Editing vehicle:', vehicle.id);
    } catch (error) {
      console.error('Error editing vehicle:', error);
    }
  };

  const handleDelete = async () => {
    try {
        var coordinatesX = vehicle.x;
        var coordinatesY = vehicle.y;
        setDeletedCoordinates({ x: coordinatesX, y: coordinatesY});
        setOpenReassignDialog(true);

        await new Promise(resolve => {
            const checkDialog = setInterval(() => {
                if (!openReassignDialog) {
                    clearInterval(checkDialog);
                    resolve();
                }
            }, 1000000);
        });
        
        await axios.delete(`http://localhost:8080/user/vehicles/${vehicle.id}`, {
            headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        });
        
        dispatch(setNotification({
            color: 'success',
            message: 'Вы удалили машину'
        }));
    } catch (error) {
        dispatch(setNotification({
            color: 'error',
            message: error.response.data.message
          }));
    }
  };

  const handleReassignCoordinates = async () => {
        try {
            await axios.post('http://localhost:8080/user/vehicles/reassign-coordinates', {
                vehicleId: selectedVehicleId,
                coordinates: deletedCoordinates
            }, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });

            dispatch(setNotification({
                color: 'success',
                message: 'Координаты успешно перевязаны'
            }));
        } catch (error) {
            dispatch(setNotification({
                color: 'error',
                message: error.response?.data?.message || 'Ошибка при перевязке координат'
            }));
        } finally {
            setOpenReassignDialog(false);
        }
    };

    const handleSkipReassign = async () => {
        try {
            await axios.post('http://localhost:8080/user/vehicles/reassign-coordinates', {
                vehicleId: null,
                coordinates: deletedCoordinates
            }, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }
            });
        } finally {
            // setOpenReassignDialog(false);
        }
    };

  return (
    <>
        <TableRow hover>
            <TableCell>{vehicle.name}</TableCell>
            <TableCell>{vehicle.type}</TableCell>
            <TableCell>{vehicle.fuelType}</TableCell>
            <TableCell>
            <Box>
                <IconButton 
                size="small" 
                onClick={() => setOpenInfo(true)}
                color="info"
                >
                <InfoIcon />
                </IconButton>
                {canModify && (
                <>
                    <IconButton 
                    size="small" 
                    onClick={handleEdit}
                    color="primary"
                    >
                    <EditIcon />
                    </IconButton>
                    <IconButton 
                    size="small" 
                    onClick={handleDelete}
                    color="error"
                    >
                    <DeleteIcon />
                    </IconButton>
                </>
                )}
            </Box>
            </TableCell>
        </TableRow>

        <Dialog open={openInfo} onClose={() => setOpenInfo(false)}>
            <DialogTitle>Информация о машине</DialogTitle>
            <DialogContent>
            <DialogContentText>
                <strong>ID:</strong> {vehicle.id}<br />
                <strong>Name:</strong> {vehicle.name}<br />
                <strong>Coordinates:</strong> X: {vehicle.x}, Y: {vehicle.y}<br />
                <strong>Type:</strong> {vehicle.type}<br />
                <strong>Engine Power:</strong> {vehicle.enginePower}<br />
                <strong>Number of Wheels:</strong> {vehicle.numberOfWheels}<br />
                <strong>Capacity:</strong> {vehicle.capacity}<br />
                <strong>Distance Travelled:</strong> {vehicle.distanceTravelled}<br />
                <strong>Fuel Consumption:</strong> {vehicle.fuelConsumption}<br />
                <strong>Fuel Type:</strong> {vehicle.fuelType}<br />
                <strong>Users:</strong> {vehicle.namesUsers.join(', ')}<br />
                <strong>Permission to Edit:</strong> {vehicle.permissionToEdit ? 'Yes' : 'No'}
            </DialogContentText>
            </DialogContent>
        </Dialog>

        <Dialog open={openReassignDialog} onClose={() => setOpenReassignDialog(false)}>
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
                                .filter(v => v.id !== vehicle.id && ((v.permissionToEdit && (userRoles.includes('ADMIN'))) || v.namesUsers.includes(currentUser)))
                                .map(v => (
                                    <MenuItem key={v.id} value={v.id}>
                                        {v.name}
                                    </MenuItem>
                                ))}
                    </Select>
                </FormControl>
            </DialogContent>
            <DialogActions>
                <Button onClick={handleSkipReassign} color="primary">
                    Пропустить
                </Button>
                <Button 
                    onClick={handleReassignCoordinates} 
                    color="primary"
                    disabled={!selectedVehicleId}
                >
                    Перевязать
                </Button>
            </DialogActions>
        </Dialog>
    </>
  );
};

export default VehicleTableRow;