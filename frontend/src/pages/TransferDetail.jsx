import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { transferApi, aiApi } from '../services/api'
import StatusBadge from '../components/StatusBadge'
import { ArrowLeft, Edit, Trash2, Bot, FileText, Lightbulb, Globe2, Shield, Calendar, AlertCircle, CheckCircle2, Loader2 } from 'lucide-react'

export default function TransferDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [transfer, setTransfer] = useState(null)
  const [loading, setLoading] = useState(true)
  const [aiLoading, setAiLoading] = useState({})
  const [aiResults, setAiResults] = useState({})
  const [deleteConfirm, setDeleteConfirm] = useState(false)

  useEffect(() => {
    transferApi.getById(id)
      .then(r => setTransfer(r.data))
      .catch(() => navigate('/transfers'))
      .finally(() => setLoading(false))
  }, [id, navigate])

  const callAi = async (type) => {
    setAiLoading(p => ({ ...p, [type]: true }))
    try {
      const fn = type === 'describe' ? aiApi.describe : type === 'recommend' ? aiApi.recommend : aiApi.generateReport
      const { data } = await fn(id)
      setAiResults(p => ({ ...p, [type]: data }))
    } catch { setAiResults(p => ({ ...p, [type]: { error: 'AI call failed.' } })) }
    finally { setAiLoading(p => ({ ...p, [type]: false })) }
  }

  const handleDelete = async () => { await transferApi.delete(id); navigate('/transfers') }

  if (loading) return <div className="flex items-center justify-center h-64"><Loader2 className="animate-spin text-primary-400" size={32} /></div>
  if (!transfer) return null

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <button onClick={() => navigate('/transfers')} className="btn-secondary"><ArrowLeft size={15} />Back</button>
        <div className="flex gap-2">
          <button onClick={() => navigate(`/transfers/${id}/edit`)} className="btn-secondary"><Edit size={15} />Edit</button>
          {!deleteConfirm
            ? <button onClick={() => setDeleteConfirm(true)} className="btn-danger"><Trash2 size={15} />Delete</button>
            : <div className="flex items-center gap-2">
                <span className="text-sm text-red-300">Confirm?</span>
                <button onClick={handleDelete} className="btn-danger !py-1.5">Yes</button>
                <button onClick={() => setDeleteConfirm(false)} className="btn-secondary !py-1.5">No</button>
              </div>
          }
        </div>
      </div>

      <div className="card mb-6">
        <div className="flex flex-wrap items-start gap-3 mb-4">
          <h1 className="text-xl font-bold text-white flex-1">{transfer.title}</h1>
          <StatusBadge status={transfer.status} />
          <StatusBadge status={transfer.riskLevel} type="risk" />
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">
          {[
            { icon: Globe2, label: 'Source Country', value: transfer.sourceCountry },
            { icon: Globe2, label: 'Destination', value: transfer.destinationCountry },
            { icon: Shield, label: 'Data Category', value: transfer.dataCategory },
            { icon: Shield, label: 'Mechanism', value: transfer.transferMechanism },
            { icon: Calendar, label: 'Deadline', value: transfer.deadlineDate || '—' },
            { icon: CheckCircle2, label: 'Compliance Score', value: transfer.complianceScore != null ? `${transfer.complianceScore}%` : '—' },
          ].map(({ icon: Icon, label, value }) => (
            <div key={label} className="flex items-start gap-2">
              <Icon size={14} className="text-gray-500 mt-0.5" />
              <div><p className="text-xs text-gray-500">{label}</p><p className="text-gray-200 font-medium">{value}</p></div>
            </div>
          ))}
        </div>
        {transfer.complianceScore != null && (
          <div className="mt-4">
            <div className="h-2 bg-gray-800 rounded-full">
              <div className={`h-2 rounded-full transition-all duration-500 ${transfer.complianceScore >= 75 ? 'bg-green-500' : transfer.complianceScore >= 50 ? 'bg-yellow-500' : 'bg-red-500'}`}
                style={{ width: `${transfer.complianceScore}%` }} />
            </div>
          </div>
        )}
      </div>

      <div className="card">
        <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2"><Bot size={18} className="text-primary-400" />AI Features</h2>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mb-4">
          {[
            { key: 'describe', label: 'Describe', icon: FileText },
            { key: 'recommend', label: 'Recommend', icon: Lightbulb },
            { key: 'report', label: 'Generate Report', icon: FileText },
          ].map(({ key, label, icon: Icon }) => (
            <button key={key} id={`ai-${key}-btn`} onClick={() => callAi(key)} disabled={aiLoading[key]} className="btn-secondary justify-center py-3">
              {aiLoading[key] ? <Loader2 size={15} className="animate-spin" /> : <Icon size={15} />}
              {aiLoading[key] ? 'Loading…' : label}
            </button>
          ))}
        </div>
        {Object.entries(aiResults).map(([key, result]) => (
          <div key={key} className="mt-4 p-4 rounded-xl bg-gray-800/50 border border-gray-700">
            <div className="flex items-center justify-between mb-2">
              <h4 className="font-semibold text-gray-200 text-sm capitalize">{key} Result</h4>
              {result.is_fallback && <span className="flex items-center gap-1 text-xs text-yellow-400"><AlertCircle size={12} />Fallback</span>}
            </div>
            {result.error
              ? <p className="text-red-400 text-sm">{result.error}</p>
              : <pre className="text-xs text-gray-300 overflow-auto max-h-64 whitespace-pre-wrap">{JSON.stringify(result, null, 2)}</pre>
            }
          </div>
        ))}
      </div>
    </div>
  )
}
