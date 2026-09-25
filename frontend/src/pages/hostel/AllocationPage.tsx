import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Button,
  Grid2 as Grid,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  CircularProgress,
  Alert,
  LinearProgress,
} from '@mui/material';
import { UserCheck, ArrowRightLeft, LogOut as VacateIcon, Plus } from 'lucide-react';
import { hostelApi } from '../../api/hostelApi';
import { OccupancyStatsDto, RoomAllocationDto, RoomDto } from '../../types/hostel';
import { StatusBadge } from '../../components/common/StatusBadge';

export const AllocationPage: React.FC = () => {
  const [stats, setStats] = useState<OccupancyStatsDto | null>(null);
  const [allocations, setAllocations] = useState<RoomAllocationDto[]>([]);
  const [rooms, setRooms] = useState<RoomDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Dialogs
  const [openAllocateModal, setOpenAllocateModal] = useState(false);
  const [openTransferModal, setOpenTransferModal] = useState(false);
  const [openVacateModal, setOpenVacateModal] = useState(false);

  // Active target for transfer / vacate
  const [selectedAllocation, setSelectedAllocation] = useState<RoomAllocationDto | null>(null);

  // Form Fields
  const [studentId, setStudentId] = useState('');
  const [selectedBedId, setSelectedBedId] = useState('');
  const [academicYear, setAcademicYear] = useState('2026-2027');
  const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0]);

  const [newBedId, setNewBedId] = useState('');
  const [reason, setReason] = useState('');

  const fetchData = async () => {
    setLoading(true);
    try {
      const [statsData, allocData, roomData] = await Promise.all([
        hostelApi.getStats(),
        hostelApi.getAllocations(),
        hostelApi.getAllRooms(),
      ]);
      setStats(statsData);
      setAllocations(allocData);
      setRooms(roomData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load allocation data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleAllocate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      await hostelApi.allocateBed({
        studentId,
        bedId: selectedBedId,
        academicYear,
        startDate,
      });
      setOpenAllocateModal(false);
      setStudentId('');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Allocation failed');
    }
  };

  const handleTransfer = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAllocation) return;
    setError(null);
    try {
      await hostelApi.transferBed({
        allocationId: selectedAllocation.id,
        newBedId,
        reason,
      });
      setOpenTransferModal(false);
      setSelectedAllocation(null);
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Transfer failed');
    }
  };

  const handleVacate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedAllocation) return;
    setError(null);
    try {
      await hostelApi.vacateBed({
        allocationId: selectedAllocation.id,
        reason,
      });
      setOpenVacateModal(false);
      setSelectedAllocation(null);
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Vacate operation failed');
    }
  };

  // Extract vacant beds from loaded rooms
  const vacantBeds = rooms.flatMap(r => r.beds || []).filter(b => b.status === 'VACANT');

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>
            Room Allocation Engine
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Assign, transfer, and vacate student hostel accommodations with capacity enforcement
          </Typography>
        </Box>
        <Button
          variant="contained"
          color="primary"
          startIcon={<Plus size={18} />}
          onClick={() => setOpenAllocateModal(true)}
        >
          Allocate Bed
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {/* Occupancy Stats Section */}
      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              TOTAL BED CAPACITY
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5 }}>
              {stats?.totalCapacity || 0}
            </Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              OCCUPIED BEDS
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5, color: 'primary.light' }}>
              {stats?.occupiedBeds || 0}
            </Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              VACANT AVAILABLE BEDS
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5, color: '#34D399' }}>
              {stats?.vacantBeds || 0}
            </Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
              <Typography variant="caption" color="text.secondary" fontWeight={600}>
                OCCUPANCY RATE
              </Typography>
              <Typography variant="subtitle2" sx={{ fontWeight: 700 }}>
                {stats?.occupancyPercentage || 0}%
              </Typography>
            </Box>
            <LinearProgress
              variant="determinate"
              value={stats?.occupancyPercentage || 0}
              sx={{ height: 8, borderRadius: 4, bgcolor: 'rgba(255,255,255,0.1)' }}
            />
          </Paper>
        </Grid>
      </Grid>

      {/* Allocations Table */}
      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
        Active Room & Bed Allocations
      </Typography>

      {loading ? (
        <Box display="flex" justifyContent="center" py={6}>
          <CircularProgress />
        </Box>
      ) : allocations.length === 0 ? (
        <Paper sx={{ p: 5, textAlign: 'center', borderRadius: 3, bgcolor: 'background.paper' }}>
          <UserCheck size={48} color="#94A3B8" />
          <Typography variant="h6" sx={{ mt: 2, fontWeight: 600 }}>
            No active room allocations
          </Typography>
          <Typography variant="body2" color="text.secondary" mb={3}>
            Click "Allocate Bed" above to assign a room bed to an enrolled student.
          </Typography>
        </Paper>
      ) : (
        <TableContainer component={Paper} sx={{ borderRadius: 3, className: 'glass-card' }}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell sx={{ fontWeight: 700 }}>Student</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Room</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Bed</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Academic Year</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Start Date</TableCell>
                <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                <TableCell align="right" sx={{ fontWeight: 700 }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {allocations.map((alloc) => (
                <TableRow key={alloc.id} hover>
                  <TableCell>
                    <Typography variant="subtitle2" sx={{ fontWeight: 600 }}>
                      {alloc.studentName}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {alloc.studentEmail}
                    </Typography>
                  </TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>Room {alloc.roomNumber}</TableCell>
                  <TableCell sx={{ fontWeight: 600 }}>{alloc.bedNumber}</TableCell>
                  <TableCell>{alloc.academicYear}</TableCell>
                  <TableCell>{alloc.startDate}</TableCell>
                  <TableCell>
                    <StatusBadge
                      label={alloc.status}
                      status={alloc.status === 'ACTIVE' ? 'success' : alloc.status === 'TRANSFERRED' ? 'warning' : 'default'}
                    />
                  </TableCell>
                  <TableCell align="right">
                    {alloc.status === 'ACTIVE' && (
                      <Box display="flex" justifyContent="flex-end" gap={1}>
                        <Button
                          size="small"
                          variant="outlined"
                          color="info"
                          startIcon={<ArrowRightLeft size={14} />}
                          onClick={() => {
                            setSelectedAllocation(alloc);
                            setOpenTransferModal(true);
                          }}
                        >
                          Transfer
                        </Button>
                        <Button
                          size="small"
                          variant="outlined"
                          color="error"
                          startIcon={<VacateIcon size={14} />}
                          onClick={() => {
                            setSelectedAllocation(alloc);
                            setOpenVacateModal(true);
                          }}
                        >
                          Vacate
                        </Button>
                      </Box>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Modal: Allocate Bed */}
      <Dialog open={openAllocateModal} onClose={() => setOpenAllocateModal(false)} maxWidth="sm" fullWidth>
        <form onSubmit={handleAllocate}>
          <DialogTitle sx={{ fontWeight: 700 }}>Allocate Room Bed to Student</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Student User ID (UUID)"
              margin="dense"
              value={studentId}
              onChange={(e) => setStudentId(e.target.value)}
              required
              helperText="Enter the student's unique UUID"
            />
            <TextField
              select
              fullWidth
              label="Select Available Vacant Bed"
              margin="dense"
              value={selectedBedId}
              onChange={(e) => setSelectedBedId(e.target.value)}
              required
              SelectProps={{ native: true }}
            >
              <option value="">-- Choose Vacant Bed --</option>
              {vacantBeds.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.bedNumber} (Room ID: {b.roomId})
                </option>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Academic Year"
              margin="dense"
              value={academicYear}
              onChange={(e) => setAcademicYear(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Allocation Start Date"
              type="date"
              margin="dense"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              required
              InputLabelProps={{ shrink: true }}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenAllocateModal(false)}>Cancel</Button>
            <Button type="submit" variant="contained">Allocate Bed</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Modal: Transfer Bed */}
      <Dialog open={openTransferModal} onClose={() => setOpenTransferModal(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleTransfer}>
          <DialogTitle sx={{ fontWeight: 700 }}>Transfer Room Bed</DialogTitle>
          <DialogContent>
            <Typography variant="body2" color="text.secondary" mb={2}>
              Transferring allocation for student: <strong>{selectedAllocation?.studentName}</strong>
            </Typography>
            <TextField
              select
              fullWidth
              label="Select Target Vacant Bed"
              margin="dense"
              value={newBedId}
              onChange={(e) => setNewBedId(e.target.value)}
              required
              SelectProps={{ native: true }}
            >
              <option value="">-- Choose Target Bed --</option>
              {vacantBeds.map((b) => (
                <option key={b.id} value={b.id}>
                  {b.bedNumber} (Room ID: {b.roomId})
                </option>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Reason for Transfer"
              margin="dense"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              required
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenTransferModal(false)}>Cancel</Button>
            <Button type="submit" variant="contained" color="warning">Confirm Transfer</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Modal: Vacate Bed */}
      <Dialog open={openVacateModal} onClose={() => setOpenVacateModal(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleVacate}>
          <DialogTitle sx={{ fontWeight: 700 }}>Vacate Room Bed</DialogTitle>
          <DialogContent>
            <Typography variant="body2" color="text.secondary" mb={2}>
              Vacating accommodation for student: <strong>{selectedAllocation?.studentName}</strong>
            </Typography>
            <TextField
              fullWidth
              label="Reason for Vacating"
              margin="dense"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              required
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenVacateModal(false)}>Cancel</Button>
            <Button type="submit" variant="contained" color="error">Confirm Vacate</Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};
