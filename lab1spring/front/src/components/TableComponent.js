import React, { useEffect, useState } from 'react';
import { 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow, 
    Paper,
    TablePagination,
    TextField, 
    IconButton, 
    Box,
    Typography
  } from '@mui/material';
import VehicleTableRow from './VehicleTableRow';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import UnfoldMoreIcon from '@mui/icons-material/UnfoldMore';
  


const TableComponent = ({ vehicles }) => {
    const [filteredVehicles, setFilteredVehicles] = useState([]);
    const [searchValues, setSearchValues] = useState({
        id: '',
        name: '',
        type: '',
        fuelType: ''
    });
    const [sortConfig, setSortConfig] = useState({
        key: null,
        direction: 'asc'
    });
    const [page, setPage] = useState(0);
    const [rowsPerPage] = useState(10);

    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    const handleSearch = (field, value) => {
        setSearchValues(prev => ({
            ...prev,
            [field]: value
        }));
    };

    useEffect(() => {
        let result = [...vehicles];

        result = result.filter(vehicle => {
            return (
                vehicle.id.toString().toLowerCase().includes(searchValues.id.toLowerCase()) &&
                vehicle.name.toLowerCase().includes(searchValues.name.toLowerCase()) &&
                vehicle.type.toLowerCase().includes(searchValues.type.toLowerCase()) &&
                vehicle.fuelType.toLowerCase().includes(searchValues.fuelType.toLowerCase())
            );
        });

        if (sortConfig.key) {
            result.sort((a, b) => {
                if (a[sortConfig.key] < b[sortConfig.key]) {
                    return sortConfig.direction === 'asc' ? -1 : 1;
                }
                if (a[sortConfig.key] > b[sortConfig.key]) {
                    return sortConfig.direction === 'asc' ? 1 : -1;
                }
                return 0;
            });
        }

        setFilteredVehicles(result);
        setPage(0);
    }, [vehicles, searchValues, sortConfig]);
    

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };
    

    

    return (
        <Paper sx={{borderRadius: 2, backgroundColor: 'rgba(0, 0, 0, 0.87)', }}>
            <TableContainer>
                <Table size="small" sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Typography variant="subtitle2">ID</Typography>
                                        <IconButton size="small" onClick={() => handleSort('id')}>
                                            {sortConfig.key === 'id' 
                                                ? (sortConfig.direction === 'asc' 
                                                    ? <ArrowUpwardIcon fontSize="small" />
                                                    : <ArrowDownwardIcon fontSize="small" />)
                                                : <UnfoldMoreIcon fontSize="small" />
                                            }
                                        </IconButton>
                                    </Box>
                                    <TextField
                                        size="small"
                                        placeholder="Поиск по ID"
                                        value={searchValues.id}
                                        onChange={(e) => handleSearch('id', e.target.value)}
                                        variant="outlined"
                                        fullWidth
                                    />
                                </Box>
                            </TableCell>
                            <TableCell>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Typography variant="subtitle2">Name</Typography>
                                        <IconButton size="small" onClick={() => handleSort('name')}>
                                            {sortConfig.key === 'name' 
                                                ? (sortConfig.direction === 'asc' 
                                                    ? <ArrowUpwardIcon fontSize="small" />
                                                    : <ArrowDownwardIcon fontSize="small" />)
                                                : <UnfoldMoreIcon fontSize="small" />
                                            }
                                        </IconButton>
                                    </Box>
                                    <TextField
                                        size="small"
                                        placeholder="Поиск по имени"
                                        value={searchValues.name}
                                        onChange={(e) => handleSearch('name', e.target.value)}
                                        variant="outlined"
                                        fullWidth
                                    />
                                </Box>
                            </TableCell>
                            <TableCell>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Typography variant="subtitle2">Тип тачки</Typography>
                                        <IconButton size="small" onClick={() => handleSort('type')}>
                                            {sortConfig.key === 'type' 
                                                ? (sortConfig.direction === 'asc' 
                                                    ? <ArrowUpwardIcon fontSize="small" />
                                                    : <ArrowDownwardIcon fontSize="small" />)
                                                : <UnfoldMoreIcon fontSize="small" />
                                            }
                                        </IconButton>
                                    </Box>
                                    <TextField
                                        size="small"
                                        placeholder="Поиск по типу"
                                        value={searchValues.type}
                                        onChange={(e) => handleSearch('type', e.target.value)}
                                        variant="outlined"
                                        fullWidth
                                    />
                                </Box>
                            </TableCell>
                            <TableCell>
                                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Typography variant="subtitle2">Топливо</Typography>
                                        <IconButton size="small" onClick={() => handleSort('fuelType')}>
                                            {sortConfig.key === 'fuelType' 
                                                ? (sortConfig.direction === 'asc' 
                                                    ? <ArrowUpwardIcon fontSize="small" />
                                                    : <ArrowDownwardIcon fontSize="small" />)
                                                : <UnfoldMoreIcon fontSize="small" />
                                            }
                                        </IconButton>
                                    </Box>
                                    <TextField
                                        size="small"
                                        placeholder="Поиск по топливу"
                                        value={searchValues.fuelType}
                                        onChange={(e) => handleSearch('fuelType', e.target.value)}
                                        variant="outlined"
                                        fullWidth
                                    />
                                </Box>
                            </TableCell>
                            <TableCell sx={{width: '15%'}}>
                                <Typography variant="subtitle2">Actions</Typography>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredVehicles
                            .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                            .map((vehicle) => (
                                <VehicleTableRow 
                                    key={vehicle.id}
                                    vehicle={vehicle}
                                    currentUser={localStorage.getItem('name')}
                                    userRoles={localStorage.getItem('roles')}
                                    vehicles={vehicles}
                                />
                            ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                component="div"
                count={filteredVehicles.length}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                rowsPerPageOptions={[10]}
            />
        </Paper>
    );
};

export default TableComponent;
