import { coverUrlFromIsbn } from '@/api/openLibraryApi'

export function libraryItemFromStockBook(book) {
  return {
    id: `stock:${book.id}`,
    source: 'stock',
    stockBookId: book.id,
    title: book.title,
    authors: book.authors.map((author) => author.name),
    year: book.year ?? null,
    coverUrl: coverUrlFromIsbn(book.isbn),
    isbn: book.isbn,
  }
}

export function libraryItemFromSearchResult(result) {
  return {
    id: `openlibrary:${result.externalId}`,
    source: 'openlibrary',
    externalId: result.externalId,
    title: result.title,
    authors: result.authors,
    year: result.year,
    coverUrl: result.coverUrl,
    isbn: result.isbn,
  }
}
