import { useState, useEffect } from 'react';
import { 
    Paper, 
    Box, 
    FormControl, 
    InputLabel, 
    Select, 
    MenuItem, 
    TextField, 
    Button,
} from '@mui/material';
import TableComponent from './TableComponent';
import DeleteByFuelTypeButton from './DeleteByFuelTypeButton';

const VehicleFilter = ({ vehicles }) => {
    const [filterType, setFilterType] = useState('');
    const [filterParams, setFilterParams] = useState({
        namePrefix: '',
        vehicleType: '',
        wheelsMin: '',
        wheelsMax: ''
    });
    const [filteredVehicles, setFilteredVehicles] = useState([]);

    useEffect(() => {
        if (filterType) {
            applyFilter();
        }
    }, [vehicles]);

    const handleFilterTypeChange = (event) => {
        setFilterType(event.target.value);
        setFilteredVehicles([]);
    };

    const handleParamChange = (param, value) => {
        setFilterParams(prev => ({
            ...prev,
            [param]: value
        }));
    };

    const applyFilter = () => {
        if (!vehicles || vehicles.length === 0) {
            setFilteredVehicles([]);
            return;
        }

        let result = [];
        
        switch (filterType) {
            case 'minEnginePower':
                result = [vehicles.reduce((min, current) => 
                    (current.enginePower < min.enginePower) ? current : min
                , vehicles[0])];
                break;

            case 'namePrefix':
                result = vehicles.filter(vehicle => 
                    vehicle.name.toLowerCase().startsWith(filterParams.namePrefix.toLowerCase())
                );
                break;

            case 'vehicleType':
                result = vehicles.filter(vehicle => 
                    vehicle.type === filterParams.vehicleType
                );
                break;

            case 'wheelsRange':
                const min = Number(filterParams.wheelsMin);
                const max = Number(filterParams.wheelsMax);
                result = vehicles.filter(vehicle => 
                    vehicle.numberOfWheels >= min && vehicle.numberOfWheels <= max
                );
                break;

            default:
                result = [];
        }

        setFilteredVehicles(result);
    };

    return (
        <Paper sx={{ p: 2, mb: 2, borderRadius: 2, backgroundColor: 'rgba(0, 0, 0, 0.87)', }}>
            <Box sx={{ display: 'flex', gap: 2, mb: 2, alignItems: 'flex-end' }}>
                <FormControl sx={{ minWidth: 200 }}>
                    <InputLabel>Тип фильтра</InputLabel>
                    <Select
                        value={filterType}
                        onChange={handleFilterTypeChange}
                        label="Тип фильтра"
                    >
                        <MenuItem value="minEnginePower">Минимальная мощность двигателя</MenuItem>
                        <MenuItem value="namePrefix">Поиск по началу имени</MenuItem>
                        <MenuItem value="vehicleType">Поиск по типу</MenuItem>
                        <MenuItem value="wheelsRange">Поиск по количеству колёс</MenuItem>
                    </Select>
                </FormControl>

                {filterType === 'namePrefix' && (
                    <TextField
                        label="Начало имени"
                        value={filterParams.namePrefix}
                        onChange={(e) => handleParamChange('namePrefix', e.target.value)}
                    />
                )}

                {filterType === 'vehicleType' && (
                    <FormControl sx={{ minWidth: 200 }}>
                        <InputLabel>Тип транспорта</InputLabel>
                        <Select
                            value={filterParams.vehicleType}
                            onChange={(e) => handleParamChange('vehicleType', e.target.value)}
                            label="Тип транспорта"
                        >
                            <MenuItem value="BICYCLE">Велосипед</MenuItem>
                            <MenuItem value="PLANE">Самолет</MenuItem>
                            <MenuItem value="BOAT">Лодка</MenuItem>
                        </Select>
                    </FormControl>
                )}

                {filterType === 'wheelsRange' && (
                    <>
                        <TextField
                            label="Минимум колёс"
                            type="number"
                            value={filterParams.wheelsMin}
                            onChange={(e) => handleParamChange('wheelsMin', e.target.value)}
                        />
                        <TextField
                            label="Максимум колёс"
                            type="number"
                            value={filterParams.wheelsMax}
                            onChange={(e) => handleParamChange('wheelsMax', e.target.value)}
                        />
                    </>
                )}

                {filterType && (
                    <Button 
                        variant="contained" 
                        onClick={applyFilter}
                        sx={{ height: 40 }}
                    >
                        Применить
                    </Button>
                )}
            </Box>

            {filteredVehicles.length > 0 && (
                <TableComponent vehicles={filteredVehicles} />
            )}
            <DeleteByFuelTypeButton 
                vehicles={vehicles}
                currentUser={localStorage.getItem('name')}
                userRoles={localStorage.getItem('roles')}
            />
        </Paper>
    );
};

export default VehicleFilter;