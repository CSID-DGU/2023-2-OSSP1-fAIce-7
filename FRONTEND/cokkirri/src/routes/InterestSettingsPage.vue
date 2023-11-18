
<template>
  <div class="interest-settings">
    <h2>관심분야 설정</h2>
    <!-- 각 관심분야별 카테고리와 항목 설정 -->
    <div v-for="i in 3" :key="i" class="interest-section">
      <!-- 카테고리 선택 -->
      <div>
        <label :for="'category' + i">카테고리 {{ i }}</label>
        <select v-model="interests[i - 1].category" @change="updateItems(i)">
          <option disabled value="">카테고리</option>
          <option v-for="(items, category) in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>
      <!-- 항목 선택 -->
      <div>
        <label :for="'item' + i">항목 {{ i }}</label>
        <select v-model="interests[i - 1].item">
          <option disabled value="">항목</option>
          <option v-for="item in availableItems(i)" :key="item" :value="item">
            {{ item }}
          </option>
        </select>
      </div>
    </div>
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
      interests: [{ category: '', item: '' }, { category: '', item: '' }, { category: '', item: '' }]
    };
  },
  computed: {
    isComplete() {
      return this.interests.every(interest => interest.category && interest.item);
    }
  },
  methods: {
    updateItems(interestIndex) {
      // 현재 선택된 카테고리에 따라 항목 목록 업데이트
      this.interests[interestIndex - 1].item = '';
    },
    availableItems(index) {
    const currentCategory = this.interests[index - 1]?.category;
    const currentItem = this.interests[index - 1]?.item;
    const selectedItems = this.interests.map(i => i.item);

    if (currentCategory) {
      return this.categories[currentCategory].filter(item => {
        // 이미 선택된 항목은 선택이 불가하도록 하여 중복 방지
        return item === currentItem || !selectedItems.includes(item);
      });
    } else {
      return [];
    }
  },
    submitInterests() {
    // 관심분야 데이터를 백엔드로 전송
    axios.post('/user/interests', {
        id: this.$store.state.id,
        category1: this.interests[0].category,
        hobby1: this.interests[0].item,
        category2: this.interests[1].category,
        hobby2: this.interests[1].item,
        category3: this.interests[2].category,
        hobby3: this.interests[2].item
      })
      .then(response => {
         console.log("서버 응답:", response.data); // 서버 응답 로깅
        // Vuex 스토어에 관심분야 업데이트
        console.log(this.interests);
        this.$store.commit('setUserInterests', true);

        // 성공 시 페이지 리디렉션
        alert("관심분야 설정이 완료되었습니다.")
        this.$router.push('/Starting'); // 예: Starting 페이지로 리디렉션
      })
      .catch(error => {
        // 실패 시 경고 메시지 표시 후 로그인 화면으로 이동
        console.error('오류:', error);
        alert("설정이 완료되지 않아 서비스를 이용할 수 없습니다.");
        this.$router.push('/');
      });
    },
  ...mapMutations(['setUserInterests']) // Vuex 뮤테이션 매핑
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