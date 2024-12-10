import { Box, Paper, Button, ButtonGroup, TextField } from '@mui/material'; 
import { useState, useEffect } from 'react';
import AirplanemodeActiveIcon from '@mui/icons-material/AirplanemodeActive';
import DirectionsBikeIcon from '@mui/icons-material/DirectionsBike';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import AddIcon from '@mui/icons-material/Add';
import RemoveIcon from '@mui/icons-material/Remove';
import VehicleTableRow from './VehicleTableRow';

const VehicleMap = ({ vehicles }) => {
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const currentUser = localStorage.getItem('name');
    const userRoles = JSON.parse(localStorage.getItem('roles') || '[]');
    const [scale, setScale] = useState(1);
    const [bounds, setBounds] = useState({
        maxX: Math.max(...vehicles.map(v => v.x), 200),
        maxY: Math.max(...vehicles.map(v => v.y), 200),
        minX: Math.min(...vehicles.map(v => v.x), -200),
        minY: Math.min(...vehicles.map(v => v.y), -200)
    });

    useEffect(() => {
        setSelectedVehicle(null);
    }, [vehicles]);

    const fuelTypeColors = {
        KEROSENE: '#FF9800',  
        DIESEL: '#795548',  
        ELECTRICITY: '#2196F3',
        MANPOWER: '#4CAF50', 
        PLASMA: '#9C27B0'  
    };


    const getVehicleIcon = (type, fuelType) => {
        const color = fuelTypeColors[fuelType] || '#757575';

        const iconProps = {
            sx: { 
                color,
                fontSize: '2rem',
                cursor: 'pointer',
                transition: 'transform 0.2s',
                '&:hover': {
                    transform: 'scale(1.2)'
                }
            }
        };

        switch (type.toUpperCase()) {
            case 'PLANE':
                return <AirplanemodeActiveIcon {...iconProps} />;
            case 'BICYCLE':
                return <DirectionsBikeIcon {...iconProps} />;
            case 'BOAT':
                return <DirectionsBoatIcon {...iconProps} />;
            default:
                console.warn(`Unknown vehicle type: ${type}`);
                return <DirectionsBoatIcon {...iconProps} />;
        }
    };

    const handleZoom = (zoomIn) => {
        setScale(prev => {
            const newScale = zoomIn ? prev * 1.2 : prev / 1.2;
            return Math.min(Math.max(newScale, 0.5), 2);
        });
    };

    const handleBoundChange = (axis, isMax, value) => {
        const numValue = Number(value);
        if (!isNaN(numValue)) {
            setBounds(prev => ({
                ...prev,
                [isMax ? `max${axis}` : `min${axis}`]: numValue
            }));
        }
    };

    const scaleCoordinate = (value, min, max, targetMin, targetMax) => {
        return ((value - min) / (max - min)) * (targetMax - targetMin) * scale + targetMin;
    };

    return (
        <Box sx={{backgroundColor: 'rgba(0, 0, 0, 0.6)', display: 'flex', flexDirection: 'column', gap: 2, p: 2, borderRadius: 2, }}>
            <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Box>
                        <div style={{ color: '#ddd', marginBottom: '8px' }}>X ось:</div>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <TextField
                                label="Максимальное X"
                                type="number"
                                size="small"
                                value={bounds.maxX}
                                onChange={(e) => handleBoundChange('X', true, e.target.value)}
                                sx={{
                                    width: '120px',
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': { borderColor: '#ddd' },
                                        '&:hover fieldset': { borderColor: '#fff' },
                                    },
                                    '& .MuiInputLabel-root': { color: '#ddd' },
                                    '& .MuiInputBase-input': { color: '#fff' }
                                }}
                            />
                            <TextField
                                label="Минимальное X"
                                type="number"
                                size="small"
                                value={bounds.minX}
                                onChange={(e) => handleBoundChange('X', false, e.target.value)}
                                sx={{
                                    width: '120px',
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': { borderColor: '#ddd' },
                                        '&:hover fieldset': { borderColor: '#fff' },
                                    },
                                    '& .MuiInputLabel-root': { color: '#ddd' },
                                    '& .MuiInputBase-input': { color: '#fff' }
                                }}
                            />
                        </Box>
                    </Box>

                    <Box>
                        <div style={{ color: '#ddd', marginBottom: '8px' }}>Y ось:</div>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                            <TextField
                                label="Максимальное Y"
                                type="number"
                                size="small"
                                value={bounds.maxY}
                                onChange={(e) => handleBoundChange('Y', true, e.target.value)}
                                sx={{
                                    width: '120px',
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': { borderColor: '#ddd' },
                                        '&:hover fieldset': { borderColor: '#fff' },
                                    },
                                    '& .MuiInputLabel-root': { color: '#ddd' },
                                    '& .MuiInputBase-input': { color: '#fff' }
                                }}
                            />
                            <TextField
                                label="Минимальное Y"
                                type="number"
                                size="small"
                                value={bounds.minY}
                                onChange={(e) => handleBoundChange('Y', false, e.target.value)}
                                sx={{
                                    width: '120px',
                                    '& .MuiOutlinedInput-root': {
                                        '& fieldset': { borderColor: '#ddd' },
                                        '&:hover fieldset': { borderColor: '#fff' },
                                    },
                                    '& .MuiInputLabel-root': { color: '#ddd' },
                                    '& .MuiInputBase-input': { color: '#fff' }
                                }}
                            />
                        </Box>
                    </Box>
                </Box>
                <Box sx={{ml: 2, mt: 2}}>
                    <ButtonGroup sx={{ mt: 2}} size="small">
                        <Button onClick={() => handleZoom(true)}><AddIcon /></Button>
                        <Button onClick={() => handleZoom(false)}><RemoveIcon /></Button>
                    </ButtonGroup>
                    <Box sx={{ mt: 2, color: '#ddd' }}>
                        <div>Скалирование: {scale.toFixed(2)}x</div>
                    </Box>
                </Box>
            </Box>
            <Paper 
                sx={{ 
                    width: '100%', 
                    height: '500px', 
                    position: 'relative',
                    backgroundColor: 'rgba(0, 0, 0, 0.9)',
                    overflow: 'hidden',
                    border: '1px solid #ddd'
                }}
            >
                <Box 
                    sx={{ 
                        position: 'absolute',
                        top: 10,
                        right: 10,
                        backgroundColor: 'rgba(0, 0, 0, 0.9)',
                        padding: 1,
                        borderRadius: 1,
                        border: '1px solid #ddd'
                    }}
                >
                    {Object.entries(fuelTypeColors).map(([fuelType, color]) => (
                        <Box 
                            key={fuelType} 
                            sx={{ 
                                display: 'flex', 
                                alignItems: 'center', 
                                gap: 1,
                                mb: 0.5
                            }}
                        >
                            <Box 
                                sx={{ 
                                    width: 16, 
                                    height: 16, 
                                    backgroundColor: color,
                                    borderRadius: '50%'
                                }} 
                            />
                            <span>{fuelType === 'MANPOWER' ? 'Человек' : fuelType === 'DIESEL' ? 'Дизель' : fuelType === 'KEROSENE' ? 'Керосин' : fuelType === 'ELECTRICITY' ? 'Электричество' : fuelType === 'PLASMA' ? 'Плазма' : 'Неизвестно'}</span>
                        </Box>
                    ))}
                </Box>

                <Box sx={{
                    position: 'absolute',
                    left: '50%',
                    bottom: 0,
                    width: '1px',
                    height: '100%',
                    backgroundColor: '#ddd'
                }} />
                <Box sx={{
                    position: 'absolute',
                    bottom: '50%',
                    left: 0,
                    width: '100%',
                    height: '1px',
                    backgroundColor: '#ddd'
                }} />

                {vehicles.map((vehicle) => {
                    const x = scaleCoordinate(vehicle.x, bounds.minX, bounds.maxX, 50, 950);
                    const y = scaleCoordinate(vehicle.y, bounds.minY, bounds.maxY, 50, 450);

                    return (
                        <Box
                            key={vehicle.id}
                            sx={{
                                position: 'absolute',
                                left: `${x}px`,
                                bottom: `${y}px`,
                                transform: 'translate(-50%, 50%)',
                                zIndex: 1
                            }}
                            onClick={() => setSelectedVehicle(vehicle)}
                            title={`Type: ${vehicle.type}, Fuel: ${vehicle.fuelType}, X: ${vehicle.x}, Y: ${vehicle.y}`}
                        >
                            {getVehicleIcon(vehicle.type, vehicle.fuelType)}
                        </Box>
                    );
                })}
            </Paper>

            {selectedVehicle && (
                <Paper sx={{ p: 2 }}>
                    <VehicleTableRow 
                        vehicle={selectedVehicle}
                        currentUser={currentUser}
                        userRoles={userRoles}
                        vehicles={vehicles}
                    />
                </Paper>
            )}
        </Box>
    );
};

export default VehicleMap;