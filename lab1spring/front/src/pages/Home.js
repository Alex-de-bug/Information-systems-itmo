import React from 'react';

import NewVehicleForm from '../components/NewVehicleForm';
import TableComponent from '../components/TableComponent';
import { Box, Grid } from '@mui/material';



const Home = () => {

  return (
      <Box sx={{paddingLeft: '8%', paddingRight: '9%', paddingBottom: '13%', paddingTop: '2%', alignItems: 'center'}}>
        <Grid container direction="row" spacing={17}>
          <Grid item xs={6}>
            <NewVehicleForm />
          </Grid>
          <Grid item xs={6}>
            <TableComponent />
          </Grid>
        </Grid>
      </Box>
  );
};

export default Home;
