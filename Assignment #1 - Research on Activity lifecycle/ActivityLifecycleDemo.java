import java.util.Scanner;

public class ActivityLifecycleDemo {
    private String activityName;

    public ActivityLifecycleDemo(String name) {
        this.activityName = name;
    }

    public void onCreate() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onCreate: Setting up UI and resources...");
        System.out.print("------------------------------------------------------ \n");
    }

    public void onStart() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onStart: Activity is now visible to the user.");
        System.out.print("------------------------------------------------------ \n");
    }

    public void onResume() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onResume: User can now interact with " + activityName + ".");
        System.out.print("------------------------------------------------------ \n");
    }

    public void onPause() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onPause: Another activity is in front, pausing " + activityName + ".");
        System.out.print("------------------------------------------------------ \n");
    }

    public void onStop() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onStop: Activity is fully hidden.");
        System.out.print("------------------------------------------------------ \n");
    }

    public void onDestroy() {
        System.out.print("------------------------------------------------------ \n");
        System.out.println(activityName + " → onDestroy: Cleaning up resources before closing.");
        System.out.print("------------------------------------------------------ \n");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create an Activity
        System.out.print("====================================================== \n");
        System.out.print("Enter the name of your App: ");
        String name = scanner.nextLine();
        System.out.print("====================================================== \n");

        ActivityLifecycleDemo activity = new ActivityLifecycleDemo(name);

        boolean running = true;
        while (running) {
            System.out.println("Simulate " + name + " Activity Lifecycle:");
            System.out.println("1. Create Activity");
            System.out.println("2. Start Activity");
            System.out.println("3. Resume Activity");
            System.out.println("4. Pause Activity");
            System.out.println("5. Stop Activity");
            System.out.println("6. Destroy Activity");
            System.out.println("7. Exit Simulation");
            
            System.out.print("Choose an action: ");
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1 -> activity.onCreate();
                case 2 -> activity.onStart();
                case 3 -> activity.onResume();
                case 4 -> activity.onPause();
                case 5 -> activity.onStop();
                case 6 -> activity.onDestroy();
                case 7 -> {
                    System.out.println("Exiting simulation...");
                    running = false;
                }
                default -> {
                    System.out.print("------------------------------------------------------ \n");
                    System.out.println("Invalid choice, try again!");
                    System.out.print("------------------------------------------------------ \n");
                }
            }
        }

        scanner.close();
    }
}
