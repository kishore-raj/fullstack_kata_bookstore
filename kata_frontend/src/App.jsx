import { BrowserRouter, Navigate, Route, Routes, Link } from 'react-router-dom';
import BookPage from './pages/BookPage.jsx';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <header className="app-header">
        <strong className="app-brand">Kata Bookstore</strong>
        <nav>
          <Link to="/books">Books</Link>
        </nav>
      </header>
      <Routes>
        <Route path="/" element={<Navigate to="/books" replace />} />
        <Route path="/books" element={<BookPage />} />
      </Routes>
    </BrowserRouter>
  );
}
