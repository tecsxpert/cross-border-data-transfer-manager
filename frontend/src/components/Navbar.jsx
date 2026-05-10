import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import {
  LayoutDashboard, ArrowLeftRight, BarChart3,
  Shield, LogOut, Globe2, Menu, X
} from 'lucide-react'
import { useState } from 'react'

const nav = [
  { to: '/dashboard',  label: 'Dashboard',  icon: LayoutDashboard },
  { to: '/transfers',  label: 'Transfers',  icon: ArrowLeftRight },
  { to: '/analytics',  label: 'Analytics',  icon: BarChart3 },
  { to: '/audit-log',  label: 'Audit Log',  icon: Shield },
]

export default function Navbar() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [open, setOpen] = useState(false)

  const handleLogout = () => { logout(); navigate('/login') }

  return (
    <header className="sticky top-0 z-50 bg-gray-900/90 backdrop-blur-md border-b border-gray-800">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/dashboard" className="flex items-center gap-2.5">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <Globe2 size={18} className="text-white" />
            </div>
            <span className="font-bold text-white hidden sm:block">Tool-135</span>
          </Link>

          {/* Desktop nav */}
          <nav className="hidden md:flex items-center gap-1">
            {nav.map(({ to, label, icon: Icon }) => (
              <Link
                key={to} to={to}
                className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm font-medium transition-colors duration-150 ${
                  location.pathname.startsWith(to)
                    ? 'bg-primary-600/20 text-primary-400'
                    : 'text-gray-400 hover:text-gray-100 hover:bg-gray-800'
                }`}
              >
                <Icon size={16} />{label}
              </Link>
            ))}
          </nav>

          {/* User + logout */}
          <div className="flex items-center gap-3">
            <span className="hidden sm:block text-sm text-gray-400">
              <span className="text-gray-200 font-medium">{user?.username}</span>
              <span className="ml-1.5 px-1.5 py-0.5 bg-primary-900/50 text-primary-300 rounded text-xs border border-primary-800">
                {user?.role}
              </span>
            </span>
            <button onClick={handleLogout} className="btn-secondary !px-3 !py-2 text-red-400 hover:text-red-300 hover:bg-red-900/20">
              <LogOut size={15} />
            </button>
            <button className="md:hidden p-2 text-gray-400" onClick={() => setOpen(!open)}>
              {open ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>

        {/* Mobile nav */}
        {open && (
          <div className="md:hidden pb-3 border-t border-gray-800 pt-3 space-y-1">
            {nav.map(({ to, label, icon: Icon }) => (
              <Link
                key={to} to={to}
                onClick={() => setOpen(false)}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  location.pathname.startsWith(to)
                    ? 'bg-primary-600/20 text-primary-400'
                    : 'text-gray-400 hover:text-gray-100 hover:bg-gray-800'
                }`}
              >
                <Icon size={16} />{label}
              </Link>
            ))}
          </div>
        )}
      </div>
    </header>
  )
}
