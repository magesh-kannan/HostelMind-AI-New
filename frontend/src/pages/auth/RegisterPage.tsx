import React, { useState } from 'react';
import { Box, Card, CardContent, Typography, TextField, Button, Alert, Link, CircularProgress, MenuItem, Select, FormControl, InputLabel } from '@mui/material';
import { Bot, UserPlus } from 'lucide-react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Role } from '../../types/auth';

export const RegisterPage: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [role, setRole] = useState<Role>('ROLE_STUDENT');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await register({
        email,
        password,
        fullName,
        phoneNumber,
        roles: [role],
      });
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Please check input values.');
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
        backgroundImage: 'radial-gradient(circle at 50% 50%, rgba(16, 185, 129, 0.15) 0%, rgba(15, 23, 42, 0.95) 70%)',
        p: 2,
      }}
    >
      <Card sx={{ maxWidth: 460, width: '100%', p: 2, className: 'glass-card' }}>
        <CardContent>
          <Box display="flex" flexDirection="column" alignItems="center" mb={3}>
            <Box
              sx={{
                p: 1.5,
                borderRadius: 3,
                background: 'linear-gradient(135deg, #10B981 0%, #4F46E5 100%)',
                color: '#fff',
                mb: 1.5,
              }}
            >
              <Bot size={32} />
            </Box>
            <Typography variant="h5" sx={{ fontWeight: 800 }}>
              Create Account
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center" mt={0.5}>
              Join HostelMind-AI Institutional Portal
            </Typography>
          </Box>

          {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

          <form onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Full Name"
              variant="outlined"
              margin="dense"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Institutional Email"
              type="email"
              variant="outlined"
              margin="dense"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Password (min 8 chars)"
              type="password"
              variant="outlined"
              margin="dense"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Phone Number"
              variant="outlined"
              margin="dense"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />
            <FormControl fullWidth margin="dense">
              <InputLabel>Primary Role</InputLabel>
              <Select
                value={role}
                label="Primary Role"
                onChange={(e) => setRole(e.target.value as Role)}
              >
                <MenuItem value="ROLE_STUDENT">Student</MenuItem>
                <MenuItem value="ROLE_WARDEN">Hostel Warden</MenuItem>
                <MenuItem value="ROLE_STAFF">Staff / Maintenance</MenuItem>
                <MenuItem value="ROLE_HIGHER_OFFICIAL">Higher Official</MenuItem>
                <MenuItem value="ROLE_ADMIN">System Administrator</MenuItem>
              </Select>
            </FormControl>

            <Button
              type="submit"
              fullWidth
              variant="contained"
              size="large"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} /> : <UserPlus size={20} />}
              sx={{
                mt: 3,
                mb: 2,
                py: 1.4,
                background: 'linear-gradient(135deg, #10B981 0%, #059669 100%)',
                fontWeight: 700,
              }}
            >
              {loading ? 'Creating Account...' : 'Register'}
            </Button>
          </form>

          <Box textAlign="center" mt={2}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?{' '}
              <Link component={RouterLink} to="/login" color="secondary.light" underline="hover" sx={{ fontWeight: 600 }}>
                Sign in here
              </Link>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
};
