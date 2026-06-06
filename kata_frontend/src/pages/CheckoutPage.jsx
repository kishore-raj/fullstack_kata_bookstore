import { useState } from 'react';
import { Link, useNavigate, useOutletContext } from 'react-router-dom';
import { placeOrder } from '../api/orders';

function money(n) {
  if (n == null || Number.isNaN(Number(n))) return '—';
  return `$${Number(n).toFixed(2)}`;
}

export default function CheckoutPage() {
  const navigate = useNavigate();
  const { refreshCart } = useOutletContext();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit() {
    setError('');
    setBusy(true);
    try {
      const o = await placeOrder();
      setOrder(o);
      await refreshCart();
    } catch (e) {
      if (e?.status === 401) {
        navigate('/login');
        return;
      }
      setError(e.message || 'Checkout failed');
    } finally {
      setBusy(false);
    }
  }

  if (order) {
    return (
      <section>
        <h1>Order placed</h1>
        <p className="muted">Order #{order.orderId}</p>
        <p className="muted">Placed at: {order.placedAt}</p>
        <table className="simple">
          <thead>
            <tr>
              <th>Title</th>
              <th className="num">Qty</th>
              <th className="num">Line</th>
            </tr>
          </thead>
          <tbody>
            {(order.items || []).map((cartItem) => (
              <tr key={cartItem.bookId}>
                <td>{cartItem.title}</td>
                <td className="num">{cartItem.quantity}</td>
                <td className="num">{money(cartItem.itemTotal)}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <p>
          <strong>Total:</strong> {money(order.totalAmount)}
        </p>
        <p>
          <Link to="/books">Back to books</Link>
        </p>
      </section>
    );
  }

  return (
    <section>
      <h1>Checkout</h1>
      <p className="muted">Places an order from your current cart and clears the cart on the server.</p>
      {error && <p className="error">{error}</p>}
      <p>
        <button type="button" className="btn" disabled={busy} onClick={submit}>
          {busy ? '…' : 'Place order'}
        </button>
      </p>
      <p>
        <Link to="/cart">Back to cart</Link>
      </p>
    </section>
  );
}
