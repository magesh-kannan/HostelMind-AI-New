import React, { useState } from 'react';
import {
  Box, Typography, Grid, Card, Button, TextField,
  Chip, Paper, IconButton, Alert,
  Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, Dialog, DialogTitle, DialogContent, DialogActions,
  FormControl, InputLabel, Select, MenuItem, Rating,
} from '@mui/material';
import {
  Restaurant as MessIcon,
  QrCode as QrIcon,
  QrCodeScanner as ScannerIcon,
  Star as StarIcon,
  Add as AddIcon,
  CheckCircle as CheckIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { messApi } from '../../api/messApi';
import type {
  DayOfWeek,
  MealType,
  MessMenuDto,
  QrTokenResponse,
  MealAttendanceDto,
  MessFeedbackDto,
} from '../../types/mess';

const DAYS: DayOfWeek[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
const MEALS: MealType[] = ['BREAKFAST', 'LUNCH', 'SNACKS', 'DINNER'];

export const MessManagementPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState(0);

  // ─── Operational Stats ───
  const { data: stats, refetch: refetchStats } = useQuery({
    queryKey: ['mess-stats'],
    queryFn: messApi.getMessStats,
  });

  // ─── Menu Timetable State ───
  const [selectedDay, setSelectedDay] = useState<DayOfWeek>('MONDAY');
  const [editMenuOpen, setEditMenuOpen] = useState(false);
  const [menuMealType, setMenuMealType] = useState<MealType>('LUNCH');
  const [menuItemsText, setMenuItemsText] = useState('');
  const [menuCalories, setMenuCalories] = useState('650');
  const [menuNotes, setMenuNotes] = useState('');

  const { data: weeklyMenu, refetch: refetchMenu } = useQuery({
    queryKey: ['weekly-menu'],
    queryFn: () => messApi.getWeeklyMenu(),
  });

  const saveMenuMutation = useMutation({
    mutationFn: () =>
      messApi.createOrUpdateMenu({
        hostelId: '00000000-0000-0000-0000-000000000001',
        dayOfWeek: selectedDay,
        mealType: menuMealType,
        items: menuItemsText,
        calorieCount: Number(menuCalories),
        specialNotes: menuNotes || undefined,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['weekly-menu'] });
      setEditMenuOpen(false);
      setMenuItemsText('');
    },
  });

  // ─── QR Pass Generator State ───
  const [passMealType, setPassMealType] = useState<MealType>('LUNCH');
  const [generatedPass, setGeneratedPass] = useState<QrTokenResponse | null>(null);

  const generatePassMutation = useMutation({
    mutationFn: () => messApi.generateQrToken({ mealType: passMealType }),
    onSuccess: (data) => setGeneratedPass(data),
  });

  // ─── Live Scanner State ───
  const [scanInput, setScanInput] = useState('');
  const [scanSuccess, setScanSuccess] = useState<MealAttendanceDto | null>(null);
  const [scanError, setScanError] = useState('');

  const { data: attendanceList, refetch: refetchAttendance } = useQuery({
    queryKey: ['mess-attendance'],
    queryFn: () => messApi.getAttendance(),
    enabled: activeTab === 2,
  });

  const scanMutation = useMutation({
    mutationFn: (token: string) => messApi.scanQrToken({ qrToken: token }),
    onSuccess: (data) => {
      setScanSuccess(data);
      setScanError('');
      setScanInput('');
      queryClient.invalidateQueries({ queryKey: ['mess-attendance'] });
      queryClient.invalidateQueries({ queryKey: ['mess-stats'] });
    },
    onError: (err: any) => {
      setScanError(err.response?.data?.message || err.message || 'Invalid or duplicate QR pass');
      setScanSuccess(null);
    },
  });

  // ─── Feedback State ───
  const [fbRating, setFbRating] = useState<number | null>(5);
  const [fbComment, setFbComment] = useState('');

  const { data: feedbackList } = useQuery({
    queryKey: ['mess-feedback'],
    queryFn: messApi.getAllFeedback,
    enabled: activeTab === 3,
  });

  const submitFbMutation = useMutation({
    mutationFn: () =>
      messApi.submitFeedback({
        rating: fbRating || 5,
        comment: fbComment,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['mess-feedback'] });
      queryClient.invalidateQueries({ queryKey: ['mess-stats'] });
      setFbComment('');
    },
  });

  return (
    <Box>
      {/* ── Page Header ── */}
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
            Mess Operations & Meal Attendance
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Weekly menu planner, digital QR meal pass scanner, attendance tracking & feedback
          </Typography>
        </Box>
        <Box display="flex" gap={1}>
          <IconButton onClick={() => { refetchStats(); refetchMenu(); refetchAttendance(); }} size="small">
            <RefreshIcon />
          </IconButton>
        </Box>
      </Box>

      {/* ── Dashboard KPI Cards ── */}
      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #3b82f6' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              TOTAL MEALS SERVED TODAY
            </Typography>
            <Typography variant="h4" fontWeight={800} color="primary" mt={0.5}>
              {stats?.totalServedToday ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #f59e0b' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              BREAKFAST / LUNCH SERVED
            </Typography>
            <Typography variant="h4" fontWeight={800} sx={{ color: '#f59e0b' }} mt={0.5}>
              {stats?.breakfastServedToday ?? 0} / {stats?.lunchServedToday ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #06b6d4' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              DINNER SERVED TODAY
            </Typography>
            <Typography variant="h4" fontWeight={800} sx={{ color: '#06b6d4' }} mt={0.5}>
              {stats?.dinnerServedToday ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #22c55e' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              AVERAGE MESS RATING
            </Typography>
            <Box display="flex" alignItems="center" gap={1} mt={0.5}>
              <Typography variant="h4" fontWeight={800} sx={{ color: '#22c55e' }}>
                {stats?.averageRating ?? 0}
              </Typography>
              <Rating value={stats?.averageRating ?? 0} precision={0.1} readOnly size="small" />
            </Box>
          </Card>
        </Grid>
      </Grid>

      {/* ── Navigation Tabs ── */}
      <Paper sx={{ mb: 3, borderRadius: 3 }}>
        <Tabs value={activeTab} onChange={(_, v) => setActiveTab(v)} variant="fullWidth">
          <Tab icon={<MessIcon />} label="Weekly Menu Planner" iconPosition="start" />
          <Tab icon={<QrIcon />} label="Digital Meal Pass (QR)" iconPosition="start" />
          <Tab icon={<ScannerIcon />} label="Mess Live QR Scanner" iconPosition="start" />
          <Tab icon={<StarIcon />} label="Mess Feedback" iconPosition="start" />
        </Tabs>
      </Paper>

      {/* ── Tab 0: Weekly Menu Planner ── */}
      {activeTab === 0 && (
        <Box>
          <Box display="flex" alignItems="center" justifyContent="space-between" mb={2}>
            <Box display="flex" gap={1} flexWrap="wrap">
              {DAYS.map((d) => (
                <Chip
                  key={d}
                  label={d}
                  onClick={() => setSelectedDay(d)}
                  color={selectedDay === d ? 'primary' : 'default'}
                  sx={{ fontWeight: 700 }}
                />
              ))}
            </Box>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setEditMenuOpen(true)}
            >
              Update {selectedDay} Menu
            </Button>
          </Box>

          <Grid container spacing={2}>
            {MEALS.map((meal) => {
              const item = weeklyMenu?.find(
                (m: MessMenuDto) => m.dayOfWeek === selectedDay && m.mealType === meal
              );
              return (
                <Grid item xs={12} sm={6} md={3} key={meal}>
                  <Card sx={{ p: 2.5, borderRadius: 3, height: '100%', borderTop: '4px solid #3b82f6' }}>
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={1.5}>
                      <Typography variant="subtitle1" fontWeight={800} color="primary">
                        {meal}
                      </Typography>
                      {item?.calorieCount && (
                        <Chip label={`${item.calorieCount} kcal`} size="small" variant="outlined" />
                      )}
                    </Box>
                    <Typography variant="body2" sx={{ minHeight: 60, whiteSpace: 'pre-wrap' }}>
                      {item?.items || 'No menu published yet.'}
                    </Typography>
                    {item?.specialNotes && (
                      <Typography variant="caption" color="text.secondary" fontStyle="italic" display="block" mt={1}>
                        Note: {item.specialNotes}
                      </Typography>
                    )}
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        </Box>
      )}

      {/* ── Tab 1: Digital Meal Pass (QR) ── */}
      {activeTab === 1 && (
        <Grid container spacing={3} justifyContent="center">
          <Grid item xs={12} md={6}>
            <Card sx={{ p: 3, borderRadius: 3, textAlign: 'center' }}>
              <Typography variant="h6" fontWeight={800} mb={1}>
                Generate Digital Meal QR Pass
              </Typography>
              <Typography variant="body2" color="text.secondary" mb={3}>
                Select meal session to generate your instant QR pass for mess entry:
              </Typography>

              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Select Meal Session</InputLabel>
                <Select
                  label="Select Meal Session"
                  value={passMealType}
                  onChange={(e) => setPassMealType(e.target.value as MealType)}
                >
                  {MEALS.map((m) => (
                    <MenuItem key={m} value={m}>{m}</MenuItem>
                  ))}
                </Select>
              </FormControl>

              <Button
                variant="contained"
                size="large"
                fullWidth
                onClick={() => generatePassMutation.mutate()}
                startIcon={<QrIcon />}
              >
                Generate Pass
              </Button>

              {generatedPass && (
                <Box mt={3} p={3} border="2px dashed" borderColor="primary.main" borderRadius={3} bgcolor="action.hover">
                  <Chip label="ACTIVE MEAL PASS" color="success" sx={{ fontWeight: 800, mb: 1.5 }} />
                  <Typography variant="h5" fontWeight={800} color="primary" mb={1}>
                    {generatedPass.mealType} PASS
                  </Typography>
                  <Paper
                    elevation={0}
                    sx={{
                      p: 2,
                      bgcolor: '#fff',
                      fontFamily: 'monospace',
                      wordBreak: 'break-all',
                      fontSize: '0.75rem',
                      borderRadius: 2,
                      mb: 2,
                      border: '1px solid #ccc',
                    }}
                  >
                    {generatedPass.qrToken}
                  </Paper>
                  <Typography variant="caption" color="text.secondary">
                    Show this QR token to the mess counter scanner for entry.
                  </Typography>
                </Box>
              )}
            </Card>
          </Grid>
        </Grid>
      )}

      {/* ── Tab 2: Mess Live QR Scanner Terminal ── */}
      {activeTab === 2 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={5}>
            <Card sx={{ p: 3, borderRadius: 3 }}>
              <Box display="flex" alignItems="center" gap={1} mb={2}>
                <ScannerIcon color="primary" />
                <Typography variant="h6" fontWeight={800}>
                  Mess Counter Scanner
                </Typography>
              </Box>
              <Typography variant="body2" color="text.secondary" mb={2}>
                Scan student QR token at mess entrance counter to record meal service:
              </Typography>

              {scanError && <Alert severity="error" sx={{ mb: 2 }}>{scanError}</Alert>}
              {scanSuccess && (
                <Alert severity="success" icon={<CheckIcon />} sx={{ mb: 2 }}>
                  Meal Served! Student ID: {scanSuccess.studentId?.substring(0, 8)} ({scanSuccess.mealType})
                </Alert>
              )}

              <TextField
                label="Paste / Scan QR Token String"
                fullWidth
                multiline
                rows={3}
                value={scanInput}
                onChange={(e) => setScanInput(e.target.value)}
                placeholder="MESS_PASS|..."
              />
              <Button
                variant="contained"
                size="large"
                fullWidth
                sx={{ mt: 2 }}
                disabled={!scanInput.trim() || scanMutation.isPending}
                onClick={() => scanMutation.mutate(scanInput.trim())}
                startIcon={<CheckIcon />}
              >
                Verify & Serve Meal
              </Button>
            </Card>
          </Grid>

          <Grid item xs={12} md={7}>
            <Typography variant="h6" fontWeight={800} mb={2}>
              Today's Live Meal Attendance Log
            </Typography>
            <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: 'action.hover' }}>
                    <TableCell sx={{ fontWeight: 700 }}>Meal</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Student ID</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Scanned Time</TableCell>
                    <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {attendanceList?.map((a: MealAttendanceDto) => (
                    <TableRow key={a.id}>
                      <TableCell><Chip label={a.mealType} size="small" /></TableCell>
                      <TableCell>{a.studentId?.substring(0, 8)}...</TableCell>
                      <TableCell>{new Date(a.scannedAt).toLocaleTimeString()}</TableCell>
                      <TableCell><Chip label={a.status} color="success" size="small" /></TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </Grid>
      )}

      {/* ── Tab 3: Mess Feedback ── */}
      {activeTab === 3 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={5}>
            <Card sx={{ p: 3, borderRadius: 3 }}>
              <Typography variant="h6" fontWeight={800} mb={2}>
                Submit Mess Feedback
              </Typography>
              <Box display="flex" flexDirection="column" gap={2}>
                <Box>
                  <Typography variant="caption" fontWeight={600} display="block" mb={0.5}>
                    Rate Meal Quality:
                  </Typography>
                  <Rating
                    value={fbRating}
                    onChange={(_, v) => setFbRating(v)}
                    size="large"
                  />
                </Box>
                <TextField
                  label="Your Comments / Suggestions"
                  fullWidth
                  multiline
                  rows={4}
                  value={fbComment}
                  onChange={(e) => setFbComment(e.target.value)}
                  placeholder="Tell us about food taste, hygiene, portion size..."
                />
                <Button
                  variant="contained"
                  disabled={submitFbMutation.isPending}
                  onClick={() => submitFbMutation.mutate()}
                  startIcon={<StarIcon />}
                >
                  Submit Review
                </Button>
              </Box>
            </Card>
          </Grid>

          <Grid item xs={12} md={7}>
            <Typography variant="h6" fontWeight={800} mb={2}>
              Student Feedback Feed
            </Typography>
            <Box display="flex" flexDirection="column" gap={1.5}>
              {feedbackList?.map((fb: MessFeedbackDto) => (
                <Paper key={fb.id} sx={{ p: 2, borderRadius: 2 }}>
                  <Box display="flex" justifyContent="space-between" alignItems="center" mb={0.5}>
                    <Rating value={fb.rating} readOnly size="small" />
                    <Typography variant="caption" color="text.secondary">
                      {new Date(fb.createdAt).toLocaleDateString()}
                    </Typography>
                  </Box>
                  <Typography variant="body2">
                    {fb.comment || 'No comment provided.'}
                  </Typography>
                </Paper>
              ))}
            </Box>
          </Grid>
        </Grid>
      )}

      {/* ── Edit Menu Dialog ── */}
      <Dialog open={editMenuOpen} onClose={() => setEditMenuOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Update {selectedDay} Menu</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <FormControl fullWidth>
            <InputLabel>Meal Session</InputLabel>
            <Select
              label="Meal Session"
              value={menuMealType}
              onChange={(e) => setMenuMealType(e.target.value as MealType)}
            >
              {MEALS.map((m) => (
                <MenuItem key={m} value={m}>{m}</MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Menu Items (comma-separated)"
            fullWidth
            multiline
            rows={3}
            value={menuItemsText}
            onChange={(e) => setMenuItemsText(e.target.value)}
            placeholder="e.g. Rice, Sambar, Chapati, Curd, Salad"
          />

          <TextField
            label="Approximate Calories (kcal)"
            type="number"
            fullWidth
            value={menuCalories}
            onChange={(e) => setMenuCalories(e.target.value)}
          />

          <TextField
            label="Dietary Notes (optional)"
            fullWidth
            value={menuNotes}
            onChange={(e) => setMenuNotes(e.target.value)}
            placeholder="e.g. Vegetarian, Contains dairy"
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setEditMenuOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            disabled={!menuItemsText || saveMenuMutation.isPending}
            onClick={() => saveMenuMutation.mutate()}
          >
            Save Menu
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
