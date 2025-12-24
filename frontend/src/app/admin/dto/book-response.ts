export interface BookResponse {
  id: number;
  title: string;
  isbn: string;
  publicationYear: number;
  totalCopies: number;
  availableCopies: number;
  image?: string;
  description?: string;
  category?: {
    id: number;
    name: string;
  };
  authors?: Array<{
    id: number;
    name: string;
  }>;
}


