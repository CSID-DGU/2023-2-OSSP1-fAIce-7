import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from collections import defaultdict, Counter
import seaborn as sns
import pandas as pd

# Function definitions
def load_hobbies_with_bits_and_probabilities(filepath):
    df = pd.read_csv(filepath, encoding='cp949', header=None, names=['Hobby', 'Bit', 'Probability'])
    hobbies_bit_map = df.set_index('Hobby')['Bit'].to_dict()
    probabilities = df['Probability'].values / df['Probability'].sum()  # Normalize probabilities
    return hobbies_bit_map, probabilities, list(df['Hobby'])

def generate_user_dataset(hobbies, probabilities, num_users=100, avg_hobbies=4):
    users_dataset = {}
    for user_id in range(1, num_users + 1):
        num_hobbies = np.random.poisson(avg_hobbies)
        num_hobbies = max(1, min(num_hobbies, 10))
        user_hobbies = np.random.choice(list(hobbies.keys()), size=num_hobbies, replace=False, p=probabilities)
        users_dataset[user_id] = list(user_hobbies)
    return users_dataset

def jaccard_similarity(set1, set2):
    intersection = len(set1.intersection(set2))
    union = len(set1.union(set2))
    if union > 6:
        union = 6
    if union < 2:
        union = 2
    return 0.6 * intersection / union if union > 0 else 0

def compare_hobby_bits(user_hobby_bit, other_hobby_bit):
    score = 0
    for i in range(min(len(user_hobby_bit), len(other_hobby_bit))):
        if user_hobby_bit[i] == other_hobby_bit[i]:
            score += 1
    return score

def calculate_bit_score(user_hobbies, other_hobbies, hobbies_bit_map):
    total_score = 0
    compare_count = 0
    for user_hobby in user_hobbies:
        for other_hobby in other_hobbies:
            user_hobby_bit = hobbies_bit_map.get(user_hobby, "530")
            other_hobby_bit = hobbies_bit_map.get(other_hobby, "530")
            total_score += compare_hobby_bits(user_hobby_bit, other_hobby_bit)
            compare_count += 1
    return (total_score / compare_count) * (1 / 3.0) if compare_count > 0 else 0

def gale_shapley_all_users(users_dataset, hobbies_bit_map):
    preferences = {}
    for user in users_dataset:
        scores = []
        user_hobbies = set(users_dataset[user])
        for other_user in users_dataset:
            if user != other_user:
                other_user_hobbies = set(users_dataset[other_user])
                jaccard_score = jaccard_similarity(user_hobbies, other_user_hobbies)
                bit_score = calculate_bit_score(user_hobbies, other_user_hobbies, hobbies_bit_map)
                combined_score = jaccard_score + bit_score
                scores.append((combined_score, other_user))
        scores.sort(reverse=True)
        preferences[user] = [user for score, user in scores]
    
    engaged = {}
    free_users = list(users_dataset.keys())
    proposals = {user: [] for user in users_dataset}
    while free_users:
        user = free_users[0]
        user_prefs = preferences[user]
        for pref in user_prefs:
            if pref not in proposals[user]:
                proposals[user].append(pref)
                if pref not in engaged:
                    engaged[pref] = user
                    free_users.remove(user)
                    break
                else:
                    current_partner = engaged[pref]
                    if preferences[pref].index(user) < preferences[pref].index(current_partner):
                        engaged[pref] = user
                        free_users.remove(user)
                        if current_partner not in free_users:
                            free_users.append(current_partner)
                        break
        else:
            break
    return engaged

# Load hobbies data
csv_file_path = 'hobbies.csv'
hobbies_bit_map, probabilities, hobbies_list = load_hobbies_with_bits_and_probabilities(csv_file_path)

# Generate user dataset
users_dataset = generate_user_dataset(hobbies_bit_map, probabilities, num_users=10000, avg_hobbies=4)

# Calculate the average number of hobbies per user
average_hobbies_per_user = np.mean([len(hobbies) for hobbies in users_dataset.values()])
variance_hobbies_per_user = np.var([len(hobbies) for hobbies in users_dataset.values()])
print(f"Average number of hobbies per user: {average_hobbies_per_user}")
print(f"Variance in the number of hobbies per user: {variance_hobbies_per_user}")

# Perform Gale-Shapley algorithm
matches = gale_shapley_all_users(users_dataset, hobbies_bit_map)

print(matches, sep='\n')

# Calculating scores for plotting
jaccard_scores = []
bit_scores = []
combined_scores = []

