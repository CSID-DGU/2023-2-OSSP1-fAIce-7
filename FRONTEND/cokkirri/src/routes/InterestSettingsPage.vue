<template>
  <div class="background-setting">
      <div class="container">
          <div>
              <div class="frame-body">
                  <div>
                      <router-link to="/my" class="my-link">&lt;</router-link>
                      <div style="clear:both;"></div>

                      <div class="heart-img-box">
                          <div class="heart-img"></div>
                      </div>
                      
                      <div class="heart-txt">관심분야 </div>

                      <button class="heart-btn-edit" @click="submitInterests()" :disabled="!isComplete">저장</button>
                      <div style="clear:both;"></div>

                      <div class="line-for-division"></div>

                      <div class="frame-sub-body">

                          <!-- 관심분야 설정 섹션 -->
                          <div class="interest-settings" :style="{height: settingsHeight + 'px'}">
                            <div v-for="(interest, index) in interests" :key="index" class="interest-section" 
                              :class="{'selected': selectedInterests.includes(index)}" 
                              @click="toggleSelection(index)">
                                  <div class="input-wrapper">
                                      <div class="index-number">{{ index + 1 }}.</div>
                                      <input v-model="interest.inputText" @input="filterItems(index)" placeholder="관심분야 입력" class="interest-input">
                                      <!-- <div class="remove-button" @click="removeInterest(index)">
                                          <div class="remove-icon">-</div>
                                      </div> -->
                                      <div class="trash-button" @click="clearInputText(index)" v-if="interest.inputText">
                                           <div class="trash-icon">🗑️</div>
                                      </div>
                                      <input v-if="interest.inputText === '사회 및 기타활동 >> 기타'" v-model="interest.additionalInput" class="additional-input" placeholder="기타 입력란">
                                  </div>
                                  <ul v-if="interest.inputText &&   interest.filteredItems.length">
                                      <li v-for="(item, itemIndex) in interest.filteredItems" :key="itemIndex" @click="selectItem(index, item)">
                                          {{ formatItem(item) }}
                                      </li> 
                                  </ul>
                              </div>
                              <div v-if="interests.length === 0" class="no-interests-message">+ 를 눌러 취미를 추가하세요.</div>  <!-- 선택한 취미가 없을 때 표시 -->
                              <div class ="button-container">
                                <button class="add-button" @click="addInterest" :disabled="interests.length >= 10">+</button>
                                <button class="delete-button" @click="deleteInterest" :disabled="selectedInterests.length === 0">-</button>
                              </div>
                          </div>   
                      </div>
                  </div>
              </div>
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
    interests: [{ inputText: '', filteredItems: [] }],
    // 이미 입력된 관심분야 목록을 저장할 배열 추가
    existingInterests: [],
    selectedInterests: [],
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
  settingsHeight() {
    const baseHeight = 180;  // 기본 높이(패딩 등 포함)
    const itemHeight = 65;  // 각 관심분야 항목의 높이
    const dropdownHeight = 17.5;  // 각 드롭다운 항목의 추정 높이

    let dropdownTotalHeight = 0;
    this.interests.forEach(interest => {
      if (interest.filteredItems.length > 0) {
        // 드롭다운 항목 수에 따라 추가 높이 계산
        dropdownTotalHeight += dropdownHeight * interest.filteredItems.length;
      }
    });

    return baseHeight + (itemHeight * this.interests.length) + dropdownTotalHeight;
  }
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
  // 관심분야 선택 및 해제
  toggleSelection(index) {
    const selectedIndex = this.selectedInterests.indexOf(index);
    if (selectedIndex === -1) {
      this.selectedInterests.push(index);
    } else {
      this.selectedInterests.splice(selectedIndex, 1);
    }
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
  // 선택된 관심분야 삭제
  deleteInterest() {
    this.selectedInterests.sort().reverse().forEach(index => {
      this.interests.splice(index, 1);
    });
    this.selectedInterests = [];
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
          this.$router.push('/my');
      })
      .catch(error => {
          console.error('제출 실패:', error); // 오류 로그
          alert("설정이 완료되지 않아 서비스를 이용할 수 없습니다.");
          this.$router.push('/interestSettingsPage');
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

//배경화면 설정
.background-setting {
  height: 100vh;
  width: 100vw;
  
  background-image: url("../assets/mypage/background.png"); // 배경 이미지
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center;
  display: grid;
  grid-template-rows: auto;
  justify-items: center;
  align-items: center;
}

//container 클래스 위치 조정
.container{
  display: flex;
  flex-direction: column; //행 방향 정렬
  align-items: center;  //가로 방향 정렬
}
.frame-body{
  width: 996px;
  height: 600px;
  background-color: #FFFFFF;
  border: 7px solid #ECBC76;
  border-radius: 20px;

  .my-link{
      width: 51px;
      height: 46px;

      margin-top: 0px;
      margin-left: 17px;
      float:left;

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
  .my-link:hover{
      color: darken($color: #B87514, $amount: 20%);
  }
  .heart-img-box{
      width: 69px;
      height: 55px;
          
      margin-top: 0px;
      margin-left: 68px;
      float: left;

      display: flex;
      justify-content: left;
      align-items: center;
  }
  .heart-img{
      width: 40px;
      height: 40px;

      float: left;

      background-image: url("../assets/mypage/heart.png");
      background-repeat: no-repeat;
  }
  .heart-txt{
      width: 200px;
      height: 55px;

      float: left;

      display: flex;
      justify-content: left;
      align-items: center;
          
      font-style: normal;
      font-weight: 400;
      font-size: 30px;
      line-height: 45px;
  }
  .heart-btn-edit{
      width: 163px;
      height: 55px;
      background-color: #B87514;

      cursor: pointer;

      margin-top: 0px;
      margin-left: 444px;
      border-radius: 20px;
      float: left;

      color: #FFFFFF;
      display: flex;
      justify-content: center;
      align-items: center;

      font-style: normal;
      font-weight: 400;
      font-size: 23px;
      line-height: 28px;
  }
  .heart-btn-edit:hover {
          background-color: darken($color: #B87514, $amount: 10%);
  }
  .line-for-division{
      width: 891px;
      height: 1px;
      margin-top: 25px;
      margin-left: 53px;
      margin-bottom: 0px;

      border: 1px solid #B87514;
  } 
  .frame-sub-body{
      width: 870px;
      height: 350px;
      
      margin-left: 100px;
      margin-top: 30px; 

      background: #FFFFFF;
      border-radius: 20px;
      overflow-y: scroll;

      .interest-settings {
          width: 800px;
          height: 800px;
          background-color: #FFFFFF;
          border: 7px solid #ECBC76;
          border-radius: 20px;
          padding: 20px;
          box-sizing: border-box;
          .interest-section {
            padding-top: 20px; /* margin-top 대신 padding-top 사용 */
            border-bottom: 1px solid #B87514;
            background-color: transparent; /* 기본 배경색 설정 */
            transition: background-color 0.3s; /* 부드러운 색상 전환 효과 */

            &.selected {
              background-color: #ECBC76; /* 선택된 항목의 배경색 */
            }
          }
          .input-wrapper {
              display: flex;
              align-items: center;
              margin-bottom: 20px;
          }
          .index-number {
              margin-right: 10px;
          }
          .remove-button {
              margin-left: 10px;
              margin-right: 10px;
          }
          .remove-icon { 
              width: 20px;
              height: 20px;
              border-radius: 50%;
              text-align: center;
              align-items: center; /* 세로 방향 가운데 정렬 */
              line-height: 20px;
              cursor: pointer;
              color: white;
              font-size: 30px;

              background-color: #B87514;
          }
          .remove-icon:hover{
              background-color: darken($color: #B87514, $amount: 10%);  
          }
/*            .trash-button {
              width: 20px;
              height: 20px;
              border-radius: 50%;
              text-align: center;
              line-height: 20px;
              cursor: pointer;
              color: white;
              font-size: 12px;

              margin-right: 10px;
          } 
          .trash-icon {
              background-color: #FF4141;
          }
          */
          .no-interests-message{
              display: flex;
              justify-content: center;

              font-style: normal;
              font-weight: 500;
              font-size: 25px;
              line-height: 38px;
              color: #B87514;
          }
          .button-container{
              display: flex;  
              justify-content: center;
              width: 100%;
          }
          .add-button {
              width: 163px;
              height: 55px;
              margin-top: 20px;
              margin-right: 10px;
              border-radius: 20px;
              font-size: 30px;
              line-height: 28px;
              color: #FFFFFF;
              cursor: pointer;
              background-color: #6BCB77;
          }
          .delete-button {
              width: 163px;
              height: 55px;
              margin-top: 20px;
              margin-left: 10px;
              border-radius: 20px;
              font-size: 30px;
              line-height: 28px;
              color: #FFFFFF;
              cursor: pointer;
              background-color: #FF6961;
          }
          .add-button:not([disabled]):hover{
              background-color: darken($color: #6BCB77, $amount: 10%);
          }
          .delete-button:not([disabled]):hover {
              background-color: darken($color: #FF6961, $amount: 10%);
          }
          .trash-button:hover, .trash-icon:hover {
              background-color: darken($color: #4CAF50, $amount: 10%);
          }
      }
  }
}
      //스크롤바 스타일
      .frame-sub-body::-webkit-scrollbar {  //스크롤바의 너비
          width: 8px;
      }
      .frame-sub-body::-webkit-scrollbar-track {  //트랙(바탕 부분)의 색
          width: 30px;
          background: #FFDBAA;
      }
      .frame-sub-body::-webkit-scrollbar-thumb {  //스크롤바의 이동 부분
          height: 10%;
          background-color: #B87514;
          border-radius: 10px;
      }
</style>