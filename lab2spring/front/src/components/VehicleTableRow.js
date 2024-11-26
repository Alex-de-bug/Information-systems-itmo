import { TableRow, TableCell, IconButton, Box} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import { useState } from 'react';
import InfoIcon from '@mui/icons-material/Info';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';

import DeleteVehicleDialog from './DeleteVehicleDialog';
import EditVehicleDialog from './EditVehicleDialog';





const VehicleTableRow = ({ vehicle, currentUser, userRoles, vehicles }) => {
  const [openInfo, setOpenInfo] = useState(false);
  const [openReassignDialog, setOpenReassignDialog] = useState(false);
  const [openEditDialog, setOpenEditDialog] = useState(false);

  const canModify = 
    (vehicle.permissionToEdit && (userRoles.includes('ADMIN'))) || vehicle.namesUsers.includes(currentUser);



  return (
    <>
        <TableRow hover>
            <TableCell>{vehicle.id}</TableCell>
            <TableCell>{vehicle.name}</TableCell>
            <TableCell>{vehicle.type === 'PLANE' ? 'Самолёт' : vehicle.type === 'BOAT' ? 'Лодка' : vehicle.type === 'BICYCLE' ? 'Велосипед' : 'Неизвестно'}</TableCell>
            <TableCell>{vehicle.fuelType === 'MANPOWER' ? 'Человек' : vehicle.fuelType === 'DIESEL' ? 'Дизель' : vehicle.fuelType === 'KEROSENE' ? 'Керосин' : vehicle.fuelType === 'ELECTRICITY' ? 'Электричество' : vehicle.fuelType === 'PLASMA' ? 'Плазма' : 'Неизвестно'}</TableCell>
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
                        onClick={() => setOpenEditDialog(true)}
                        color="primary"
                    >
                        <EditIcon />
                    </IconButton>
                    <IconButton 
                        size="small" 
                        onClick={() => setOpenReassignDialog(true)}
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
                <strong>Название:</strong> {vehicle.name}<br />
                <strong>Координаты:</strong> X: {vehicle.x}, Y: {vehicle.y}<br />
                <strong>Тип:</strong> {vehicle.type === 'PLANE' ? 'Самолёт' : vehicle.type === 'BOAT' ? 'Лодка' : vehicle.type === 'BICYCLE' ? 'Велосипед' : 'Неизвестно'}<br />
                <strong>Мощность двигателя:</strong> {vehicle.enginePower}<br />
                <strong>Количество колёс:</strong> {vehicle.numberOfWheels}<br />
                <strong>Вместимость:</strong> {vehicle.capacity}<br />
                <strong>Пройденный путь:</strong> {vehicle.distanceTravelled}<br />
                <strong>Расход топлива:</strong> {vehicle.fuelConsumption}<br />
                <strong>Тип топлива:</strong> {vehicle.fuelType === 'MANPOWER' ? 'Человек' : vehicle.fuelType === 'DIESEL' ? 'Дизель' : vehicle.fuelType === 'KEROSENE' ? 'Керосин' : vehicle.fuelType === 'ELECTRICITY' ? 'Электричество' : vehicle.fuelType === 'PLASMA' ? 'Плазма' : 'Неизвестно'}<br />
                <strong>Пользователи:</strong> {vehicle.namesUsers.join(', ')}<br />
                <strong>Разрешение на редактирование:</strong> {vehicle.permissionToEdit ? 'Да' : 'Нет'}
            </DialogContentText>
            </DialogContent>
        </Dialog>

        <DeleteVehicleDialog 
                open={openReassignDialog}
                onClose={() => setOpenReassignDialog(false)}
                vehicle={vehicle}
                vehicles={vehicles}
                currentUser={currentUser}
                userRoles={userRoles}
        />
        <EditVehicleDialog 
                open={openEditDialog}
                onClose={() => setOpenEditDialog(false)}
                vehicle={vehicle}
        />
    </>
  );
};

export default VehicleTableRow;
