import React, { useState } from 'react';
import { Box, Card, CardContent, Typography, TextField, Button, Alert, Link, CircularProgress } from '@mui/material';
import { Bot, LogIn } from 'lucide-react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const LoginPage: React.FC = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login({ email, password });
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid login credentials. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#0F172A',
        backgroundImage: 'radial-gradient(circle at 50% 50%, rgba(79, 70, 229, 0.15) 0%, rgba(15, 23, 42, 0.95) 70%)',
        p: 2,
      }}
    >
      <Card sx={{ maxWidth: 420, width: '100%', p: 2, className: 'glass-card' }}>
        <CardContent>
          <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
            <Box
              sx={{
                p: 1.5,
                borderRadius: 3,
                background: 'linear-gradient(135deg, #4F46E5 0%, #10B981 100%)',
                color: '#fff',
                mb: 1.5,
              }}
            >
              <Bot size={32} />
            </Box>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>
              HostelMind<Typography component="span" variant="h5" color="primary.light" sx={{ fontWeight: 800 }}>-AI</Typography>
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center" mt={0.5}>
              Sign in to your Institutional Operations Dashboard
            </Typography>
          </Box>

          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Institutional Email"
              variant="outlined"
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="e.g. student@hostelmind.ai"
            />
            <TextField
              fullWidth
              label="Password"
              type="password"
              variant="outlined"
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} /> : <LogIn size={20} />}
              sx={{
                mt: 3,
                mb: 2,
                py: 1.4,
                background: 'linear-gradient(135deg, #4F46E5 0%, #4338CA 100%)',
                fontWeight: 700,
              }}
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </Button>
          </form>

          <Box textAlign="center" mt={2}>
            <Typography variant="body2" color="text.secondary">
              Don't have an account?{' '}
              <Link component={RouterLink} to="/register" color="primary.light" underline="hover" sx={{ fontWeight: 600 }}>
                Register here
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};
