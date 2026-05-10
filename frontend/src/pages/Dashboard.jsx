import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { statsApi, transferApi } from '../services/api'
import KPICard from '../components/KPICard'
import StatusBadge from '../components/StatusBadge'
import { ArrowLeftRight, CheckCircle2, Clock, XCircle, TrendingUp, Plus, Download, RefreshCw } from 'lucide-react'

export default function Dashboard() {
  const [stats, setStats] = useState(null)
  const [recent, setRecent] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  const load = async () => {
    setLoading(true)
    try {
      const [s, t] = await Promise.all([
        statsApi.get(),
        transferApi.getAll({ page: 0, size: 5, sortBy: 'createdAt', direction: 'desc' }),
      ])
      setStats(s.data)
      setRecent(t.data.content || [])
    } catch (e) {
      console.error(e)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  const handleExport = async () => {
    try {
      const res = await transferApi.exportCsv()
      const url = URL.createObjectURL(res.data)
      const a = document.createElement('a'); a.href = url; a.download = 'transfers.csv'; a.click()
      URL.revokeObjectURL(url)
    } catch (e) { console.error(e) }
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-2xl font-bold text-white">Dashboard</h1>
          <p className="text-gray-400 text-sm mt-1">Cross-border data transfer overview</p>
        </div>
        <div className="flex items-center gap-3">
          <button onClick={load} className="btn-secondary !px-3"><RefreshCw size={15} /></button>
          <button onClick={handleExport} className="btn-secondary"><Download size={15} />Export CSV</button>
          <button id="new-transfer-btn" onClick={() => navigate('/transfers/new')} className="btn-primary">
            <Plus size={15} />New Transfer
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      {loading ? (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          {[...Array(4)].map((_, i) => <div key={i} className="skeleton h-28 rounded-xl" />)}
        </div>
      ) : (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <KPICard title="Total Transfers" value={stats?.total} icon={ArrowLeftRight} color="blue" subtitle="All time" />
          <KPICard title="Pending Review" value={stats?.pending} icon={Clock} color="yellow" subtitle="Awaiting action" />
          <KPICard title="Approved" value={stats?.approved} icon={CheckCircle2} color="green" subtitle="Compliant" />
          <KPICard title="Avg Compliance" value={stats?.avgComplianceScore ? `${stats.avgComplianceScore}%` : '—'} icon={TrendingUp} color="purple" subtitle="Score" />
        </div>
      )}

      {/* Recent Transfers */}
      <div className="card">
        <div className="flex items-center justify-between mb-5">
          <h2 className="text-lg font-semibold text-white">Recent Transfers</h2>
          <button onClick={() => navigate('/transfers')} className="text-primary-400 hover:text-primary-300 text-sm font-medium">
            View all →
          </button>
        </div>

        {loading ? (
          <div className="space-y-3">
            {[...Array(5)].map((_, i) => <div key={i} className="skeleton h-14 rounded-lg" />)}
          </div>
        ) : recent.length === 0 ? (
          <div className="text-center py-12 text-gray-500">
            <ArrowLeftRight size={40} className="mx-auto mb-3 opacity-30" />
            <p>No transfers yet. Create your first one.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-800">
                  <th className="text-left pb-3 text-gray-400 font-medium">Title</th>
                  <th className="text-left pb-3 text-gray-400 font-medium hidden sm:table-cell">Route</th>
                  <th className="text-left pb-3 text-gray-400 font-medium">Status</th>
                  <th className="text-left pb-3 text-gray-400 font-medium hidden md:table-cell">Risk</th>
                  <th className="text-left pb-3 text-gray-400 font-medium hidden lg:table-cell">Score</th>
                </tr>
              </thead>
              <tbody>
                {recent.map(t => (
                  <tr key={t.id} className="table-row" onClick={() => navigate(`/transfers/${t.id}`)}>
                    <td className="py-3.5 pr-4 text-gray-200 font-medium max-w-[200px] truncate">{t.title}</td>
                    <td className="py-3.5 pr-4 text-gray-400 hidden sm:table-cell">
                      {t.sourceCountry} → {t.destinationCountry}
                    </td>
                    <td className="py-3.5 pr-4"><StatusBadge status={t.status} /></td>
                    <td className="py-3.5 pr-4 hidden md:table-cell"><StatusBadge status={t.riskLevel} type="risk" /></td>
                    <td className="py-3.5 text-gray-300 hidden lg:table-cell">
                      {t.complianceScore != null ? (
                        <div className="flex items-center gap-2">
                          <div className="flex-1 bg-gray-800 rounded-full h-1.5 w-16">
                            <div
                              className="h-1.5 rounded-full bg-primary-600"
                              style={{ width: `${t.complianceScore}%` }}
                            />
                          </div>
                          <span>{t.complianceScore}%</span>
                        </div>
                      ) : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}
