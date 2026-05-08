import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import API from "../services/api";
import Navbar from "../components/Navbar";
import { useAuth } from "../contexts/AuthContext";

export default function Detail() {
  const { id } = useParams();
  const [transfer, setTransfer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [aiAnalysis, setAiAnalysis] = useState(null);
  const [aiLoading, setAiLoading] = useState(false);
  const { logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTransfer = async () => {
      try {
        setLoading(true);
        const res = await API.get(`/data-transfers/${id}`);
        setTransfer(res.data);
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };
    void fetchTransfer();
  }, [id]);

  const handleLogout = () => {
    logout();
    window.location.href = "/";
  };

  const handleAiAnalysis = async () => {
    setAiLoading(true);
    try {
      const response = await fetch('http://localhost:5000/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(transfer),
      });
      const data = await response.json();
      setAiAnalysis(data.analysis);
    } catch (error) {
      console.error('AI analysis failed:', error);
      setAiAnalysis('Error: Unable to analyze transfer');
    } finally {
      setAiLoading(false);
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center h-screen">Loading...</div>;
  }

  if (!transfer) {
    return <div className="p-6">Transfer not found.</div>;
  }

  return (
    <div>
      <Navbar onLogout={handleLogout} />
      <div className="p-6 bg-gray-50 min-h-screen">
        <button
          onClick={() => navigate(-1)}
          className="mb-4 px-4 py-2 bg-slate-600 text-white rounded hover:bg-slate-700"
        >
          Back to transfers
        </button>
        <div className="bg-white rounded-lg shadow p-6">
          <h1 className="text-3xl font-bold mb-4">Transfer Details</h1>
          {transfer.complianceScore && (
            <div className="mb-4">
              <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                transfer.complianceScore >= 80 ? "bg-green-100 text-green-800" :
                transfer.complianceScore >= 60 ? "bg-yellow-100 text-yellow-800" :
                "bg-red-100 text-red-800"
              }`}>
                Compliance Score: {transfer.complianceScore}/100
              </span>
            </div>
          )}
          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <p className="text-sm text-gray-500">Source Country</p>
              <p className="mt-1 text-lg font-semibold">{transfer.sourceCountry}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Destination Country</p>
              <p className="mt-1 text-lg font-semibold">{transfer.destinationCountry}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Data Type</p>
              <p className="mt-1 text-lg font-semibold">{transfer.dataType}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Status</p>
              <p className="mt-1 text-lg font-semibold">{transfer.status}</p>
            </div>
            <div className="md:col-span-2">
              <p className="text-sm text-gray-500">Description</p>
              <p className="mt-1 text-lg">{transfer.description || "No description provided."}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Created Date</p>
              <p className="mt-1 text-lg">{new Date(transfer.createdDate).toLocaleString()}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Last Modified</p>
              <p className="mt-1 text-lg">{new Date(transfer.lastModifiedDate).toLocaleString()}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Compliance Score</p>
              <p className="mt-1 text-lg font-semibold">{transfer.complianceScore || "N/A"}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Risk Level</p>
              <p className="mt-1 text-lg font-semibold">{transfer.riskLevel || "N/A"}</p>
            </div>
            <div className="md:col-span-2">
              <p className="text-sm text-gray-500">Legal Basis</p>
              <p className="mt-1 text-lg">{transfer.legalBasis || "N/A"}</p>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow p-6 mt-6">
          <h2 className="text-xl font-bold mb-4">AI Compliance Analysis</h2>
          <button
            onClick={handleAiAnalysis}
            disabled={aiLoading}
            className="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700 disabled:opacity-50"
          >
            {aiLoading ? "Analyzing..." : "Analyze with AI"}
          </button>
          {aiAnalysis && (
            <div className="mt-4 p-4 bg-gray-50 rounded">
              <pre className="whitespace-pre-wrap">{aiAnalysis}</pre>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
