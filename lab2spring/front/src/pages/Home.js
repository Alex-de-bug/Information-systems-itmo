import React from 'react';

import NewVehicleForm from '../components/NewVehicleForm';
import TableComponent from '../components/TableComponent';
import { Box, Grid } from '@mui/material';

import { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';
import VehicleFilter from '../components/VehicleFilter';
import VehicleMap from '../components/VehicleMap';
import VehicleImport from '../components/VehicleImport';

const Home = () => {

  const [vehicles, setVehicles] = useState([]);
  const stompClientRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem('token');

    const fetchVehicles = async () => {
        try {
            const response = await axios.get(`http://${process.env.REACT_APP_SERVER}/api/user/vehicles`, {
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
        const socket = new SockJS(`http://${process.env.REACT_APP_SERVER}/ws`);
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
      <Box sx={{paddingLeft: '8%', paddingRight: '9%', paddingBottom: '13%', paddingTop: '2%', alignItems: 'center'}}>
        <Grid sx={{mb: 2}} container direction="row" spacing={17}>
          <Grid item xs={6}>
            <NewVehicleForm />
          </Grid>
          <Grid item xs={6}>
            <TableComponent vehicles={vehicles} />
          </Grid>
        </Grid>
        <Grid container direction="row" spacing={17}>
          <Grid item xs={6}>
          <VehicleMap 
              vehicles={vehicles}
          />
          </Grid>
          <Grid item xs={6}>
            <VehicleImport/>
            <VehicleFilter vehicles={vehicles} />
          </Grid>
        </Grid>
      </Box>
  );
};

export default Home;
