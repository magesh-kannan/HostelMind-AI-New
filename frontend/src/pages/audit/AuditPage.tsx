import React, { useState, useMemo } from 'react';
import {
  Box, Card, CardContent, Typography, Grid, Chip, TextField,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  LinearProgress, InputAdornment, FormControl, InputLabel, Select, MenuItem,
  Tabs, Tab, Avatar,
} from '@mui/material';
import { ShieldCheck, Search, Activity, Database, Zap } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { auditApi } from '../../api/auditApi';
import { useAuth } from '../../context/AuthContext';

// ── Colour palette per entity type ───────────────────────────────────────────
const ENTITY_COLORS: Record<string, string> = {
  Complaint: '#6366f1',
  Invoice: '#10b981',
  Payment: '#f59e0b',
  EmergencySosAlert: '#ef4444',
  FacilityAsset: '#3b82f6',
  MessMenu: '#8b5cf6',
  MealAttendance: '#06b6d4',
  User: '#ec4899',
  RoomAllocation: '#84cc16',
};

const getEntityColor = (entity?: string) =>
  entity ? (ENTITY_COLORS[entity] ?? '#94a3b8') : '#94a3b8';

// ── Mini stat card ────────────────────────────────────────────────────────────
const StatPill: React.FC<{ label: string; count: number; color: string }> = ({
  label, count, color,
}) => (
  <Box
    sx={{
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      px: 2, py: 1,
      borderRadius: 2,
      border: '1px solid',
      borderColor: `${color}33`,
      bgcolor: `${color}0d`,
      mb: 1,
    }}
  >
    <Typography variant="body2" fontWeight={600} color="text.primary">
      {label}
    </Typography>
    <Chip label={count} size="small" sx={{ bgcolor: color, color: '#fff', fontWeight: 700, height: 22 }} />
  </Box>
);

