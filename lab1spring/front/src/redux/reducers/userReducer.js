const initialState = {
    user: null,
    roles: [],
    token: null,
};
  
const userReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        roles: action.payload.roles,
        token: action.payload.token,
      };
    case 'LOGOUT':
      return initialState;
    default:
      return state;
  }
};
  
export default userReducer;
  