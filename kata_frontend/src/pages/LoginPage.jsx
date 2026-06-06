import { useState } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import { login, register } from '../api/auth';

export default function LoginPage() {
  const navigate = useNavigate();
  const { refreshCart } = useOutletContext();
  const [mode, setMode] = useState('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [msg, setMsg] = useState('');
  const [busy, setBusy] = useState(false);

  async function onLogin(e) {
    e.preventDefault();
    setMsg('');
    setBusy(true);
    try {
      const user = await login({ username: username.trim(), password });
      if (user?.username) sessionStorage.setItem('bookstoreUsername', user.username);
      await refreshCart();
      navigate('/books');
    } catch (err) {
      setMsg(err.message || 'Login failed');
    } finally {
      setBusy(false);
    }
  }

  async function onRegister(e) {
    e.preventDefault();
    setMsg('');
    setBusy(true);
    try {
      await register({
        username: username.trim(),
        password,
        email: email.trim(),
      });
      const user = await login({ username: username.trim(), password });
      if (user?.username) sessionStorage.setItem('bookstoreUsername', user.username);
      await refreshCart();
      navigate('/books');
    } catch (err) {
      setMsg(err.message || 'Register failed');
    } finally {
      setBusy(false);
    }
  }

  return (
    <section>
      <h1>Account</h1>
      <p className="muted">Register a new user or log in. Session is stored in a cookie.</p>
      {msg && <p className="error">{msg}</p>}

      <p>
        <button type="button" className="btn" onClick={() => setMode('login')}>
          Login
        </button>{' '}
        <button type="button" className="btn" onClick={() => setMode('register')}>
          Register
        </button>
      </p>

      {mode === 'login' ? (
        <form className="stack" onSubmit={onLogin}>
          <label>
            Username
            <input value={username} onChange={(e) => setUsername(e.target.value)} required />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>
          <button type="submit" className="btn" disabled={busy}>
            {busy ? '…' : 'Log in'}
          </button>
        </form>
      ) : (
        <form className="stack" onSubmit={onRegister}>
          <label>
            Username
            <input value={username} onChange={(e) => setUsername(e.target.value)} required minLength={3} />
          </label>
          <label>
            Email
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          </label>
          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
            />
          </label>
          <button type="submit" className="btn" disabled={busy}>
            {busy ? '…' : 'Register and log in'}
          </button>
        </form>
      )}
    </section>
  );
}
