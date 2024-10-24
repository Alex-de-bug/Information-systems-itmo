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
    const stompClientRef = useRef(null); 
    const [page, setPage] = useState(0);
    const [rowsPerPage] = useState(10);

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
        <Paper sx={{borderRadius: 2}}>
            <TableContainer>
                <Table size="small" sx={{ tableLayout: 'fixed' }}>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Тип тачки</TableCell>
                            <TableCell>Топливо</TableCell>
                            <TableCell sx={{width: '15%'}}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {[...vehicles]
                            .reverse()
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
                count={vehicles.length}
                page={page}
                onPageChange={handleChangePage}
                rowsPerPage={rowsPerPage}
                rowsPerPageOptions={[10]}
            />
        </Paper>
    );
};

export default TableComponent;
