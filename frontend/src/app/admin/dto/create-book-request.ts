export interface CreateBookRequest {
  title: string;
  publicationYear: number;
  isbn: string;
  categoryId: number;
  authorIds?: number[];
}

