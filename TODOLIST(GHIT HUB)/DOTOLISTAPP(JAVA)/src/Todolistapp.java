import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class Todolistapp {
    private static final String FILE_NAME = "tasks.txt";
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        private static final List<Task> tasks = new ArrayList<>();
        private static final Scanner scanner = new Scanner(System.in);
   
        public static void main(String[] args) {
            loadTasks();
            while (true) {
                System.out.println("To-Do List App");
                System.out.println("1. Add Task");
                System.out.println("2. View Tasks");
                System.out.println("3. Mark Task as Completed");
                System.out.println("4. Remove Task");
                System.out.println("5. View Overdue Tasks");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
   
                String input = scanner.nextLine().trim();
                switch (input) {
                    case "1": addTask(); break;
                    case "2": viewTasks(); break;
                    case "3": markTaskAsCompleted(); break;
                    case "4": removeTask(); break;
                    case "5": viewOverdueTasks(); break;
                    case "6": saveTasks(); System.out.println("Goodbye, have a good day!"); return;
                    default: System.out.println("Invalid choice! Please enter a number between 1 and 6.");
                }
            }
        }
   
        private static void addTask() {
            System.out.print("Enter task description: ");
            String description = scanner.nextLine().trim();
            if (description.isEmpty()) {
                System.out.println("Task cannot be empty!");
                return;
            }
   
            System.out.print("Enter priority (High/Medium/Low): ");
            String priority = scanner.nextLine().trim().toLowerCase();
            if (!Arrays.asList("high", "medium", "low").contains(priority)) {
                System.out.println("Invalid priority! Setting to Medium.");
                priority = "medium";
            }
   
            System.out.print("Enter target completion date (YYYY-MM-DD): ");
            String targetDate = scanner.nextLine().trim();
            tasks.add(new Task(description, priority, targetDate, false));
            tasks.sort(Comparator.comparing(Task::getPriorityValue));
            saveTasks();
            viewTasks();
        }
   
        private static void viewTasks() {
            System.out.println("\nTASK LIST:");
            if (tasks.isEmpty()) {
                System.out.println("No tasks available.");
            } else {
                System.out.printf("%-5s | %-30s | %-10s | %-12s | %-10s\n", "No", "Task", "Priority", "Target Date", "Status");
                System.out.println("---------------------------------------------------------------");
                for (int i = 0; i < tasks.size(); i++) {
                    Task t = tasks.get(i);
                    String status = t.completed ? "Done" : (t.isOverdue(new Date()) ? "Overdue" : "Pending");
                    System.out.printf("%-5d | %-30s | %-10s | %-12s | %-10s\n", (i + 1), t.description, t.priority, t.targetDate, status);
                }
            }
        }
   
        private static void markTaskAsCompleted() {
            viewTasks();
            if (tasks.isEmpty()) return;
            System.out.print("Enter task number to mark as completed: ");
            try {
                int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (index >= 0 && index < tasks.size()) {
                    tasks.get(index).completed = true;
                    saveTasks();
                    System.out.println("Task marked as completed!");
                    viewTasks();
                } else {
                    System.out.println("Invalid task number!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
   
        private static void removeTask() {
            viewTasks();
            if (tasks.isEmpty()) return;
            System.out.print("Enter task number to remove: ");
            try {
                int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                if (index >= 0 && index < tasks.size()) {
                    System.out.print("Are you sure you want to remove this task? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        tasks.remove(index);
                        saveTasks();
                        System.out.println("Task removed successfully!");
                        viewTasks();
                    }
                } else {
                    System.out.println("Invalid task number!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
   
        private static void viewOverdueTasks() {
            System.out.println("\nOVERDUE TASKS:");
            Date today = new Date();
            boolean found = false;
            for (Task task : tasks) {
                if (!task.completed && task.isOverdue(today)) {
                    found = true;
                    System.out.println("- " + task.description + " (Due: " + task.targetDate + ")");
                }
            }
            if (!found) System.out.println("No overdue tasks.");
        }
   
        private static void saveTasks() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
                for (Task task : tasks) {
                    writer.println(task.priority + "," + task.targetDate + "," + task.completed + "," + task.description);
                }
            } catch (IOException e) {
                System.out.println("Error saving tasks: " + e.getMessage());
            }
        }
   
        private static void loadTasks() {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        tasks.add(new Task(parts[3], parts[0], parts[1], Boolean.parseBoolean(parts[2])));
                    }
                }
                tasks.sort(Comparator.comparing(Task::getPriorityValue));
            } catch (IOException e) {
                System.out.println("Error loading tasks: " + e.getMessage());
            }
        }
    }
   
    class Task {
        String description, priority, targetDate;
        boolean completed;
   
        Task(String description, String priority, String targetDate, boolean completed) {
            this.description = description;
            this.priority = priority;
            this.targetDate = targetDate;
            this.completed = completed;
        }
   
        int getPriorityValue() {
            return priority.equals("high") ? 1 : priority.equals("medium") ? 2 : 3;
        }
   
        boolean isOverdue(Date today) {
            try {
                Date dueDate = Todolistapp.DATE_FORMAT.parse(targetDate);
            return today.after(dueDate);
        } catch (Exception e) {
            return false;
        }
    }
}
