import { AxiosResponse } from 'axios';
import { axiosClient } from './axiosClient';
import type {
  FeeStructureDto,
  CreateFeeStructureRequest,
  InvoiceDto,
  CreateInvoiceRequest,
  PaymentDto,
  RecordPaymentRequest,
  BillingStatsDto,
} from '../types/billing';

const BASE = '/billing';

export const billingApi = {
  // Fee structures
  createFeeStructure: (data: CreateFeeStructureRequest): Promise<FeeStructureDto> =>
    axiosClient.post<FeeStructureDto>(`${BASE}/fee-structures`, data).then((r: AxiosResponse<FeeStructureDto>) => r.data),

  getFeeStructures: (hostelId?: string): Promise<FeeStructureDto[]> =>
    axiosClient.get<FeeStructureDto[]>(`${BASE}/fee-structures`, { params: { hostelId } }).then((r: AxiosResponse<FeeStructureDto[]>) => r.data),

  // Invoices
  createInvoice: (data: CreateInvoiceRequest): Promise<InvoiceDto> =>
    axiosClient.post<InvoiceDto>(`${BASE}/invoices`, data).then((r: AxiosResponse<InvoiceDto>) => r.data),

  getAllInvoices: (): Promise<InvoiceDto[]> =>
    axiosClient.get<InvoiceDto[]>(`${BASE}/invoices`).then((r: AxiosResponse<InvoiceDto[]>) => r.data),

  getInvoiceById: (id: string): Promise<InvoiceDto> =>
    axiosClient.get<InvoiceDto>(`${BASE}/invoices/${id}`).then((r: AxiosResponse<InvoiceDto>) => r.data),

  getStudentInvoices: (studentId: string): Promise<InvoiceDto[]> =>
    axiosClient.get<InvoiceDto[]>(`${BASE}/invoices/student/${studentId}`).then((r: AxiosResponse<InvoiceDto[]>) => r.data),

  getHostelInvoices: (hostelId: string): Promise<InvoiceDto[]> =>
    axiosClient.get<InvoiceDto[]>(`${BASE}/invoices/hostel/${hostelId}`).then((r: AxiosResponse<InvoiceDto[]>) => r.data),

  // Payments
  recordPayment: (data: RecordPaymentRequest): Promise<PaymentDto> =>
    axiosClient.post<PaymentDto>(`${BASE}/payments`, data).then((r: AxiosResponse<PaymentDto>) => r.data),

  getInvoicePayments: (invoiceId: string): Promise<PaymentDto[]> =>
    axiosClient.get<PaymentDto[]>(`${BASE}/payments/invoice/${invoiceId}`).then((r: AxiosResponse<PaymentDto[]>) => r.data),

  // Stats & Process
  getBillingStats: (): Promise<BillingStatsDto> =>
    axiosClient.get<BillingStatsDto>(`${BASE}/stats`).then((r: AxiosResponse<BillingStatsDto>) => r.data),

  processOverdue: (): Promise<void> =>
    axiosClient.post(`${BASE}/process-overdue`).then(() => undefined),
};
