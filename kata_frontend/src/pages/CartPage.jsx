import { useCallback, useEffect, useState } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import { fetchCart, removeCartItem as deleteCartItemRequest, updateCartItemQuantity } from '../api/cart';

function money(n) {
  if (n == null || Number.isNaN(Number(n))) return '—';
  return `$${Number(n).toFixed(2)}`;
}

export default function CartPage() {
  const { refreshCart } = useOutletContext();
  const [cart, setCart] = useState(null);
  const [error, setError] = useState('');
  const [savingCartItemBookId, setSavingCartItemBookId] = useState(null);

  const load = useCallback(async () => {
    setError('');
    try {
      const c = await fetchCart();
      setCart(c);
      await refreshCart();
    } catch (e) {
      if (e?.status === 401) {
        setError('Please log in to see your cart.');
        setCart(null);
      } else {
        setError(e.message || 'Could not load cart');
      }
    }
  }, [refreshCart]);

  useEffect(() => {
    load();
  }, [load]);

  async function saveQty(bookId, quantity) {
    setSavingCartItemBookId(bookId);
    try {
      const c = await updateCartItemQuantity(bookId, quantity);
      setCart(c);
      await refreshCart();
    } catch (e) {
      setError(e.message || 'Update failed');
    } finally {
      setSavingCartItemBookId(null);
    }
  }

  async function handleRemoveCartItem(bookId) {
    setSavingCartItemBookId(bookId);
    try {
      await deleteCartItemRequest(bookId);
      await load();
    } catch (e) {
      setError(e.message || 'Remove failed');
    } finally {
      setSavingCartItemBookId(null);
    }
  }

  if (error && !cart) {
    return (
      <section>
        <h1>Cart</h1>
        <p className="error">{error}</p>
        <p>
          <Link to="/login">Login</Link>
        </p>
      </section>
    );
  }

  const cartItems = cart?.items ?? [];
  const empty = cartItems.length === 0;

  return (
    <section>
      <h1>Cart</h1>
      {error && <p className="error">{error}</p>}
      {empty ? (
        <p className="muted">Your cart is empty.</p>
      ) : (
        <>
          <table className="simple">
            <thead>
              <tr>
                <th>Title</th>
                <th className="num">Each</th>
                <th>Qty</th>
                <th className="num">Item total</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {cartItems.map((cartItem) => (
                <tr key={cartItem.bookId}>
                  <td>{cartItem.title}</td>
                  <td className="num">{money(cartItem.unitPrice)}</td>
                  <td>
                    <CartItemQty
                      bookId={cartItem.bookId}
                      quantity={cartItem.quantity}
                      disabled={savingCartItemBookId === cartItem.bookId}
                      onSave={(q) => saveQty(cartItem.bookId, q)}
                    />
                  </td>
                  <td className="num">{money(cartItem.itemTotal)}</td>
                  <td className="row-actions">
                    <button
                      type="button"
                      className="btn"
                      disabled={savingCartItemBookId === cartItem.bookId}
                      onClick={() => handleRemoveCartItem(cartItem.bookId)}
                    >
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <p>
            <strong>Total:</strong> {money(cart?.totalAmount)}{' '}
            <Link to="/checkout">Checkout</Link>
          </p>
        </>
      )}
    </section>
  );
}

function CartItemQty({ bookId, quantity, disabled, onSave }) {
  const [val, setVal] = useState(String(quantity));

  useEffect(() => {
    setVal(String(quantity));
  }, [bookId, quantity]);

  return (
    <span>
      <input
        className="input-sm"
        type="number"
        min={1}
        disabled={disabled}
        value={val}
        onChange={(e) => setVal(e.target.value)}
      />{' '}
      <button
        type="button"
        className="btn"
        disabled={disabled}
        onClick={() => {
          const n = parseInt(val, 10);
          onSave(Number.isFinite(n) && n >= 1 ? n : 1);
        }}
      >
        Update
      </button>
    </span>
  );
}
