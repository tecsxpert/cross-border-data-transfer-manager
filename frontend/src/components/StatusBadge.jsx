export default function StatusBadge({ status, type = 'status' }) {
  if (!status) return null
  const s = status.toUpperCase()

  if (type === 'status') {
    const map = {
      PENDING:  'badge-pending',
      APPROVED: 'badge-approved',
      REJECTED: 'badge-rejected',
    }
    return <span className={map[s] || 'badge-pending'}>{status}</span>
  }

  if (type === 'risk') {
    const map = {
      LOW:      'badge-low',
      MEDIUM:   'badge-medium',
      HIGH:     'badge-high',
      CRITICAL: 'badge-critical',
    }
    return <span className={map[s] || 'badge-medium'}>{status}</span>
  }

  return <span className="badge-pending">{status}</span>
}
