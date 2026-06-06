import { useEffect, useState } from 'react';
import { fetchBooks } from '../api/books';
import './BookPage.css';

function formatPrice(price) {
  if (price == null || Number.isNaN(Number(price))) return '—';
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(Number(price));
}

export default function BookPage() {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchBooks();
        if (!cancelled) {
          setBooks(Array.isArray(data) ? data : []);
        }
      } catch (e) {
        if (!cancelled) {
          setError(e instanceof Error ? e.message : 'Failed to load books');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }
    load();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <main className="book-page">
      <h1>Books</h1>
      <p className="muted">Catalog from the bookstore API.</p>

      {loading && <p className="muted">Loading…</p>}
      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}

      {!loading && !error && books.length === 0 && <p>No books available.</p>}

      {!loading && !error && books.length > 0 && (
        <table className="book-table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Author</th>
              <th>Price</th>
            </tr>
          </thead>
          <tbody>
            {books.map((b) => (
              <tr key={b.id}>
                <td>{b.title}</td>
                <td>{b.author}</td>
                <td>{formatPrice(b.price)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </main>
  );
}
