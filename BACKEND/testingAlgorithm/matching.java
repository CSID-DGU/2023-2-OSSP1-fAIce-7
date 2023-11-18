import java.util.*;

/**
 * 게일-섀플리 알고리즘 테스팅용 코드
 * 선호도 계산은 아직 미구현하였습니다.
 *
 * 실행 후, n 값(정수 값, 매칭에 참가하는 사람의 수를 의미합니다. 취미는 20개 중 랜덤으로 3개를 선택합니다.)
 * 을 입력하면 선호도 행렬을 출력 한 뒤, 사람마다 매칭 결과를 출력합니다.
 *
 * coded by jhan0121
 * 2023-11-15 11:30
 *
 * */

class User implements Comparable<User> {
    String email;
    String[] hobbies; // Change to an array to store hobbies

    public User(String email) {
        this.email = email;
        this.hobbies = new String[3]; // Initialize the hobbies array
    }

    @Override
    public int compareTo(User otherUser) {
        // Compare users based on their email (names)
        return this.email.compareTo(otherUser.email);
    }

    public void setRandomHobbies(List<String> availableHobbies) {
        List<String> shuffledHobbies = new ArrayList<>(availableHobbies);
        // Shuffle the available hobbies to assign random hobbies to the user
        Collections.shuffle(shuffledHobbies);
        for (int i = 0; i < 3; i++) {
            this.hobbies[i] = shuffledHobbies.get(i);
        }
    }
}

public class GaleShapleyAlgorithm {
    private static List<User> students; // Declare students as a class member

    public GaleShapleyAlgorithm(int numStudents) {
        students = new ArrayList<>(numStudents); // Initialize students in the constructor
    }

    // Calculate preference score based on the number of common hobbies
    public static int calculatePreferenceScore(User student1, User student2) {
        return countCommonHobbies(student1, student2); // Use the number of common hobbies as the preference score
    }

    // Helper method to count the number of common hobbies between two students
    public static int countCommonHobbies(User student1, User student2) {
        int commonHobbies = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (student1.hobbies[i].equals(student2.hobbies[j])) {
                    commonHobbies++;
                }
            }
        }
        return commonHobbies;
    }

    public static void main(String[] args) {
        int numStudents = 20;
        System.out.print("n: ");
        Scanner sc = new Scanner(System.in);
        numStudents = sc.nextInt();
        new GaleShapleyAlgorithm(numStudents); // Initialize the class and students list

        List<String> availableHobbies = Arrays.asList(
                "Reading", "Music", "Sports", "Art", "Cooking",
                "Gaming", "Hiking", "Travel", "Coding", "Photography",
                "Dancing", "Singing", "Yoga", "Movies", "Chess",
                "Swimming", "Shopping", "Writing", "Camping", "Fishing"
        );

        // Create students and assign random hobbies
        for (int i = 0; i < numStudents; i++) {
            User student = new User("student" + i + "@example.com");
            student.setRandomHobbies(availableHobbies);
            students.add(student);
        }

        // Precompute preference matrix
        int[][] preferenceMatrix = new int[numStudents][numStudents];
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numStudents; j++) {
                if (i != j) {
                    preferenceMatrix[i][j] = calculatePreferenceScore(students.get(i), students.get(j));
                }
            }
        }

        // Print the preference matrix
        System.out.println("Preference Matrix:");
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numStudents; j++) {
                System.out.print(preferenceMatrix[i][j] + " ");
            }
            System.out.println();
        }

        // Initialize matching arrays with null values
        Map<User, User> matching = new HashMap<>();
        for (User student : students) {
            matching.put(student, null);
        }

        // Implement Gale-Shapley algorithm with preference scores (excluding self-matching)
        while (true) {
            boolean allStudentsMatched = true;
            for (User student : students) {
                if (matching.get(student) == null) {
                    allStudentsMatched = false;
                    List<User> studentPreferences = new ArrayList<>(students);
                    Collections.shuffle(studentPreferences);
                    int maxPreferenceScore = -1;
                    User bestMatch = null;

                    for (User preferredStudent : studentPreferences) {
                        if (!student.equals(preferredStudent) && matching.get(preferredStudent) == null) { // Exclude self-matching
                            int preferenceScore = calculatePreferenceScore(student, preferredStudent);
                            if (preferenceScore > maxPreferenceScore) {
                                maxPreferenceScore = preferenceScore;
                                bestMatch = preferredStudent;
                            }
                        }
                    }

                    if (bestMatch != null) {
                        matching.put(bestMatch, student);
                        matching.put(student, bestMatch);
                    }
                }
            }
            if (allStudentsMatched) {
                break;
            }
        }

        // Print the final matching sorted by student names
        List<User> sortedStudents = new ArrayList<>(matching.keySet());
        Collections.sort(sortedStudents);

        for (User student : sortedStudents) {
            User partner = matching.get(student);
            if (partner != null) {
                System.out.println(student.email + " is matched with " + partner.email);
            } else {
                System.out.println(student.email + " is not matched with anyone.");
            }
        }
        sc.close();
    }
}