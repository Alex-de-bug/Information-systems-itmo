import React, { useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const TableComponent = () => {
    useEffect(() => {
        const socket = new SockJS('http://localhost:8080/ws');

        const token = localStorage.getItem('token'); // Получаем токен из localStorage или другого источника

        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`, // Устанавливаем токен в заголовках
            },
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
                client.subscribe('/topic/tableUpdates', (message) => {
                    console.log('Received message:', message.body);
                    // Обработка сообщения и обновление таблицы
                });
            },
            onStompError: (frame) => {
                console.error('Error: ' + frame.headers.message);
            },
        });

        client.activate(); // Активируем клиента WebSocket

        return () => {
            client.deactivate(); // Деактивируем клиента при размонтировании компонента
        };
    }, []);

    return (
        <div>
            {/* Ваша таблица или другие компоненты */}
        </div>
    );
};

export default TableComponent;
