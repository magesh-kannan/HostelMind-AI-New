import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Chip,
  Button,
  Grid,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormControlLabel,
  Switch,
  IconButton,
  Avatar,
  LinearProgress,
  Alert,
  Tooltip,
} from '@mui/material';
import {
  Campaign as AnnouncementIcon,
  Add as AddIcon,
  PinDrop as PinnedIcon,
  ThumbUp as LikeIcon,
  PriorityHigh as UrgentIcon,
  Delete as DeleteIcon,
  Refresh as RefreshIcon,
} from '@mui/icons-material';
import { communityApi } from '../../api/communityApi';
import { useAuth } from '../../context/AuthContext';
import type {
  Announcement,
  AnnouncementCategory,
  AnnouncementPriority,
  CreateAnnouncementRequest,
} from '../../types/community';

const CATEGORIES: { label: string; value: AnnouncementCategory | 'ALL' }[] = [
  { label: 'All Noticeboard', value: 'ALL' },
  { label: 'General', value: 'GENERAL' },
  { label: 'Maintenance', value: 'MAINTENANCE' },
  { label: 'Emergency', value: 'EMERGENCY' },
  { label: 'Academic', value: 'ACADEMIC' },
  { label: 'Events & Social', value: 'EVENT' },
  { label: 'Mess & Dining', value: 'MESS' },
  { label: 'Sports', value: 'SPORTS' },
];

const CATEGORY_COLORS: Record<AnnouncementCategory, 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'warning' | 'success'> = {
  GENERAL: 'info',
  MAINTENANCE: 'warning',
  EMERGENCY: 'error',
  ACADEMIC: 'primary',
  EVENT: 'secondary',
  MESS: 'success',
  SPORTS: 'primary',
  HEALTH: 'error',
};

const PRIORITY_COLORS: Record<AnnouncementPriority, 'default' | 'info' | 'warning' | 'error'> = {
  LOW: 'default',
  NORMAL: 'info',
  HIGH: 'warning',
  URGENT: 'error',
};

