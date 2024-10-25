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




const VehicleTableRow = ({ vehicle, currentUser, userRoles, vehicles }) => {
  const [openInfo, setOpenInfo] = useState(false);
  const [openReassignDialog, setOpenReassignDialog] = useState(false);

  const canModify = 
    (vehicle.permissionToEdit && (userRoles.includes('ADMIN'))) || vehicle.namesUsers.includes(currentUser);

//Редактирование машины --------------------------------------------------------
  const handleEdit = async () => {
    try {
      console.log('Editing vehicle:', vehicle.id);
    } catch (error) {
      console.error('Error editing vehicle:', error);
    }
  };
//Редактирование машины --------------------------------------------------------


  return (
    <>
        <TableRow hover>
            <TableCell>{vehicle.id}</TableCell>
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

        <DeleteVehicleDialog 
                open={openReassignDialog}
                onClose={() => setOpenReassignDialog(false)}
                vehicle={vehicle}
                vehicles={vehicles}
                currentUser={currentUser}
                userRoles={userRoles}
            />
    </>
  );
};

export default VehicleTableRow;