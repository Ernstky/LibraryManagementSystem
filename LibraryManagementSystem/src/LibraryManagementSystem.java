import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class LibraryManagementSystem {

    // LinkedHashMap keeps insertion order (nice when displaying)
    private final Map<String, Patron> patrons = new LinkedHashMap<>();

    public boolean patronIdExists(String id) {
        return patrons.containsKey(id);
    }

    public int getPatronCount() {
        return patrons.size();
    }

    public boolean addPatron(Patron patron) {
        if (patrons.containsKey(patron.getId())) return false;
        patrons.put(patron.getId(), patron);
        return true;
    }

    public boolean removePatronById(String id) {
        return patrons.remove(id) != null;
    }

    public void displayAllPatrons() {
        if (patrons.isEmpty()) {
            System.out.println("No patrons are currently in the system.");
            return;
        }
        System.out.println("\n--- Patron List ---");
        for (Patron p : patrons.values()) {
            System.out.println(p.toFormattedString());
        }
        System.out.println("-------------------\n");
    }

    /**
     * File format required (dash-separated):
     * ID-Name-Address-Fine
     * Example:
     * 1234567-John Doe-123 Main St-12.50
     */
    public ImportResult loadPatronsFromFile(String filePath) {
        int added = 0;
        int skipped = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                Patron parsed = parsePatronLine(line);
                if (parsed == null) {
                    skipped++;
                    continue;
                }

                if (patronIdExists(parsed.getId())) {
                    // duplicate ID in system => skip
                    skipped++;
                    continue;
                }

                addPatron(parsed);
                added++;
            }
        } catch (IOException e) {
            return new ImportResult(0, 0, "File error: " + e.getMessage());
        }

        return new ImportResult(added, skipped, null);
    }

    // ---------- Validation helpers ----------

    public static boolean isValidId(String id) {
        return id != null && id.matches("\\d{7}");
    }

    public static boolean isValidFine(double fine) {
        return fine >= 0.0 && fine <= 250.0;
    }

    public static Double tryParseFine(String fineText) {
        if (fineText == null) return null;
        String trimmed = fineText.trim();
        if (trimmed.isEmpty()) return null;

        // requirement: fine must be numeric and must not include a dollar sign
        if (trimmed.contains("$")) return null;

        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Parses a line into a Patron or returns null if invalid.
     * Uses split("-", 4) so addresses can contain dashes and still work.
     */
    private Patron parsePatronLine(String line) {
        String[] parts = line.split("-", 4);
        if (parts.length != 4) return null;

        String id = parts[0].trim();
        String name = parts[1].trim();
        String address = parts[2].trim();
        String fineStr = parts[3].trim();

        if (!isValidId(id)) return null;
        if (name.isEmpty()) return null;
        if (address.isEmpty()) return null;

        Double fine = tryParseFine(fineStr);
        if (fine == null) return null;
        if (!isValidFine(fine)) return null;

        return new Patron(id, name, address, fine);
    }

    // ---------- Small result object for nice output ----------
    public static class ImportResult {
        public final int added;
        public final int skipped;
        public final String errorMessage;

        public ImportResult(int added, int skipped, String errorMessage) {
            this.added = added;
            this.skipped = skipped;
            this.errorMessage = errorMessage;
        }

        public boolean hasError() {
            return errorMessage != null;
        }
    }
}

