import java.util.*;
import java.io.*;

// Contact Class
class Contact {
    String name;
    String phone;

    Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}

// Thread class for SOS
class SOSThread extends Thread {
    List<Contact> contacts;

    SOSThread(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void run() {
        String location = WomenSafetyApp.getLocation();

        System.out.println("\n🚨 Sending SOS Alert in Background...");

        // Synchronizing access to shared list
        synchronized (contacts) {
            for (Contact c : contacts) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted");
                }

                System.out.println("Alert sent to " + c.name + " (" + c.phone + ")");
                System.out.println("Message: HELP! I am in danger. My location: " + location);
            }
        }

        System.out.println("✅ All contacts alerted successfully!");
    }
}

// Main Class
public class WomenSafetyApp {

    // Thread-safe list
    static List<Contact> contacts = Collections.synchronizedList(new ArrayList<>());
    static Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "contacts.txt";

    public static void main(String[] args) {

        loadContactsFromFile();

        int choice;

        do {
            System.out.println("\n===== Women Safety Smart Companion =====");
            System.out.println("1. Add Trusted Contact");
            System.out.println("2. View Contacts");
            System.out.println("3. Emergency SOS");
            System.out.println("4. Fake Call Trigger");
            System.out.println("5. Voice Activation (Type 'help me')");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addContact();
                    break;
                case 2:
                    viewContacts();
                    break;
                case 3:
                    sendSOS();
                    break;
                case 4:
                    fakeCall();
                    break;
                case 5:
                    voiceActivation();
                    break;
                case 6:
                    saveContactsToFile();
                    System.out.println("Stay Safe!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 6);
    }

    // Add Contact (synchronized method)
    static synchronized void addContact() {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Phone: ");
        String phone = sc.nextLine();

        contacts.add(new Contact(name, phone));
        System.out.println("Contact Added Successfully!");
    }

    // View Contacts
    static void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts added!");
            return;
        }

        System.out.println("\nTrusted Contacts:");

        synchronized (contacts) {
            for (Contact c : contacts) {
                System.out.println("Name: " + c.name + ", Phone: " + c.phone);
            }
        }
    }

    // SOS Feature
    static void sendSOS() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts to alert!");
            return;
        }

        SOSThread sosThread = new SOSThread(contacts);
        sosThread.start();
    }

    // Fake Call Feature
    static void fakeCall() {
        System.out.print("Enter Fake Caller Name: ");
        String caller = sc.nextLine();

        System.out.println("\n📞 Incoming Call...");
        System.out.println("Caller: " + caller);
        System.out.println("Ringing...");
    }

    // Voice Activation Simulation
    static void voiceActivation() {
        System.out.print("Say something: ");
        String input = sc.nextLine();

        if (input.equalsIgnoreCase("help me")) {
            System.out.println("Voice detected! Triggering SOS...");
            sendSOS();
        } else {
            System.out.println("No emergency detected.");
        }
    }

    // Simulated Location
    static String getLocation() {
        return "Lat: 28.7041, Long: 77.1025 (Simulated Location - Chandigarh)";
    }

    // Save Contacts
    static void saveContactsToFile() {
        synchronized (contacts) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {

                for (Contact c : contacts) {
                    writer.write(c.name + "," + c.phone);
                    writer.newLine();
                }

                System.out.println("Contacts saved to file!");

            } catch (IOException e) {
                System.out.println("Error saving contacts: " + e.getMessage());
            }
        }
    }

    // Load Contacts
    static void loadContactsFromFile() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                contacts.add(new Contact(data[0], data[1]));
            }

            reader.close();

        } catch (IOException e) {
            System.out.println("Error loading contacts: " + e.getMessage());
        }
    }
}
