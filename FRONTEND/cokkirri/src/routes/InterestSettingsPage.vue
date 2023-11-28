<template>
  <div class="layout">
    <div class="interest-settings">
      <h2>관심분야 설정</h2>
      <div v-for="(interest, index) in interests" :key="index" class="interest-section">
        <!-- 사용자 입력 필드 -->
        <div class="input-wrapper">
          <!-- 인덱스 표시 -->
          <div class="index-number">{{ index + 1 }}.</div>
          <input v-model="interest.inputText" @input="filterItems(index)" placeholder="관심분야 입력">
          <!-- 휴지통 버튼 -->
          <div class="trash-button" @click="clearInputText(index)" v-if="interest.inputText">
            <div class="trash-icon">🗑️</div>
          </div>
          <!-- 삭제 버튼 -->
          <div class="remove-button" @click="removeInterest(index)" v-if="interest.inputText">
            <div class="circle-button">-</div>
          </div>
          <!-- '기타 입력란' 활성화 -->
          <input v-if="interest.inputText === '사회 및 기타활동 >> 기타'" v-model="interest.additionalInput" class="additional-input" placeholder="기타 입력란">
        </div>
        <!-- 필터링된 항목 리스트 -->
        <ul v-if="interest.inputText && interest.filteredItems.length">
          <li v-for="(item, itemIndex) in interest.filteredItems" :key="itemIndex" @click="selectItem(index, item)">
            {{ formatItem(item) }}
          </li>
        </ul>
      </div>
      <!-- 항목 추가 버튼 -->
      <button @click="addInterest" :disabled="interests.length >= 10" class="add-button">+</button>
      <!-- 항목 완료 버튼 -->
      <button @click="submitInterests" :disabled="!isComplete" class="complete-button">완료</button>
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
      categories: {
        // 카테고리 데이터
        '문화예술관람활동': ['전시회 관람 (미술, 사진, 건축, 디자인 등)', '박물관 관람', '음악연주회 관람(클래식, 오페라 등)', '전통예술공연 관람(국악, 민속놀이 등)', '연극공연 관람(뮤지컬 포함)', '무용공연 관람', '영화관람', '연예공연 관람(쇼, 콘서트, 마술 쇼 등)'],
        '문화예술참여활동': ['문학행사참여', '글짓기/독서토론', '미술활동(그림, 서예, 조각, 디자인, 도예, 만화 등)', '악기연주/노래교실', '전통예술 배우기(사물놀이, 줄타기 등)', '사진촬영(디지털카메라 포함)', '연극', '춤/무용(발레, 한국무용, 현대무용, 방송댄스, 스트릿댄스, 비보잉 등)'],
        '스포츠관람활동': ['농구', '배구', '야구', '축구', '족구', '테니스', '스쿼시', '당구', '포켓볼', '볼링', '탁구', '골프', '수영', '윈드서핑', '수상스키', '스노보드', '스키', '아이스 스케이트', '아이스 하키', '보디빌딩', '배드민턴', '줄넘기', '체조', '훌라후프', '마라톤', '태권도', '유도', '합기도', '검도', '권투', '탱고', '왈츠', '자이보', '맘보', '폴카', '차차차', '사이클링', '산악자전거', '인라인 스케이트', '승마', '클라이밍'],
        '스포츠참여활동': ['농구', '배구', '야구', '축구', '족구', '테니스', '스쿼시', '당구', '포켓볼', '볼링', '탁구', '골프', '수영', '윈드서핑', '수상스키', '스킨스쿠버다이빙', '래프팅', '요트', '스노보드', '스키', '아이스 스케이트', '아이스 하키', '헬스', '에어로빅', '요가', '필라테스', '태보', '배드민턴', '줄넘기', '체조', '훌라후프', '마라톤', '태권도', '유도', '합기도', '검도', '권투', '탱고', '왈츠', '자이보', '맘보', '폴카', '차차차', '사이클링', '산악자전거', '인라인 스케이트', '승마', '클라이밍'],
        '관광활동': ['문화유적방문(고궁, 절, 유적지 등)', '자연명승 및 풍경 관람', '삼림욕', '국내여행', '해외여행', '소풍/야유회', '온천/해수욕', '유람선 타기', '테마파크/놀이공원/동물원/식물원 가기', '지역축제 참가', '자동차 드라이브'],
        '취미오락활동': ['수집활동(스크랩 포함)', '생활공예(십자수, 비즈공예, DIY, 꽃꽂이 등)', '요리', '다도', '반려동물 돌보기', '노래방 가기', '인테리어(집. 자동차 등)', '등산', '낚시', '홈페이지/블로그 관리', 'SNS', '미디어 제작', '인터넷 서핑', '컴퓨터게임', '모바일게임', '콘솔게임', '보드게임', '퍼즐/큐브', '바둑', '체스', '장기', '쇼핑', '외식', '독서(웹소설 포함)', '만화(애니, 웹툰)', '피부관리', '헤어관리', '네일아트', '마사지', '공부', '이색/테마카페 체험(방탈출, VR, 낚시카페 등)', '원예(화분, 화단가꾸기 등)'],
        '휴식활동': ['산책', '목욕/사우나/찜질방', '낮잠', 'TV시청', '영상시청(VOD, 유튜브, 넷플릭스, 웨이브, 티빙, 디즈니플러스 등)', '라디오/팟캐스트 청취', '음악 감상', '신문/잡지 보기'],
        '사회 및 기타활동': ['사회봉사활동', '종교활동', '클럽/나이트/디스코/캬바레 가기', '기타']
      },
      interests: [{ inputText: '', filteredItems: [] }],
      // 이미 입력된 관심분야 목록을 저장할 배열 추가
      existingInterests: [],
    };
  },
  computed: {
    isComplete() {
      const hasDuplicate = this.interests.some((interest, index) =>
        interest.inputText &&
        (this.isExistingInterest(interest.inputText) || this.isDuplicateInterest(index))
      );

      const isAdditionalInputComplete = this.interests.every(interest => {
        if (interest.inputText === '사회 및 기타활동 >> 기타') {
          return interest.additionalInput && interest.additionalInput.trim() !== '';
        }
        return true;
      });

      const isAdditionalInputUnique = this.interests.every((interest, index) => {
        if (interest.inputText === '사회 및 기타활동 >> 기타' && interest.additionalInput) {
          const additionalInputLower = interest.additionalInput.toLowerCase();
          return !this.interests.some((otherInterest, otherIndex) => 
            otherIndex !== index &&
            otherInterest.inputText.split(' >> ')[1].toLowerCase() === additionalInputLower
          );
        }
        return true;
      });

      const areAllInterestsValid = this.interests.every(interest =>
        interest.inputText && this.isValidItem(interest.inputText)
      );

      return !hasDuplicate && areAllInterestsValid && isAdditionalInputComplete && isAdditionalInputUnique;
    },
  },
  mounted() {
    this.fetchUserInterests(); // 마운트 시 사용자의 현재 관심분야를 로드
  },
  methods: {
    filterItems(index) {
      const inputText = this.interests[index].inputText.toLowerCase();
      this.interests[index].filteredItems = Object.entries(this.categories)
        .flatMap(([category, items]) => {
          // 현재 입력 중인 텍스트 필드를 제외하고 이미 입력된 항목 필터링
          const filteredItems = items
            .filter(item => item.toLowerCase().includes(inputText))
            .map(item => ({ category, item }));
          if (index > 0) {
            const existingInterests = this.interests
              .slice(0, index)
              .map(interest => interest.inputText.toLowerCase());
            return filteredItems.filter(item =>
              !existingInterests.includes(`${category} >> ${item.item.toLowerCase()}`)
            );
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
      if (this.interests.length < 10) {
        this.interests.push({ inputText: '', filteredItems: [] });
      }
    },
    removeInterest(index) {
      this.interests.splice(index, 1);
    },
    clearInputText(index) {
      this.interests[index].inputText = ''; // 입력한 텍스트 지우기
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
    submitInterests() {
      // 관심 분야 데이터를 서버로 전송
      const interestData = {};
      const userId = this.$store.state.id; // Vuex 스토어에서 사용자 ID 로드
      this.interests.filter(interest => !this.isExistingInterest(interest.inputText))
        .forEach((interest, index) => {
          // '사회 및 기타활동 >> 기타'가 선택되었을 경우 기타 입력란의 내용을 설정
          if (interest.inputText === '사회 및 기타활동 >> 기타' && interest.additionalInput) {
            interestData[`item${index + 1}`] = `기타 >> ${interest.additionalInput}`;
          } else {
            // 그 외의 경우는 카테고리와 항목을 결합하여 설정
            interestData[`item${index + 1}`] = interest.inputText;
          }
        });

      axios.post('/api/interests/save', { userId, interests: interestData })
        .then(response => {
          // 성공적으로 데이터를 전송했을 때의 처리
          console.log('관심 분야가 성공적으로 제출되었습니다:', response.data);
          alert("관심분야 설정이 완료되었습니다.")
          this.$router.push('/Starting');
        })
        .catch(error => {
          // 오류 발생 시의 처리
          console.error('관심 분야 제출 중 오류 발생:', error);
          alert("설정이 완료되지 않아 서비스를 이용할 수 없습니다.");
          this.$router.push('/');
        });
      this.updateUserInterests(this.interests.map(interest => interest.inputText));
    },
    ...mapActions(['fetchUserInterests', 'updateUserInterests']), // Vuex 액션 매핑
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
  width: 100%; /* 전체 너비의 100% */
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
  margin-bottom: 15px; /* 버튼 하단 마진 추가 */
}

/* 삭제 버튼 스타일 */
.input-wrapper {
  display: flex;
  align-items: center; /* 세로 중앙 정렬 */
}

.remove-button {
  display: flex;
  align-items: center; /* 세로 중앙 정렬 */
  cursor: pointer;
}

.circle-button {
  width: 20px;
  height: 20px;
  background-color: #4CAF50; /* 초록색 배경 */
  color: white; /* 흰색 글자 색상 */
  border-radius: 50%; /* 원 모양의 버튼을 만듭니다. */
  text-align: center;
  line-height: 20px;
  cursor: pointer;
  margin-left: 5px; /* 텍스트 필드와 간격 조절 */
}

.circle-button:hover {
  background-color: #45a049; /* 마우스 호버 시 배경 색상 변경 */
}

/* 번호 표시 스타일 */
.index-number {
  margin-right: 10px; /* 숫자와 텍스트 필드 사이의 공간 조정 */
}

/* 휴지통 모양의 아이콘 스타일 */
.trash-button {
  display: flex;
  align-items: center; /* 세로 중앙 정렬 */
  cursor: pointer;
}

.trash-icon {
  width: 20px;
  height: 20px;
  /* 원하는 아이콘 배경 스타일을 설정하세요 */
  background-color: #4CAF50; /* 배경 색상 */
  border-radius: 50%; /* 원 모양의 배경 */
  text-align: center;
  line-height: 20px;
  cursor: pointer;
  margin-left: 5px; /* 텍스트 필드와 간격 조절 */
  color: white; /* 아이콘 색상 */
  font-size: 12px; /* 아이콘 크기 설정 */
}

/* 휴지통 아이콘 마우스 오버 시 스타일 */
.trash-icon:hover {
  background-color: #45a049; /* 마우스 오버 시 배경 색상 변경 */
}

/* 추가 버튼 스타일 */
.add-button {
  width: 30px; /* 버튼 너비 */
  height: 30px; /* 버튼 높이 */
  background-color: #4CAF50; /* 버튼 배경 색상 */
  color: white; /* 버튼 글자 색상 */
  border: none; /* 테두리 없음 */
  border-radius: 50%; /* 원 모양의 버튼을 만듭니다. */
  cursor: pointer; /* 마우스 오버 시 커서 변경 */
  display: flex; /* 내부 요소 수평 정렬을 위해 필요한 설정 */
  justify-content: center; /* 내부 요소 수평 정렬을 위해 필요한 설정 */
  align-items: center; /* 내부 요소 수직 정렬을 위해 필요한 설정 */
  font-size: 18px; /* 아이콘 크기 설정 */
  margin-left: 80px;
  margin-bottom: 20px;
  &:disabled {
    background-color: #ccc !important; /* 배경 색상 변경 */
    cursor: not-allowed !important; /* 비활성화된 상태에서는 색상 변경 금지 */
  }
}

.add-button:hover {
  background-color: #45a049; /* 마우스 호버 시 배경 색상 변경 */
}

/* 완료 버튼 스타일 */
.complete-button {
  width: 100%; /* 버튼 너비 */
  padding: 10px; /* 버튼 내부 패딩 */
  background-color: #4CAF50; /* 버튼 배경 색상 */
  color: white; /* 버튼 글자 색상 */
  border: none; /* 테두리 없음 */
  border-radius: 4px; /* 테두리 둥근 정도 */
  cursor: pointer; /* 마우스 오버 시 커서 변경 */
  &:disabled {
    background-color: #ccc !important; /* 배경 색상 변경 */
    cursor: not-allowed !important; /* 비활성화된 상태에서는 색상 변경 금지 */
  }
}

.complete-button:hover {
  background-color: #45a049; /* 마우스 호버 시 배경 색상 변경 */
}

.additional-input {
  margin-left: 10px;
}
</style>