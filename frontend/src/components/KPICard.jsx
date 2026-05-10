export default function KPICard({ title, value, subtitle, icon: Icon, color = 'blue' }) {
  const colors = {
    blue:   'from-blue-900/30 to-blue-800/10 border-blue-800/50 text-blue-400',
    green:  'from-green-900/30 to-green-800/10 border-green-800/50 text-green-400',
    yellow: 'from-yellow-900/30 to-yellow-800/10 border-yellow-800/50 text-yellow-400',
    red:    'from-red-900/30 to-red-800/10 border-red-800/50 text-red-400',
    purple: 'from-purple-900/30 to-purple-800/10 border-purple-800/50 text-purple-400',
  }

  return (
    <div className={`card bg-gradient-to-br ${colors[color]} !border rounded-xl p-5 transition-all duration-200 hover:scale-[1.02]`}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-gray-400 font-medium">{title}</p>
          <p className="text-3xl font-bold text-white mt-1">{value ?? '—'}</p>
          {subtitle && <p className="text-xs text-gray-500 mt-1">{subtitle}</p>}
        </div>
        {Icon && (
          <div className={`p-3 rounded-xl bg-current/10`}>
            <Icon size={22} className={colors[color].split(' ').at(-1)} />
          </div>
        )}
      </div>
    </div>
  )
}
