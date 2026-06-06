import { del, get, patch, post } from './client';
import { API_PATHS, cartItemPath } from './paths';

export function fetchCart() {
  return get(API_PATHS.CART);
}

export function addCartItem(bookId, quantity) {
  return post(API_PATHS.CART_ITEMS, { bookId, quantity });
}

export function updateCartItemQuantity(bookId, quantity) {
  return patch(cartItemPath(bookId), { quantity });
}

export function removeCartItem(bookId) {
  return del(cartItemPath(bookId));
}
