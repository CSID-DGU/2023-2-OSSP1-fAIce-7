import java.util.*;
public class want {

    static class User {
        String email;
        String[] hobbies;

        public User(String email, String[] hobbies) {
            this.email = email;
            this.hobbies = hobbies;
        }

        public double calculateJaccardSimilarity(User otherUser) {
            Set<String> intersection = new HashSet<>();
            Set<String> union = new HashSet<>();

            Collections.addAll(union, this.hobbies);

            for (String hobby : otherUser.hobbies) {
                if (union.contains(hobby)) {
                    intersection.add(hobby);
                }
                union.add(hobby);
            }

            if (union.isEmpty()) {
                return 0.0; // Avoid division by zero
            }

            return (double) intersection.size() / union.size();
        }

        public int countCommonCategories(User otherUser) {
            Set<String> allHobbies = new HashSet<>(Arrays.asList(this.hobbies));
            allHobbies.addAll(Arrays.asList(otherUser.hobbies));

            Set<String> categories = new HashSet<>();

            // 카테고리 매핑 정보
            Map<String, String> categoryMappings = new HashMap<>();
            categoryMappings.put("축구", "스포츠");
            categoryMappings.put("야구", "스포츠");
            categoryMappings.put("콘솔게임", "게임");
            categoryMappings.put("농구", "스포츠");
            categoryMappings.put("독서", "실내");

            for (String hobby : allHobbies) {
                if (categoryMappings.containsKey(hobby)) {
                    categories.add(categoryMappings.get(hobby));
                }
            }

            return categories.size();
        }

    }

    public static void main(String[] args) {
        // 예시 학생 데이터 생성
        User student1 = new User("student1@example.com", new String[]{"축구", "야구", "콘솔게임"});
        User student2 = new User("student2@example.com", new String[]{"축구", "농구", "독서"});

        // 자카드 유사도 계산
        double jaccardSimilarity = student1.calculateJaccardSimilarity(student2);

        // 공통 카테고리 개수 계산
        int commonCategoryCount = student1.countCommonCategories(student2);

        // 결과 출력
        System.out.println("자카드 유사도: " + jaccardSimilarity);
        System.out.println("공통 카테고리 개수: " + commonCategoryCount);
    }
}
