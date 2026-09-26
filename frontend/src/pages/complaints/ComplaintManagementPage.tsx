import React, { useState } from 'react';
import {
  Box, Typography, Grid, Card, CardContent, Chip, Table, TableBody,
  TableCell, TableContainer, TableHead, TableRow, Paper, Button,
  Dialog, DialogTitle, DialogContent, DialogActions, TextField,
  Select, MenuItem, FormControl, InputLabel, IconButton, Tooltip,
  CircularProgress, Alert, LinearProgress, Skeleton,
} from '@mui/material';
import {
  Add as AddIcon,
  FilterList as FilterIcon,
  Refresh as RefreshIcon,
  Timeline as TimelineIcon,
  Assignment as AssignIcon,
  CheckCircle as CheckIcon,
  Warning as WarningIcon,
  HourglassEmpty as PendingIcon,
  Close as CloseIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { complaintApi } from '../../api/complaintApi';
import { hostelApi } from '../../api/hostelApi';
import type { HostelDto } from '../../types/hostel';
import type {
  ComplaintDto,
  ComplaintCategory,
  ComplaintPriority,
  ComplaintStatus,
  ComplaintStatusHistoryDto,
} from '../../types/complaint';

// ─── Constants ────────────────────────────────────────────────────────────────

const CATEGORIES: ComplaintCategory[] = [
  'ELECTRICAL', 'PLUMBING', 'FURNITURE', 'HOUSEKEEPING',
  'INTERNET_CONNECTIVITY', 'SECURITY', 'FOOD_QUALITY', 'NOISE',
  'PEST_CONTROL', 'AC_COOLING', 'WATER_SUPPLY', 'LAUNDRY',
  'MEDICAL', 'ADMINISTRATION', 'OTHER',
];

const PRIORITIES: ComplaintPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'URGENT', 'CRITICAL'];

const STATUS_COLORS: Record<ComplaintStatus, string> = {
  NEW: '#6366f1',
  CLASSIFIED: '#8b5cf6',
  PRIORITIZED: '#f59e0b',
  ASSIGNED: '#3b82f6',
  IN_PROGRESS: '#06b6d4',
  WAITING_FOR_STUDENT: '#f97316',
  RESOLVED: '#22c55e',
  VERIFIED: '#10b981',
  CLOSED: '#6b7280',
  REOPENED: '#ef4444',
};

const PRIORITY_COLORS: Record<ComplaintPriority, string> = {
  LOW: '#6b7280',
  MEDIUM: '#3b82f6',
  HIGH: '#f59e0b',
  URGENT: '#ef4444',
  CRITICAL: '#dc2626',
};

// ─── Status Badge ─────────────────────────────────────────────────────────────

const StatusBadge: React.FC<{ status: ComplaintStatus }> = ({ status }) => (
  <Chip
    label={status.replace(/_/g, ' ')}
    size="small"
    sx={{
      bgcolor: STATUS_COLORS[status] + '22',
      color: STATUS_COLORS[status],
      fontWeight: 700,
      fontSize: '0.7rem',
      border: `1px solid ${STATUS_COLORS[status]}44`,
    }}
  />
);

const PriorityBadge: React.FC<{ priority: ComplaintPriority }> = ({ priority }) => (
  <Chip
    label={priority}
    size="small"
    sx={{
      bgcolor: PRIORITY_COLORS[priority] + '20',
      color: PRIORITY_COLORS[priority],
      fontWeight: 600,
      fontSize: '0.7rem',
    }}
  />
);

// ─── Stats Card ───────────────────────────────────────────────────────────────

const StatCard: React.FC<{
  label: string; value: number; color: string; icon: React.ReactNode;
}> = ({ label, value, color, icon }) => (
  <Card
    sx={{
      background: `linear-gradient(135deg, ${color}15 0%, ${color}05 100%)`,
      border: `1px solid ${color}30`,
      borderRadius: 3,
      position: 'relative',
      overflow: 'hidden',
      '&::before': {
        content: '""',
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        height: 3,
        bgcolor: color,
      },
    }}
  >
    <CardContent>
      <Box display="flex" alignItems="center" justifyContent="space-between">
        <Box>
          <Typography variant="h3" sx={{ fontWeight: 800, color }}>
            {value}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5, fontWeight: 500 }}>
            {label}
          </Typography>
        </Box>
        <Box
          sx={{
            width: 52,
            height: 52,
            borderRadius: 2,
            bgcolor: color + '20',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color,
          }}
        >
          {icon}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

// ─── Status History Timeline ───────────────────────────────────────────────────

