import java.lang.reflect.Array;
import java.util.*;

/**
 * 게일-섀플리 알고리즘 테스팅용 코드
 * 선호도 계산은 아직 완벽 구현 못함
 *
 * 실행 후, n 값(정수 값, 매칭에 참가하는 사람의 수를 의미합니다. 취미는 20개 중 랜덤으로 3개를 선택합니다.)
 * 을 입력하면 선호도 행렬을 출력 한 뒤, 사람마다 매칭 결과를 출력합니다.
 *
 * new!! : 자카드 유사도 구현 완료!!
 *
 * next plan: 카테고리 별 점수 부여 구현
 * 
 * coded by jhan0121
 * 2023-11-18 17:31
 * */

class User implements Comparable<User> {
    int id; // Add an 'id' field to store the user's ID
    String email;
    String[] hobbies; // Change to an array to store hobbies

    public User(int id, String email) {
        this.id = id; // Store the user's ID
        this.email = email;
        this.hobbies = new String[3]; // Initialize the hobbies array
    }

    @Override
    public int compareTo(User otherUser) {
        // Compare users based on their email (names)
        return this.email.compareTo(otherUser.email);
    }

    public boolean isSame(User otherUser) {
        return this.email.equals(otherUser.email);
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
    private static Random random;

    public GaleShapleyAlgorithm(int numStudents) {
        students = new ArrayList<>(numStudents); // Initialize students in the constructor
        random = new Random();
    }

    // Calculate preference score based on the number of common hobbies
    public static double calculatePreferenceScore(User student1, User student2) {
        if (student1.isSame(student2)) {
            return -1;
        } else {
            return countCommonHobbies(student1, student2); // Use the number of common hobbies as the preference score
        }
    }

    // Helper method to count the number of common hobbies between two students
    public static double countCommonHobbies(User student1, User student2) {
        int commonHobbies = 0;
        HashSet<String> union = new HashSet<>(List.of(student1.hobbies));
        HashSet<String> retain = new HashSet<>(List.of(student1.hobbies));

         union.addAll(List.of(student2.hobbies));
         retain.retainAll(List.of(student2.hobbies));

        return (double) retain.size() / union.size();
    }


    public static void main(String[] args) {
        int numStudents = 20;
        System.out.print("n: ");
        Scanner sc = new Scanner(System.in);
        numStudents = sc.nextInt();
        new GaleShapleyAlgorithm(numStudents); // Initialize the class and students list
        List<Double> cost = new ArrayList<>(numStudents);
        // Set a specific seed for random shuffling and preference score calculation
        long seed = System.currentTimeMillis();
        random.setSeed(seed);

        List<String> availableHobbies = Arrays.asList(
                "Reading", "Music", "Sports", "Art", "Cooking",
                "Gaming", "Hiking", "Travel", "Coding", "Photography",
                "Dancing", "Singing", "Yoga", "Movies", "Chess",
                "Swimming", "Shopping", "Writing", "Camping", "Fishing"
        );

        // Create students and assign random hobbies
        for (int i = 0; i < numStudents; i++) {
            User student = new User(i, "student" + i + "@example.com"); // Pass the user's ID and email
            student.setRandomHobbies(availableHobbies);
            students.add(student);
        }

        // Precompute preference matrix
        double[][] preferenceMatrix = new double[numStudents][numStudents];
        for (int i = 0; i < numStudents; i++) {
            for (int j = 0; j < numStudents; j++) {
                preferenceMatrix[i][j] = calculatePreferenceScore(students.get(i), students.get(j));
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
                    double maxPreferenceScore = -1;
                    User bestMatch = null;

                    for (User preferredStudent : studentPreferences) {
                        if (!student.equals(preferredStudent) && matching.get(preferredStudent) == null) { // Exclude self-matching
//                            double preferenceScore = calculatePreferenceScore(student, preferredStudent);
                            double preferenceScore = preferenceMatrix[student.id][preferredStudent.id];
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
        double sum = 0;
        ArrayList<Integer> count = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0));

        for (User student : sortedStudents) {
            User partner = matching.get(student);
            if (partner != null) {
                System.out.println("P" + student.id + " is matched with P" + partner.id + " " + preferenceMatrix[student.id][partner.id]);
                sum += preferenceMatrix[student.id][partner.id];
                if (preferenceMatrix[student.id][partner.id] == 0) {
                    count.set(0, count.get(0) + 1);
                } else if (preferenceMatrix[student.id][partner.id] == 0.2) {
                    count.set(1, count.get(1) + 1);
                } else if (preferenceMatrix[student.id][partner.id] == 0.5) {
                    count.set(2, count.get(2) + 1);
                }else if (preferenceMatrix[student.id][partner.id] == 1) {
                    count.set(3, count.get(3) + 1);
                }

            } else {
                System.out.println(student.email + " is not matched with anyone.");
            }
        }

        ArrayList<Double> idx = new ArrayList<>(Arrays.asList(0.0, 0.2, 0.5, 1.0));

        System.out.println();
        for (int i = 0; i < 4; i++) {
            System.out.print(idx.get(i) + ": " + count.get(i) / 2 + " // ");
        }

        System.out.println("average: " + sum / numStudents);
        sc.close();
    }
}
