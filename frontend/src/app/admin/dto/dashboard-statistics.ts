export interface DashboardStatistics {
  totalBooks: number;
  activeMembers: number;
  totalBorrowedBooks: number;
  overdueBooks: number;
  monthlyLoanData: MonthlyLoanData[];
  categoryDistribution: CategoryDistribution[];
  recentActivities: ActivityLog[];
}

export interface MonthlyLoanData {
  month: string;
  borrowedCount: number;
  returnedCount: number;
}

export interface CategoryDistribution {
  categoryName: string;
  bookCount: number;
}

export interface ActivityLog {
  type: string;
  description: string;
  memberName: string | null;
  bookTitle: string;
  timestamp: string;
}

