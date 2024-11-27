import { 
    Button, 
} from '@mui/material';
import { useState, useRef } from 'react';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { setNotification } from '../redux/slices/userSlice';

const VehicleImport = () => {
    const dispatch = useDispatch();
    const [selectedFile, setSelectedFile] = useState(null);
    const fileInputRef = useRef(null);

    const handleFileSelect = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleSubmit = async () => {
        if (!selectedFile) {
            dispatch(setNotification({
                color: 'error',
                message: 'Пожалуйста, выберите файл'
            }));
            return;
        }

        const formData = new FormData();
        formData.append('file', selectedFile);

        try {
            await axios.post(
                `http://${process.env.REACT_APP_SERVER}/api/user/vehicles/import`,
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('token')}`
                    }
                }
            );

            dispatch(setNotification({
                color: 'success',
                message: 'Импорт прошёл успешно'
            }));
            setSelectedFile(null);
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        } catch (error) {
            console.log(error);
            dispatch(setNotification({
                color: 'error',
                message: error.response?.data?.message || 'Произошла ошибка при импорте'
            }));
            setSelectedFile(null);
            if (fileInputRef.current) {
                fileInputRef.current.value = '';
            }
        }
      };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', alignItems: 'flex-start' }}>
            <input
                ref={fileInputRef}
                type="file"
                accept=".csv"
                onChange={handleFileSelect}
                style={{ marginBottom: '10px' }}
            />
            <Button 
                variant="contained" 
                color="primary" 
                onClick={handleSubmit}
                disabled={!selectedFile}
            >
                Импортировать
            </Button>
        </div>
    );
};

export default VehicleImport;