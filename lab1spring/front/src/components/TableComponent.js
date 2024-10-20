import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import axios from 'axios';


const TableComponent = () => {

    const [vehicles, setVehicles] = useState([]);


    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Token not found.');
            return;
        }

        const fetchVehicles = async () => {
            try {
                const response = await axios.get('http://localhost:8080/vehicles', {
                    headers: {
                        Authorization: `Bearer ${token.replaceAll('"', '')}`,
                    }
                });
                setVehicles(response.data); // Assuming response.data is the list of vehicles
            } catch (error) {
                console.error('Error fetching vehicles:', error);
            }
        };

        fetchVehicles();

        const ws = new WebSocket('ws://localhost:8080/ws');
        const client = new Client({
            webSocketFactory: () => ws,
            connectHeaders: {
                Authorization: `Bearer ${token.replaceAll('"', '')}`,
            },
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
                client.subscribe('/topic/tableUpdates', (message) => {
                    console.log('Received message:', message.body);
                    fetchVehicles();
                });
            },
            onWebSocketClose: () => {
                console.log('WebSocket connection closed.');
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            },

            debug: function (str) {
                console.log(str);
              }

        });

        client.activate();

        return () => {
            client.deactivate();
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
                <tbody>
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
