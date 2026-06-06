import { useEffect, useState } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import { fetchBooks } from '../api/books';
import { addCartItem } from '../api/cart';

function money(n) {
  if (n == null || Number.isNaN(Number(n))) return '—';
  return `$${Number(n).toFixed(2)}`;
}

export default function BooksPage() {
  const { refreshCart } = useOutletContext();
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [qtyById, setQtyById] = useState({});
  const [busyId, setBusyId] = useState(null);
  const [cartMsg, setCartMsg] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const data = await fetchBooks();
        if (!cancelled) setBooks(Array.isArray(data) ? data : []);
      } catch (e) {
        if (!cancelled) setError(e.message || 'Could not load books');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  function qtyFor(id) {
    const q = qtyById[id];
    return q >= 1 ? q : 1;
  }

  async function addOne(book) {
    setCartMsg('');
    setBusyId(book.id);
    try {
      await addCartItem(book.id, qtyFor(book.id));
      await refreshCart();
    } catch (e) {
      if (e?.status === 401) {
        setCartMsg('You need to log in first.');
      } else {
        setCartMsg(e.message || 'Could not add to cart');
      }
    } finally {
      setBusyId(null);
    }
  }

  if (loading) return <p className="muted">Loading books…</p>;
  if (error) return <p className="error">{error}</p>;

  return (
    <section>
      <h1>Books</h1>
      <p className="muted">Titles, authors, and prices from the API.</p>
      {cartMsg && (
        <p className="error">
          {cartMsg} <Link to="/login">Go to login</Link>
        </p>
      )}
      {books.length === 0 ? (
        <p className="muted">No books.</p>
      ) : (
        <table className="simple">
          <thead>
            <tr>
              <th>Title</th>
              <th>Author</th>
              <th className="num">Price</th>
              <th>Qty</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {books.map((b) => (
              <tr key={b.id}>
                <td>{b.title}</td>
                <td>{b.author}</td>
                <td className="num">{money(b.price)}</td>
                <td>
                  <input
                    className="input-sm"
                    type="number"
                    min={1}
                    value={qtyFor(b.id)}
                    onChange={(e) => {
                      const n = parseInt(e.target.value, 10);
                      setQtyById((prev) => ({
                        ...prev,
                        [b.id]: Number.isFinite(n) && n >= 1 ? n : 1,
                      }));
                    }}
                    disabled={busyId === b.id}
                  />
                </td>
                <td className="row-actions">
                  <button
                    type="button"
                    className="btn"
                    disabled={busyId === b.id}
                    onClick={() => addOne(b)}
                  >
                    Add to cart
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}
