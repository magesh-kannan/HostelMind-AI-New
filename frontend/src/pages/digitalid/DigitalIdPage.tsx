import React, { useRef } from 'react';
import {
  Box, Card, CardContent, Typography, Avatar, Chip, Grid,
  CircularProgress, Alert, Button, Divider, Tooltip, LinearProgress,
} from '@mui/material';
import {
  User, Shield, CreditCard, Download, QrCode, CheckCircle, XCircle,
} from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { auditApi } from '../../api/auditApi';
import { useAuth } from '../../context/AuthContext';
import type { UserDto, Role } from '../../types/auth';

// ── Role badge config ─────────────────────────────────────────────────────────
const ROLE_CONFIG: Record<Role, { label: string; color: string; bg: string }> = {
  ROLE_ADMIN: { label: 'Administrator', color: '#ef4444', bg: '#fee2e2' },
  ROLE_WARDEN: { label: 'Warden', color: '#f59e0b', bg: '#fef3c7' },
  ROLE_STUDENT: { label: 'Student', color: '#6366f1', bg: '#eef2ff' },
  ROLE_STAFF: { label: 'Staff', color: '#10b981', bg: '#d1fae5' },
  ROLE_HIGHER_OFFICIAL: { label: 'Higher Official', color: '#8b5cf6', bg: '#ede9fe' },
};

const getRolePrimary = (roles: Role[]): Role =>
  roles.includes('ROLE_ADMIN') ? 'ROLE_ADMIN'
  : roles.includes('ROLE_HIGHER_OFFICIAL') ? 'ROLE_HIGHER_OFFICIAL'
  : roles.includes('ROLE_WARDEN') ? 'ROLE_WARDEN'
  : roles.includes('ROLE_STAFF') ? 'ROLE_STAFF'
  : 'ROLE_STUDENT';

const getInitials = (name: string) =>
  name.split(' ').map((n) => n[0]).join('').toUpperCase().slice(0, 2);

