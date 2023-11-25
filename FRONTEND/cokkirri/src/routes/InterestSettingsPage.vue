<template>
  <div class="layout">
    <div class="interest-settings">
      <!-- 관심분야 설정 제목 -->
      <h2>관심분야 설정</h2>
      <!-- 각 관심분야 항목 설정 -->
      <div v-for="(interest, index) in interests" :key="index" class="interest-section">
        <!-- 카테고리 선택 -->
        <div>
          <label :for="'category' + index">카테고리 {{ index + 1 }}</label>
          <select v-model="interest.category">
            <option disabled value="">카테고리</option>
            <!-- 카테고리 옵션 반복 -->
            <option v-for="category in categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>
        <!-- 항목 선택 -->
        <div>
          <label :for="'item' + index">항목 {{ index + 1 }}</label>
          <select v-model="interest.item">
            <option disabled value="">항목</option>
            <!-- 항목 옵션 반복 -->
            <option v-for="item in availableItems(index)" :key="item" :value="item">
              {{ item }}
            </option>
          </select>
        </div>
        <!-- 관심도 설정 슬라이더 및 점수 표시 -->
        <div>
          <label :for="'interest' + index">관심도 {{ index + 1 }}</label>
          <input type="range" v-model.number="interest.score" min="1" max="10">
          <span>{{ interest.score }}</span>
        </div>
        <!-- 삭제 버튼 -->
        <div class="button-container">
        <button @click="removeInterest(index)">-</button>
        </div>
      </div>
      <!-- 항목 추가 버튼 -->
      <div class="button-container">
        <button @click="addInterest">+</button>
      </div>
      <!-- 항목 완료 버튼 -->
      <div class="button-container">
        <button :disabled="!isComplete" @click="submitInterests">완료</button>
      </div>
    </div>
    <!-- 남은 관심도 점수 표시 영역 -->
    <div class="remaining-score-display">
      남은 관심도 점수: {{ 10 - interests.reduce((total, interest) => total + interest.score, 0) }}
    </div>
  </div>
</template>

<script>
import axios from '../api/index.js';
import { mapMutations } from 'vuex';

export default {
  data() {
    return {
      categories: {}, // 카테고리 데이터
      interests: [{ category: '', item: '', score: 10 }], // 초기 관심분야 데이터
    };
  },
  computed: {
    isComplete() {
      // 모든 관심분야가 설정되었는지 확인하고, 관심도 점수의 총합이 10점인지 확인
      return this.interests.every(interest => interest.category && interest.item && interest.score) &&
             this.interests.reduce((total, interest) => total + interest.score, 0) === 10;
    },
    
    remainingScore() {
      let totalScore = this.interests.reduce((sum, interest) => sum + Number(interest.score), 0);
      return 10 - totalScore;
      }
    },

  methods: {
    ...mapMutations(['setUserInterests']),
    fetchCategories() {
      // 백엔드에서 카테고리 데이터 불러오기
      axios.get('/api/interest-categories')
        .then(response => {
          console.log(response.data);
          this.categories = response.data;
        })
        .catch(error => {
          console.error('카테고리 불러오기 실패:', error);
        });
    },

    availableItems(index) {
      // 선택 가능한 항목 필터링
      const selectedItems = this.interests
        .filter((_, i) => i !== index)
        .map(interest => interest.item);
      return this.categories[this.interests[index].category]?.filter(item => !selectedItems.includes(item)) || [];
    },

    addInterest() {
      // 남은 관심도 점수를 계산
      const remainingScore = this.remainingScore;

      if (remainingScore > 0) {
        // 남은 관심도 점수가 있으면 새 관심분야 추가
        this.interests.push({ category: '', item: '', score: Math.min(1, remainingScore) });
      } else {
        // 남은 관심도 점수가 없으면 경고 표시
        alert("더 이상 관심도 점수를 할당할 수 없습니다.");
      }
    },

    removeInterest(index) {
      this.interests.splice(index, 1);
    },

    submitInterests() {
      // 관심분야 데이터 백엔드로 전송
      axios.post('/hobby/interests', {
          id: this.$store.state.id,
          interests: this.interests.map(interest => ({
            category: interest.category,
            item: interest.item,
            score: interest.score
          }))
        })
        .then(response => {
          console.log("서버 응답:", response.data);
          this.$store.commit('setUserInterests', true);
          alert("관심분야 설정이 완료되었습니다.")
          this.$router.push('/Starting');
        })
        .catch(error => {
          console.error('오류:', error);
          alert("설정이 완료되지 않아 서비스를 이용할 수 없습니다.");
          this.$router.push('/InterestSettingsPage');
        });
    },
    
    created() {
      this.fetchCategories(); // 컴포넌트 생성 시 fetchCategories 호출
    },
  }
};

