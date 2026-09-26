import React, { useState } from 'react';
import {
  Box, Typography, Grid, Card, Button, TextField,
  Chip, Paper, IconButton,
  Tabs, Tab, Table, TableBody, TableCell, TableContainer, TableHead,
  TableRow, Dialog, DialogTitle, DialogContent, DialogActions,
  FormControl, InputLabel, Select, MenuItem, LinearProgress, Divider,
} from '@mui/material';
import {
  Receipt as InvoiceIcon,
  AccountBalance as FeeIcon,
  Add as AddIcon,
  Refresh as RefreshIcon,
  Payments as CashIcon,
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { billingApi } from '../../api/billingApi';
import { hostelApi } from '../../api/hostelApi';
import type {
  InvoiceDto,
  FeeStructureDto,
  InvoiceStatus,
  PaymentMethod,
  PaymentGatewayProvider,
} from '../../types/billing';

const INVOICE_STATUS_COLORS: Record<InvoiceStatus, string> = {
  DRAFT: '#6b7280',
  ISSUED: '#3b82f6',
  PARTIALLY_PAID: '#f59e0b',
  PAID: '#22c55e',
  OVERDUE: '#ef4444',
  CANCELLED: '#9ca3af',
};

const DEFAULT_HOSTEL_ID = '22222222-2222-2222-2222-222222222222';

export const BillingManagementPage: React.FC = () => {
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState(0);

  const { data: hostels } = useQuery({
    queryKey: ['hostels'],
    queryFn: hostelApi.getHostels,
  });
  const activeHostelId = hostels?.[0]?.id || DEFAULT_HOSTEL_ID;

  // ─── Stats ───
  const { data: stats } = useQuery({
    queryKey: ['billing-stats'],
    queryFn: billingApi.getBillingStats,
  });

  // ─── Invoices ───
  const [invoiceFilterStatus, setInvoiceFilterStatus] = useState<InvoiceStatus | 'ALL'>('ALL');
  const [createInvoiceOpen, setCreateInvoiceOpen] = useState(false);
  const [payInvoice, setPayInvoice] = useState<InvoiceDto | null>(null);

  const { data: invoices, isLoading: invoicesLoading, refetch: refetchInvoices } = useQuery({
    queryKey: ['invoices'],
    queryFn: billingApi.getAllInvoices,
  });

  // Invoice Create Form State
  const [studentId, setStudentId] = useState('');
  const [academicYear, setAcademicYear] = useState('2026-2027');
  const [billingPeriod, setBillingPeriod] = useState('Semester 1');
  const [dueDate, setDueDate] = useState(new Date().toISOString().split('T')[0]);
  const [rentAmount, setRentAmount] = useState('5000');
  const [messFee, setMessFee] = useState('2500');
  const [utilityDeposit, setUtilityDeposit] = useState('1000');

  const createInvoiceMutation = useMutation({
    mutationFn: () =>
      billingApi.createInvoice({
        studentId,
        academicYear,
        billingPeriod,
        dueDate,
        rentAmount: Number(rentAmount),
        messFee: Number(messFee),
        utilityDeposit: Number(utilityDeposit),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      queryClient.invalidateQueries({ queryKey: ['billing-stats'] });
      setCreateInvoiceOpen(false);
      setStudentId('');
    },
  });

  // Payment Record Form State
  const [payAmount, setPayAmount] = useState('');
  const [payMethod, setPayMethod] = useState<PaymentMethod>('CREDIT_CARD');
  const [payGateway, setPayGateway] = useState<PaymentGatewayProvider>('STRIPE');
  const [payRef, setPayRef] = useState('');

  const recordPaymentMutation = useMutation({
    mutationFn: () =>
      billingApi.recordPayment({
        invoiceId: payInvoice!.id,
        amount: Number(payAmount),
        paymentMethod: payMethod,
        gatewayProvider: payGateway,
        transactionReference: payRef || undefined,
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['invoices'] });
      queryClient.invalidateQueries({ queryKey: ['billing-stats'] });
      setPayInvoice(null);
      setPayAmount('');
      setPayRef('');
    },
  });

  // ─── Fee Structures ───
  const [createFeeOpen, setCreateFeeOpen] = useState(false);
  const [fsRoomType, setFsRoomType] = useState('DOUBLE');
  const [fsRent, setFsRent] = useState('5000');
  const [fsMess, setFsMess] = useState('2500');
  const [fsDeposit, setFsDeposit] = useState('1000');

  const { data: feeStructures } = useQuery({
    queryKey: ['fee-structures'],
    queryFn: () => billingApi.getFeeStructures(),
  });

  const createFeeMutation = useMutation({
    mutationFn: () =>
      billingApi.createFeeStructure({
        hostelId: activeHostelId,
        roomType: fsRoomType,
        academicYear: '2026-2027',
        rentAmount: Number(fsRent),
        messFee: Number(fsMess),
        utilityDeposit: Number(fsDeposit),
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['fee-structures'] });
      setCreateFeeOpen(false);
    },
  });

  const displayedInvoices = invoices
    ? invoiceFilterStatus === 'ALL'
      ? invoices
      : invoices.filter((i: InvoiceDto) => i.status === invoiceFilterStatus)
    : [];

  return (
    <Box>
      {/* ── Page Header ── */}
      <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 800, letterSpacing: '-0.5px' }}>
            Fees, Billing & Automated Invoicing
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage fee structures, issue student invoices, and process Stripe / Razorpay payments
          </Typography>
        </Box>
        <Box display="flex" gap={1}>
          <IconButton onClick={() => refetchInvoices()} size="small">
            <RefreshIcon />
          </IconButton>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setCreateInvoiceOpen(true)}
            sx={{ fontWeight: 700 }}
          >
            Issue Invoice
          </Button>
        </Box>
      </Box>

      {/* ── Dashboard Stat Cards ── */}
      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #3b82f6' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              TOTAL BILLED
            </Typography>
            <Typography variant="h4" fontWeight={800} color="primary" mt={0.5}>
              ${stats?.totalBilled?.toLocaleString() ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #22c55e' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              TOTAL COLLECTED
            </Typography>
            <Typography variant="h4" fontWeight={800} sx={{ color: '#22c55e' }} mt={0.5}>
              ${stats?.totalCollected?.toLocaleString() ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #f59e0b' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              OUTSTANDING BALANCE
            </Typography>
            <Typography variant="h4" fontWeight={800} sx={{ color: '#f59e0b' }} mt={0.5}>
              ${stats?.totalOutstanding?.toLocaleString() ?? 0}
            </Typography>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ p: 2, borderRadius: 3, borderLeft: '4px solid #ef4444' }}>
            <Typography variant="caption" color="text.secondary" fontWeight={600}>
              OVERDUE INVOICES
            </Typography>
            <Typography variant="h4" fontWeight={800} color="error" mt={0.5}>
              {stats?.overdueInvoicesCount ?? 0}
            </Typography>
          </Card>
        </Grid>
      </Grid>

      {/* ── Navigation Tabs ── */}
      <Paper sx={{ mb: 3, borderRadius: 3 }}>
        <Tabs value={activeTab} onChange={(_, v) => setActiveTab(v)} variant="fullWidth">
          <Tab icon={<InvoiceIcon />} label="Student Invoices" iconPosition="start" />
          <Tab icon={<FeeIcon />} label="Fee Structures" iconPosition="start" />
        </Tabs>
      </Paper>

      {/* ── Tab 0: Student Invoices ── */}
      {activeTab === 0 && (
        <Box>
          {/* Status Filters */}
          <Box display="flex" gap={1} mb= {2} flexWrap="wrap">
            {(['ALL', 'ISSUED', 'PARTIALLY_PAID', 'PAID', 'OVERDUE'] as const).map((s) => (
              <Chip
                key={s}
                label={s.replace(/_/g, ' ')}
                size="small"
                onClick={() => setInvoiceFilterStatus(s)}
                color={invoiceFilterStatus === s ? 'primary' : 'default'}
              />
            ))}
          </Box>

          <TableContainer component={Paper} sx={{ borderRadius: 3 }}>
            {invoicesLoading && <LinearProgress />}
            <Table>
              <TableHead>
                <TableRow sx={{ bgcolor: 'action.hover' }}>
                  <TableCell sx={{ fontWeight: 700 }}>Invoice #</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Academic Year</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Period</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Total</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Paid</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Remaining</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Due Date</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Status</TableCell>
                  <TableCell sx={{ fontWeight: 700 }}>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {displayedInvoices.map((inv: InvoiceDto) => (
                  <TableRow key={inv.id} hover>
                    <TableCell sx={{ fontWeight: 700 }}>{inv.invoiceNumber}</TableCell>
                    <TableCell>{inv.academicYear}</TableCell>
                    <TableCell>{inv.billingPeriod}</TableCell>
                    <TableCell>${inv.totalAmount?.toLocaleString()}</TableCell>
                    <TableCell>${inv.paidAmount?.toLocaleString()}</TableCell>
                    <TableCell sx={{ fontWeight: 700, color: inv.remainingBalance > 0 ? 'error.main' : 'success.main' }}>
                      ${inv.remainingBalance?.toLocaleString()}
                    </TableCell>
                    <TableCell>{inv.dueDate}</TableCell>
                    <TableCell>
                      <Chip
                        label={inv.status}
                        size="small"
                        sx={{
                          bgcolor: INVOICE_STATUS_COLORS[inv.status] + '22',
                          color: INVOICE_STATUS_COLORS[inv.status],
                          fontWeight: 700,
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      {inv.status !== 'PAID' && (
                        <Button
                          size="small"
                          variant="outlined"
                          startIcon={<CashIcon />}
                          onClick={() => {
                            setPayInvoice(inv);
                            setPayAmount(inv.remainingBalance.toString());
                          }}
                        >
                          Pay
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      )}

      {/* ── Tab 1: Fee Structures ── */}
      {activeTab === 1 && (
        <Box>
          <Box display="flex" justifyContent="flex-end" mb={2}>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setCreateFeeOpen(true)}
            >
              Add Fee Template
            </Button>
          </Box>

          <Grid container spacing={2}>
            {feeStructures?.map((fs: FeeStructureDto) => (
              <Grid item xs={12} md={4} key={fs.id}>
                <Card sx={{ p: 3, borderRadius: 3 }}>
                  <Typography variant="h6" fontWeight={700} color="primary" mb={1}>
                    {fs.roomType} Room Structure
                  </Typography>
                  <Typography variant="caption" color="text.secondary" display="block" mb={2}>
                    Academic Year: {fs.academicYear}
                  </Typography>
                  <Divider sx={{ my: 1 }} />
                  <Box display="flex" justifyContent="space-between" py={0.5}>
                    <Typography variant="body2">Room Rent:</Typography>
                    <Typography variant="body2" fontWeight={700}>${fs.rentAmount}</Typography>
                  </Box>
                  <Box display="flex" justifyContent="space-between" py={0.5}>
                    <Typography variant="body2">Mess Charges:</Typography>
                    <Typography variant="body2" fontWeight={700}>${fs.messFee}</Typography>
                  </Box>
                  <Box display="flex" justifyContent="space-between" py={0.5}>
                    <Typography variant="body2">Utility Deposit:</Typography>
                    <Typography variant="body2" fontWeight={700}>${fs.utilityDeposit}</Typography>
                  </Box>
                  <Divider sx={{ my: 1 }} />
                  <Box display="flex" justifyContent="space-between" py={0.5}>
                    <Typography variant="subtitle2" fontWeight={800}>Total Term Fee:</Typography>
                    <Typography variant="subtitle2" fontWeight={800} color="primary">${fs.totalFee}</Typography>
                  </Box>
                </Card>
              </Grid>
            ))}
          </Grid>
        </Box>
      )}

      {/* ── Issue Invoice Modal ── */}
      <Dialog open={createInvoiceOpen} onClose={() => setCreateInvoiceOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Issue Student Invoice</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <TextField
            label="Student ID (UUID)"
            fullWidth
            value={studentId}
            onChange={(e) => setStudentId(e.target.value)}
            placeholder="e.g. 22222222-2222-2222-2222-222222222222"
          />
          <TextField
            label="Academic Year"
            fullWidth
            value={academicYear}
            onChange={(e) => setAcademicYear(e.target.value)}
          />
          <TextField
            label="Billing Period"
            fullWidth
            value={billingPeriod}
            onChange={(e) => setBillingPeriod(e.target.value)}
          />
          <TextField
            label="Due Date"
            type="date"
            fullWidth
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            InputLabelProps={{ shrink: true }}
          />
          <TextField
            label="Rent Amount ($)"
            type="number"
            fullWidth
            value={rentAmount}
            onChange={(e) => setRentAmount(e.target.value)}
          />
          <TextField
            label="Mess Fee ($)"
            type="number"
            fullWidth
            value={messFee}
            onChange={(e) => setMessFee(e.target.value)}
          />
          <TextField
            label="Utility Deposit ($)"
            type="number"
            fullWidth
            value={utilityDeposit}
            onChange={(e) => setUtilityDeposit(e.target.value)}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setCreateInvoiceOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            disabled={!studentId || createInvoiceMutation.isPending}
            onClick={() => createInvoiceMutation.mutate()}
          >
            Issue Invoice
          </Button>
        </DialogActions>
      </Dialog>

      {/* ── Record Payment Modal ── */}
      <Dialog open={!!payInvoice} onClose={() => setPayInvoice(null)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Record Payment</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <Typography variant="body2" color="text.secondary">
            Invoice: <strong>{payInvoice?.invoiceNumber}</strong> (Remaining: ${payInvoice?.remainingBalance})
          </Typography>
          <TextField
            label="Payment Amount ($)"
            type="number"
            fullWidth
            value={payAmount}
            onChange={(e) => setPayAmount(e.target.value)}
          />
          <FormControl fullWidth>
            <InputLabel>Payment Method</InputLabel>
            <Select
              label="Payment Method"
              value={payMethod}
              onChange={(e) => setPayMethod(e.target.value as PaymentMethod)}
            >
              {['CREDIT_CARD', 'DEBIT_CARD', 'UPI', 'NET_BANKING', 'CASH', 'BANK_TRANSFER'].map((m) => (
                <MenuItem key={m} value={m}>{m.replace(/_/g, ' ')}</MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl fullWidth>
            <InputLabel>Payment Gateway</InputLabel>
            <Select
              label="Payment Gateway"
              value={payGateway}
              onChange={(e) => setPayGateway(e.target.value as PaymentGatewayProvider)}
            >
              <MenuItem value="STRIPE">Stripe</MenuItem>
              <MenuItem value="RAZORPAY">Razorpay</MenuItem>
              <MenuItem value="OFFLINE">Offline / Cash</MenuItem>
            </Select>
          </FormControl>
          <TextField
            label="Transaction Reference (optional)"
            fullWidth
            value={payRef}
            onChange={(e) => setPayRef(e.target.value)}
            placeholder="e.g. ch_3M00000000000"
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setPayInvoice(null)}>Cancel</Button>
          <Button
            variant="contained"
            disabled={!payAmount || recordPaymentMutation.isPending}
            onClick={() => recordPaymentMutation.mutate()}
          >
            Submit Payment
          </Button>
        </DialogActions>
      </Dialog>

      {/* ── Create Fee Structure Modal ── */}
      <Dialog open={createFeeOpen} onClose={() => setCreateFeeOpen(false)} maxWidth="xs" fullWidth>
        <DialogTitle sx={{ fontWeight: 700 }}>Add Fee Template</DialogTitle>
        <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: 2 }}>
          <TextField
            label="Room Type"
            fullWidth
            value={fsRoomType}
            onChange={(e) => setFsRoomType(e.target.value)}
          />
          <TextField
            label="Room Rent ($)"
            type="number"
            fullWidth
            value={fsRent}
            onChange={(e) => setFsRent(e.target.value)}
          />
          <TextField
            label="Mess Fee ($)"
            type="number"
            fullWidth
            value={fsMess}
            onChange={(e) => setFsMess(e.target.value)}
          />
          <TextField
            label="Utility Deposit ($)"
            type="number"
            fullWidth
            value={fsDeposit}
            onChange={(e) => setFsDeposit(e.target.value)}
          />
        </DialogContent>
        <DialogActions sx={{ p: 2 }}>
          <Button onClick={() => setCreateFeeOpen(false)}>Cancel</Button>
          <Button
            variant="contained"
            disabled={createFeeMutation.isPending}
            onClick={() => createFeeMutation.mutate()}
          >
            Save Fee Template
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
