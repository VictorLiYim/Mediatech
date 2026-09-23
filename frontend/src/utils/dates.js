const dateFormatter = new Intl.DateTimeFormat('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
const dateTimeFormatter = new Intl.DateTimeFormat('fr-FR', {
  weekday: 'long', day: 'numeric', month: 'long', hour: '2-digit', minute: '2-digit',
})

export function formatDate(value) {
  return value ? dateFormatter.format(new Date(value)) : ''
}

export function formatDateTime(value) {
  return value ? dateTimeFormatter.format(new Date(value)) : ''
}

export function daysUntil(value) {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(value)
  target.setHours(0, 0, 0, 0)
  return Math.round((target - today) / 86_400_000)
}
