import { get } from './client';
import { API_PATHS } from './paths';

export function fetchBooks() {
  return get(API_PATHS.BOOKS_ALL);
}
