// ─── Complaint domain types ────────────────────────────────────────────────

export type ComplaintStatus =
  | 'NEW'
  | 'CLASSIFIED'
  | 'PRIORITIZED'
  | 'ASSIGNED'
  | 'IN_PROGRESS'
  | 'WAITING_FOR_STUDENT'
  | 'RESOLVED'
  | 'VERIFIED'
  | 'CLOSED'
  | 'REOPENED';

export type ComplaintPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT' | 'CRITICAL';

export type ComplaintCategory =
  | 'ELECTRICAL'
  | 'PLUMBING'
  | 'FURNITURE'
  | 'HOUSEKEEPING'
  | 'INTERNET_CONNECTIVITY'
  | 'SECURITY'
  | 'FOOD_QUALITY'
  | 'NOISE'
  | 'PEST_CONTROL'
  | 'AC_COOLING'
  | 'WATER_SUPPLY'
  | 'LAUNDRY'
  | 'MEDICAL'
  | 'ADMINISTRATION'
  | 'OTHER';

export interface ComplaintDto {
  id: string;
  studentId: string;
  studentName: string;
  hostelId: string;
  roomId?: string;
  assignedToId?: string;
  assignedToName?: string;
  title: string;
  description: string;
  category: ComplaintCategory;
  priority: ComplaintPriority;
  status: ComplaintStatus;
  attachmentUrl?: string;
  slaDeadline?: string;
  reopenCount: number;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  closedAt?: string;
}

export interface ComplaintStatusHistoryDto {
  id: string;
  complaintId: string;
  changedByUserId?: string;
  changedByUserName?: string;
  fromStatus?: ComplaintStatus;
  toStatus: ComplaintStatus;
  note?: string;
  changedAt: string;
}

export interface ComplaintStatsDto {
  totalComplaints: number;
  newComplaints: number;
  inProgressComplaints: number;
  resolvedComplaints: number;
  closedComplaints: number;
  overdueComplaints: number;
}

export interface CreateComplaintRequest {
  hostelId: string;
  roomId?: string;
  title: string;
  description: string;
  category: ComplaintCategory;
  priority?: ComplaintPriority;
  attachmentUrl?: string;
}

export interface UpdateComplaintStatusRequest {
  status: ComplaintStatus;
  assignedToId?: string;
  priority?: ComplaintPriority;
  slaDeadline?: string;
  note?: string;
}
