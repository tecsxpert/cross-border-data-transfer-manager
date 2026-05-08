import { useState } from "react";
import API from "../services/api";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Login() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await API.post("/auth/login", form);
      login(res.data.token);
      navigate("/dashboard");
    } catch (error) {
      console.error("Login error:", error);
      const responseMessage = error?.response?.data || error?.message || "Login Failed";
      setError(typeof responseMessage === "string" ? responseMessage : JSON.stringify(responseMessage));
    }
  };

  return (
    <div className="flex items-center justify-center h-screen bg-gray-100">
      <div className="p-8 bg-white shadow-lg rounded w-96">
        <h2 className="text-2xl font-bold mb-6 text-center">Login</h2>

        {error && <div className="text-red-500 text-sm mb-4">{error}</div>}

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Username"
            className="border p-3 w-full mb-3 rounded"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            required
          />

          <input
            type="password"
            placeholder="Password"
            className="border p-3 w-full mb-3 rounded"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            required
          />

          <button className="bg-blue-600 text-white px-4 py-3 w-full rounded font-semibold hover:bg-blue-700">
            Login
          </button>
        </form>

        <p className="text-center text-sm mt-4 text-gray-600">
          Demo: user1/password123
        </p>
      </div>
    </div>
  );
}