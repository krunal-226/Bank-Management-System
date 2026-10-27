import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private String type;
    private double amount;
    private LocalDateTime timestamp;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getFormattedDate() {
        if (timestamp == null) return "";
        return timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));
    }

    public String getFormattedAmount() {
        boolean isPositive = type.equalsIgnoreCase("Deposit") || type.equalsIgnoreCase("Account Created");
        return String.format("%sRs. %,.2f", isPositive ? "+" : "-", amount);
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + ": Rs. " + amount;
    }
}
