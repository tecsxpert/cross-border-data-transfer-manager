import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";
import Navbar from "../components/Navbar";
import { useAuth } from "../contexts/AuthContext";

export default function List() {
  const [data, setData] = useState([]);
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showModal, setShowModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form, setForm] = useState({
    sourceCountry: "",
    destinationCountry: "",
    dataType: "",
    status: "Pending",
    description: "",
    complianceScore: "",
    riskLevel: "Low",
    legalBasis: "",
  });
  const navigate = useNavigate();
  const { logout } = useAuth();

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      let url = `/data-transfers?page=${page}&size=10`;
      if (debouncedSearch || statusFilter || startDate || endDate) {
        const params = new URLSearchParams();
        if (debouncedSearch) params.append("q", debouncedSearch);
        if (statusFilter) params.append("status", statusFilter);
        if (startDate) params.append("startDate", `${startDate}T00:00:00`);
        if (endDate) params.append("endDate", `${endDate}T23:59:59`);
        params.append("page", String(page));
        params.append("size", "10");
        url = `/data-transfers/search/filtered?${params.toString()}`;
      }
      const res = await API.get(url);
      if (res.data.content) {
        setData(res.data.content);
        setTotalPages(res.data.totalPages || 1);
      } else {
        setData(res.data);
        setTotalPages(1);
      }
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  }, [page, debouncedSearch, statusFilter, startDate, endDate]);

  useEffect(() => {
    const timeout = setTimeout(() => setDebouncedSearch(search), 400);
    return () => clearTimeout(timeout);
  }, [search]);

  useEffect(() => {
    const load = async () => {
      await fetchData();
    };

    void load();
  }, [fetchData]);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      if (editingItem) {
        await API.put(`/data-transfers/${editingItem.id}`, form);
      } else {
        await API.post("/data-transfers", form);
      }
      setShowModal(false);
      setEditingItem(null);
      setForm({
        sourceCountry: "",
        destinationCountry: "",
        dataType: "",
        status: "Pending",
        description: "",
        complianceScore: "",
        riskLevel: "Low",
        legalBasis: "",
      });
      fetchData();
    } catch (error) {
      console.error(error);
      alert(editingItem ? "Error updating transfer" : "Error creating transfer");
    }
  };

  const handleEdit = (item) => {
    setEditingItem(item);
    setForm({
      sourceCountry: item.sourceCountry,
      destinationCountry: item.destinationCountry,
      dataType: item.dataType,
      status: item.status,
      description: item.description || "",
      complianceScore: item.complianceScore || "",
      riskLevel: item.riskLevel || "Low",
      legalBasis: item.legalBasis || "",
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm("Are you sure?")) {
      try {
        await API.delete(`/data-transfers/${id}`);
        fetchData();
      } catch (error) {
        console.error(error);
        alert("Error deleting transfer");
      }
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const filteredData = statusFilter
    ? data.filter((item) => item.status === statusFilter)
    : data;

  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }

  return (
    <div>
      <Navbar onLogout={handleLogout} />
      <div className="p-6 bg-gray-50 min-h-screen">
        <div className="flex justify-between items-center mb-6">
          <h1 className="text-3xl font-bold">Data Transfers</h1>
          <button
            onClick={() => {
              setForm({
                sourceCountry: "",
                destinationCountry: "",
                dataType: "",
                status: "Pending",
                description: "",
                complianceScore: "",
                riskLevel: "Low",
                legalBasis: "",
              });
              setShowModal(true);
            }}
            className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          >
            + New Transfer
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <input
            type="text"
            placeholder="Search by country..."
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
            className="border p-3 rounded"
          />
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
            className="border p-3 rounded"
          >
            <option value="">All Status</option>
            <option value="Approved">Approved</option>
            <option value="Pending">Pending</option>
          </select>
          <input
            type="date"
            value={startDate}
            onChange={(e) => {
              setStartDate(e.target.value);
              setPage(0);
            }}
            className="border p-3 rounded"
          />
          <input
            type="date"
            value={endDate}
            onChange={(e) => {
              setEndDate(e.target.value);
              setPage(0);
            }}
            className="border p-3 rounded"
          />
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <table className="w-full">
            <thead className="bg-gray-200">
              <tr>
                <th className="p-4 text-left">ID</th>
                <th className="p-4 text-left">Source</th>
                <th className="p-4 text-left">Destination</th>
                <th className="p-4 text-left">Type</th>
                <th className="p-4 text-left">Status</th>
                <th className="p-4 text-left">Compliance</th>
                <th className="p-4 text-left">Risk</th>
                <th className="p-4 text-left">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredData.length === 0 ? (
                <tr>
                  <td colSpan="8" className="p-4 text-center text-gray-500">
                    No records found
                  </td>
                </tr>
              ) : (
                filteredData.map((item) => (
                  <tr key={item.id} className="border-t hover:bg-gray-50">
                    <td className="p-4">{item.id}</td>
                    <td className="p-4">{item.sourceCountry}</td>
                    <td className="p-4">{item.destinationCountry}</td>
                    <td className="p-4">{item.dataType}</td>
                    <td className="p-4">
                      <span
                        className={`px-3 py-1 rounded-full text-sm font-semibold ${
                          item.status === "Approved"
                            ? "bg-green-100 text-green-800"
                            : "bg-yellow-100 text-yellow-800"
                        }`}
                      >
                        {item.status}
                      </span>
                    </td>
                    <td className="p-4">{item.complianceScore || "N/A"}</td>
                    <td className="p-4">{item.riskLevel || "N/A"}</td>
                    <td className="p-4 space-x-2">
                      <button
                        onClick={() => navigate(`/list/${item.id}`)}
                        className="bg-slate-600 text-white px-3 py-1 rounded text-sm hover:bg-slate-700"
                      >
                        View
                      </button>
                      <button
                        onClick={() => handleEdit(item)}
                        className="bg-blue-600 text-white px-3 py-1 rounded text-sm hover:bg-blue-700"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(item.id)}
                        className="bg-red-600 text-white px-3 py-1 rounded text-sm hover:bg-red-700"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {!search && totalPages > 1 && (
          <div className="flex justify-center mt-6 space-x-2">
            <button
              onClick={() => setPage(Math.max(0, page - 1))}
              disabled={page === 0}
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
            >
              Previous
            </button>
            <span className="px-4 py-2">
              Page {page + 1} of {totalPages}
            </span>
            <button
              onClick={() => setPage(Math.min(totalPages - 1, page + 1))}
              disabled={page === totalPages - 1}
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50"
            >
              Next
            </button>
          </div>
        )}

        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white p-8 rounded-lg shadow-lg w-96">
              <h2 className="text-2xl font-bold mb-4">{editingItem ? "Edit Transfer" : "New Transfer"}</h2>
              <form onSubmit={handleCreate}>
                <input
                  type="text"
                  placeholder="Source Country"
                  value={form.sourceCountry}
                  onChange={(e) => setForm({ ...form, sourceCountry: e.target.value })}
                  className="border p-2 w-full mb-3"
                  required
                />
                <input
                  type="text"
                  placeholder="Destination Country"
                  value={form.destinationCountry}
                  onChange={(e) => setForm({ ...form, destinationCountry: e.target.value })}
                  className="border p-2 w-full mb-3"
                  required
                />
                <input
                  type="text"
                  placeholder="Data Type"
                  value={form.dataType}
                  onChange={(e) => setForm({ ...form, dataType: e.target.value })}
                  className="border p-2 w-full mb-3"
                  required
                />
                <select
                  value={form.status}
                  onChange={(e) => setForm({ ...form, status: e.target.value })}
                  className="border p-2 w-full mb-3"
                >
                  <option value="Pending">Pending</option>
                  <option value="Approved">Approved</option>
                </select>
                <textarea
                  placeholder="Description"
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                  className="border p-2 w-full mb-4"
                />
                <input
                  type="number"
                  placeholder="Compliance Score (0-100)"
                  value={form.complianceScore}
                  onChange={(e) => setForm({ ...form, complianceScore: e.target.value })}
                  className="border p-2 w-full mb-3"
                  min="0"
                  max="100"
                />
                <select
                  value={form.riskLevel}
                  onChange={(e) => setForm({ ...form, riskLevel: e.target.value })}
                  className="border p-2 w-full mb-3"
                >
                  <option value="Low">Low</option>
                  <option value="Medium">Medium</option>
                  <option value="High">High</option>
                </select>
                <input
                  type="text"
                  placeholder="Legal Basis"
                  value={form.legalBasis}
                  onChange={(e) => setForm({ ...form, legalBasis: e.target.value })}
                  className="border p-2 w-full mb-4"
                />
                <div className="flex gap-2">
                  <button
                    type="submit"
                    className="flex-1 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                  >
                    {editingItem ? "Update" : "Create"}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setShowModal(false);
                      setEditingItem(null);
                      setForm({
                        sourceCountry: "",
                        destinationCountry: "",
                        dataType: "",
                        status: "Pending",
                        description: "",
                        complianceScore: "",
                        riskLevel: "Low",
                        legalBasis: "",
                      });
                    }}
                    className="flex-1 bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500"
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
