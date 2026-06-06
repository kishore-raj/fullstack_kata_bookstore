import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { fetchOrders } from '../api/orders';

function money(n) {
  if (n == null || Number.isNaN(Number(n))) return '—';
  return `$${Number(n).toFixed(2)}`;
}

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setError('');
      try {
        const data = await fetchOrders();
        if (!cancelled) setOrders(Array.isArray(data) ? data : []);
      } catch (e) {
        if (!cancelled) {
          if (e?.status === 401) {
            setError('Please log in to see your orders.');
          } else {
            setError(e.message || 'Could not load orders');
          }
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, []);

  if (loading) return <p className="muted">Loading orders…</p>;

  if (error) {
    return (
      <section>
        <h1>Orders</h1>
        <p className="error">{error}</p>
        <p>
          <Link to="/login">Login</Link>
        </p>
      </section>
    );
  }

  return (
    <section>
      <h1>Orders</h1>
      <p className="muted">Past orders from your account.</p>
      {orders.length === 0 ? (
        <p className="muted">No orders yet.</p>
      ) : (
        <ul className="order-list">
          {orders.map((o) => (
            <li key={o.orderId} className="order-card">
              <p>
                <strong>Order #{o.orderId}</strong> — {o.placedAt} — total {money(o.totalAmount)}
              </p>
              <table className="simple">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th className="num">Qty</th>
                    <th className="num">Line</th>
                  </tr>
                </thead>
                <tbody>
                  {(o.items || []).map((cartItem) => (
                    <tr key={`${o.orderId}-${cartItem.bookId}`}>
                      <td>{cartItem.title}</td>
                      <td className="num">{cartItem.quantity}</td>
                      <td className="num">{money(cartItem.itemTotal)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
