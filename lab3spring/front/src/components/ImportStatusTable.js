import { 
    Paper, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow,
    TablePagination,
    Typography,
    Chip 
} from '@mui/material';
import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';


const ImportStatusTable = () => {
    const [statuses, setStatuses] = useState([]);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const stompClientRef = useRef(null);

    

    useEffect(() => {
        const token = localStorage.getItem('token');
    
        const fetchStatuses = async () => {
            try {
                const response = await axios.get(`http://${process.env.REACT_APP_SERVER}/api/user/vehicles/istat`, {
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                });
                console.log(response);
                setStatuses(response.data);
            } catch (error) {
                console.error('Ошибка при получении статусов');
            }
        };
    
        fetchStatuses();
    
        const connectWebSocket = () => {
            const socket = new SockJS(`http://${process.env.REACT_APP_SERVER}/ws`);
            const stompClient = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`,
                },
                onConnect: (frame) => {
                    console.log('Connected: ' + frame);
                    stompClient.subscribe('/topic/istat', (message) => {
                        const data = JSON.parse(message.body);
                        console.log(data);
                        fetchStatuses();
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

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const currentPageData = statuses.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage
    );

    return (
        <Paper sx={{borderRadius: 2, backgroundColor: 'rgba(0, 0, 0, 0.87)', }}>
            <Typography variant="h6" p={2}>
                История импортов
            </Typography>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Статус</TableCell>
                            <TableCell>Пользователь</TableCell>
                            <TableCell>Количество машин</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {currentPageData.map((status) => (
                            <TableRow key={status.id}>
                                <TableCell>{status.id}</TableCell>
                                <TableCell>
                                    <StatusChip status={status.status} />
                                </TableCell>
                                <TableCell>{status.username}</TableCell>
                                <TableCell>{status.count}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[5, 10, 25]}
                component="div"
                count={statuses.length}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
                labelRowsPerPage="Строк на странице:"
                labelDisplayedRows={({ from, to, count }) => 
                    `${from}-${to} из ${count}`}
            />
        </Paper>
    );
};

const StatusChip = ({ status }) => {
    const getColor = () => {
        switch (status) {
            case 'DONE':
                return 'success';
            case 'ERROR':
                return 'error';
            default:
                return 'default';
        }
    };

    const getLocStat = () => {
        switch (status) {
            case 'DONE':
                return 'успешно';
            case 'ERROR':
                return 'ошибка';
            default:
                return 'default';
        }
    };

    return (
        <Chip 
            label={getLocStat()} 
            color={getColor()} 
            size="small" 
        />
    );
};

export default ImportStatusTable;