export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export type MealType = 'BREAKFAST' | 'LUNCH' | 'SNACKS' | 'DINNER';

export type MealAttendanceStatus = 'SERVED' | 'SKIPPED' | 'CANCELLED';

export interface MessMenuDto {
  id: string;
  hostelId: string;
  dayOfWeek: DayOfWeek;
  mealType: MealType;
  items: string;
  calorieCount: number;
  specialNotes?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMessMenuRequest {
  hostelId: string;
  dayOfWeek: DayOfWeek;
  mealType: MealType;
  items: string;
  calorieCount?: number;
  specialNotes?: string;
}

export interface GenerateQrTokenRequest {
  mealType: MealType;
  date?: string;
}

export interface QrTokenResponse {
  qrToken: string;
  studentId: string;
  mealType: MealType;
  date: string;
  expiresAt: string;
}

export interface ScanQrTokenRequest {
  qrToken: string;
}

export interface MealAttendanceDto {
  id: string;
  studentId: string;
  studentName?: string;
  hostelId?: string;
  mealType: MealType;
  attendanceDate: string;
  qrToken: string;
  status: MealAttendanceStatus;
  scannedAt: string;
}

export interface CreateMessFeedbackRequest {
  menuId?: string;
  rating: number;
  comment?: string;
}

export interface MessFeedbackDto {
  id: string;
  studentId: string;
  menuId?: string;
  rating: number;
  comment?: string;
  createdAt: string;
}

export interface MessStatsDto {
  totalServedToday: number;
  breakfastServedToday: number;
  lunchServedToday: number;
  snacksServedToday: number;
  dinnerServedToday: number;
  averageRating: number;
}
