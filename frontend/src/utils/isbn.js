export function normalizeIsbn(isbn) {
  return isbn ? String(isbn).replace(/[^0-9Xx]/g, '').toUpperCase() : ''
}
