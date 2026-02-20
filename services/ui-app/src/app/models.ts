export interface EventItem {
  id: string;
  name: string;
  city: string;
  price: number;
  createdAt?: string;
}

export interface CreateEventRequest {
  name: string;
  city: string;
  price: number;
}

export interface EventAnalytics {
  eventId: string;
  eventName: string;
  city: string;
  price: number;
  bookingsCount: number;
  createdAt: string;
}

export interface BookingRequest {
  userId: string;
  eventId: string;
  eventName: string;
}

export interface BookingResponse {
  bookingId: string;
  userId: string;
  eventId: string;
  paymentRequired: boolean;
}

export interface ChargePaymentRequest {
  bookingId: string;
  userId: string;
  paymentGateway: string;
  paymentMethodType: string;
  amount: number;
}

export interface ChargePaymentResponse {
  bookingId: string;
  userId: string;
  paymentGateway: string;
  paymentMethodType: string;
  status: string;
  chargedAmount: number;
  remainingBalance: number;
}