// ── Main Page ─────────────────────────────────────────────────────────────────
export const AuditPage: React.FC = () => {
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes('ROLE_ADMIN') || user?.roles?.includes('ROLE_HIGHER_OFFICIAL');

  const [tab, setTab] = useState(0);
  const [search, setSearch] = useState('');
  const [entityFilter, setEntityFilter] = useState('');

  const { data: logs = [], isLoading: logsLoading } = useQuery({
    queryKey: ['auditLogs', isAdmin],
    queryFn: () => (isAdmin ? auditApi.getAllLogs() : auditApi.getMyLogs()),
    refetchInterval: 60_000,
  });

  const { data: stats } = useQuery({
    queryKey: ['auditStats'],
    queryFn: auditApi.getStats,
    enabled: isAdmin,
  });

  // ── Filter ──
  const filtered = useMemo(() => {
    return logs.filter((log) => {
      const matchSearch =
        !search ||
        log.action.toLowerCase().includes(search.toLowerCase()) ||
        (log.details ?? '').toLowerCase().includes(search.toLowerCase()) ||
        (log.entityType ?? '').toLowerCase().includes(search.toLowerCase());
      const matchEntity = !entityFilter || log.entityType === entityFilter;
      return matchSearch && matchEntity;
    });
  }, [logs, search, entityFilter]);

  const entityTypes = useMemo(() => {
    const set = new Set(logs.map((l) => l.entityType).filter(Boolean) as string[]);
    return Array.from(set).sort();
  }, [logs]);

  const topActions = useMemo(() => {
    if (!stats?.byAction) return [];
    return Object.entries(stats.byAction)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 8);
  }, [stats]);

  const topEntities = useMemo(() => {
    if (!stats?.byEntityType) return [];
    return Object.entries(stats.byEntityType)
      .sort((a, b) => b[1] - a[1]);
  }, [stats]);

  return (
    <Box sx={{ p: 3 }}>
      {/* ── Header ───────────────────────────────────────────────────── */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" fontWeight={800} sx={{ letterSpacing: -0.5, display: 'flex', alignItems: 'center', gap: 1 }}>
          <ShieldCheck size={32} color="#6366f1" />
          Audit Logs &amp; AI Runs
        </Typography>
        <Typography variant="body2" color="text.secondary" mt={0.5}>
          {isAdmin
            ? 'Full system event trail — every action, entity change, and AI run captured.'
            : 'Your personal activity trail across the HostelMind platform.'}
        </Typography>
      </Box>

      {/* ── Summary cards ────────────────────────────────────────────── */}
      {isAdmin && stats && (
        <Grid container spacing={2} mb={3}>
          {[
            { label: 'Total Events', value: stats.totalEvents, icon: <Activity size={22} />, color: '#6366f1' },
            { label: 'Entity Types', value: Object.keys(stats.byEntityType).length, icon: <Database size={22} />, color: '#10b981' },
            { label: 'Distinct Actions', value: Object.keys(stats.byAction).length, icon: <Zap size={22} />, color: '#f59e0b' },
          ].map((s) => (
            <Grid item xs={12} md={4} key={s.label}>
              <Card
                sx={{
                  background: `linear-gradient(135deg, ${s.color}10, ${s.color}05)`,
                  border: '1px solid', borderColor: `${s.color}30`, borderRadius: 3,
                  transition: 'transform 0.2s',
                  '&:hover': { transform: 'translateY(-3px)' },
                }}
              >
                <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Avatar sx={{ bgcolor: `${s.color}20`, color: s.color, width: 48, height: 48 }}>
                    {s.icon}
                  </Avatar>
                  <Box>
                    <Typography variant="h4" fontWeight={800} color={s.color}>{s.value}</Typography>
                    <Typography variant="body2" color="text.secondary">{s.label}</Typography>
                  </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      <Grid container spacing={3}>
        {/* ── Log table ────────────────────────────────────────────── */}
        <Grid item xs={12} md={isAdmin ? 8 : 12}>
          <Card sx={{ borderRadius: 3 }}>
            {/* Filters */}
            <Box sx={{ p: 2, display: 'flex', gap: 2, borderBottom: 1, borderColor: 'divider', flexWrap: 'wrap' }}>
              <TextField
                size="small"
                placeholder="Search by action, entity, or details…"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                InputProps={{ startAdornment: <InputAdornment position="start"><Search size={16} /></InputAdornment> }}
                sx={{ flexGrow: 1, minWidth: 220 }}
              />
              <FormControl size="small" sx={{ minWidth: 180 }}>
                <InputLabel>Entity Type</InputLabel>
                <Select
                  label="Entity Type"
                  value={entityFilter}
                  onChange={(e) => setEntityFilter(e.target.value)}
                >
                  <MenuItem value="">All</MenuItem>
                  {entityTypes.map((et) => (
                    <MenuItem key={et} value={et}>{et}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Box>

            {logsLoading && <LinearProgress />}

            <Box sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}>
              <Tabs value={tab} onChange={(_, v) => setTab(v)}>
                <Tab label={`All (${filtered.length})`} sx={{ fontWeight: 600 }} />
                <Tab label="Emergency SOS" sx={{ fontWeight: 600 }} />
                <Tab label="AI & System" sx={{ fontWeight: 600 }} />
              </Tabs>
            </Box>

            <TableContainer sx={{ maxHeight: 520 }}>
              <Table size="small" stickyHeader>
                <TableHead>
                  <TableRow sx={{ '& th': { fontWeight: 700, bgcolor: 'background.paper' } }}>
                    <TableCell>Action</TableCell>
                    <TableCell>Entity</TableCell>
                    <TableCell sx={{ maxWidth: 240 }}>Details</TableCell>
                    <TableCell>Time</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {filtered
                    .filter((log) => {
                      if (tab === 1) return log.entityType === 'EmergencySosAlert';
                      if (tab === 2) return log.action?.includes('AI') || log.action?.includes('SYSTEM') || log.entityType === 'AiAgentRun';
                      return true;
                    })
                    .slice(0, 200)
                    .map((log) => (
                      <TableRow
                        key={log.id}
                        sx={{ '&:hover': { bgcolor: 'action.hover' } }}
                      >
                        <TableCell>
                          <Typography variant="caption" fontWeight={700} sx={{ fontFamily: 'monospace', fontSize: '0.75rem' }}>
                            {log.action}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          {log.entityType && (
                            <Chip
                              label={log.entityType}
                              size="small"
                              sx={{
                                bgcolor: `${getEntityColor(log.entityType)}18`,
                                color: getEntityColor(log.entityType),
                                fontWeight: 600,
                                fontSize: '0.7rem',
                                border: '1px solid',
                                borderColor: `${getEntityColor(log.entityType)}40`,
                              }}
                            />
                          )}
                        </TableCell>
                        <TableCell sx={{ maxWidth: 240 }}>
                          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                            {log.details ?? '—'}
                          </Typography>
                        </TableCell>
                        <TableCell>
                          <Typography variant="caption" color="text.secondary" noWrap>
                            {log.createdAt
                              ? new Date(log.createdAt).toLocaleString('en-IN', { hour12: false })
                              : '—'}
                          </Typography>
                        </TableCell>
                      </TableRow>
                    ))}
                  {filtered.length === 0 && !logsLoading && (
                    <TableRow>
                      <TableCell colSpan={4} align="center" sx={{ py: 5, color: 'text.secondary' }}>
                        No audit events match your filters
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </Card>
        </Grid>

        {/* ── Right sidebar: stats breakdown ──────────────────────── */}
        {isAdmin && (
          <Grid item xs={12} md={4}>
            <Card sx={{ borderRadius: 3, mb: 2 }}>
              <CardContent>
                <Typography variant="subtitle2" fontWeight={700} mb={1.5} color="text.secondary">
                  BY ENTITY TYPE
                </Typography>
                {topEntities.map(([entity, count]) => (
                  <StatPill
                    key={entity}
                    label={entity}
                    count={Number(count)}
                    color={getEntityColor(entity)}
                  />
                ))}
                {topEntities.length === 0 && (
                  <Typography variant="body2" color="text.secondary">No data yet</Typography>
                )}
              </CardContent>
            </Card>

            <Card sx={{ borderRadius: 3 }}>
              <CardContent>
                <Typography variant="subtitle2" fontWeight={700} mb={1.5} color="text.secondary">
                  TOP ACTIONS
                </Typography>
                {topActions.map(([action, count]) => (
                  <StatPill
                    key={action}
                    label={action.replace(/_/g, ' ')}
                    count={Number(count)}
                    color="#6366f1"
                  />
                ))}
                {topActions.length === 0 && (
                  <Typography variant="body2" color="text.secondary">No data yet</Typography>
                )}
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};
