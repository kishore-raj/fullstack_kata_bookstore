
export const API_PATHS = Object.freeze({
  BOOKS_ALL: '/books/all',

  AUTH_REGISTER: '/auth/register',
  AUTH_LOGIN: '/auth/login',
  AUTH_LOGOUT: '/auth/logout',

  CART: '/cart',
  CART_ITEMS: '/cart/items',

  ORDERS_CHECKOUT: '/orders/checkout',
  ORDERS_LIST: '/orders/list',
});


export function cartItemPath(bookId) {
  return `${API_PATHS.CART_ITEMS}/${bookId}`;
}

export function orderPath(orderId) {
  return `${API_PATHS.ORDERS}/${orderId}`;
}

