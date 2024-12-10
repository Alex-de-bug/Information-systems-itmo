import { createSlice } from '@reduxjs/toolkit';

const initialState = {
    user: null,
    roles: [],
    token: null,
    message: null,
    color: null,
};

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {
        loginSuccess: (state, action) => {
            localStorage.setItem('token', action.payload.token);
            localStorage.setItem('roles', JSON.stringify(action.payload.roles));
            localStorage.setItem('name', action.payload.user);
            state.user = action.payload.user;
            state.roles = action.payload.roles;
            state.token = action.payload.token;
        },
        logout: (state) => {
            localStorage.removeItem('name');
            localStorage.removeItem('token');
            localStorage.removeItem('roles');
            state.user = null;
            state.roles = [];
            state.token = null;
        },
        setNotification: (state, action) => {
            state.message = "";
            state.color = "";
            state.message = action.payload.message;
            state.color = action.payload.color;
        },
        
    },
});

export const { loginSuccess, logout, setNotification, clearNotification } = userSlice.actions;

export default userSlice.reducer;
