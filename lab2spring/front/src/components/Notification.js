// components/Notification.js
import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';

import { setNotification } from '../redux/slices/userSlice';

const Notification = () => {
  const dispatch = useDispatch();
  const { message, color } = useSelector((state) => state.user);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (message) {
      setOpen(true);
    }
  }, [message]);

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpen(false);
    dispatch(setNotification({ message: null, color: null }));
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={3000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
    >
      {message && (
        <Alert onClose={handleClose} severity={color} sx={{ width: '100%' }}>
          {message}
        </Alert>
      )}
    </Snackbar>
  );
};

export default Notification;
