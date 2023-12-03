<template>
<div class="background-setting">
  <div class="layout">
    <!-- 관심분야 설정 섹션 -->
    <div class="interest-settings">
      <router-link to="/my" class="my-link">&#60; </router-link>
      <h2>관심분야 설정</h2>
      <div v-for="(interest, index) in interests" :key="index" class="interest-section">
        <div class="input-wrapper">
          <div class="index-number">{{ index + 1 }}.</div>
          <input v-model="interest.inputText" @input="filterItems(index)" placeholder="관심분야 입력" class="interest-input">
          <div class="trash-button" @click="clearInputText(index)" v-if="interest.inputText">
            <div class="trash-icon">🗑️</div>
          </div>
          <div class="remove-button" @click="removeInterest(index)" v-if="interest.inputText">
            <div class="circle-button">-</div>
          </div>
          <input v-if="interest.inputText === '사회 및 기타활동 >> 기타'" v-model="interest.additionalInput" class="additional-input" placeholder="기타 입력란">
        </div>
        <ul v-if="interest.inputText && interest.filteredItems.length">
          <li v-for="(item, itemIndex) in interest.filteredItems" :key="itemIndex" @click="selectItem(index, item)">
            {{ formatItem(item) }}
          </li>
        </ul>
      </div>
      <button @click="addInterest" :disabled="interests.length >= 10" class="add-button">+</button>
      <button @click="submitInterests" :disabled="!isComplete" class="complete-button">완료</button>
    </div>
  </div>
</div>
</template>

