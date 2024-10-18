import React, { useEffect } from 'react';
import { Client } from '@stomp/stompjs';

const TableComponent = () => {
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error('Token not found.');
            return;
        }


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
            {/* Ваша таблица или другие компоненты */}
        </div>
    );
};

export default TableComponent;