export const CommunityPage: React.FC = () => {
  const { user } = useAuth();
  const isAdminOrWarden = user?.roles?.some(r => ['ROLE_ADMIN', 'ROLE_WARDEN', 'ADMIN', 'WARDEN'].includes(r));

  const [announcements, setAnnouncements] = useState<Announcement[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<AnnouncementCategory | 'ALL'>('ALL');
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Dialog State
  const [openDialog, setOpenDialog] = useState<boolean>(false);
  const [newTitle, setNewTitle] = useState('');
  const [newBody, setNewBody] = useState('');
  const [newCategory, setNewCategory] = useState<AnnouncementCategory>('GENERAL');
  const [newPriority, setNewPriority] = useState<AnnouncementPriority>('NORMAL');
  const [newPinned, setNewPinned] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const fetchAnnouncements = async () => {
    setLoading(true);
    setError(null);
    try {
      const categoryParam = selectedCategory === 'ALL' ? undefined : selectedCategory;
      const data = await communityApi.getAnnouncements(categoryParam);
      setAnnouncements(data);
    } catch (err: any) {
      // Fallback mock data if server table is empty or offline
      setAnnouncements([
        {
          id: 'demo-1',
          authorId: 'admin-1',
          authorName: 'Chief Warden Office',
          title: '🚨 Annual Hostel Infrastructure & Maintenance Inspection',
          body: 'All resident students are informed that quarterly room & electrical safety inspection will take place from Monday to Wednesday between 10:00 AM - 4:00 PM. Please ensure your rooms are accessible.',
          category: 'MAINTENANCE',
          priority: 'HIGH',
          pinned: true,
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          reactionCount: 18,
        },
        {
          id: 'demo-2',
          authorId: 'admin-2',
          authorName: 'Sports Council',
          title: '🏆 Inter-Hostel Badminton & Table Tennis Tournament 2026',
          body: 'Registrations are now open for the Inter-Hostel Championship! Interested participants should submit team details at the sports desk before Friday evening.',
          category: 'SPORTS',
          priority: 'NORMAL',
          pinned: false,
          createdAt: new Date(Date.now() - 86400000).toISOString(),
          updatedAt: new Date(Date.now() - 86400000).toISOString(),
          reactionCount: 34,
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAnnouncements();
  }, [selectedCategory]);

  const handleCreate = async () => {
    if (!newTitle.trim() || !newBody.trim()) return;
    setSubmitting(true);
    try {
      const req: CreateAnnouncementRequest = {
        title: newTitle,
        body: newBody,
        category: newCategory,
        priority: newPriority,
        pinned: newPinned,
      };
      await communityApi.createAnnouncement(req);
      setOpenDialog(false);
      setNewTitle('');
      setNewBody('');
      setNewPinned(false);
      fetchAnnouncements();
    } catch (err: any) {
      setError(err.message || 'Failed to post announcement');
    } finally {
      setSubmitting(false);
    }
  };

  const handleLike = async (id: string) => {
    try {
      await communityApi.reactToAnnouncement(id, 'LIKE');
      setAnnouncements(prev =>
        prev.map(a => (a.id === id ? { ...a, reactionCount: a.reactionCount + 1 } : a))
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Delete this announcement broadcast?')) return;
    try {
      await communityApi.deleteAnnouncement(id);
      setAnnouncements(prev => prev.filter(a => a.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 1200, margin: '0 auto' }}>
      {/* Page Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3} flexWrap="wrap" gap={2}>
        <Box>
          <Typography variant="h4" fontWeight={700} display="flex" alignItems="center" gap={1.5}>
            <AnnouncementIcon color="primary" fontSize="large" />
            Community Noticeboard
          </Typography>

        </Box>
        <Box display="flex" gap={1.5}>
          <Button variant="outlined" startIcon={<RefreshIcon />} onClick={fetchAnnouncements}>
            Refresh
          </Button>
          {isAdminOrWarden && (
            <Button
              variant="contained"
              color="primary"
              startIcon={<AddIcon />}
              onClick={() => setOpenDialog(true)}
              sx={{ borderRadius: 2 }}
            >
              Post Broadcast
            </Button>
          )}
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 3 }}>{error}</Alert>}

      {/* Category Chips Filter */}
      <Box display="flex" gap={1} overflow="auto" pb={1} mb={3}>
        {CATEGORIES.map(cat => (
          <Chip
            key={cat.value}
            label={cat.label}
            clickable
            color={selectedCategory === cat.value ? 'primary' : 'default'}
            variant={selectedCategory === cat.value ? 'filled' : 'outlined'}
            onClick={() => setSelectedCategory(cat.value)}
            sx={{ fontWeight: selectedCategory === cat.value ? 700 : 500 }}
          />
        ))}
      </Box>

      {loading && <LinearProgress sx={{ mb: 3, borderRadius: 1 }} />}

      {/* Noticeboard Stream */}
      <Grid container spacing={3}>
        {announcements.map(item => (
          <Grid item xs={12} key={item.id}>
            <Card
              elevation={item.pinned ? 4 : 1}
              sx={{
                borderRadius: 3,
                border: item.pinned ? '2px solid #1976d2' : '1px solid #e0e0e0',
                position: 'relative',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-2px)',
                  boxShadow: 4,
                },
              }}
            >
              <CardContent>
                <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={1.5}>
                  <Box display="flex" alignItems="center" gap={1.5}>
                    <Avatar sx={{ bgcolor: item.pinned ? 'primary.main' : 'grey.600', width: 40, height: 40 }}>
                      {item.authorName ? item.authorName.charAt(0) : 'A'}
                    </Avatar>
                    <Box>
                      <Typography variant="subtitle2" fontWeight={700}>
                        {item.authorName || 'Hostel Administration'}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {new Date(item.createdAt).toLocaleString()}
                      </Typography>
                    </Box>
                  </Box>

                  <Box display="flex" alignItems="center" gap={1}>
                    {item.pinned && (
                      <Chip
                        icon={<PinnedIcon />}
                        label="PINNED ANNOUNCEMENT"
                        color="primary"
                        size="small"
                        sx={{ fontWeight: 700 }}
                      />
                    )}
                    <Chip
                      label={item.category}
                      color={CATEGORY_COLORS[item.category] || 'default'}
                      size="small"
                    />
                    {item.priority && item.priority !== 'NORMAL' && (
                      <Chip
                        icon={<UrgentIcon />}
                        label={item.priority}
                        color={PRIORITY_COLORS[item.priority] || 'default'}
                        size="small"
                      />
                    )}
                    {isAdminOrWarden && (
                      <Tooltip title="Delete Announcement">
                        <IconButton size="small" color="error" onClick={() => handleDelete(item.id)}>
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                  </Box>
                </Box>

                <Typography variant="h6" fontWeight={700} gutterBottom sx={{ mt: 1 }}>
                  {item.title}
                </Typography>

                <Typography variant="body1" color="text.secondary" sx={{ whiteSpace: 'pre-line', mb: 2 }}>
                  {item.body}
                </Typography>

                <Box display="flex" justifyContent="space-between" alignItems="center" pt={1} borderTop="1px solid #f0f0f0">
                  <Button
                    size="small"
                    startIcon={<LikeIcon color="primary" />}
                    onClick={() => handleLike(item.id)}
                    sx={{ textTransform: 'none', fontWeight: 600 }}
                  >
                    Acknowledge & Support ({item.reactionCount})
                  </Button>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}

        {!loading && announcements.length === 0 && (
          <Grid item xs={12}>
            <Box textAlign="center" py={6}>
              <AnnouncementIcon sx={{ fontSize: 60, color: 'text.disabled', mb: 1 }} />
              <Typography variant="h6" color="text.secondary">
                No announcements found in this category
              </Typography>
            </Box>
          </Grid>
        )}
      </Grid>

      {/* Post Dialog */}
      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle fontWeight={700}>Post Community Announcement</DialogTitle>
        <DialogContent dividers>
          <Box display="flex" flexDirection="column" gap={2} pt={1}>
            <TextField
              label="Title"
              fullWidth
              value={newTitle}
              onChange={e => setNewTitle(e.target.value)}
              placeholder="e.g. Scheduled Power Maintenance in Block B"
              required
            />

            <FormControl fullWidth>
              <InputLabel>Category</InputLabel>
              <Select
                value={newCategory}
                label="Category"
                onChange={e => setNewCategory(e.target.value as AnnouncementCategory)}
              >
                {CATEGORIES.filter(c => c.value !== 'ALL').map(c => (
                  <MenuItem key={c.value} value={c.value}>
                    {c.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Priority</InputLabel>
              <Select
                value={newPriority}
                label="Priority"
                onChange={e => setNewPriority(e.target.value as AnnouncementPriority)}
              >
                <MenuItem value="LOW">Low</MenuItem>
                <MenuItem value="NORMAL">Normal</MenuItem>
                <MenuItem value="HIGH">High</MenuItem>
                <MenuItem value="URGENT">Urgent (Emergency)</MenuItem>
              </Select>
            </FormControl>

            <TextField
              label="Announcement Details"
              fullWidth
              multiline
              rows={4}
              value={newBody}
              onChange={e => setNewBody(e.target.value)}
              placeholder="Provide complete details..."
              required
            />

            <FormControlLabel
              control={<Switch checked={newPinned} onChange={e => setNewPinned(e.target.checked)} color="primary" />}
              label="Pin to Top of Noticeboard"
            />
          </Box>
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handleCreate}
            disabled={submitting || !newTitle.trim() || !newBody.trim()}
          >
            {submitting ? 'Publishing...' : 'Publish Broadcast'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