<script>
import { mapMutations } from 'vuex';
import { mapActions } from 'vuex';
import axios from '../api/index.js';
export default {
  data() {
    return {
      categories: {}, // 카테고리 데이터
      interests: [{ inputText: '', filteredItems: [] }], // 현재 설정 중인 관심분야 목록
      existingInterests: [] // 이미 설정된 관심분야 목록
    };
  },
  computed: {
    isComplete() {
      const hasDuplicate = this.interests.some((interest, index) => {
        // '사회 및 기타활동 >> 기타'는 중복 검증에서 제외
        if (interest.inputText === '사회 및 기타활동 >> 기타') {
          return false;
        }
        const inputText = interest.inputText || ''; // undefined 방지
        return inputText &&
          (this.isExistingInterest(inputText) || this.isDuplicateInterest(index));
      });

      const isAdditionalInputComplete = this.interests.every(interest => {
        if (interest.inputText === '사회 및 기타활동 >> 기타') {
          return interest.additionalInput && interest.additionalInput.trim() !== '';
        }
        return true;
      });

      const isAdditionalInputUnique = this.interests.every((interest, index) => {
        if (interest.inputText === '사회 및 기타활동 >> 기타' && interest.additionalInput) {
          const additionalInputLower = interest.additionalInput.toLowerCase();
          return !this.interests.some((otherInterest, otherIndex) => {
            if (otherIndex !== index && otherInterest.inputText === '사회 및 기타활동 >> 기타') {
              return otherInterest.additionalInput.toLowerCase() === additionalInputLower;
            }
            return false;
          });
        }
        return true;
      });

      const areAllInterestsValid = this.interests.every(interest => {
        const inputText = interest.inputText || ''; // undefined 방지
        return inputText && this.isValidItem(inputText);
      });

      return !hasDuplicate && areAllInterestsValid && isAdditionalInputComplete && isAdditionalInputUnique;
    },
  },
  mounted() {
    if (this.$store.state.isSetInterests) {
      this.loadUserInterests(); // 첫 로그인을 제외하고 관심분야 데이터 로드
      console.log("loadUserInterests 실행");
    }
    this.loadCSV();
  },
  methods: {
    async loadCSV() {
      try {
        const response = await fetch('/hobbies.csv');
        const text = await response.text();
        const lines = text.split('\n');

        this.categories = {}; // 카테고리 초기화
        lines.forEach(line => {
          const items = this.parseCSVLine(line);
          if (items.length >= 2) {
            const category = items[0]; // 카테고리
            const item = items[1]; // 항목

            if (!this.categories[category]) {
              this.categories[category] = [];
            }
            this.categories[category].push(item);
          }
        });
      } catch (error) {
        console.error('CSV 파일 로딩 오류:', error);
      }
    },

    parseCSVLine(line) {
      const items = [];
      let currentItem = '';
      let quoteCount = 0;

      for (let i = 0; i < line.length; i++) {
        const char = line[i];

        if (char === '"') {
          quoteCount++;
          if (quoteCount === 3) {
            // 트리플 따옴표를 만나면 따옴표를 무시하고 다음 문자부터 시작
            quoteCount = 0;
          }
        } else if (char === ',' && quoteCount === 0) {
          // 쉼표가 트리플 따옴표 외부에 있을 때만 새 항목으로 구분
          items.push(currentItem.trim());
          currentItem = '';
        } else {
          currentItem += char;
        }
      }

      if (currentItem.length > 0) {
        items.push(currentItem.trim()); // 마지막 항목 추가
      }

      return items;
    },

    filterItems(index) {
      const inputText = this.interests[index].inputText.toLowerCase();
      this.interests[index].filteredItems = Object.entries(this.categories)
        .flatMap(([category, items]) => {
          // 카테고리 이름 검사
          const isCategoryMatched = category.toLowerCase().includes(inputText);

          const filteredItems = items
            .filter(item => item.toLowerCase().includes(inputText) || isCategoryMatched)
            .map(item => ({ category, item }));

          // 중복 검증 적용
          if (index > 0) {
            const existingInterests = this.interests
              .slice(0, index)
              .map(interest => {
                // '사회 및 기타활동 >> 기타'의 경우 추가 입력값을 포함하여 중복 여부 판단
                if (interest.inputText === '사회 및 기타활동 >> 기타' && interest.additionalInput) {
                  return `${interest.inputText} >> ${interest.additionalInput}`.toLowerCase();
                }
                return interest.inputText.toLowerCase();
              });

            return filteredItems.filter(item => {
              const fullItemText = `${category} >> ${item.item}`.toLowerCase();
              return !existingInterests.includes(fullItemText);
            });
          }
          return filteredItems;
        });
    },
    selectItem(index, { category, item }) {
      // '기타' 항목 선택 시 추가 입력란 활성화
      if (item === '기타') {
        this.interests[index].inputText = `${category} >> 기타`;
        this.interests[index].additionalInput = '';
      } else {
        this.interests[index].inputText = `${category} >> ${item}`;
        this.interests[index].additionalInput = undefined;
      }
      this.interests[index].filteredItems = [];
    },
    formatItem({ category, item }) {
      return `${category} >> ${item}`;
    },
    isValidItem(inputText) {
      return Object.entries(this.categories).some(([category, items]) =>
        items.some(item => `${category} >> ${item}` === inputText)
      );
    },
    addInterest() {
      const lastInterest = this.interests[this.interests.length - 1];
      // 마지막 항목이 '기타' 항목인 경우
      if (lastInterest && lastInterest.inputText === '사회 및 기타활동 >> 기타' && !lastInterest.additionalInput) {
        alert('기타 항목의 추가 정보를 입력해주세요.');
        return; // 추가 입력을 유도하고 메소드 종료
      }
      if (this.interests.length < 10) {
        this.interests.push({ inputText: '', filteredItems: [] });
      }
    },
    removeInterest(index) {
      if (index > -1 && index < this.interests.length) {
        this.interests.splice(index, 1);
      }
    },
    clearInputText(index) {
      this.interests[index].inputText = '';
      // '사회 및 기타활동 >> 기타'인 경우에만 additionalInput을 초기화
      if (this.interests[index].inputText === '사회 및 기타활동 >> 기타') {
        this.interests[index].additionalInput = '';
      }
    },
    isDuplicateInterest(index) {
      if (index > 0) {
        const currentInputText = this.interests[index].inputText.toLowerCase();
        const existingInterests = this.interests
          .slice(0, index)
          .map(interest => interest.inputText.toLowerCase());
        return existingInterests.includes(currentInputText);
      }
      return false;
    },
    isExistingInterest(inputText) {
      // 이미 입력된 관심분야 목록에 해당 항목이 있는지 확인
      return this.existingInterests.includes(inputText.toLowerCase());
    },
    loadUserInterests() {
      console.log("loadUserInterests() 메소드 실행 시작");
      this.$store.dispatch('fetchUserInterests').then(() => {
        // 이메일 주소와 배열을 제외한 관심분야 데이터만 필터링
        const filteredInterests = this.$store.state.userInterests.filter(interest =>
          typeof interest === 'string' && !interest.includes('@')
        );
        console.log("필터링된 관심분야: ", filteredInterests);

        // '기타' 관심분야 처리
        this.interests = filteredInterests.map(interest => {
          if (interest.startsWith('기타 >>')) {
            // '기타' 항목 처리
            const additionalText = interest.split('>>')[1].trim(); // 추가 텍스트 추출
            return {
              inputText: '사회 및 기타활동 >> 기타',
              additionalInput: additionalText,
              filteredItems: []
            };
          } else {
            // 일반 항목 처리
            return {
              inputText: interest,
              additionalInput: '',
              filteredItems: []
            };
          }
        });
      });
    },
    submitInterests() {
      // 관심 분야 데이터를 서버로 전송
      const userId = this.$store.state.id; // Vuex 스토어에서 사용자 ID 로드
      const interestData = this.interests
        .filter(interest => !this.isExistingInterest(interest.inputText))
        .reduce((acc, interest, index) => {
        // '사회 및 기타활동 >> 기타'가 선택되었을 경우 기타 입력란의 내용을 설정
        const key = `item${index + 1}`;
        acc[key] = interest.inputText === '사회 및 기타활동 >> 기타' && interest.additionalInput
          ? `기타 >> ${interest.additionalInput}`
          : interest.inputText;
        return acc;
      }, {});

      const payload = { // JSON 객체로 구성
        userId: userId, // Vuex 스토어에서 사용자 ID 로드
        interests: interestData
      };

      console.log('Sending request with payload:', JSON.stringify(payload, null, 2));

      axios.post('/api/interests/save/', payload)
        .then(response => {
            console.log('제출 성공:', response.data); // 성공 로그
            alert("관심분야 설정이 완료되었습니다.");
            this.$router.push('/Starting');
        })
        .catch(error => {
            console.error('제출 실패:', error); // 오류 로그
            alert("설정이 완료되지 않아 서비스를 이용할 수 없습니다.");
            this.$router.push('/login');
        });
      // this.updateUserInterests(this.interests.map(interest => interest.inputText));
    },
    ...mapActions(['fetchUserInterests', 'updateUserInterests', 'logout']), // Vuex 액션 매핑
    ...mapMutations(['setUserInterests']) // Vuex 뮤테이션 매핑
  },
};
</script>

