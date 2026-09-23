import axios from 'axios'

const openLibraryClient = axios.create({ baseURL: 'https://openlibrary.org' })

const COVERS_URL = 'https://covers.openlibrary.org/b'
const SEARCH_FIELDS = 'key,title,author_name,first_publish_year,cover_i,isbn,number_of_pages_median'
export const SEARCH_PAGE_SIZE = 20

export function coverUrlFromCoverId(coverId, size = 'M') {
  return coverId ? `${COVERS_URL}/id/${coverId}-${size}.jpg` : null
}

export function coverUrlFromIsbn(isbn, size = 'M') {
  return isbn ? `${COVERS_URL}/isbn/${isbn}-${size}.jpg?default=false` : null
}

function pickIsbn(isbns = []) {
  return isbns.find((isbn) => isbn.length === 13) ?? isbns[0] ?? null
}

function toSearchResult(doc) {
  return {
    externalId: doc.key.replace('/works/', ''),
    title: doc.title,
    authors: doc.author_name ?? [],
    year: doc.first_publish_year ?? null,
    coverUrl: coverUrlFromCoverId(doc.cover_i),
    isbn: pickIsbn(doc.isbn),
    allIsbns: doc.isbn ?? [],
    pageCount: doc.number_of_pages_median ?? null,
  }
}

export async function searchBooks(query, page = 1) {
  const { data } = await openLibraryClient.get('/search.json', {
    params: { q: query, page, limit: SEARCH_PAGE_SIZE, fields: SEARCH_FIELDS },
  })
  return {
    total: data.numFound,
    results: data.docs.map(toSearchResult),
  }
}

export async function fetchWorkDescription(externalId) {
  const { data } = await openLibraryClient.get(`/works/${externalId}.json`)
  const description = data.description
  if (!description) return null
  return typeof description === 'string' ? description : description.value
}

export async function fetchFirstPublishYearByIsbn(isbn) {
  const { data } = await openLibraryClient.get('/search.json', {
    params: { isbn, fields: 'first_publish_year', limit: 1 },
  })
  return data.docs[0]?.first_publish_year ?? null
}
