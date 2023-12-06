import pandas as pd
import numpy as np
import random
from collections import deque, Counter
import matplotlib.pyplot as plt
import datetime
import time

# CSV 파일에서 취미, 비트값 및 확률을 로드하는 함수
def load_hobbies_with_bits_and_probabilities(filepath):
    df = pd.read_csv(filepath, encoding='cp949', header=None, names=['Hobby', 'Bit', 'Probability'])
    hobbies_bit_map = df.set_index('Hobby')['Bit'].to_dict()
    probabilities = df['Probability'].values / df['Probability'].sum()  # 확률 정규화
    return hobbies_bit_map, probabilities, list(df['Hobby'])

# 사용자 데이터셋 생성 함수 (평균적으로 4개의 취미)
def generate_user_dataset(hobbies, probabilities, num_users=10000, avg_hobbies=4):
    users_dataset = {}

    for user_id in range(1, num_users + 1):
        num_hobbies = np.random.poisson(avg_hobbies)
        num_hobbies = max(1, min(num_hobbies, 10))
        user_hobbies = np.random.choice(list(hobbies.keys()), size=num_hobbies, replace=False, p=probabilities)
        users_dataset[user_id] = list(user_hobbies)
    return users_dataset

# 유사도 점수 계산 함수
def calculate_category_score(user_hobbies, other_hobbies, hobbies_bit_map):
    total_score = 0
    compare_count = 0
    for user_hobby in user_hobbies:
        for other_hobby in other_hobbies:
            user_hobby_bit = hobbies_bit_map.get(user_hobby, "530")
            other_hobby_bit = hobbies_bit_map.get(other_hobby, "530")
            total_score += compare_hobby_bits(user_hobby_bit, other_hobby_bit)
            compare_count += 1
    return (total_score / compare_count) * (1 / 3.0) if compare_count > 0 else 0

def compare_hobby_bits(user_hobby_bit, other_hobby_bit):
    score = 0
    for i in range(len(user_hobby_bit)):
        if user_hobby_bit[i] == other_hobby_bit[i]:
            score += 1
        else:
            break
    return score

# 게일-셰플리 알고리즘을 사용한 매칭 함수
def gale_shapley_matching(users, score_map):
    preferences = {user: deque(sorted(score_map[user].items(), key=lambda x: x[1], reverse=True)) for user in users}
    matches = {user: None for user in users}
    reverse_matches = {}

    changed = True
    while changed:
        changed = False
        for proposer in preferences:
            if matches[proposer] is not None:
                continue

            if not preferences[proposer]:
                continue

            top_choice, top_score = preferences[proposer].popleft()
            current_match = reverse_matches.get(top_choice)

            if current_match is None or score_map[top_choice][proposer] > score_map[top_choice][current_match]:
                if current_match is not None:
                    matches[current_match] = None
                matches[proposer] = top_choice
                reverse_matches[top_choice] = proposer
                changed = True

    return matches

# 사용 예시
hobbies_bit_map, probabilities, names = load_hobbies_with_bits_and_probabilities('./Simulator/Simulator/hobbies.csv')
print(hobbies_bit_map)
users_dataset = generate_user_dataset(hobbies_bit_map, probabilities)

# 생성된 사용자 데이터셋 중 10명의 예시 출력
print("Example Users Dataset:")
for user_id, hobbies in list(users_dataset.items())[:10]:
    print(f"User {user_id}: {hobbies}")

# 유사도 점수 맵 생성
score_map = {}
print("\n\nCalculating...\n")
start_time = time.time()
for user1 in users_dataset:
    score_map[user1] = {}
    for user2 in users_dataset:
        if user1 != user2:
            score = calculate_category_score(users_dataset[user1], users_dataset[user2], hobbies_bit_map)
            score_map[user1][user2] = score

# 현재까지의 계산 시간 출력
elapsed_time = time.time() - start_time
print(f"Elapsed Time: {int(elapsed_time)} seconds\n")

# 최적의 매칭 실행
best_matched_pairs = gale_shapley_matching(users_dataset.keys(), score_map)

# 매칭된 쌍 중 10쌍의 유사도 점수 출력
print("\nExample Matched Pairs:")
matched_pairs_example = {k: best_matched_pairs[k] for k in list(best_matched_pairs)[:10]}
for user, match in matched_pairs_example.items():
    if match:
        score = score_map[user][match]
        print(f"Pair: User {user} and User {match}, Score: {score}")

# 최적 매칭 결과의 유사도 점수만 추출
best_scores = [score_map[user][best_matched_pairs[user]] for user in best_matched_pairs if best_matched_pairs[user] is not None]

# 유사도 점수의 평균과 분산 계산 및 출력
average_score = np.mean(best_scores)
variance_score = np.var(best_scores)

print(f"\nAverage Matching Score: {average_score}")
print(f"\nVariance of Matching Scores: {variance_score}")

# 유사도 점수 분포를 그래프로 표시
score_counts = Counter(best_scores)

plt.plot(list(score_counts.keys()), list(score_counts.values()), marker='o', linestyle='none')
plt.title('Matching Simulator Distribution')
plt.xlabel('Similarity Score')
plt.ylabel('Counts of Pairs')
plt.show()