<template>
<div class="background-setting">
  <div class="layout">
    <router-link to="/my" class="my-link">&lt;</router-link>
    <!-- 관심분야 설정 섹션 -->
    <div class="interest-settings">
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
      categories: {
        // 카테고리 데이터
        '문화예술관람활동': ['전시회 관람 (미술, 사진, 건축, 디자인 등)', '박물관 관람', '음악 연주회 관람(클래식, 오페라 등)', '전통예술공연 관람(국악, 민속놀이 등)', '연극공연 관람(뮤지컬 포함)', '무용 공연 관람', '영화 관람', '연예 공연 관람(쇼, 콘서트, 마술 쇼 등)'],
        '문화예술참여활동': ['문학 행사 참여', '글짓기/독서 토론', '미술 활동(그림, 서예, 조각, 디자인, 도예, 만화 등)', '악기 연주/노래 교실', '전통예술 배우기(사물놀이, 줄타기 등)', '사진 촬영(디지털 카메라 포함)', '연극', '춤/무용(발레, 한국무용, 현대무용, 방송댄스, 스트릿댄스, 비보잉 등)'],
        '스포츠관람활동': ['농구 관람', '배구 관람', '야구 관람', '축구 관람', '족구 관람', '테니스 관람', '스쿼시 관람', '당구 관람', '포켓볼 관람', '볼링 관람', '탁구 관람', '골프 관람', '수영 관람', '윈드서핑 관람', '수상스키 관람', '스노보드 관람', '스키 관람', '아이스 스케이트 관람', '아이스 하키 관람', '보디빌딩', '배드민턴 관람', '줄넘기 관람', '체조  관람', '훌라후프 관람', '마라톤 관람', '태권도 관람', '유도 관람', '합기도 관람', '검도 관람', '권투 관람', '사이클링 관람', '산악자전거 관람', '인라인 스케이트 관람', '승마 관람', '클라이밍 관람'],
        '스포츠참여활동': ['농구', '배구', '야구', '축구', '족구', '테니스', '스쿼시', '당구', '포켓볼', '볼링', '탁구', '골프', '수영', '윈드서핑', '수상스키', '스킨스쿠버다이빙', '래프팅', '요트', '스노보드', '스키', '아이스 스케이트', '아이스 하키', '헬스', '에어로빅', '요가', '필라테스', '배드민턴', '줄넘기', '체조', '훌라후프', '마라톤', '태권도', '유도', '합기도', '검도', '권투', '탱고', '왈츠', '자이보', '맘보', '폴카', '차차차', '사이클링', '산악자전거', '인라인 스케이트', '승마', '클라이밍'],
        '관광활동': ['문화유적 방문(고궁, 절, 유적지 등)', '자연명승 및 풍경 관람', '삼림욕', '국내여행', '해외여행', '소풍/야유회', '온천/해수욕', '유람선 타기', '테마파크/놀이공원/동물원/식물원 가기', '지역축제 참가', '자동차 드라이브'],
        '취미오락활동': ['수집 활동(스크랩 포함)', '생활공예(십자수, 비즈공예, DIY, 꽃꽂이 등)', '요리', '다도', '반려 동물 돌보기', '노래방 가기', '인테리어(집, 자동차 등)', '등산', '낚시', '홈페이지/블로그 관리', 'SNS', '미디어 제작', '인터넷 서핑', '컴퓨터 게임', '모바일 게임', '콘솔 게임', '보드 게임', '퍼즐/큐브', '바둑', '체스', '장기', '쇼핑', '외식', '독서(웹소설 포함)', '만화(애니, 웹툰)', '피부 관리', '헤어 관리', '네일 아트', '마사지', '공부', '이색/테마카페 체험(방탈출, VR, 낚시카페 등)', '원예(화분, 화단 가꾸기 등)'],
        '휴식활동': ['산책', '목욕/사우나/찜질방', '낮잠', 'TV시청', '영상 시청(VOD, 유튜브, 넷플릭스, 웨이브, 티빙, 디즈니플러스 등)', '라디오/팟캐스트 청취', '음악 감상', '신문/잡지 보기'],
        '사회 및 기타활동': ['사회봉사활동', '종교 활동', '클럽/나이트/디스코/캬바레 가기', '기타']
      },
      interests: [{ inputText: '', filteredItems: [] }],
      // 이미 입력된 관심분야 목록을 저장할 배열 추가
      existingInterests: [],
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
  },
  methods: {
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

.layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.my-link{
  width: 35px;
  height: 31px;
  margin-top: 5px;
  margin-left: 17px;
  float:left;

  cursor: pointer;
  text-decoration: none;

  font-size: 35px;
  color: #B87514;
  display: flex;

  position:absolute;
  top: 60px;
  left: 225px;
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