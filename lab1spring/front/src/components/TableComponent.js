import React, { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';


const TableComponent = () => {

    const [vehicles, setVehicles] = useState([]);
    const stompClientRef = useRef(null); 
    

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
        <div>
            <h2>Vehicles Table</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>X</th>
                        <th>Y</th>
                        <th>Type</th>
                        <th>Engine Power</th>
                        <th>Number of Wheels</th>
                        <th>Capacity</th>
                        <th>Distance Travelled</th>
                        <th>Fuel Consumption</th>
                        <th>Fuel Type</th>
                        <th>Users</th>
                        <th>Permission to Edit</th>
                    </tr>
                </thead>
                <tbody id="tbody">
                    {vehicles.map((vehicle) => (
                        <tr key={vehicle.id}>
                            <td>{vehicle.id}</td>
                            <td>{vehicle.name}</td>
                            <td>{vehicle.x}</td>
                            <td>{vehicle.y}</td>
                            <td>{vehicle.type}</td>
                            <td>{vehicle.enginePower}</td>
                            <td>{vehicle.numberOfWheels}</td>
                            <td>{vehicle.capacity}</td>
                            <td>{vehicle.distanceTravelled}</td>
                            <td>{vehicle.fuelConsumption}</td>
                            <td>{vehicle.fuelType}</td>
                            <td>{vehicle.namesUsers.join(', ')}</td>
                            <td>{vehicle.permissionToEdit ? 'Yes' : 'No'}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default TableComponent;
