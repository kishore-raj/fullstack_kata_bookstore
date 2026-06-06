import { post } from './client';
import { API_PATHS } from './paths';

export function register(body) {
  return post(API_PATHS.AUTH_REGISTER, body);
}

export function login(body) {
  return post(API_PATHS.AUTH_LOGIN, body);
}

export function logout() {
  return post(API_PATHS.AUTH_LOGOUT, undefined);
}