for user1, user2 in matches.items():
    user1_hobbies = set(users_dataset[user1])
    user2_hobbies = set(users_dataset[user2])
    jaccard_score = jaccard_similarity(user1_hobbies, user2_hobbies)
    bit_score = calculate_bit_score(user1_hobbies, user2_hobbies, hobbies_bit_map)
    combined_score = jaccard_score + bit_score
    jaccard_scores.append(jaccard_score)
    bit_scores.append(bit_score)
    combined_scores.append(combined_score)
    print(f"Matched Pair: {user1} - {user2}, Jaccard Score: {jaccard_score}, Bit Score: {bit_score}, Combined Score: {combined_score}")

jaccard_mean = np.mean(jaccard_scores)
jaccard_variance = np.var(jaccard_scores)
bit_mean = np.mean(bit_scores)
bit_variance = np.var(bit_scores)
combined_mean = np.mean(combined_scores)
combined_variance = np.var(combined_scores)

print("jaccard_mean: %f, jaccard_variance: %f" %(jaccard_mean, jaccard_variance))
print("bit_mean: %f, bit_variance: %f" %(bit_mean, bit_variance))
print("combined_mean: %f, combined_variance: %f" %(combined_mean, combined_variance))

# 임계값 설정
threshold = 0.2125000
threshold2 = 0.467000
threshold3 = 0.867000
threshold4 = 1.100000

res = []
res1 = []
res2 = []
res3 = []

# 임계값을 초과하는 매칭 쌍의 수 계산
combined_count_above_threshold = 0
for score in combined_scores:
    if score > threshold:
        res.append(score)
        combined_count_above_threshold += 1

print(">%f" %threshold)
print(combined_count_above_threshold // 2)

print()

combined_count_above_threshold2 = 0
for score in combined_scores:
    if score - threshold2 > 0:
        res1.append(score)
        combined_count_above_threshold2 += 1
    
print(">%f" %threshold2)
print(combined_count_above_threshold2 // 2)

print()

combined_count_above_threshold3 = 0
for score in combined_scores:
    if score > threshold3:
        res2.append(score)
        combined_count_above_threshold3 += 1

print(">%f" %threshold3)
print(combined_count_above_threshold3 // 2)

print()

combined_count_above_threshold4 = 0
for score in combined_scores:
    if score - threshold4 > 0:
        res3.append(score)
        combined_count_above_threshold4 += 1
    
print(">%f" %threshold4)
print(combined_count_above_threshold4 // 2)

print(len(res), len(res1), len(res2), len(res3))

# Plotting the graphs
plt.figure(figsize=(18, 6))
plt.subplot(1, 3, 1)
plt.hist(jaccard_scores, bins=30, color='skyblue')
plt.title('Jaccard Similarity Scores')
plt.xlabel('Score')
plt.ylabel('Frequency')
plt.subplot(1, 3, 2)
plt.hist(bit_scores, bins=30, color='lightgreen')
plt.title('Category Bit Scores')
plt.xlabel('Score')
plt.ylabel('Frequency')
plt.subplot(1, 3, 3)
plt.hist(combined_scores, bins=30, color='salmon')
plt.title('Combined Jaccard and Bit Scores')
plt.xlabel('Score')
plt.ylabel('Frequency')
plt.tight_layout()
plt.show()

# Prepare data for the plot
hobbies_count_pairs = [(len(users_dataset[user1]), len(users_dataset[user2])) for user1, user2 in matches.items()]

# Unzip the pairs for plotting
hobbies_count_a, hobbies_count_b = zip(*hobbies_count_pairs)

# Calculate the count of each hobby pair combination
hobby_pairs_count = Counter((len(users_dataset[user1]), len(users_dataset[user2])) for user1, user2 in matches.items())

heatmap_data = pd.DataFrame(0, index=range(1, 11), columns=range(1, 11))

for (hobbies_a, hobbies_b), count in hobby_pairs_count.items():
    if 1 <= hobbies_a <= 10 and 1 <= hobbies_b <= 10:
        heatmap_data.at[hobbies_a, hobbies_b] = count

# Plotting the heatmap
plt.figure(figsize=(10, 8))
ax = sns.heatmap(heatmap_data, annot=True, cmap="YlGnBu", fmt="d")
plt.title('Number of Matched Pairs by Hobbies Count (1-10)')
plt.xlabel('Hobbies Count of User B')
plt.ylabel('Hobbies Count of User A')
plt.show()