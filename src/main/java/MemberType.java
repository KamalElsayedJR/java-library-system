public enum MemberType {
    STUDENT(2, 14),
    STAFF(5, 30);

    private final int maxLoans;
    private final int loanDays;

    MemberType(int maxLoans, int loanDays) {
        this.maxLoans = maxLoans;
        this.loanDays = loanDays;
    }

    public int maxLoans() { return maxLoans; }
    public int loanDays() { return loanDays; }
}
