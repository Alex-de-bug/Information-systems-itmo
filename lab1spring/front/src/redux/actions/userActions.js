export const loginUser = (user, roles, token) => ({
    type: 'LOGIN_SUCCESS',
    payload: { user, roles, token },
});
  
export const logoutUser = () => ({
    type: 'LOGOUT',
});
  