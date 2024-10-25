import React, { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import { 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow, 
    Paper,
    TablePagination
  } from '@mui/material';
  import VehicleTableRow from './VehicleTableRow';

  


const TableComponent = () => {

    const [vehicles, setVehicles] = useState([]);
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
    const stompClientRef = useRef(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage] = useState(10);

    // Обработчик сортировки
    const handleSort = (key) => {
        let direction = 'asc';
        if (sortConfig.key === key && sortConfig.direction === 'asc') {
            direction = 'desc';
        }
        setSortConfig({ key, direction });
    };

    // Обработчик поиска
    const handleSearch = (field, value) => {
        setSearchValues(prev => ({
            ...prev,
            [field]: value
        }));
    };

    // Применение фильтров и сортировки
    useEffect(() => {
        let result = [...vehicles];

        // Применяем поиск
        result = result.filter(vehicle => {
            return (
                vehicle.id.toString().toLowerCase().includes(searchValues.id.toLowerCase()) &&
                vehicle.name.toLowerCase().includes(searchValues.name.toLowerCase()) &&
                vehicle.type.toLowerCase().includes(searchValues.type.toLowerCase()) &&
                vehicle.fuelType.toLowerCase().includes(searchValues.fuelType.toLowerCase())
            );
        });

        // Применяем сортировку
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
        setPage(0); // Сброс на первую страницу при фильтрации
    }, [vehicles, searchValues, sortConfig]);
    

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };
    

    useEffect(() => {
        const token = localStorage.getItem('token');

        const fetchVehicles = async () => {
            try {
                const response = await axios.get('http://localhost:8080/user/vehicles', {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    }
                });
                setVehicles(response.data); 
            } catch (error) {
                console.error('Error fetching vehicles:', error);
            }
        };

        fetchVehicles();

        const connectWebSocket = () => {
            const socket = new SockJS('http://localhost:8080/ws');
            const stompClient = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`,
                },
                onConnect: (frame) => {
                    console.log('Connected: ' + frame);
                    stompClient.subscribe('/topic/tableUpdates', (message) => {
                        const data = JSON.parse(message.body);
                        console.log(data);
                        fetchVehicles();
                    });
                },
                debug: (str) => {
                    console.log(str);
                }
            });

            stompClient.activate();
            stompClientRef.current = stompClient;
        };

        connectWebSocket();

        return () => {
            if (stompClientRef.current) stompClientRef.current.deactivate();
        };

    }, []);

    return (
        <Paper sx={{borderRadius: 2, backgroundColor: 'rgba(0, 0, 0, 0.87)', }}>
            <TableContainer>
                <Table size="small" sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                ID
                                <div>
                                    <input
                                        type="text"
                                        placeholder="Поиск по ID"
                                        value={searchValues.id}
                                        onChange={(e) => handleSearch('id', e.target.value)}
                                        style={{ width: '100%' }}
                                    />
                                    <button onClick={() => handleSort('id')}>
                                        {sortConfig.key === 'id' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '↕'}
                                    </button>
                                </div>
                            </TableCell>
                            <TableCell>
                                Name
                                <div>
                                    <input
                                        type="text"
                                        placeholder="Поиск по имени"
                                        value={searchValues.name}
                                        onChange={(e) => handleSearch('name', e.target.value)}
                                        style={{ width: '100%' }}
                                    />
                                    <button onClick={() => handleSort('name')}>
                                        {sortConfig.key === 'name' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '↕'}
                                    </button>
                                </div>
                            </TableCell>
                            <TableCell>
                                Тип тачки
                                <div>
                                    <input
                                        type="text"
                                        placeholder="Поиск по типу"
                                        value={searchValues.type}
                                        onChange={(e) => handleSearch('type', e.target.value)}
                                        style={{ width: '100%' }}
                                    />
                                    <button onClick={() => handleSort('type')}>
                                        {sortConfig.key === 'type' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '↕'}
                                    </button>
                                </div>
                            </TableCell>
                            <TableCell>
                                Топливо
                                <div>
                                    <input
                                        type="text"
                                        placeholder="Поиск по топливу"
                                        value={searchValues.fuelType}
                                        onChange={(e) => handleSearch('fuelType', e.target.value)}
                                        style={{ width: '100%' }}
                                    />
                                    <button onClick={() => handleSort('fuelType')}>
                                        {sortConfig.key === 'fuelType' ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '↕'}
                                    </button>
                                </div>
                            </TableCell>
                            <TableCell sx={{width: '15%'}}>Actions</TableCell>
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