<style lang="scss" scoped>
@import "../scss/main";

.background-setting {
    height: 100vh;
    width: 100vw;
    margin: 0;
    background-image: url("../assets/mypage/background.png"); // 배경 이미지
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center center;
    display: flex;
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.interest-settings {
    width: 996px;
    height: 600px;
    background-color: #FFFFFF;
    border: 7px solid #ECBC76;
    border-radius: 20px;
    padding: 20px;
    box-sizing: border-box;

    .my-link {
        width: 51px;
        height: 46px;
        margin-top: 17px;
        margin-left: 17px;
        float: left;
        cursor: pointer;
        text-decoration: none;
        font-style: normal;
        font-weight: 400;
        font-size: 40px;
        line-height: 75px;
        color: #B87514;
        display: flex;
        align-items: center;
    }

    .interest-section {
        margin-top: 30px;
        border-top: 1px solid #B87514;
        padding-top: 20px;
    }

    .add-button, .complete-button {
        width: 163px;
        height: 55px;
        margin-top: 20px;
        border-radius: 20px;
        font-size: 23px;
        line-height: 28px;
        color: #FFFFFF;
        display: flex;
        justify-content: center;
        align-items: center;
        cursor: pointer;
    }

    .add-button {
        background-color: #B87514;
    }

    .complete-button {
        background-color: #4CAF50;
    }

    .add-button:hover, .complete-button:hover {
        background-color: darken($color: #B87514, $amount: 10%);
    }

    .input-wrapper {
        display: flex;
        align-items: center;
        margin-bottom: 10px;
    }

    .index-number, .trash-button, .remove-button {
        margin-right: 10px;
    }

    .circle-button, .trash-icon {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        text-align: center;
        line-height: 20px;
        cursor: pointer;
        color: white;
        font-size: 12px;
    }

    .circle-button {
        background-color: #4CAF50;
    }

    .trash-icon {
        background-color: #FF4141;
    }

    .circle-button:hover, .trash-icon:hover {
        background-color: darken($color: #4CAF50, $amount: 10%);
    }
}
</style>