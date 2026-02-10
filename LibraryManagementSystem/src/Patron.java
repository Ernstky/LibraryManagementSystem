public class Patron {
    private final String id;         // keep as String to preserve leading zeros if any
    private final String name;
    private final String address;
    private final double overdueFine;

    public Patron(String id, String name, String address, double overdueFine) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.overdueFine = overdueFine;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getOverdueFine() {
        return overdueFine;
    }

    public String toFormattedString() {
        return String.format("ID: %s | Name: %s | Address: %s | Fine: $%.2f",
                id, name, address, overdueFine);
    }

    @Override
    public String toString() {
        return toFormattedString();
    }
}
