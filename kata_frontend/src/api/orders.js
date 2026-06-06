import { get, post } from './client';
import { API_PATHS } from './paths';

export function placeOrder() {
  return post(API_PATHS.ORDERS_CHECKOUT, undefined);
}

export function fetchOrders() {
  return get(API_PATHS.ORDERS_LIST);
}
