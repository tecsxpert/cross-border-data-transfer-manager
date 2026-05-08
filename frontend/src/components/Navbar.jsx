import { Link } from "react-router-dom";

export default function Navbar({ onLogout }) {
  return (
    <nav className="bg-blue-600 text-white p-4 shadow-lg">
      <div className="max-w-6xl mx-auto flex justify-between items-center">
        <Link to="/dashboard" className="text-2xl font-bold hover:text-blue-100">
          Data Transfer Manager
        </Link>
        <ul className="flex gap-6 items-center">
          <li>
            <Link to="/dashboard" className="hover:text-blue-100">
              Dashboard
            </Link>
          </li>
          <li>
            <Link to="/list" className="hover:text-blue-100">
              Transfers
            </Link>
          </li>
          <li>
            <Link to="/analytics" className="hover:text-blue-100">
              Analytics
            </Link>
          </li>
          <li>
            <button
              onClick={onLogout}
              className="bg-red-600 px-4 py-2 rounded hover:bg-red-700"
            >
              Logout
            </button>
          </li>
        </ul>
      </div>
    </nav>
  );
}