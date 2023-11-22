<template>
  <div class="interest-settings">
    <h2>관심분야 설정</h2>
    <div v-for="(interest, index) in interests" :key="index" class="interest-section">
      <!-- 카테고리 선택 -->
      <div>
        <label :for="'category' + index">카테고리 {{ index + 1 }}</label>
        <select v-model="interest.category">
          <option disabled value="">카테고리</option>
          <!-- 백엔드에서 불러온 카테고리 옵션 -->
        </select>
      </div>
      <!-- 항목 선택 -->
      <div>
        <label :for="'item' + index">항목 {{ index + 1 }}</label>
        <select v-model="interest.item">
          <option disabled value="">항목</option>
          <option v-for="item in availableItems(index)" :key="item" :value="item">
            {{ item }}
          </option>
        </select>
      </div>
      <!-- 관심도 설정 슬라이더 및 점수 표시 -->
      <div>
        <label :for="'interest' + index">관심도 {{ index + 1 }}</label>
        <input type="range" v-model="interest.score" min="1" max="10">
        <span>{{ interest.score }}</span> <!-- 현재 관심도 점수 표시 -->
      </div>
      <!-- 삭제 버튼 -->
      <button @click="removeInterest(index)">-</button>
    </div>
    <!-- 항목 추가 버튼 -->
    <button @click="addInterest">+</button>
    <button :disabled="!isComplete" @click="submitInterests">완료</button>
  </div>
</template>

<script>
import axios from '../api/index.js';
import { mapMutations } from 'vuex';

export default {
  data() {
    return {
      categories: {
        '스포츠': ['축구', '농구', '야구', '당구'],
        '게임': ['컴퓨터', '스위치', '보드게임', '오락실'],
        '관람/감상': ['영화', '드라마', '뮤지컬', '전시회'],
        '미용/패션': ['의류', '악세사리', '화장품', '네일'],
        '애완동물': ['강아지', '고양이', '조류', '식물'],
        '창작': ['그림', '음악', '사진', '글쓰기'],
      },
      interests: [{ category: '', item: '', score: 10 }], // 초기 관심분야 데이터
      // categories: {} // 카테고리 데이터
    };
  },
  /*
  created() {
    this.fetchCategories(); // 컴포넌트 생성 시 카테고리 데이터 불러오기
  },
  */
  computed: {
    isComplete() {
      // 모든 관심분야가 적절히 설정되었는지 및 총점이 10점인지 확인
      return this.interests.every(interest => interest.category && interest.item && interest.score) &&
             this.interests.reduce((total, interest) => total + interest.score, 0) === 10;
    }
  },

  methods: {
    ...mapMutations(['setUserInterests']), // Vuex 뮤테이션 매핑

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

      const currentItem = this.interests[index];

      if (currentItem) {
        const currentItemCategory = currentItem.category;
        return this.categories[currentItemCategory]?.filter(item => !selectedItems.includes(item) && item !== currentItem.item) || [];
      } else {
        return [];
      }
    },

    addInterest() {
      // 관심분야 추가
      this.interests.push({ category: '', item: '', score: 1 });
      this.redistributeScores();
    },

    removeInterest(index) {
      // 관심분야 제거
      const removedScore = this.interests[index].score;
      this.interests.splice(index, 1);
      if (this.interests.length > 0 && index > 0) {
        this.interests[index - 1].score += removedScore;
      }
      this.redistributeScores();
    },

    redistributeScores() {
      // 관심도 총합 10점 유지
      let totalScore = this.interests.reduce((total, interest) => total + interest.score, 0);
      let excess = totalScore - 10;

      if (excess !== 0) {
        for (let i = 0; i < this.interests.length; i++) {
          if (excess > 0) {
            let deduction = Math.min(this.interests[i].score - 1, excess);
            this.interests[i].score -= deduction;
            excess -= deduction;
          } else {
            let addition = Math.min(1 - this.interests[i].score, -excess);
            this.interests[i].score += addition;
            excess += addition;
          }

          if (excess === 0) break;
        }
      }
    },

    submitInterests() {
      // 관심분야 데이터 백엔드로 전송
      axios.post('/user/interests', {
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
          this.$router.push('/');
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

.interest-settings {
    background-color: #FFF;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2);
}

.interest-section {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
}

select {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

button {
    width: 100%;
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:disabled {
    background-color: #ccc;
}
</style>