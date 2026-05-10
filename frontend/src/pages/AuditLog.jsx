import { useEffect, useState } from 'react'
import { statsApi } from '../services/api'
import { Shield, Loader2 } from 'lucide-react'

export default function AuditLogPage() {
  const [logs, setLogs] = useState([])
  const [loading, setLoading] = useState(true)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    setLoading(true)
    statsApi.auditLog({ page, size: 20 }).then(r => {
      setLogs(r.data.content || [])
      setTotalPages(r.data.totalPages || 0)
    }).finally(() => setLoading(false))
  }, [page])

  const ACTION_COLORS = { CREATE: 'text-green-400', UPDATE: 'text-blue-400', DELETE: 'text-red-400' }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white flex items-center gap-2"><Shield size={22} className="text-primary-400" />Audit Log</h1>
        <p className="text-gray-400 text-sm mt-1">All create, update, and delete operations</p>
      </div>

      <div className="card !p-0 overflow-hidden">
        {loading ? (
          <div className="p-6 space-y-3">{[...Array(8)].map((_, i) => <div key={i} className="skeleton h-10 rounded" />)}</div>
        ) : logs.length === 0 ? (
          <div className="text-center py-16 text-gray-500"><Shield size={40} className="mx-auto mb-3 opacity-20" /><p>No audit logs yet</p></div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-800 bg-gray-900/50">
                  <th className="text-left px-6 py-3 text-gray-400 font-medium">Action</th>
                  <th className="text-left px-6 py-3 text-gray-400 font-medium">Entity</th>
                  <th className="text-left px-6 py-3 text-gray-400 font-medium hidden md:table-cell">Performed By</th>
                  <th className="text-left px-6 py-3 text-gray-400 font-medium hidden lg:table-cell">Details</th>
                  <th className="text-left px-6 py-3 text-gray-400 font-medium">Time</th>
                </tr>
              </thead>
              <tbody>
                {logs.map(log => (
                  <tr key={log.id} className="border-b border-gray-800 hover:bg-gray-800/30">
                    <td className="px-6 py-3">
                      <span className={`font-medium ${ACTION_COLORS[log.action] || 'text-gray-300'}`}>{log.action}</span>
                    </td>
                    <td className="px-6 py-3 text-gray-400">{log.entityType} #{log.entityId}</td>
                    <td className="px-6 py-3 text-gray-400 hidden md:table-cell">{log.performedBy}</td>
                    <td className="px-6 py-3 text-gray-500 text-xs hidden lg:table-cell max-w-xs truncate">{log.details}</td>
                    <td className="px-6 py-3 text-gray-500 text-xs whitespace-nowrap">
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        {totalPages > 1 && (
          <div className="flex items-center justify-between px-6 py-4 border-t border-gray-800">
            <button onClick={() => setPage(p => p - 1)} disabled={page === 0} className="btn-secondary disabled:opacity-40">Prev</button>
            <span className="text-sm text-gray-500">Page {page + 1} of {totalPages}</span>
            <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1} className="btn-secondary disabled:opacity-40">Next</button>
          </div>
        )}
      </div>
    </div>
  )
}