const StatusHistoryModal: React.FC<{
  open: boolean;
  complaintId: string | null;
  onClose: () => void;
}> = ({ open, complaintId, onClose }) => {
  const { data: history, isLoading } = useQuery({
    queryKey: ['complaint-history', complaintId],
    queryFn: () => complaintApi.getHistory(complaintId!),
    enabled: !!complaintId && open,
  });

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
        <TimelineIcon color="primary" />
        Status History
        <IconButton onClick={onClose} sx={{ ml: 'auto' }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        {isLoading && <CircularProgress />}
        {history && history.length === 0 && (
          <Typography color="text.secondary" textAlign="center" py={3}>
            No history found
          </Typography>
        )}
        {history?.map((entry: ComplaintStatusHistoryDto, idx: number) => (
          <Box key={entry.id} display="flex" gap={2} mb={2}>
            <Box display="flex" flexDirection="column" alignItems="center">
              <Box
                sx={{
                  width: 32,
                  height: 32,
                  borderRadius: '50%',
                  bgcolor: STATUS_COLORS[entry.toStatus] + '30',
                  border: `2px solid ${STATUS_COLORS[entry.toStatus]}`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '0.65rem',
                  fontWeight: 700,
                  color: STATUS_COLORS[entry.toStatus],
                }}
              >
                {idx + 1}
              </Box>
              {idx < history.length - 1 && (
                <Box sx={{ width: 2, flex: 1, bgcolor: 'divider', mt: 0.5, mb: 0.5 }} />
              )}
            </Box>
            <Box flex={1} pb={1}>
              <Box display="flex" gap={1} alignItems="center" flexWrap="wrap">
                {entry.fromStatus && <StatusBadge status={entry.fromStatus} />}
                {entry.fromStatus && <Typography variant="caption">→</Typography>}
                <StatusBadge status={entry.toStatus} />
              </Box>
              {entry.note && (
                <Typography variant="body2" sx={{ mt: 0.5, color: 'text.secondary', fontStyle: 'italic' }}>
                  "{entry.note}"
                </Typography>
              )}
              <Typography variant="caption" color="text.disabled">
                {entry.changedByUserName ?? 'System'} •{' '}
                {new Date(entry.changedAt).toLocaleString()}
              </Typography>
            </Box>
          </Box>
        ))}
      </DialogContent>
    </Dialog>
  );
};

// ─── Create Complaint Modal ────────────────────────────────────────────────────

const CreateComplaintModal: React.FC<{
  open: boolean;
  onClose: () => void;
}> = ({ open, onClose }) => {
  const queryClient = useQueryClient();
  const [selectedHostelId, setSelectedHostelId] = useState('22222222-2222-2222-2222-222222222222');
  const [form, setForm] = useState({
    title: '',
    description: '',
    category: '' as ComplaintCategory | '',
    priority: 'MEDIUM' as ComplaintPriority,
  });
  const [error, setError] = useState('');

  const { data: hostels } = useQuery({
    queryKey: ['hostels'],
    queryFn: hostelApi.getHostels,
  });

  const mutation = useMutation({
    mutationFn: () =>
      complaintApi.createComplaint({
        hostelId: selectedHostelId,
        title: form.title,
        description: form.description,
        category: form.category as ComplaintCategory,
        priority: form.priority,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-complaints'] });
      queryClient.invalidateQueries({ queryKey: ['complaint-stats'] });
      onClose();
      setForm({ title: '', description: '', category: '', priority: 'MEDIUM' });
      setError('');
    },
    onError: (err: any) => {
      setError(err.response?.data?.message ?? 'Failed to submit complaint');
    },
  });

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ fontWeight: 700 }}>
        <Box display="flex" alignItems="center" gap={1}>
          <AddIcon color="primary" />
          Submit New Complaint
        </Box>
      </DialogTitle>
      <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, pt: 2.5 }}>
        {error && <Alert severity="error" onClose={() => setError('')}>{error}</Alert>}

        <FormControl fullWidth required>
          <InputLabel>Select Hostel Block</InputLabel>
          <Select
            label="Select Hostel Block"
            value={selectedHostelId}
            onChange={(e) => setSelectedHostelId(e.target.value)}
          >
            {hostels && hostels.length > 0 ? (
              hostels.map((h: HostelDto) => (
                <MenuItem key={h.id} value={h.id}>
                  {h.name} ({h.genderType})
                </MenuItem>
              ))
            ) : (
              <MenuItem value="22222222-2222-2222-2222-222222222222">
                Alpha Resident Hostel (COED)
              </MenuItem>
            )}
          </Select>
        </FormControl>

        <TextField
          label="Title"
          fullWidth
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          inputProps={{ maxLength: 255 }}
          required
        />

        <FormControl fullWidth required>
          <InputLabel>Category</InputLabel>
          <Select
            label="Category"
            value={form.category}
            onChange={(e) => setForm({ ...form, category: e.target.value as ComplaintCategory })}
          >
            {CATEGORIES.map((c) => (
              <MenuItem key={c} value={c}>{c.replace(/_/g, ' ')}</MenuItem>
            ))}
          </Select>
        </FormControl>

        <FormControl fullWidth>
          <InputLabel>Priority</InputLabel>
          <Select
            label="Priority"
            value={form.priority}
            onChange={(e) => setForm({ ...form, priority: e.target.value as ComplaintPriority })}
          >
            {PRIORITIES.map((p) => (
              <MenuItem key={p} value={p}>
                <Box display="flex" alignItems="center" gap={1}>
                  <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: PRIORITY_COLORS[p] }} />
                  {p}
                </Box>
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Description"
          fullWidth
          multiline
          rows={4}
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
          required
          helperText="Describe the issue in detail"
        />
      </DialogContent>
      <DialogActions sx={{ p: 2.5 }}>
        <Button onClick={onClose} color="inherit">Cancel</Button>
        <Button
          variant="contained"
          disabled={!form.title || !form.category || !form.description || mutation.isPending}
          onClick={() => mutation.mutate()}
          startIcon={mutation.isPending ? <CircularProgress size={16} /> : <AddIcon />}
        >
          Submit Complaint
        </Button>
      </DialogActions>
    </Dialog>
  );
};

