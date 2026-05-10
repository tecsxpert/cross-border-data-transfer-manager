import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './contexts/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Navbar from './components/Navbar'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import TransferList from './pages/TransferList'
import TransferDetail from './pages/TransferDetail'
import TransferForm from './pages/TransferForm'
import Analytics from './pages/Analytics'
import AuditLog from './pages/AuditLog'

function Layout({ children }) {
  return (
    <div className="min-h-screen bg-gray-950">
      <Navbar />
      <main>{children}</main>
    </div>
  )
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={
            <ProtectedRoute><Layout><Dashboard /></Layout></ProtectedRoute>
          } />
          <Route path="/transfers" element={
            <ProtectedRoute><Layout><TransferList /></Layout></ProtectedRoute>
          } />
          <Route path="/transfers/new" element={
            <ProtectedRoute><Layout><TransferForm /></Layout></ProtectedRoute>
          } />
          <Route path="/transfers/:id" element={
            <ProtectedRoute><Layout><TransferDetail /></Layout></ProtectedRoute>
          } />
          <Route path="/transfers/:id/edit" element={
            <ProtectedRoute><Layout><TransferForm /></Layout></ProtectedRoute>
          } />
          <Route path="/analytics" element={
            <ProtectedRoute><Layout><Analytics /></Layout></ProtectedRoute>
          } />
          <Route path="/audit-log" element={
            <ProtectedRoute><Layout><AuditLog /></Layout></ProtectedRoute>
          } />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
