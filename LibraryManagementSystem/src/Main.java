import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        LibraryManagementSystem lms = new LibraryManagementSystem();

        System.out.println("Library Management System (LMS)");
        System.out.println("Console-based patron manager\n");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option (1-5): ");

            switch (choice) {
                case 1 -> importFromFile(lms);
                case 2 -> addPatronManually(lms);
                case 3 -> removePatron(lms);
                case 4 -> lms.displayAllPatrons();
                case 5 -> {
                    running = false;
                    System.out.println("Goodbye.");
                }
                default -> System.out.println("Invalid choice. Please select 1-5.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
                Menu:
                1) Import patrons from text file
                2) Add a new patron manually
                3) Remove a patron by ID
                4) Display all patrons
                5) Exit
                """);
    }

    private static void importFromFile(LibraryManagementSystem lms) {
        System.out.print("Enter file path: ");
        String path = scanner.nextLine().trim();

        if (path.isEmpty()) {
            System.out.println("File path cannot be empty.");
            return;
        }

        LibraryManagementSystem.ImportResult result = lms.loadPatronsFromFile(path);
        if (result.hasError()) {
            System.out.println(result.errorMessage);
            return;
        }

        System.out.printf("Import complete. Added: %d | Skipped: %d%n", result.added, result.skipped);
    }

    private static void addPatronManually(LibraryManagementSystem lms) {
        String id;
        while (true) {
            System.out.print("Enter 7-digit ID: ");
            id = scanner.nextLine().trim();

            if (!LibraryManagementSystem.isValidId(id)) {
                System.out.println("Invalid ID. Must be exactly 7 digits.");
                continue;
            }
            if (lms.patronIdExists(id)) {
                System.out.println("That ID already exists. Try another.");
                continue;
            }
            break;
        }

        String name;
        while (true) {
            System.out.print("Enter name: ");
            name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else {
                break;
            }
        }

        String address;
        while (true) {
            System.out.print("Enter address: ");
            address = scanner.nextLine().trim();
            if (address.isEmpty()) {
                System.out.println("Address cannot be empty.");
            } else {
                break;
            }
        }

        double fine;
        while (true) {
            System.out.print("Enter overdue fine (0 - 250, numbers only, no $): ");
            String fineStr = scanner.nextLine().trim();

            Double parsed = LibraryManagementSystem.tryParseFine(fineStr);
            if (parsed == null) {
                System.out.println("Invalid fine. Enter a numeric value without $.");
                continue;
            }
            if (!LibraryManagementSystem.isValidFine(parsed)) {
                System.out.println("Fine must be between 0 and 250.");
                continue;
            }
            fine = parsed;
            break;
        }

        Patron patron = new Patron(id, name, address, fine);
        boolean added = lms.addPatron(patron);
        if (added) {
            System.out.println("Patron added successfully.");
        } else {
            System.out.println("Could not add patron (duplicate ID).");
        }
    }

    private static void removePatron(LibraryManagementSystem lms) {
        System.out.print("Enter patron ID to remove: ");
        String id = scanner.nextLine().trim();

        if (!LibraryManagementSystem.isValidId(id)) {
            System.out.println("Invalid ID format. Must be 7 digits.");
            return;
        }

        boolean removed = lms.removePatronById(id);
        if (removed) {
            System.out.println("Patron removed successfully.");
        } else {
            System.out.println("No patron found with that ID.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
