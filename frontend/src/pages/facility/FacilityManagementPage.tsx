import React, { useState } from 'react';
import {
  Box, Grid, Card, CardContent, Typography, Chip, Button, Tabs, Tab,
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  MenuItem, Select, FormControl, InputLabel, Alert, LinearProgress,
  CircularProgress, IconButton, Tooltip, Avatar,
} from '@mui/material';
import {
  Zap, AlertTriangle, Wrench, CheckCircle, Settings,
  Plus, Activity, Shield, XCircle,
} from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { facilityApi } from '../../api/facilityApi';
import type {
  AssetStatus, AssetCategory, SosType, SosAlertStatus, MaintenanceFrequency,
} from '../../types/facility';

// ── Helpers ──────────────────────────────────────────────────────────────────

const SOS_STATUS_COLOR: Record<SosAlertStatus, 'error' | 'warning' | 'success' | 'default'> = {
  TRIGGERED: 'error',
  ACKNOWLEDGED: 'warning',
  RESOLVED: 'success',
  FALSE_ALARM: 'default',
};

const ASSET_STATUS_COLOR: Record<AssetStatus, 'success' | 'warning' | 'error' | 'default'> = {
  OPERATIONAL: 'success',
  UNDER_MAINTENANCE: 'warning',
  OUT_OF_SERVICE: 'error',
};

const SOS_NEXT_STATUS: Partial<Record<SosAlertStatus, SosAlertStatus[]>> = {
  TRIGGERED: ['ACKNOWLEDGED', 'FALSE_ALARM', 'RESOLVED'],
  ACKNOWLEDGED: ['RESOLVED', 'FALSE_ALARM'],
};

// ── Stat Card ────────────────────────────────────────────────────────────────

interface StatCardProps {
  label: string;
  value: number | string;
  icon: React.ReactNode;
  color: string;
  gradient: string;
}

const StatCard: React.FC<StatCardProps> = ({ label, value, icon, color, gradient }) => (
  <Card
    sx={{
      background: gradient,
      border: '1px solid',
      borderColor: `${color}33`,
      borderRadius: 3,
      transition: 'transform 0.2s, box-shadow 0.2s',
      '&:hover': { transform: 'translateY(-4px)', boxShadow: `0 12px 32px ${color}40` },
    }}
  >
    <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
      <Avatar sx={{ bgcolor: `${color}22`, color, width: 52, height: 52 }}>{icon}</Avatar>
      <Box>
        <Typography variant="h4" fontWeight={700} color={color}>
          {value}
        </Typography>
        <Typography variant="body2" color="text.secondary" fontWeight={500}>
          {label}
        </Typography>
      </Box>
    </CardContent>
  </Card>
);

// ── Main Page ─────────────────────────────────────────────────────────────────

