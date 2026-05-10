import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { transferApi } from '../services/api'
import StatusBadge from '../components/StatusBadge'
import { Plus, Search, Filter, ChevronLeft, ChevronRight, ArrowLeftRight } from 'lucide-react'

const STATUSES = ['', 'PENDING', 'APPROVED', 'REJECTED']

export default function TransferList() {
  const navigate = useNavigate()
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0 })
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [q, setQ] = useState('')
  const [status, setStatus] = useState('')
  const [debouncedQ, setDebouncedQ] = useState('')

  // Debounce search
  useEffect(() => {
    const t = setTimeout(() => setDebouncedQ(q), 400)
    return () => clearTimeout(t)
  }, [q])

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const params = { page, size: 10, q: debouncedQ || undefined, status: status || undefined }
      const res = debouncedQ || status
        ? await transferApi.search(params)
        : await transferApi.getAll({ page, size: 10, sortBy: 'createdAt', direction: 'desc' })
      setData(res.data)
    } catch (e) { console.error(e) }
    finally { setLoading(false) }
  }, [page, debouncedQ, status])

  useEffect(() => { load() }, [load])

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-white">Transfers</h1>
          <p className="text-gray-400 text-sm mt-1">{data.totalElements} total records</p>
        </div>
        <button id="create-transfer-btn" onClick={() => navigate('/transfers/new')} className="btn-primary">
          <Plus size={15} />New Transfer
        </button>
      </div>

      {/* Filters */}
      <div className="card mb-6 !p-4">
        <div className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
            <input
              id="search-input"
              className="input pl-9"
              placeholder="Search by title, country, category…"
              value={q}
              onChange={e => { setQ(e.target.value); setPage(0) }}
            />
          </div>
          <div className="relative">
            <Filter size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500" />
            <select
              id="status-filter"
              className="input pl-9 pr-8 appearance-none cursor-pointer"
              value={status}
              onChange={e => { setStatus(e.target.value); setPage(0) }}
            >
              {STATUSES.map(s => <option key={s} value={s}>{s || 'All Statuses'}</option>)}
            </select>
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="card !p-0 overflow-hidden">
        {loading ? (
          <div className="p-6 space-y-3">
            {[...Array(6)].map((_, i) => <div key={i} className="skeleton h-12 rounded" />)}
          </div>
        ) : data.content.length === 0 ? (
          <div className="text-center py-16 text-gray-500">
            <ArrowLeftRight size={48} className="mx-auto mb-3 opacity-20" />
            <p className="font-medium">No transfers found</p>
            <p className="text-sm mt-1">Try adjusting your search or create a new transfer</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-800 bg-gray-900/50">
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium">Title</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium hidden md:table-cell">Source → Destination</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium hidden lg:table-cell">Category</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium">Status</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium hidden sm:table-cell">Risk</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium hidden xl:table-cell">Score</th>
                  <th className="text-left px-6 py-3.5 text-gray-400 font-medium hidden lg:table-cell">Deadline</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map(t => (
                  <tr key={t.id} className="table-row" onClick={() => navigate(`/transfers/${t.id}`)}>
                    <td className="px-6 py-4 text-gray-200 font-medium max-w-[180px] truncate">{t.title}</td>
                    <td className="px-6 py-4 text-gray-400 hidden md:table-cell whitespace-nowrap">
                      {t.sourceCountry} → {t.destinationCountry}
                    </td>
                    <td className="px-6 py-4 text-gray-400 hidden lg:table-cell">{t.dataCategory}</td>
                    <td className="px-6 py-4"><StatusBadge status={t.status} /></td>
                    <td className="px-6 py-4 hidden sm:table-cell"><StatusBadge status={t.riskLevel} type="risk" /></td>
                    <td className="px-6 py-4 text-gray-300 hidden xl:table-cell">
                      {t.complianceScore != null ? `${t.complianceScore}%` : '—'}
                    </td>
                    <td className="px-6 py-4 text-gray-400 hidden lg:table-cell">
                      {t.deadlineDate ? new Date(t.deadlineDate).toLocaleDateString() : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {data.totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-gray-800">
            <p className="text-sm text-gray-500">
              Page {page + 1} of {data.totalPages}
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setPage(p => p - 1)}
                disabled={page === 0}
                className="btn-secondary !px-3 !py-2 disabled:opacity-40"
              >
                <ChevronLeft size={16} />
              </button>
              <button
                onClick={() => setPage(p => p + 1)}
                disabled={page >= data.totalPages - 1}
                className="btn-secondary !px-3 !py-2 disabled:opacity-40"
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