// ─── Update Status Modal ───────────────────────────────────────────────────────

const VALID_TRANSITIONS: Record<ComplaintStatus, ComplaintStatus[]> = {
  NEW: ['CLASSIFIED'],
  CLASSIFIED: ['PRIORITIZED'],
  PRIORITIZED: ['ASSIGNED'],
  ASSIGNED: ['IN_PROGRESS'],
  IN_PROGRESS: ['WAITING_FOR_STUDENT', 'RESOLVED'],
  WAITING_FOR_STUDENT: ['IN_PROGRESS', 'RESOLVED'],
  RESOLVED: ['VERIFIED', 'REOPENED'],
  VERIFIED: ['CLOSED', 'REOPENED'],
  CLOSED: ['REOPENED'],
  REOPENED: ['CLASSIFIED'],
};

const UpdateStatusModal: React.FC<{
  open: boolean;
  complaint: ComplaintDto | null;
  onClose: () => void;
}> = ({ open, complaint, onClose }) => {
  const queryClient = useQueryClient();
  const [targetStatus, setTargetStatus] = useState<ComplaintStatus | ''>('');
  const [note, setNote] = useState('');
  const [error, setError] = useState('');

  const allowedNext = complaint ? VALID_TRANSITIONS[complaint.status] : [];

  const mutation = useMutation({
    mutationFn: () =>
      complaintApi.updateStatus(complaint!.id, {
        status: targetStatus as ComplaintStatus,
        note: note || undefined,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-complaints'] });
      queryClient.invalidateQueries({ queryKey: ['complaint-stats'] });
      queryClient.invalidateQueries({ queryKey: ['complaint-history', complaint?.id] });
      onClose();
      setTargetStatus('');
      setNote('');
      setError('');
    },
    onError: (err: any) => {
      setError(err.response?.data?.message ?? 'Transition failed');
    },
  });

  if (!complaint) return null;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle sx={{ fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1 }}>
        <AssignIcon color="primary" />
        Update Complaint Status
      </DialogTitle>
      <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, pt: 2.5 }}>
        {error && <Alert severity="error" onClose={() => setError('')}>{error}</Alert>}

        <Box>
          <Typography variant="caption" color="text.secondary">Current Status</Typography>
          <Box mt={0.5}>
            <StatusBadge status={complaint.status} />
          </Box>
        </Box>

        <FormControl fullWidth required>
          <InputLabel>Transition To</InputLabel>
          <Select
            label="Transition To"
            value={targetStatus}
            onChange={(e) => setTargetStatus(e.target.value as ComplaintStatus)}
          >
            {allowedNext.map((s) => (
              <MenuItem key={s} value={s}>
                <Box display="flex" alignItems="center" gap={1}>
                  <Box sx={{ width: 10, height: 10, borderRadius: '50%', bgcolor: STATUS_COLORS[s] }} />
                  {s.replace(/_/g, ' ')}
                </Box>
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Note (optional)"
          multiline
          rows={2}
          fullWidth
          value={note}
          onChange={(e) => setNote(e.target.value)}
          helperText="Add a comment on this status change"
        />
      </DialogContent>
      <DialogActions sx={{ p: 2.5 }}>
        <Button onClick={onClose} color="inherit">Cancel</Button>
        <Button
          variant="contained"
          disabled={!targetStatus || mutation.isPending}
          onClick={() => mutation.mutate()}
          startIcon={mutation.isPending ? <CircularProgress size={16} /> : <CheckIcon />}
        >
          Update Status
        </Button>
      </DialogActions>
    </Dialog>
  );
};

// ─── Main Page ────────────────────────────────────────────────────────────────

export const ComplaintManagementPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [filterStatus, setFilterStatus] = useState<ComplaintStatus | 'ALL'>('ALL');
  const [createOpen, setCreateOpen] = useState(false);
  const [historyComplaintId, setHistoryComplaintId] = useState<string | null>(null);
  const [updateComplaint, setUpdateComplaint] = useState<ComplaintDto | null>(null);

  const {
    data: complaints,
    isLoading,
    isError,
    refetch,
  } = useQuery({
    queryKey: ['my-complaints'],
    queryFn: complaintApi.getMyComplaints,
  });

  const { data: stats } = useQuery({
    queryKey: ['complaint-stats'],
    queryFn: complaintApi.getStats,
  });

  const deleteMutation = useMutation({
    mutationFn: complaintApi.deleteComplaint,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['my-complaints'] });
      queryClient.invalidateQueries({ queryKey: ['complaint-stats'] });
    },
  });

  const displayed = complaints
    ? filterStatus === 'ALL'
      ? complaints
      : complaints.filter((c: ComplaintDto) => c.status === filterStatus)
    : [];


  return (
    <Box>
      {/* ── Header ── */}
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
            Complaint Management
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Track, manage, and resolve hostel complaints
          </Typography>
        </Box>
        <Box display="flex" gap={1}>
          <Tooltip title="Refresh">
            <IconButton onClick={() => refetch()} size="small">
              <RefreshIcon />
            </IconButton>
          </Tooltip>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setCreateOpen(true)}
            sx={{ fontWeight: 700 }}
            id="submit-complaint-btn"
          >
            Submit Complaint
          </Button>
        </Box>
      </Box>

      {/* ── Stats Cards ── */}
      <Grid container spacing={2} mb={3}>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="Total"
            value={stats?.totalComplaints ?? 0}
            color="#6366f1"
            icon={<AssignIcon />}
          />
        </Grid>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="New"
            value={stats?.newComplaints ?? 0}
            color="#8b5cf6"
            icon={<AddIcon />}
          />
        </Grid>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="In Progress"
            value={stats?.inProgressComplaints ?? 0}
            color="#06b6d4"
            icon={<PendingIcon />}
          />
        </Grid>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="Resolved"
            value={stats?.resolvedComplaints ?? 0}
            color="#22c55e"
            icon={<CheckIcon />}
          />
        </Grid>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="Closed"
            value={stats?.closedComplaints ?? 0}
            color="#6b7280"
            icon={<CloseIcon />}
          />
        </Grid>
        <Grid item xs={6} sm={4} md={2}>
          <StatCard
            label="Overdue"
            value={stats?.overdueComplaints ?? 0}
            color="#ef4444"
            icon={<WarningIcon />}
          />
        </Grid>
      </Grid>

      {/* ── Filter Bar ── */}
      <Card sx={{ mb: 2, borderRadius: 2 }}>
        <CardContent sx={{ py: 1.5, '&:last-child': { pb: 1.5 } }}>
          <Box display="flex" alignItems="center" gap={1} flexWrap="wrap">
            <FilterIcon color="action" fontSize="small" />
            <Typography variant="body2" fontWeight={600} mr={1}>Filter:</Typography>
            {(['ALL', 'NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED', 'REOPENED'] as const).map((s) => (
              <Chip
                key={s}
                label={s.replace(/_/g, ' ')}
                size="small"
                onClick={() => setFilterStatus(s)}
                variant={filterStatus === s ? 'filled' : 'outlined'}
                sx={
                  filterStatus === s && s !== 'ALL'
                    ? { bgcolor: STATUS_COLORS[s as ComplaintStatus] + '22', color: STATUS_COLORS[s as ComplaintStatus], fontWeight: 700 }
                    : {}
                }
              />
            ))}
            <Typography variant="caption" color="text.secondary" sx={{ ml: 'auto' }}>
              {displayed.length} complaint{displayed.length !== 1 ? 's' : ''}
            </Typography>
          </Box>
        </CardContent>
      </Card>

      {/* ── Complaints Table ── */}
      {isError && (
        <Alert severity="error" sx={{ mb: 2 }}>
          Failed to load complaints. Please refresh.
        </Alert>
      )}

      <TableContainer component={Paper} sx={{ borderRadius: 3, overflow: 'hidden' }}>
        {isLoading && <LinearProgress />}
        <Table>
          <TableHead>
            <TableRow sx={{ bgcolor: 'action.hover' }}>
              {['Title', 'Category', 'Priority', 'Status', 'Submitted', 'SLA', 'Actions'].map(
                (h) => (
                  <TableCell key={h} sx={{ fontWeight: 700, fontSize: '0.8rem' }}>
                    {h}
                  </TableCell>
                )
              )}
            </TableRow>
          </TableHead>
          <TableBody>
            {isLoading &&
              Array.from({ length: 4 }).map((_, i) => (
                <TableRow key={i}>
                  {Array.from({ length: 7 }).map((__, j) => (
                    <TableCell key={j}>
                      <Skeleton variant="text" />
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            {!isLoading && displayed.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 6 }}>
                  <Box>
                    <AssignIcon sx={{ fontSize: 48, color: 'text.disabled', mb: 1 }} />
                    <Typography color="text.secondary">No complaints found</Typography>
                    <Button
                      sx={{ mt: 1 }}
                      startIcon={<AddIcon />}
                      onClick={() => setCreateOpen(true)}
                    >
                      Submit your first complaint
                    </Button>
                  </Box>
                </TableCell>
              </TableRow>
            )}
            {displayed.map((complaint: ComplaintDto) => (
              <TableRow
                key={complaint.id}
                hover
                sx={{ '&:last-child td': { border: 0 } }}
              >
                <TableCell>
                  <Typography variant="body2" fontWeight={600} noWrap sx={{ maxWidth: 200 }}>
                    {complaint.title}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    Reopened: {complaint.reopenCount}×
                  </Typography>
                </TableCell>
                <TableCell>
                  <Typography variant="caption" sx={{ fontWeight: 500 }}>
                    {complaint.category.replace(/_/g, ' ')}
                  </Typography>
                </TableCell>
                <TableCell>
                  <PriorityBadge priority={complaint.priority} />
                </TableCell>
                <TableCell>
                  <StatusBadge status={complaint.status} />
                </TableCell>
                <TableCell>
                  <Typography variant="caption">
                    {new Date(complaint.createdAt).toLocaleDateString()}
                  </Typography>
                </TableCell>
                <TableCell>
                  {complaint.slaDeadline ? (
                    <Typography
                      variant="caption"
                      color={
                        new Date(complaint.slaDeadline) < new Date() ? 'error' : 'text.secondary'
                      }
                    >
                      {new Date(complaint.slaDeadline).toLocaleDateString()}
                    </Typography>
                  ) : (
                    <Typography variant="caption" color="text.disabled">—</Typography>
                  )}
                </TableCell>
                <TableCell>
                  <Box display="flex" gap={0.5}>
                    <Tooltip title="View History">
                      <IconButton
                        size="small"
                        id={`history-btn-${complaint.id}`}
                        onClick={() => setHistoryComplaintId(complaint.id)}
                      >
                        <TimelineIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="Update Status">
                      <IconButton
                        size="small"
                        id={`update-status-btn-${complaint.id}`}
                        onClick={() => setUpdateComplaint(complaint)}
                        color="primary"
                      >
                        <AssignIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    {(complaint.status === 'NEW' || complaint.status === 'CLASSIFIED') && (
                      <Tooltip title="Delete">
                        <IconButton
                          size="small"
                          id={`delete-btn-${complaint.id}`}
                          color="error"
                          onClick={() => {
                            if (confirm('Delete this complaint?'))
                              deleteMutation.mutate(complaint.id);
                          }}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* ── Modals ── */}
      <CreateComplaintModal
        open={createOpen}
        onClose={() => setCreateOpen(false)}
      />

      <StatusHistoryModal
        open={!!historyComplaintId}
        complaintId={historyComplaintId}
        onClose={() => setHistoryComplaintId(null)}
      />

      <UpdateStatusModal
        open={!!updateComplaint}
        complaint={updateComplaint}
        onClose={() => setUpdateComplaint(null)}
      />
    </Box>
  );
};
