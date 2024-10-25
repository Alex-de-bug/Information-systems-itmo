import { Box, Button, TextField, Paper, Typography, Container} from '@mui/material';
import { useState } from 'react';

const RoulettePage = () => {
    const [isSpinning, setIsSpinning] = useState(false);
    const [currentRotation, setCurrentRotation] = useState(0);
    const [selectedNumber, setSelectedNumber] = useState('');
    const [bet, setBet] = useState(0);
    const [result, setResult] = useState(null);
    const [balance, setBalance] = useState(1000);

    const numbers = [
        0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10,
        5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26
    ];

    const getNumberColor = (number) => {
        if (number === 0) return '#008000';
        return number % 2 === 0 ? '#000000' : '#FF0000';
    };

    const spinWheel = () => {
        if (isSpinning || bet <= 0 || !selectedNumber || balance < bet) return;

        setIsSpinning(true);
        setBalance(prev => prev - bet);

        const spins = 5 + Math.random() * 5;
        const extraDegrees = Math.random() * 360;
        const totalRotation = spins * 360 + extraDegrees;
        
        const finalRotation = extraDegrees;
        const numberIndex = Math.floor((360 - (finalRotation % 360)) / (360 / numbers.length));
        const winningNumber = numbers[numberIndex];

        setCurrentRotation(prev => prev + totalRotation);

        setTimeout(() => {
            setIsSpinning(false);
            setResult(winningNumber);

            if (Number(selectedNumber) === winningNumber) {
                setBalance(prev => prev + bet * 35);
                setResult(`Выигрыш! Число ${winningNumber}`);
            } else {
                setResult(`Проигрыш. Выпало число ${winningNumber}`);
            }
        }, 5000);
    };

    return (
        <Box sx={{ 
            minHeight: '100vh',
            backgroundColor: '#1a1a1a',
            color: '#fff'
        }}>

            <Container maxWidth="lg" sx={{ pt: 4 }}>
                <Box sx={{ 
                    display: 'flex', 
                    flexDirection: 'column', 
                    alignItems: 'center', 
                    gap: 3,
                    p: 3,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    borderRadius: 2
                }}>
                    <Typography variant="h4" sx={{ color: '#4CAF50' }}>
                        Баланс: ${balance}
                    </Typography>

                    <Paper 
                        sx={{ 
                            width: 500, 
                            height: 500, 
                            borderRadius: '50%',
                            position: 'relative',
                            overflow: 'hidden',
                            border: '15px solid #2C3E50',
                            backgroundColor: '#34495E',
                            boxShadow: '0 0 50px rgba(0,0,0,0.5)'
                        }}
                    >
                        {numbers.map((number, index) => (
                            <Box
                                key={number}
                                sx={{
                                    position: 'absolute',
                                    width: '50%',
                                    height: '2px',
                                    background: getNumberColor(number),
                                    left: '50%',
                                    top: '50%',
                                    transformOrigin: '0% 50%',
                                    transform: `rotate(${(index * 360) / numbers.length}deg)`,
                                    '&::after': {
                                        content: `'${number}'`,
                                        position: 'absolute',
                                        left: '75%',
                                        top: '-12px',
                                        color: '#fff',
                                        fontSize: '16px',
                                        fontWeight: 'bold',
                                        textShadow: '1px 1px 2px rgba(0,0,0,0.8)'
                                    }
                                }}
                            />
                        ))}
                        <Box
                            sx={{
                                position: 'absolute',
                                width: '100%',
                                height: '100%',
                                transform: `rotate(${currentRotation}deg)`,
                                transition: isSpinning ? 'transform 5s cubic-bezier(0.2, 0.8, 0.3, 1)' : 'none'
                            }}
                        >
                            <Box
                                sx={{
                                    position: 'absolute',
                                    top: '-10px',
                                    left: '50%',
                                    transform: 'translateX(-50%)',
                                    width: 0,
                                    height: 0,
                                    borderLeft: '15px solid transparent',
                                    borderRight: '15px solid transparent',
                                    borderTop: '30px solid #FFD700',
                                    filter: 'drop-shadow(0 0 5px rgba(0,0,0,0.5))',
                                    zIndex: 2
                                }}
                            />
                        </Box>
                    </Paper>

                    {/* Контролы */}
                    <Box sx={{ 
                        display: 'flex', 
                        gap: 2, 
                        alignItems: 'center',
                        backgroundColor: 'rgba(255,255,255,0.1)',
                        p: 3,
                        borderRadius: 2
                    }}>
                        <TextField
                            label="Номер (0-36)"
                            type="number"
                            value={selectedNumber}
                            onChange={(e) => {
                                const value = Math.min(36, Math.max(0, Number(e.target.value)));
                                setSelectedNumber(value);
                            }}
                            disabled={isSpinning}
                            sx={{ 
                                width: 120,
                                '& .MuiOutlinedInput-root': {
                                    '& fieldset': { borderColor: '#fff' },
                                    '&:hover fieldset': { borderColor: '#FFD700' },
                                },
                                '& .MuiInputLabel-root': { color: '#fff' },
                                '& .MuiInputBase-input': { color: '#fff' }
                            }}
                        />
                        <TextField
                            label="Ставка"
                            type="number"
                            value={bet}
                            onChange={(e) => setBet(Math.max(0, Number(e.target.value)))}
                            disabled={isSpinning}
                            sx={{ 
                                width: 120,
                                '& .MuiOutlinedInput-root': {
                                    '& fieldset': { borderColor: '#fff' },
                                    '&:hover fieldset': { borderColor: '#FFD700' },
                                },
                                '& .MuiInputLabel-root': { color: '#fff' },
                                '& .MuiInputBase-input': { color: '#fff' }
                            }}
                        />
                        <Button 
                            variant="contained" 
                            onClick={spinWheel}
                            disabled={isSpinning || bet <= 0 || !selectedNumber || balance < bet}
                            sx={{ 
                                backgroundColor: '#FFD700',
                                color: '#000',
                                '&:hover': {
                                    backgroundColor: '#FFC107'
                                }
                            }}
                        >
                            Крутить
                        </Button>
                    </Box>

                    {result && (
                        <Typography 
                            variant="h5" 
                            sx={{ 
                                color: result.includes('Выигрыш') ? '#4CAF50' : '#f44336',
                                textShadow: '2px 2px 4px rgba(0,0,0,0.5)'
                            }}
                        >
                            {result}
                        </Typography>
                    )}
                </Box>
            </Container>
        </Box>
    );
};

export default RoulettePage;