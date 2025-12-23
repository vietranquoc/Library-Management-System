export interface CreateBookRequest {
  title: string;
  publicationYear: number;
  isbn: string;
  quantity: number;
  description?: string;
  image: string;
  categoryId: number;
  authorIds?: number[];
  authorNames?: string[];
}

