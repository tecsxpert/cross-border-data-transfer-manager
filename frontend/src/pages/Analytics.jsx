import { useEffect, useState } from 'react'
import { transferApi, statsApi } from '../services/api'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, LineChart, Line, CartesianGrid, Legend } from 'recharts'

const COLORS = { APPROVED: '#22c55e', PENDING: '#eab308', REJECTED: '#ef4444' }
const RISK_COLORS = { LOW: '#3b82f6', MEDIUM: '#f97316', HIGH: '#ef4444', CRITICAL: '#a855f7' }

export default function Analytics() {
  const [transfers, setTransfers] = useState([])
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      transferApi.getAll({ page: 0, size: 100 }),
      statsApi.get(),
    ]).then(([t, s]) => {
      setTransfers(t.data.content || [])
      setStats(s.data)
    }).finally(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {[...Array(4)].map((_, i) => <div key={i} className="skeleton h-64 rounded-xl" />)}
      </div>
    </div>
  )

  // Status distribution
  const statusData = ['PENDING', 'APPROVED', 'REJECTED'].map(s => ({
    name: s, value: transfers.filter(t => t.status === s).length
  }))

  // Risk distribution
  const riskData = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'].map(r => ({
    name: r, value: transfers.filter(t => t.riskLevel === r).length
  }))

  // Score distribution by category
  const categories = [...new Set(transfers.map(t => t.dataCategory).filter(Boolean))]
  const categoryData = categories.map(cat => ({
    name: cat.length > 15 ? cat.substring(0, 15) + '…' : cat,
    avgScore: Math.round(transfers.filter(t => t.dataCategory === cat && t.complianceScore != null).reduce((sum, t) => sum + t.complianceScore, 0) /
      (transfers.filter(t => t.dataCategory === cat && t.complianceScore != null).length || 1))
  }))

  // Score over time
  const scoreData = transfers.filter(t => t.createdAt && t.complianceScore != null).slice(-20).map(t => ({
    date: new Date(t.createdAt).toLocaleDateString(),
    score: t.complianceScore,
  }))

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-white">Analytics</h1>
        <p className="text-gray-400 text-sm mt-1">Compliance insights across all transfers</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Status Pie */}
        <div className="card">
          <h3 className="font-semibold text-white mb-4">Transfer Status Distribution</h3>
          <ResponsiveContainer width="100%" height={240}>
            <PieChart>
              <Pie data={statusData} cx="50%" cy="50%" outerRadius={90} dataKey="value" label={({ name, value }) => `${name}: ${value}`}>
                {statusData.map((entry) => <Cell key={entry.name} fill={COLORS[entry.name]} />)}
              </Pie>
              <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Risk Bar */}
        <div className="card">
          <h3 className="font-semibold text-white mb-4">Risk Level Distribution</h3>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={riskData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="name" tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <YAxis tick={{ fill: '#9ca3af', fontSize: 12 }} />
              <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} />
              <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                {riskData.map((entry) => <Cell key={entry.name} fill={RISK_COLORS[entry.name]} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Score by Category */}
        <div className="card">
          <h3 className="font-semibold text-white mb-4">Avg Compliance Score by Category</h3>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={categoryData} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis type="number" domain={[0, 100]} tick={{ fill: '#9ca3af', fontSize: 11 }} />
              <YAxis type="category" dataKey="name" tick={{ fill: '#9ca3af', fontSize: 10 }} width={100} />
              <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} />
              <Bar dataKey="avgScore" fill="#1B4F8A" radius={[0, 4, 4, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Score over time */}
        <div className="card">
          <h3 className="font-semibold text-white mb-4">Compliance Score Trend</h3>
          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={scoreData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis dataKey="date" tick={{ fill: '#9ca3af', fontSize: 10 }} />
              <YAxis domain={[0, 100]} tick={{ fill: '#9ca3af', fontSize: 11 }} />
              <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px', color: '#e5e7eb' }} />
              <Line type="monotone" dataKey="score" stroke="#3b82f6" strokeWidth={2} dot={{ fill: '#3b82f6', r: 3 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
