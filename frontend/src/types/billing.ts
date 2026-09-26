export type InvoiceStatus = 'DRAFT' | 'ISSUED' | 'PARTIALLY_PAID' | 'PAID' | 'OVERDUE' | 'CANCELLED';

export type PaymentMethod = 'CREDIT_CARD' | 'DEBIT_CARD' | 'UPI' | 'NET_BANKING' | 'CASH' | 'BANK_TRANSFER';

export type PaymentGatewayProvider = 'STRIPE' | 'RAZORPAY' | 'OFFLINE';

export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';

export interface FeeStructureDto {
  id: string;
  hostelId: string;
  roomType: string;
  academicYear: string;
  rentAmount: number;
  utilityDeposit: number;
  messFee: number;
  otherCharges: number;
  totalFee: number;
  dueDayOfMonth: number;
  lateFeePerDay: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateFeeStructureRequest {
  hostelId: string;
  roomType: string;
  academicYear: string;
  rentAmount: number;
  utilityDeposit?: number;
  messFee?: number;
  otherCharges?: number;
  dueDayOfMonth?: number;
  lateFeePerDay?: number;
}

export interface InvoiceDto {
  id: string;
  invoiceNumber: string;
  studentId: string;
  studentName?: string;
  hostelId?: string;
  roomId?: string;
  academicYear: string;
  billingPeriod: string;
  subtotal: number;
  lateFee: number;
  totalAmount: number;
  paidAmount: number;
  remainingBalance: number;
  dueDate: string;
  status: InvoiceStatus;
  createdAt: string;
  updatedAt: string;
}

export interface CreateInvoiceRequest {
  studentId: string;
  hostelId?: string;
  roomId?: string;
  academicYear: string;
  billingPeriod: string;
  dueDate: string;
  rentAmount: number;
  utilityDeposit?: number;
  messFee?: number;
  otherCharges?: number;
}

export interface RecordPaymentRequest {
  invoiceId: string;
  amount: number;
  paymentMethod?: PaymentMethod;
  gatewayProvider?: PaymentGatewayProvider;
  transactionReference?: string;
}

export interface PaymentDto {
  id: string;
  invoiceId: string;
  studentId: string;
  amount: number;
  paymentMethod: PaymentMethod;
  transactionReference: String;
  gatewayProvider: PaymentGatewayProvider;
  status: PaymentStatus;
  paidAt: string;
}

export interface BillingStatsDto {
  totalBilled: number;
  totalCollected: number;
  totalOutstanding: number;
  totalInvoicesCount: number;
  paidInvoicesCount: number;
  overdueInvoicesCount: number;
}