</script>

<style lang="scss" scoped>
@import "../scss/main";

.background-setting {
    height: 100vh;
    width: 100vw;
    margin: 0;
    background-color: #ECBC76; 
    display: grid;
    grid-template-rows: auto;
    justify-items: center;
    align-items: center;
}

.container {
    display: flex;
    align-items: center;
    justify-content: center;
}

.layout {
  display: flex;
  justify-content: space-between;
  align-items: stretch; /* 자식 요소들의 높이를 부모 컨테이너에 맞춤 */
  width: 100%;
}

.interest-settings {
  width: 66%; /* 전체 너비의 66% */
  background-color: #FFF;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
  margin: 0; /* 마진 제거 */
}

.interest-section {
  margin-bottom: 15px; /* 각 섹션의 하단 마진 */
}

label {
  display: block;
  margin-bottom: 5px; /* 라벨 아래 마진 */
}

select {
  width: 100%; /* 셀렉트 박스 너비 */
  padding: 10px; /* 셀렉트 박스 내부 패딩 */
  border: 1px solid #ccc; /* 테두리 스타일 */
  border-radius: 4px; /* 테두리 둥근 정도 */
  box-sizing: border-box; /* 박스 모델 설정 */
}

button {
  width: 100%; /* 버튼 너비 */
  padding: 10px; /* 버튼 내부 패딩 */
  background-color: #4CAF50; /* 버튼 배경 색상 */
  color: white; /* 버튼 글자 색상 */
  border: none; /* 테두리 없음 */
  border-radius: 4px; /* 테두리 둥근 정도 */
  cursor: pointer; /* 마우스 오버 시 커서 변경 */
}

button:disabled {
  background-color: #ccc; /* 비활성화 버튼의 배경 색상 */
}
.button-container {
  display: flex;
  justify-content: space-between;
  margin-top: 15px; /* 버튼 상단 마진 추가 */
  margin-down: 15px; /* 버튼 하단 마진 추가 */
}

.remaining-score-display {
  width: 34%; /* 전체 너비의 34% */
  padding: 20px;
  background-color: #f7f7f7;
  border-radius: 10px;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
  display: flex;
  justify-content: center; /* 가운데 정렬 */
  align-items: center; /* 세로 방향으로 가운데 정렬 */
  font-size: 24px;
  font-weight: bold;
}

/* 슬라이드의 높이를 크게 조절 */
input[type="range"] {
  width: 90%;
  height: 30px; /* 슬라이드 높이 */
  -webkit-appearance: none;
  appearance: none;
  background: #ccc;
  border-radius: 5px;
  outline: none;
  opacity: 0.7;
  -webkit-transition: .2s;
  transition: opacity .2s;
}

/* 슬라이드의 크기를 슬라이드 높이에 맞게 조절 */
input[type="range"]::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 30px; /* 슬라이더 막대의 너비 */
  height: 30px; /* 슬라이더 막대의 높이와 동일하게 조절 */
  background: #4CAF50;
  cursor: pointer;
  border-radius: 50%;
  margin-left: -15px; /* 슬라이더 막대의 가운데 정렬을 위해 왼쪽으로 이동 */
}

input[type="range"]::-moz-range-thumb {
  width: 30px; /* 슬라이더 막대의 너비 */
  height: 30px; /* 슬라이더 막대의 높이와 동일하게 조절 */
  background: #4CAF50;
  cursor: pointer;
  border-radius: 50%;
  margin-left: -15px; /* 슬라이더 막대의 가운데 정렬을 위해 왼쪽으로 이동 */
}

input[type="range"]::-ms-thumb {
  width: 30px; /* 슬라이더 막대의 너비 */
  height: 30px; /* 슬라이더 막대의 높이와 동일하게 조절 */
  background: #4CAF50;
  cursor: pointer;
  border-radius: 50%;
  margin-left: -15px; /* 슬라이더 막대의 가운데 정렬을 위해 왼쪽으로 이동 */
}

select + div {
  margin-top: 10px; /* 간격 크기 조절 */
}

div + div {
  margin-top: 10px; /* 간격 크기 조절 */
}
</style>