export const FacilityManagementPage: React.FC = () => {
  const qc = useQueryClient();
  const [tab, setTab] = useState(0);

  // ── Dialogs ──
  const [addAssetOpen, setAddAssetOpen] = useState(false);
  const [addMaintOpen, setAddMaintOpen] = useState(false);
  const [sosTriggerOpen, setSosTriggerOpen] = useState(false);

  // ── Add Asset form ──
  const [assetForm, setAssetForm] = useState({
    hostelId: '',
    name: '',
    category: 'ELECTRICAL' as AssetCategory,
    location: '',
    status: 'OPERATIONAL' as AssetStatus,
  });

  // ── Add Maintenance form ──
  const [maintForm, setMaintForm] = useState({
    assetId: '',
    title: '',
    frequency: 'MONTHLY' as MaintenanceFrequency,
    assignedTechnician: '',
    nextDueDate: '',
  });

  // ── SOS form ──
  const [sosForm, setSosForm] = useState({
    hostelId: '',
    roomNumber: '',
    sosType: 'MEDICAL' as SosType,
    locationDetails: '',
  });

  // ── Queries ──
  const { data: stats } = useQuery({
    queryKey: ['facilityStats'],
    queryFn: facilityApi.getStats,
    refetchInterval: 30_000,
  });

  const { data: assets = [], isLoading: assetsLoading } = useQuery({
    queryKey: ['facilityAssets'],
    queryFn: () => facilityApi.getAssets(),
  });

  const { data: schedules = [], isLoading: schedLoading } = useQuery({
    queryKey: ['maintenanceSchedules'],
    queryFn: facilityApi.getMaintenanceSchedules,
  });

  const { data: sosAlerts = [], isLoading: sosLoading } = useQuery({
    queryKey: ['sosAlerts'],
    queryFn: facilityApi.getAllSosAlerts,
    refetchInterval: 15_000,
  });

  // ── Mutations ──
  const createAsset = useMutation({
    mutationFn: facilityApi.createAsset,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['facilityAssets'] }); qc.invalidateQueries({ queryKey: ['facilityStats'] }); setAddAssetOpen(false); },
  });

  const createMaint = useMutation({
    mutationFn: facilityApi.createMaintenanceSchedule,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['maintenanceSchedules'] }); setAddMaintOpen(false); },
  });

  const triggerSos = useMutation({
    mutationFn: facilityApi.triggerSos,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['sosAlerts'] }); qc.invalidateQueries({ queryKey: ['facilityStats'] }); setSosTriggerOpen(false); },
  });

  const updateSosStatus = useMutation({
    mutationFn: ({ id, status }: { id: string; status: SosAlertStatus }) =>
      facilityApi.updateSosStatus(id, { status }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['sosAlerts'] }); qc.invalidateQueries({ queryKey: ['facilityStats'] }); },
  });

  const updateAssetStatus = useMutation({
    mutationFn: ({ id, status }: { id: string; status: AssetStatus }) =>
      facilityApi.updateAssetStatus(id, status),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['facilityAssets'] }); qc.invalidateQueries({ queryKey: ['facilityStats'] }); },
  });

  const activeSosCount = sosAlerts.filter(
    (a) => a.status === 'TRIGGERED' || a.status === 'ACKNOWLEDGED'
  ).length;

  return (
    <Box sx={{ p: 3 }}>
      {/* ── Header ─────────────────────────────────────────────────────── */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h4" fontWeight={800} sx={{ letterSpacing: -0.5 }}>
            Facilities &amp; Emergency
          </Typography>
          <Typography variant="body2" color="text.secondary" mt={0.5}>
            Asset tracking, maintenance scheduling &amp; SOS alert management
          </Typography>
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<Plus size={16} />}
            onClick={() => setAddAssetOpen(true)}
          >
            Add Asset
          </Button>
          <Button
            variant="contained"
            color="error"
            startIcon={<Zap size={16} />}
            onClick={() => setSosTriggerOpen(true)}
            sx={{
              fontWeight: 700,
              animation: activeSosCount > 0 ? 'pulse 1.5s infinite' : 'none',
              '@keyframes pulse': {
                '0%': { boxShadow: '0 0 0 0 rgba(239,68,68,0.6)' },
                '70%': { boxShadow: '0 0 0 10px rgba(239,68,68,0)' },
                '100%': { boxShadow: '0 0 0 0 rgba(239,68,68,0)' },
              },
            }}
          >
            🚨 Trigger SOS
          </Button>
        </Box>
      </Box>

      {/* ── Active SOS Banner ─────────────────────────────────────────── */}
      {activeSosCount > 0 && (
        <Alert
          severity="error"
          icon={<AlertTriangle />}
          sx={{ mb: 3, fontWeight: 600, borderRadius: 2 }}
          action={
            <Chip label={`${activeSosCount} ACTIVE`} color="error" size="small" sx={{ fontWeight: 700 }} />
          }
        >
          HIGH URGENCY: {activeSosCount} active emergency SOS alert{activeSosCount > 1 ? 's' : ''} require immediate attention!
        </Alert>
      )}

      {/* ── Stats ─────────────────────────────────────────────────────── */}
      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            label="Total Assets"
            value={stats?.totalAssetsCount ?? '—'}
            icon={<Settings size={24} />}
            color="#6366f1"
            gradient="linear-gradient(135deg, #6366f110 0%, #818cf810 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            label="Operational"
            value={stats?.operationalAssetsCount ?? '—'}
            icon={<CheckCircle size={24} />}
            color="#10b981"
            gradient="linear-gradient(135deg, #10b98110 0%, #34d39910 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            label="Under Maintenance"
            value={stats?.underMaintenanceCount ?? '—'}
            icon={<Wrench size={24} />}
            color="#f59e0b"
            gradient="linear-gradient(135deg, #f59e0b10 0%, #fbbf2410 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            label="Active SOS"
            value={stats?.activeSosAlertsCount ?? '—'}
            icon={<Zap size={24} />}
            color="#ef4444"
            gradient="linear-gradient(135deg, #ef444410 0%, #f8717110 100%)"
          />
        </Grid>
        <Grid item xs={12} sm={6} md={2.4}>
          <StatCard
            label="Resolved SOS"
            value={stats?.resolvedSosAlertsCount ?? '—'}
            icon={<Shield size={24} />}
            color="#3b82f6"
            gradient="linear-gradient(135deg, #3b82f610 0%, #60a5fa10 100%)"
          />
        </Grid>
      </Grid>

      {/* ── Tabs ──────────────────────────────────────────────────────── */}
      <Card sx={{ borderRadius: 3 }}>
        <Box sx={{ borderBottom: 1, borderColor: 'divider', px: 2 }}>
          <Tabs value={tab} onChange={(_, v) => setTab(v)}>
            <Tab
              icon={<Activity size={16} />}
              iconPosition="start"
              label={`SOS Alerts ${activeSosCount > 0 ? `(${activeSosCount} active)` : ''}`}
              sx={{ fontWeight: 600 }}
            />
            <Tab icon={<Settings size={16} />} iconPosition="start" label="Facility Assets" sx={{ fontWeight: 600 }} />
            <Tab icon={<Wrench size={16} />} iconPosition="start" label="Maintenance" sx={{ fontWeight: 600 }} />
          </Tabs>
        </Box>

        {/* ── SOS Alerts Tab ─────────────────────────────────────────── */}
        {tab === 0 && (
          <Box sx={{ p: 2 }}>
            {sosLoading && <LinearProgress sx={{ mb: 2 }} />}
            <TableContainer component={Paper} variant="outlined" sx={{ borderRadius: 2 }}>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ '& th': { fontWeight: 700, bgcolor: 'action.hover' } }}>
                    <TableCell>Type</TableCell>
                    <TableCell>Location</TableCell>
                    <TableCell>Room</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Triggered At</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {sosAlerts.length === 0 && !sosLoading && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                        No SOS alerts recorded
                      </TableCell>
                    </TableRow>
                  )}
                  {[...sosAlerts]
                    .sort((a, b) => new Date(b.triggeredAt).getTime() - new Date(a.triggeredAt).getTime())
                    .map((alert) => (
                      <TableRow
                        key={alert.id}
                        sx={{
                          bgcolor:
                            alert.status === 'TRIGGERED' ? 'rgba(239,68,68,0.05)'
                            : alert.status === 'ACKNOWLEDGED' ? 'rgba(245,158,11,0.05)'
                            : 'inherit',
                        }}
                      >
                        <TableCell>
                          <Chip
                            label={alert.sosType}
                            size="small"
                            variant="outlined"
                            color={alert.sosType === 'MEDICAL' ? 'error' : alert.sosType === 'FIRE' ? 'warning' : 'default'}
                          />
                        </TableCell>
                        <TableCell sx={{ maxWidth: 200 }}>
                          <Typography variant="body2" noWrap>{alert.locationDetails}</Typography>
                        </TableCell>
                        <TableCell>{alert.roomNumber || '—'}</TableCell>
                        <TableCell>
                          <Chip
                            label={alert.status}
                            size="small"
                            color={SOS_STATUS_COLOR[alert.status]}
                          />
                        </TableCell>
                        <TableCell>
                          <Typography variant="caption" color="text.secondary">
                            {new Date(alert.triggeredAt).toLocaleString()}
                          </Typography>
                        </TableCell>
                        <TableCell align="center">
                          <Box sx={{ display: 'flex', gap: 0.5, justifyContent: 'center' }}>
                            {(SOS_NEXT_STATUS[alert.status] ?? []).map((next) => (
                              <Tooltip key={next} title={`Mark as ${next}`}>
                                <IconButton
                                  size="small"
                                  onClick={() => updateSosStatus.mutate({ id: alert.id, status: next })}
                                  disabled={updateSosStatus.isPending}
                                  color={next === 'RESOLVED' ? 'success' : next === 'FALSE_ALARM' ? 'default' : 'warning'}
                                >
                                  {next === 'RESOLVED' ? <CheckCircle size={16} />
                                   : next === 'FALSE_ALARM' ? <XCircle size={16} />
                                   : <Activity size={16} />}
                                </IconButton>
                              </Tooltip>
                            ))}
                          </Box>
                        </TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}

        {/* ── Assets Tab ─────────────────────────────────────────────── */}
        {tab === 1 && (
          <Box sx={{ p: 2 }}>
            {assetsLoading && <LinearProgress sx={{ mb: 2 }} />}
            <TableContainer component={Paper} variant="outlined" sx={{ borderRadius: 2 }}>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ '& th': { fontWeight: 700, bgcolor: 'action.hover' } }}>
                    <TableCell>Name</TableCell>
                    <TableCell>Category</TableCell>
                    <TableCell>Location</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Last Inspected</TableCell>
                    <TableCell align="center">Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {assets.length === 0 && !assetsLoading && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                        No assets registered. Click "Add Asset" to get started.
                      </TableCell>
                    </TableRow>
                  )}
                  {assets.map((asset) => (
                    <TableRow key={asset.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{asset.name}</TableCell>
                      <TableCell>
                        <Chip label={asset.category} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell>{asset.location}</TableCell>
                      <TableCell>
                        <Chip
                          label={asset.status}
                          size="small"
                          color={ASSET_STATUS_COLOR[asset.status]}
                        />
                      </TableCell>
                      <TableCell>
                        <Typography variant="caption" color="text.secondary">
                          {new Date(asset.lastInspectedAt).toLocaleDateString()}
                        </Typography>
                      </TableCell>
                      <TableCell align="center">
                        <FormControl size="small" sx={{ minWidth: 150 }}>
                          <Select
                            value={asset.status}
                            onChange={(e) =>
                              updateAssetStatus.mutate({ id: asset.id, status: e.target.value as AssetStatus })
                            }
                            size="small"
                          >
                            {(['OPERATIONAL', 'UNDER_MAINTENANCE', 'OUT_OF_SERVICE'] as AssetStatus[]).map((s) => (
                              <MenuItem key={s} value={s}>{s}</MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}

        {/* ── Maintenance Tab ────────────────────────────────────────── */}
        {tab === 2 && (
          <Box sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 2 }}>
              <Button
                variant="outlined"
                startIcon={<Plus size={16} />}
                onClick={() => setAddMaintOpen(true)}
              >
                Schedule Maintenance
              </Button>
            </Box>
            {schedLoading && <LinearProgress sx={{ mb: 2 }} />}
            <TableContainer component={Paper} variant="outlined" sx={{ borderRadius: 2 }}>
              <Table size="small">
                <TableHead>
                  <TableRow sx={{ '& th': { fontWeight: 700, bgcolor: 'action.hover' } }}>
                    <TableCell>Title</TableCell>
                    <TableCell>Asset</TableCell>
                    <TableCell>Frequency</TableCell>
                    <TableCell>Technician</TableCell>
                    <TableCell>Due Date</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {schedules.length === 0 && !schedLoading && (
                    <TableRow>
                      <TableCell colSpan={6} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                        No maintenance schedules. Use "Schedule Maintenance" to add one.
                      </TableCell>
                    </TableRow>
                  )}
                  {schedules.map((s) => (
                    <TableRow key={s.id}>
                      <TableCell sx={{ fontWeight: 600 }}>{s.title}</TableCell>
                      <TableCell>{s.assetName}</TableCell>
                      <TableCell>
                        <Chip label={s.frequency} size="small" variant="outlined" />
                      </TableCell>
                      <TableCell>{s.assignedTechnician || '—'}</TableCell>
                      <TableCell>
                        <Typography
                          variant="body2"
                          color={new Date(s.nextDueDate) < new Date() ? 'error' : 'inherit'}
                          fontWeight={new Date(s.nextDueDate) < new Date() ? 700 : 400}
                        >
                          {new Date(s.nextDueDate).toLocaleDateString()}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={s.status}
                          size="small"
                          color={
                            s.status === 'COMPLETED' ? 'success'
                            : s.status === 'OVERDUE' ? 'error'
                            : s.status === 'IN_PROGRESS' ? 'warning'
                            : 'default'
                          }
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>
        )}
      </Card>

      {/* ── Add Asset Dialog ──────────────────────────────────────────── */}
      <Dialog open={addAssetOpen} onClose={() => setAddAssetOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Register New Facility Asset</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          {createAsset.error && (
            <Alert severity="error">Failed to create asset. Please try again.</Alert>
          )}
          <TextField
            label="Asset Name"
            fullWidth
            value={assetForm.name}
            onChange={(e) => setAssetForm({ ...assetForm, name: e.target.value })}
          />
          <FormControl fullWidth>
            <InputLabel>Category</InputLabel>
            <Select
              label="Category"
              value={assetForm.category}
              onChange={(e) => setAssetForm({ ...assetForm, category: e.target.value as AssetCategory })}
            >
              {(['WASHROOM','ELEVATOR','GYM','STUDY_ROOM','WATER_COOLER','FIRE_EXTINGUISHER','GENERATOR','SECURITY_CAMERA','OTHER'] as AssetCategory[]).map(c => (
                <MenuItem key={c} value={c}>{c}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Location"
            fullWidth
            value={assetForm.location}
            onChange={(e) => setAssetForm({ ...assetForm, location: e.target.value })}
          />
          <TextField
            label="Hostel ID (UUID)"
            fullWidth
            value={assetForm.hostelId}
            onChange={(e) => setAssetForm({ ...assetForm, hostelId: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setAddAssetOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={() => createAsset.mutate(assetForm)}
            disabled={createAsset.isPending || !assetForm.name || !assetForm.location}
            startIcon={createAsset.isPending ? <CircularProgress size={16} /> : <Plus size={16} />}
          >
            Register Asset
          </Button>
        </DialogActions>
      </Dialog>

      {/* ── Schedule Maintenance Dialog ───────────────────────────────── */}
      <Dialog open={addMaintOpen} onClose={() => setAddMaintOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Schedule Maintenance</DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          {createMaint.error && (
            <Alert severity="error">Failed to create schedule. Please try again.</Alert>
          )}
          <FormControl fullWidth>
            <InputLabel>Asset</InputLabel>
            <Select
              label="Asset"
              value={maintForm.assetId}
              onChange={(e) => setMaintForm({ ...maintForm, assetId: e.target.value })}
            >
              {assets.map((a) => (
                <MenuItem key={a.id} value={a.id}>{a.name} — {a.location}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Title"
            fullWidth
            value={maintForm.title}
            onChange={(e) => setMaintForm({ ...maintForm, title: e.target.value })}
          />
          <FormControl fullWidth>
            <InputLabel>Frequency</InputLabel>
            <Select
              label="Frequency"
              value={maintForm.frequency}
              onChange={(e) => setMaintForm({ ...maintForm, frequency: e.target.value as MaintenanceFrequency })}
            >
              {(['DAILY','WEEKLY','MONTHLY','QUARTERLY','YEARLY'] as MaintenanceFrequency[]).map(f => (
                <MenuItem key={f} value={f}>{f}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Assigned Technician"
            fullWidth
            value={maintForm.assignedTechnician}
            onChange={(e) => setMaintForm({ ...maintForm, assignedTechnician: e.target.value })}
          />
          <TextField
            label="Next Due Date"
            type="date"
            fullWidth
            InputLabelProps={{ shrink: true }}
            value={maintForm.nextDueDate}
            onChange={(e) => setMaintForm({ ...maintForm, nextDueDate: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setAddMaintOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            onClick={() => createMaint.mutate(maintForm)}
            disabled={createMaint.isPending || !maintForm.assetId || !maintForm.title || !maintForm.nextDueDate}
            startIcon={createMaint.isPending ? <CircularProgress size={16} /> : <Wrench size={16} />}
          >
            Schedule
          </Button>
        </DialogActions>
      </Dialog>

      {/* ── Trigger SOS Dialog ────────────────────────────────────────── */}
      <Dialog open={sosTriggerOpen} onClose={() => setSosTriggerOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ fontWeight: 700, color: 'error.main', display: 'flex', alignItems: 'center', gap: 1 }}>
          <Zap size={20} /> 🚨 Trigger Emergency SOS
        </DialogTitle>
        <DialogContent sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 2 }}>
          <Alert severity="error" sx={{ fontWeight: 600 }}>
            This will immediately notify wardens and emergency responders.
          </Alert>
          {triggerSos.error && (
            <Alert severity="error">Failed to trigger SOS. Please call emergency services directly.</Alert>
          )}
          <FormControl fullWidth>
            <InputLabel>Emergency Type</InputLabel>
            <Select
              label="Emergency Type"
              value={sosForm.sosType}
              onChange={(e) => setSosForm({ ...sosForm, sosType: e.target.value as SosType })}
            >
              {(['MEDICAL','FIRE','SECURITY','NATURAL_DISASTER','OTHER'] as SosType[]).map(t => (
                <MenuItem key={t} value={t}>{t}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <TextField
            label="Location Details"
            fullWidth
            required
            placeholder="e.g. Girls Hostel Block-B, Room 204, near washroom"
            value={sosForm.locationDetails}
            onChange={(e) => setSosForm({ ...sosForm, locationDetails: e.target.value })}
          />
          <TextField
            label="Room Number (optional)"
            fullWidth
            value={sosForm.roomNumber}
            onChange={(e) => setSosForm({ ...sosForm, roomNumber: e.target.value })}
          />
        </DialogContent>
        <DialogActions sx={{ px: 3, pb: 2 }}>
          <Button onClick={() => setSosTriggerOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            color="error"
            onClick={() => triggerSos.mutate(sosForm)}
            disabled={triggerSos.isPending || !sosForm.locationDetails}
            startIcon={triggerSos.isPending ? <CircularProgress size={16} color="inherit" /> : <Zap size={16} />}
            sx={{ fontWeight: 700 }}
          >
            SEND SOS ALERT
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
