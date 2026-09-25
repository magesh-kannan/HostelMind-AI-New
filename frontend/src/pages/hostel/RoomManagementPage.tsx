import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Button,
  Grid2 as Grid,
  Card,
  CardContent,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  CircularProgress,
  Stack,
  Alert,
} from '@mui/material';
import { Home, Plus, Building, Bed as BedIcon } from 'lucide-react';
import { hostelApi } from '../../api/hostelApi';
import { CampusDto, HostelDto, RoomDto } from '../../types/hostel';
import { StatusBadge } from '../../components/common/StatusBadge';

export const RoomManagementPage: React.FC = () => {
  const [campuses, setCampuses] = useState<CampusDto[]>([]);
  const [hostels, setHostels] = useState<HostelDto[]>([]);
  const [rooms, setRooms] = useState<RoomDto[]>([]);
  const [loading, setLoading] = useState(true);

  // Dialogs
  const [openCampusModal, setOpenCampusModal] = useState(false);
  const [openHostelModal, setOpenHostelModal] = useState(false);

  // Forms
  const [campusName, setCampusName] = useState('');
  const [campusCode, setCampusCode] = useState('');

  const [hostelName, setHostelName] = useState('');
  const [selectedCampusId, setSelectedCampusId] = useState('');
  const [genderType, setGenderType] = useState<'MALE' | 'FEMALE' | 'COED'>('COED');

  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [campusData, hostelData, roomData] = await Promise.all([
        hostelApi.getCampuses(),
        hostelApi.getHostels(),
        hostelApi.getAllRooms(),
      ]);
      setCampuses(campusData);
      setHostels(hostelData);
      setRooms(roomData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load hostel infrastructure.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleCreateCampus = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await hostelApi.createCampus({ name: campusName, code: campusCode });
      setOpenCampusModal(false);
      setCampusName('');
      setCampusCode('');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create campus');
    }
  };

  const handleCreateHostel = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await hostelApi.createHostel({ campusId: selectedCampusId, name: hostelName, genderType });
      setOpenHostelModal(false);
      setHostelName('');
      fetchData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create hostel');
    }
  };

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800 }}>
            Hostel & Room Management
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Manage institutional campuses, hostels, blocks, floors, rooms, and beds
          </Typography>
        </Box>
        <Stack direction="row" spacing={2}>
          <Button
            variant="outlined"
            color="inherit"
            startIcon={<Building size={18} />}
            onClick={() => setOpenCampusModal(true)}
          >
            Add Campus
          </Button>
          <Button
            variant="contained"
            color="primary"
            startIcon={<Plus size={18} />}
            onClick={() => setOpenHostelModal(true)}
          >
            Add Hostel
          </Button>
        </Stack>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {/* Infrastructure Summary Row */}
      <Grid container spacing={3} mb={4}>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              REGISTERED CAMPUSES
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5 }}>
              {campuses.length}
            </Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              TOTAL HOSTEL BUILDINGS
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5 }}>
              {hostels.length}
            </Typography>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, sm: 4 }}>
          <Paper sx={{ p: 2.5, borderRadius: 3, bgcolor: 'background.paper', border: '1px solid', borderColor: 'divider' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              MANAGED ROOMS
            </Typography>
            <Typography variant="h4" sx={{ fontWeight: 800, mt: 0.5 }}>
              {rooms.length}
            </Typography>
          </Paper>
        </Grid>
      </Grid>

      {/* Room Directory Grid */}
      <Typography variant="h6" sx={{ fontWeight: 700, mb: 2 }}>
        Live Room Directory & Bed Status
      </Typography>

      {loading ? (
        <Box display="flex" justifyContent="center" py={6}>
          <CircularProgress />
        </Box>
      ) : rooms.length === 0 ? (
        <Paper sx={{ p: 5, textAlign: 'center', borderRadius: 3, bgcolor: 'background.paper' }}>
          <Home size={48} color="#94A3B8" />
          <Typography variant="h6" sx={{ mt: 2, fontWeight: 600 }}>
            No rooms configured yet
          </Typography>
          <Typography variant="body2" color="text.secondary" mb={3}>
            Add a campus, hostel, and blocks to configure room layouts.
          </Typography>
        </Paper>
      ) : (
        <Grid container spacing={3}>
          {rooms.map((room) => (
            <Grid key={room.id} size={{ xs: 12, sm: 6, md: 4 }}>
              <Card sx={{ borderRadius: 3, height: '100%', className: 'glass-card' }}>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={1.5}>
                    <Typography variant="h6" sx={{ fontWeight: 800 }}>
                      Room {room.roomNumber}
                    </Typography>
                    <StatusBadge
                      label={room.status}
                      status={room.status === 'AVAILABLE' ? 'success' : room.status === 'OCCUPIED' ? 'warning' : 'error'}
                    />
                  </Box>

                  <Typography variant="caption" color="text.secondary" display="block" mb={2}>
                    Type: <strong>{room.roomType}</strong> • Rent: <strong>${room.monthlyRent}/mo</strong>
                  </Typography>

                  <Typography variant="subtitle2" sx={{ fontWeight: 600, mb: 1 }}>
                    Beds ({room.occupiedCount} / {room.capacity} Occupied)
                  </Typography>

                  <Stack spacing={1}>
                    {room.beds?.map((bed) => (
                      <Paper
                        key={bed.id}
                        elevation={0}
                        sx={{
                          p: 1.2,
                          borderRadius: 2,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          bgcolor: bed.status === 'VACANT' ? 'rgba(16, 185, 129, 0.08)' : 'rgba(99, 102, 241, 0.08)',
                          border: '1px solid',
                          borderColor: bed.status === 'VACANT' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(99, 102, 241, 0.2)',
                        }}
                      >
                        <Box display="flex" alignItems="center" gap={1}>
                          <BedIcon size={16} color={bed.status === 'VACANT' ? '#10B981' : '#6366F1'} />
                          <Typography variant="body2" sx={{ fontWeight: 600 }}>
                            {bed.bedNumber}
                          </Typography>
                        </Box>
                        <Chip
                          label={bed.status}
                          size="small"
                          color={bed.status === 'VACANT' ? 'success' : 'primary'}
                          sx={{ height: 20, fontSize: '0.65rem', fontWeight: 700 }}
                        />
                      </Paper>
                    ))}
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}

      {/* Create Campus Modal */}
      <Dialog open={openCampusModal} onClose={() => setOpenCampusModal(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleCreateCampus}>
          <DialogTitle sx={{ fontWeight: 700 }}>Add Institutional Campus</DialogTitle>
          <DialogContent>
            <TextField
              fullWidth
              label="Campus Name"
              margin="dense"
              value={campusName}
              onChange={(e) => setCampusName(e.target.value)}
              required
            />
            <TextField
              fullWidth
              label="Campus Code (e.g. MAIN)"
              margin="dense"
              value={campusCode}
              onChange={(e) => setCampusCode(e.target.value)}
              required
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenCampusModal(false)}>Cancel</Button>
            <Button type="submit" variant="contained">Create Campus</Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Create Hostel Modal */}
      <Dialog open={openHostelModal} onClose={() => setOpenHostelModal(false)} maxWidth="xs" fullWidth>
        <form onSubmit={handleCreateHostel}>
          <DialogTitle sx={{ fontWeight: 700 }}>Add Hostel Building</DialogTitle>
          <DialogContent>
            <TextField
              select
              fullWidth
              label="Select Campus"
              margin="dense"
              value={selectedCampusId}
              onChange={(e) => setSelectedCampusId(e.target.value)}
              required
            >
              {campuses.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.name} ({c.code})</MenuItem>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Hostel Name"
              margin="dense"
              value={hostelName}
              onChange={(e) => setHostelName(e.target.value)}
              required
            />
            <TextField
              select
              fullWidth
              label="Gender Designation"
              margin="dense"
              value={genderType}
              onChange={(e) => setGenderType(e.target.value as any)}
            >
              <MenuItem value="COED">Co-Ed Hostel</MenuItem>
              <MenuItem value="MALE">Boys Hostel</MenuItem>
              <MenuItem value="FEMALE">Girls Hostel</MenuItem>
            </TextField>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpenHostelModal(false)}>Cancel</Button>
            <Button type="submit" variant="contained">Create Hostel</Button>
          </DialogActions>
        </form>
      </Dialog>
    </Box>
  );
};
