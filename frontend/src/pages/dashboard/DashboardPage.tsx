import React, { useEffect, useState } from 'react';
import { Box, Grid2 as Grid, Card, CardContent, Typography, Button, Paper, Stack } from '@mui/material';
import { Shield, Sparkles, Building2, Cpu, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { StatusBadge } from '../../components/common/StatusBadge';
import { axiosClient } from '../../api/axiosClient';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [health, setHealth] = useState<{ status: string; timestamp: string } | null>(null);

  useEffect(() => {
    axiosClient.get('/health')
      .then((res) => setHealth(res.data))
      .catch(() => setHealth({ status: 'DOWN', timestamp: new Date().toISOString() }));
  }, []);

  return (
    <Box>
      <Box mb={4} display="flex" justifyContent="space-between" alignItems="center">
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, mb: 0.5 }}>
            Welcome back, {user?.fullName} 👋
          </Typography>
          <Typography variant="body1" color="text.secondary">
            HostelMind-AI Institutional Management Platform • Phase 0 Active Baseline
          </Typography>
        </Box>
        <StatusBadge
          label={health?.status === 'UP' ? 'Backend Live (Spring Boot 3.4)' : 'Backend Connecting...'}
          status={health?.status === 'UP' ? 'success' : 'warning'}
        />
      </Box>

      {/* Overview Metric Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="caption" color="text.secondary" fontWeight={600}>
                  AUTHENTICATED ROLE
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700, mt: 0.5 }}>
                  {user?.roles[0]?.replace('ROLE_', '')}
                </Typography>
              </Box>
              <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: 'rgba(99, 102, 241, 0.1)', color: '#818CF8' }}>
                <Shield size={24} />
              </Box>
            </Box>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="caption" color="text.secondary" fontWeight={600}>
                  AI ORCHESTRATOR
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700, mt: 0.5 }}>
                  LangChain4j Port
                </Typography>
              </Box>
              <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: 'rgba(16, 185, 129, 0.1)', color: '#34D399' }}>
                <Cpu size={24} />
              </Box>
            </Box>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="caption" color="text.secondary" fontWeight={600}>
                  DATABASE ENGINE
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700, mt: 0.5 }}>
                  Postgres + pgvector
                </Typography>
              </Box>
              <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: 'rgba(245, 158, 11, 0.1)', color: '#FBBF24' }}>
                <Building2 size={24} />
              </Box>
            </Box>
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" alignItems="center" justifyContent="space-between">
              <Box>
                <Typography variant="caption" color="text.secondary" fontWeight={600}>
                  INFRA BUDGET
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700, mt: 0.5 }}>
                  $0.00 / month
                </Typography>
              </Box>
              <Box sx={{ p: 1.5, borderRadius: 2, bgcolor: 'rgba(16, 185, 129, 0.1)', color: '#34D399' }}>
                <CheckCircle2 size={24} />
              </Box>
            </Box>
          </Paper>
        </Grid>
      </Grid>

      {/* Architecture & Phase Status Card */}
      <Card sx={{ borderRadius: 3, mb: 4, className: 'glass-card' }}>
        <CardContent sx={{ p: 3 }}>
          <Box display="flex" alignItems="center" gap={1.5} mb={2}>
            <Sparkles size={24} color="#818CF8" />
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              Phase 0 Verification & Architecture Milestone
            </Typography>
          </Box>
          <Typography variant="body2" color="text.secondary" paragraph>
            HostelMind-AI baseline scaffolding has been built under <strong>Clean Architecture</strong>. The backend Spring Boot 3.4 engine runs on Java 21 with JWT stateless authentication, Flyway schema migrations, and Lombok abstractions.
          </Typography>

          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} mt={3}>
            <Button variant="contained" color="primary" size="large">
              Explore Hostel Engine (Phase 1 Ready)
            </Button>
            <Button variant="outlined" color="inherit" size="large">
              View Audit & AI Logs
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  );
};
