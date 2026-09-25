export type GenderType = 'MALE' | 'FEMALE' | 'COED';
export type RoomType = 'SINGLE' | 'DOUBLE' | 'TRIPLE' | 'QUAD' | 'DORMITORY';
export type RoomStatus = 'AVAILABLE' | 'OCCUPIED' | 'MAINTENANCE' | 'RESERVED';
export type BedStatus = 'VACANT' | 'OCCUPIED' | 'UNDER_MAINTENANCE';
export type AllocationStatus = 'ACTIVE' | 'TRANSFERRED' | 'VACATED' | 'CANCELLED';

export interface CampusDto {
  id: string;
  name: string;
  code: string;
  address?: string;
}

export interface HostelDto {
  id: string;
  campusId: string;
  name: string;
  genderType: GenderType;
  wardenId?: string;
}

export interface BlockDto {
  id: string;
  hostelId: string;
  name: string;
  code: string;
  totalFloors: number;
}

export interface FloorDto {
  id: string;
  blockId: string;
  floorNumber: number;
  name?: string;
}

export interface BedDto {
  id: string;
  roomId: string;
  bedNumber: string;
  status: BedStatus;
}

export interface RoomDto {
  id: string;
  floorId: string;
  roomNumber: string;
  roomType: RoomType;
  capacity: number;
  occupiedCount: number;
  status: RoomStatus;
  monthlyRent: number;
  beds: BedDto[];
}

export interface RoomAllocationDto {
  id: string;
  studentId: string;
  studentName: string;
  studentEmail: string;
  bedId: string;
  bedNumber: string;
  roomId: string;
  roomNumber: string;
  academicYear: string;
  startDate: string;
  endDate?: string;
  status: AllocationStatus;
  allocatedBy?: string;
  createdAt: string;
}

export interface OccupancyStatsDto {
  totalHostels: number;
  totalRooms: number;
  totalCapacity: number;
  occupiedBeds: number;
  vacantBeds: number;
  occupancyPercentage: number;
}

export interface CreateCampusRequest {
  name: string;
  code: string;
  address?: string;
}

export interface CreateHostelRequest {
  campusId: string;
  name: string;
  genderType: GenderType;
}

export interface CreateRoomRequest {
  floorId: string;
  roomNumber: string;
  roomType: RoomType;
  capacity: number;
  monthlyRent?: number;
}

export interface AllocateBedRequest {
  studentId: string;
  bedId: string;
  academicYear: string;
  startDate: string;
}

export interface TransferBedRequest {
  allocationId: string;
  newBedId: string;
  reason: string;
}

export interface VacateBedRequest {
  allocationId: string;
  reason: string;
}
