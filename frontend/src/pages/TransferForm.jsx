import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { transferApi } from '../services/api'
import { ArrowLeft, Save, Loader2 } from 'lucide-react'

const STATUSES = ['PENDING', 'APPROVED', 'REJECTED']
const RISKS    = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']
const CATEGORIES = ['Personal Data', 'Medical Data', 'Financial Data', 'HR Data', 'Legal Data', 'Biometric Data', 'IoT Data', 'Geospatial Data', 'Research Data', 'Analytics Data', 'Communications Data', 'Identity Data', 'Academic Data', 'Genetic Data', 'Commercial Data', 'Marketing Data', 'Security Data', 'Operational Data']
const MECHANISMS = ['Standard Contractual Clauses', 'Binding Corporate Rules', 'Adequacy Decision', 'Consent Based', 'Legitimate Interests']

const EMPTY = { title: '', description: '', sourceCountry: '', destinationCountry: '', dataCategory: '', transferMechanism: '', status: 'PENDING', riskLevel: 'MEDIUM', complianceScore: '', deadlineDate: '' }

export default function TransferForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = !!id
  const [form, setForm] = useState(EMPTY)
  const [loading, setLoading] = useState(isEdit)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!isEdit) return
    transferApi.getById(id).then(r => {
      const t = r.data
      setForm({
        title: t.title || '', description: t.description || '',
        sourceCountry: t.sourceCountry || '', destinationCountry: t.destinationCountry || '',
        dataCategory: t.dataCategory || '', transferMechanism: t.transferMechanism || '',
        status: t.status || 'PENDING', riskLevel: t.riskLevel || 'MEDIUM',
        complianceScore: t.complianceScore ?? '', deadlineDate: t.deadlineDate || '',
      })
    }).finally(() => setLoading(false))
  }, [id, isEdit])

  const set = (k, v) => setForm(p => ({ ...p, [k]: v }))

  const handleSubmit = async (e) => {
    e.preventDefault(); setError(''); setSaving(true)
    try {
      const payload = { ...form, complianceScore: form.complianceScore !== '' ? Number(form.complianceScore) : null }
      isEdit ? await transferApi.update(id, payload) : await transferApi.create(payload)
      navigate('/transfers')
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed. Please check your inputs.')
    } finally { setSaving(false) }
  }

  if (loading) return <div className="flex items-center justify-center h-64"><Loader2 className="animate-spin text-primary-400" size={32} /></div>

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center gap-4 mb-6">
        <button onClick={() => navigate('/transfers')} className="btn-secondary"><ArrowLeft size={15} />Back</button>
        <h1 className="text-2xl font-bold text-white">{isEdit ? 'Edit Transfer' : 'New Transfer'}</h1>
      </div>

      <div className="card">
        {error && <div className="mb-4 p-3 rounded-lg bg-red-900/30 border border-red-800 text-red-300 text-sm">{error}</div>}

        <form onSubmit={handleSubmit} className="space-y-5">
          <Field label="Title *" id="title">
            <input id="title" className="input" placeholder="e.g. GDPR Compliance Data Export" value={form.title} onChange={e => set('title', e.target.value)} required />
          </Field>

          <Field label="Description" id="description">
            <textarea id="description" className="input resize-none h-24" placeholder="Optional description" value={form.description} onChange={e => set('description', e.target.value)} />
          </Field>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Field label="Source Country *" id="sourceCountry">
              <input id="sourceCountry" className="input" placeholder="e.g. Germany" value={form.sourceCountry} onChange={e => set('sourceCountry', e.target.value)} required />
            </Field>
            <Field label="Destination Country *" id="destinationCountry">
              <input id="destinationCountry" className="input" placeholder="e.g. United States" value={form.destinationCountry} onChange={e => set('destinationCountry', e.target.value)} required />
            </Field>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Field label="Data Category *" id="dataCategory">
              <select id="dataCategory" className="input cursor-pointer" value={form.dataCategory} onChange={e => set('dataCategory', e.target.value)} required>
                <option value="">Select category</option>
                {CATEGORIES.map(c => <option key={c}>{c}</option>)}
              </select>
            </Field>
            <Field label="Transfer Mechanism *" id="transferMechanism">
              <select id="transferMechanism" className="input cursor-pointer" value={form.transferMechanism} onChange={e => set('transferMechanism', e.target.value)} required>
                <option value="">Select mechanism</option>
                {MECHANISMS.map(m => <option key={m}>{m}</option>)}
              </select>
            </Field>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <Field label="Status" id="status">
              <select id="status" className="input cursor-pointer" value={form.status} onChange={e => set('status', e.target.value)}>
                {STATUSES.map(s => <option key={s}>{s}</option>)}
              </select>
            </Field>
            <Field label="Risk Level" id="riskLevel">
              <select id="riskLevel" className="input cursor-pointer" value={form.riskLevel} onChange={e => set('riskLevel', e.target.value)}>
                {RISKS.map(r => <option key={r}>{r}</option>)}
              </select>
            </Field>
            <Field label="Compliance Score (0-100)" id="complianceScore">
              <input id="complianceScore" type="number" min="0" max="100" className="input" placeholder="e.g. 75" value={form.complianceScore} onChange={e => set('complianceScore', e.target.value)} />
            </Field>
          </div>

          <Field label="Deadline Date" id="deadlineDate">
            <input id="deadlineDate" type="date" className="input" value={form.deadlineDate} onChange={e => set('deadlineDate', e.target.value)} />
          </Field>

          <div className="flex justify-end gap-3 pt-2 border-t border-gray-800">
            <button type="button" onClick={() => navigate('/transfers')} className="btn-secondary">Cancel</button>
            <button id="save-btn" type="submit" className="btn-primary" disabled={saving}>
              {saving ? <><Loader2 size={15} className="animate-spin" />Saving…</> : <><Save size={15} />{isEdit ? 'Update' : 'Create'}</>}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function Field({ label, id, children }) {
  return (
    <div>
      <label htmlFor={id} className="label">{label}</label>
      {children}
    </div>
  )
}