// ── Holographic ID Card ───────────────────────────────────────────────────────
const IdCard: React.FC<{ user: UserDto }> = ({ user }) => {
  const primaryRole = getRolePrimary(user.roles as Role[]);
  const roleConf = ROLE_CONFIG[primaryRole];

  return (
    <Box
      sx={{
        width: 380,
        background: 'linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #0f172a 100%)',
        borderRadius: 4,
        p: 3,
        position: 'relative',
        overflow: 'hidden',
        boxShadow: '0 25px 60px rgba(99,102,241,0.35), 0 0 0 1px rgba(255,255,255,0.08)',
        // Holographic shimmer overlay
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0, left: 0, right: 0, bottom: 0,
          background: 'linear-gradient(135deg, transparent 0%, rgba(99,102,241,0.08) 40%, rgba(139,92,246,0.1) 60%, transparent 100%)',
          pointerEvents: 'none',
        },
        '&::after': {
          content: '""',
          position: 'absolute',
          top: -60, right: -60,
          width: 200, height: 200,
          background: 'radial-gradient(circle, rgba(139,92,246,0.15) 0%, transparent 70%)',
          pointerEvents: 'none',
        },
      }}
    >
      {/* Top strip */}
      <Box
        sx={{
          height: 4,
          borderRadius: 2,
          background: 'linear-gradient(90deg, #6366f1, #8b5cf6, #ec4899, #6366f1)',
          backgroundSize: '200% 100%',
          animation: 'shimmer 3s linear infinite',
          mb: 2.5,
          '@keyframes shimmer': {
            '0%': { backgroundPosition: '0% 0%' },
            '100%': { backgroundPosition: '200% 0%' },
          },
        }}
      />

      {/* Issuer */}
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 2 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Shield size={18} color="#8b5cf6" />
          <Typography variant="caption" color="#8b5cf6" fontWeight={700} letterSpacing={1.5} sx={{ textTransform: 'uppercase', fontSize: '0.65rem' }}>
            HostelMind AI
          </Typography>
        </Box>
        <Chip
          label={roleConf.label}
          size="small"
          sx={{
            bgcolor: `${roleConf.color}22`,
            color: roleConf.color,
            border: '1px solid',
            borderColor: `${roleConf.color}55`,
            fontWeight: 700,
            fontSize: '0.65rem',
            height: 20,
          }}
        />
      </Box>

      {/* Avatar + Name */}
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2.5, mb: 2.5 }}>
        <Avatar
          sx={{
            width: 72, height: 72,
            background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
            fontSize: '1.5rem', fontWeight: 800,
            boxShadow: '0 0 0 3px rgba(99,102,241,0.4)',
          }}
        >
          {getInitials(user.fullName)}
        </Avatar>
        <Box>
          <Typography
            variant="h6"
            fontWeight={800}
            sx={{ color: '#f1f5f9', letterSpacing: -0.3, lineHeight: 1.2 }}
          >
            {user.fullName}
          </Typography>
          <Typography variant="caption" sx={{ color: '#94a3b8', display: 'block', mt: 0.3 }}>
            {user.email}
          </Typography>
          {user.phoneNumber && (
            <Typography variant="caption" sx={{ color: '#64748b' }}>
              {user.phoneNumber}
            </Typography>
          )}
        </Box>
      </Box>

      <Divider sx={{ borderColor: 'rgba(255,255,255,0.08)', mb: 2 }} />

      {/* Details grid */}
      <Grid container spacing={1.5} mb={2}>
        <Grid item xs={6}>
          <Typography variant="caption" sx={{ color: '#475569', textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.6rem' }}>
            User ID
          </Typography>
          <Typography variant="caption" sx={{ color: '#cbd5e1', display: 'block', fontFamily: 'monospace', fontSize: '0.7rem' }}>
            {user.id.slice(0, 8).toUpperCase()}
          </Typography>
        </Grid>
        <Grid item xs={6}>
          <Typography variant="caption" sx={{ color: '#475569', textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.6rem' }}>
            Status
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.2 }}>
            {user.active ? (
              <><CheckCircle size={12} color="#10b981" /><Typography variant="caption" sx={{ color: '#10b981', fontWeight: 600 }}>Active</Typography></>
            ) : (
              <><XCircle size={12} color="#ef4444" /><Typography variant="caption" sx={{ color: '#ef4444', fontWeight: 600 }}>Inactive</Typography></>
            )}
          </Box>
        </Grid>
        <Grid item xs={12}>
          <Typography variant="caption" sx={{ color: '#475569', textTransform: 'uppercase', letterSpacing: 1, fontSize: '0.6rem' }}>
            Roles
          </Typography>
          <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mt: 0.3 }}>
            {(user.roles as Role[]).map((role) => {
              const conf = ROLE_CONFIG[role];
              return (
                <Chip
                  key={role}
                  label={conf?.label ?? role}
                  size="small"
                  sx={{
                    height: 18, fontSize: '0.6rem', fontWeight: 700,
                    bgcolor: conf ? `${conf.color}22` : 'rgba(255,255,255,0.1)',
                    color: conf?.color ?? '#fff',
                    border: 'none',
                  }}
                />
              );
            })}
          </Box>
        </Grid>
      </Grid>

      {/* QR placeholder strip */}
      <Box
        sx={{
          display: 'flex', alignItems: 'center', gap: 1.5,
          p: 1.5, borderRadius: 2,
          bgcolor: 'rgba(255,255,255,0.04)',
          border: '1px solid rgba(255,255,255,0.06)',
        }}
      >
        <QrCode size={36} color="#6366f1" />
        <Box>
          <Typography variant="caption" sx={{ color: '#64748b', fontSize: '0.6rem', display: 'block' }}>
            DIGITAL VERIFICATION CODE
          </Typography>
          <Typography variant="caption" sx={{ color: '#94a3b8', fontFamily: 'monospace', fontSize: '0.65rem', letterSpacing: 1 }}>
            HM-{user.id.slice(-8).toUpperCase()}
          </Typography>
        </Box>
      </Box>

      {/* Bottom shimmer bar */}
      <Box sx={{ height: 3, borderRadius: 2, background: 'linear-gradient(90deg, #6366f1, #ec4899)', opacity: 0.5, mt: 2 }} />
    </Box>
  );
};

// ── User Directory List ───────────────────────────────────────────────────────
const UserRow: React.FC<{ u: UserDto; isCurrentUser: boolean }> = ({ u, isCurrentUser }) => {
  const primary = getRolePrimary(u.roles as Role[]);
  const conf = ROLE_CONFIG[primary];
  return (
    <Box
      sx={{
        display: 'flex', alignItems: 'center', gap: 2, p: 1.5,
        borderRadius: 2, cursor: 'pointer',
        border: isCurrentUser ? '1px solid' : '1px solid transparent',
        borderColor: isCurrentUser ? 'primary.main' : 'transparent',
        bgcolor: isCurrentUser ? 'rgba(99,102,241,0.06)' : 'inherit',
        transition: 'background 0.15s',
        '&:hover': { bgcolor: 'action.hover' },
      }}
    >
      <Avatar sx={{ width: 36, height: 36, bgcolor: `${conf.color}22`, color: conf.color, fontSize: '0.8rem', fontWeight: 700 }}>
        {getInitials(u.fullName)}
      </Avatar>
      <Box sx={{ flexGrow: 1, minWidth: 0 }}>
        <Typography variant="body2" fontWeight={600} noWrap>
          {u.fullName} {isCurrentUser && <Chip label="You" size="small" sx={{ height: 16, fontSize: '0.6rem', ml: 0.5 }} />}
        </Typography>
        <Typography variant="caption" color="text.secondary" noWrap>{u.email}</Typography>
      </Box>
      <Chip label={conf.label} size="small" sx={{ bgcolor: conf.bg, color: conf.color, fontWeight: 600, fontSize: '0.65rem', height: 20 }} />
    </Box>
  );
};

