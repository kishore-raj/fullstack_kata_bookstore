import { useCallback, useEffect, useState } from 'react';
import {
  BrowserRouter,
  Link,
  Navigate,
  Outlet,
  Route,
  Routes,
  useLocation,
} from 'react-router-dom';
import { fetchCart } from './api/cart';
import { logout } from './api/auth';
import BooksPage from './pages/BooksPage.jsx';
import CartPage from './pages/CartPage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import CheckoutPage from './pages/CheckoutPage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';
import './App.css';

const USERNAME_KEY = 'bookstoreUsername';

function totalUnits(cart) {
  if (!cart?.items?.length) return 0;
  return cart.items.reduce((n, cartItem) => n + (Number(cartItem.quantity) || 0), 0);
}

function Layout() {
  const location = useLocation();
  const [cartUnits, setCartUnits] = useState(0);
  const [loggedIn, setLoggedIn] = useState(false);

  const refreshCart = useCallback(async () => {
    try {
      const cart = await fetchCart();
      setCartUnits(totalUnits(cart));
      setLoggedIn(true);
    } catch (e) {
      if (e?.status === 401) {
        setCartUnits(0);
        setLoggedIn(false);
        sessionStorage.removeItem(USERNAME_KEY);
      }
    }
  }, []);

  useEffect(() => {
    refreshCart();
  }, [location.pathname, refreshCart]);

  async function handleLogout() {
    try {
      await logout();
    } catch {
      /* ignoring for now */
    }
    sessionStorage.removeItem(USERNAME_KEY);
    setLoggedIn(false);
    setCartUnits(0);
    await refreshCart();
  }

  const displayName = sessionStorage.getItem(USERNAME_KEY);

  return (
    <div className="layout">
      <header className="top">
        <Link to="/books" className="logo">
          Bookstore
        </Link>
        <nav className="nav">
          <Link to="/books">Books</Link>
          <Link to="/cart">Cart ({cartUnits})</Link>
          <Link to="/orders">Orders</Link>
          {loggedIn ? (
            <>
              <span className="nav-user">{displayName || 'Signed in'}</span>
              <button type="button" className="link-btn" onClick={handleLogout}>
                Logout
              </button>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}
        </nav>
      </header>
      <div className="content">
        <Outlet context={{ refreshCart }} />
      </div>
    </div>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Navigate to="/books" replace />} />
          <Route path="/books" element={<BooksPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/checkout" element={<CheckoutPage />} />
          <Route path="/orders" element={<OrdersPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
