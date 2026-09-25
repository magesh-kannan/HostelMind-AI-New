import { createTheme } from '@mui/material/styles';

export const getAppTheme = (mode: 'dark' | 'light') =>
  createTheme({
    palette: {
      mode,
      primary: {
        main: mode === 'dark' ? '#6366F1' : '#1E293B',
        light: '#818CF8',
        dark: '#4338CA',
        contrastText: '#FFFFFF',
      },
      secondary: {
        main: '#10B981',
        light: '#34D399',
        dark: '#059669',
      },
      background: {
        default: mode === 'dark' ? '#0F172A' : '#F8FAFC',
        paper: mode === 'dark' ? '#1E293B' : '#FFFFFF',
      },
      text: {
        primary: mode === 'dark' ? '#F8FAFC' : '#0F172A',
        secondary: mode === 'dark' ? '#94A3B8' : '#475569',
      },
      divider: mode === 'dark' ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.08)',
    },
    typography: {
      fontFamily: "'Inter', sans-serif",
      h1: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 700 },
      h2: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 700 },
      h3: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 700 },
      h4: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 600 },
      h5: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 600 },
      h6: { fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 600 },
      button: { textTransform: 'none', fontWeight: 600 },
    },
    shape: {
      borderRadius: 10,
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            borderRadius: 8,
            padding: '8px 20px',
            boxShadow: 'none',
            transition: 'all 0.2s ease-in-out',
            '&:hover': {
              transform: 'translateY(-1px)',
            },
          },
        },
      },
      MuiCard: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
            boxShadow: mode === 'dark' ? '0 10px 30px rgba(0, 0, 0, 0.3)' : '0 4px 20px rgba(0, 0, 0, 0.05)',
            border: `1px solid ${mode === 'dark' ? 'rgba(255, 255, 255, 0.08)' : 'rgba(0, 0, 0, 0.06)'}`,
          },
        },
      },
    },
  });