// ── Main Page ─────────────────────────────────────────────────────────────────
export const DigitalIdPage: React.FC = () => {
  const { user: authUser } = useAuth();
  const cardRef = useRef<HTMLDivElement>(null);

  const { data: currentUserDto, isLoading: profileLoading, error: profileError } = useQuery({
    queryKey: ['myProfile'],
    queryFn: () => auditApi.getUserById(authUser!.id),
    enabled: !!authUser?.id,
  });

  const { data: allUsers = [], isLoading: usersLoading } = useQuery({
    queryKey: ['allUsers'],
    queryFn: auditApi.getAllUsers,
    enabled: authUser?.roles?.some((r) => ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL'].includes(r)),
  });

  const isAdmin = authUser?.roles?.some((r) =>
    ['ROLE_ADMIN', 'ROLE_WARDEN', 'ROLE_HIGHER_OFFICIAL'].includes(r)
  );

  return (
    <Box sx={{ p: 3 }}>
      {/* ── Header ───────────────────────────────────────────────────── */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h4" fontWeight={800} sx={{ letterSpacing: -0.5, display: 'flex', alignItems: 'center', gap: 1 }}>
          <CreditCard size={32} color="#6366f1" />
          Digital ID Card
        </Typography>
        <Typography variant="body2" color="text.secondary" mt={0.5}>
          Your holographic HostelMind identity — accepted across all hostel checkpoints
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {/* ── ID Card column ───────────────────────────────────────── */}
        <Grid item xs={12} md={5}>
          <Typography variant="overline" color="text.secondary" fontWeight={700} letterSpacing={1.5}>
            YOUR IDENTITY CARD
          </Typography>

          {profileLoading && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 6 }}>
              <CircularProgress />
            </Box>
          )}

          {profileError && (
            <Alert severity="warning" sx={{ mt: 2 }}>
              Could not load profile — showing session data instead.
            </Alert>
          )}

          {/* Render from API or fall back to auth session data */}
          {(currentUserDto || authUser) && (
            <Box sx={{ mt: 2 }} ref={cardRef}>
              <IdCard
                user={currentUserDto ?? {
                  id: authUser!.id,
                  email: authUser!.email,
                  fullName: authUser!.fullName,
                  phoneNumber: authUser!.phoneNumber,
                  active: true,
                  roles: authUser!.roles,
                }}
              />

              <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                <Tooltip title="Print / save as PDF via your browser's print dialog">
                  <Button
                    variant="outlined"
                    startIcon={<Download size={16} />}
                    onClick={() => window.print()}
                    size="small"
                  >
                    Save / Print
                  </Button>
                </Tooltip>
                <Chip
                  label={`ID: HM-${(currentUserDto?.id ?? authUser?.id ?? '').slice(-8).toUpperCase()}`}
                  size="small"
                  variant="outlined"
                  icon={<User size={12} />}
                  sx={{ fontFamily: 'monospace', fontWeight: 600 }}
                />
              </Box>
            </Box>
          )}

          {/* ── Card info ─────────────────────────────────────────── */}
          <Card sx={{ mt: 3, borderRadius: 3 }}>
            <CardContent>
              <Typography variant="subtitle2" fontWeight={700} mb={1} color="text.secondary">
                CARD INFORMATION
              </Typography>
              {[
                { label: 'Issuing Authority', value: 'HostelMind AI Platform' },
                { label: 'Card Type', value: 'Digital Identity — Hostel Resident' },
                { label: 'Format', value: 'Holographic Secure Digital' },
                { label: 'Valid', value: 'Active during enrollment period' },
              ].map((item) => (
                <Box key={item.label} sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                  <Typography variant="caption" color="text.secondary">{item.label}</Typography>
                  <Typography variant="caption" fontWeight={600}>{item.value}</Typography>
                </Box>
              ))}
            </CardContent>
          </Card>
        </Grid>

        {/* ── User directory (admin/warden) ─────────────────────── */}
        {isAdmin && (
          <Grid item xs={12} md={7}>
            <Typography variant="overline" color="text.secondary" fontWeight={700} letterSpacing={1.5}>
              RESIDENT DIRECTORY ({allUsers.length})
            </Typography>
            <Card sx={{ mt: 1, borderRadius: 3, maxHeight: 620, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
              <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}>
                <Typography variant="body2" color="text.secondary">
                  All registered users across the platform
                </Typography>
              </Box>
              <Box sx={{ overflowY: 'auto', flexGrow: 1, p: 1 }}>
                {usersLoading && <LinearProgress sx={{ m: 2 }} />}
                {allUsers.map((u) => (
                  <UserRow
                    key={u.id}
                    u={u}
                    isCurrentUser={u.id === authUser?.id}
                  />
                ))}
                {allUsers.length === 0 && !usersLoading && (
                  <Typography variant="body2" color="text.secondary" sx={{ p: 3, textAlign: 'center' }}>
                    No users found
                  </Typography>
                )}
              </Box>
            </Card>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};
