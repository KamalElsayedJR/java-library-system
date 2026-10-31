import java.util.Objects;

class Member {
    private final int id;
    private final String name;
    private final MemberType type;

    public Member(int id, String name, MemberType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MemberType getType() {
        return type;
    }

    public boolean canBorrow(int currentLoans) {
        return currentLoans < type.maxLoans();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Member)) {
            return false;
        }

        Member other = (Member) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "#%d %s (%s, max %d books)",
                id, name, type, type.maxLoans()
        );
    }
}
