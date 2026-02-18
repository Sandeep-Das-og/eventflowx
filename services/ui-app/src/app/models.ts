export interface BookingRequest {
  customerName: string;
  eventName: string;
}

export interface BookingResponse {
  bookingId: string;
}

export interface WalletResponse {
  userId: string;
  balance: number;
}

export interface EventItem {
  id: string;
  name: string;
  city: string;
  price: number;
}

export interface ErrorResponse {
  message?: string;
}
