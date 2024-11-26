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
            state.user = action.payload.user;
            state.roles = action.payload.roles;
            state.token = action.payload.token;
        },
        logout: (state) => {
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
