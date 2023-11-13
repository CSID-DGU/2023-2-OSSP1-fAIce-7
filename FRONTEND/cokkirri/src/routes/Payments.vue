<template>
  <div>

    <div class="Boxing-Payments">

      <div class = "Banner-Payments">
        <div class="Shift-Back">
          <div class="Back-icon">
            <a href="javascript:history.back();"><img :src="imagePath_arrow" alt="Arrow" /></a>
            <a href="javascript:history.back();" class="Back_txt">뒤로가기</a>
          </div>
        </div>
        <div class="Title">
          <p>하트 충전소</p>
        </div>
      </div>

      <div class="Parent-Payments">

        <div class="HeartNum">

            <div class = "HeartNum_content">
              <p class = "HeartNum_text">나의하트</p>
              <p class = "NumOfHeart">{{this.$store.state.heart}}</p>
              <img :src="imagePath_htnum" alt="Heartnum" class="htnum" />
            </div>
            
        </div>

        <div class="GoPay">
          <div class = "GoPay_content">
            <div class="GoPay_text">
              <p>하트 충전</p>
            </div>
            <div class="GoPay_heart">
              <img :src="imagePath_heart" alt="heart" class = "heart-1000"/>
              <img :src="imagePath_heart" alt="heart" class = "heart-3000"/>
              <img :src="imagePath_heart" alt="heart" class = "heart-5000"/>
              <img :src="imagePath_heart" alt="heart" class = "heart-10000"/>
            </div>

            <div class="GoPay_5ht"> <p>10개</p> </div>
            <div class="GoPay_10ht"> <p>30개</p> </div>
            <div class="GoPay_15ht"> <p>50개</p> </div>
            <div class="GoPay_20ht"> <p>100개</p> </div>

            <div class="GoPay_img">
              <img :src="imagePath_gopay" alt="Heartgopay" />
            </div>

            <div>
              <a href="#" @click.prevent="checkModule(1000)" class = "charge1000">1000원</a>
              <a href="#" @click.prevent="checkModule(3000)" class = "charge3000">3000원</a>
              <a href="#" @click.prevent="checkModule(5000)" class = "charge5000">5000원</a>
              <a href="#" @click.prevent="checkModule(10000)" class = "charge10000">10000원</a>
            </div>
          </div>
        </div>

        <div class="UseHistory">
          <div class = "UseHistory_content">

            <div class="UseHistory_text"><p>결제 내역</p></div>
            <div class="UseHistory_img"><img :src="imagePath_history" alt="UseHistory" /></div>
            
            <div class = "UseHistory_his">
              <div v-for="history in usageHistory" :key="history.date">
                <p>&gt;&gt; {{ history.date }} &nbsp; &nbsp; &nbsp;+{{ history.amount/100 }}개</p>
              </div>
            </div>
          
          </div>
        </div>

    </div>
  </div>
</div>
</template>

<script>
import axios from '../api/index.js';

export default {
  data() {
    return {
      // 이미지 삽입
      imagePath_arrow: require("@/assets/pay/arrow-left.png"),
      imagePath_htnum: require("@/assets/pay/rec_heart.png"),
      imagePath_gopay: require("@/assets/pay/rec_gopay.png"),
      imagePath_history: require("@/assets/pay/rec_history.png"),
      imagePath_heart: require("@/assets/pay/heart.png"),

      userId:"",
      payDate: "",
      usageHistory: [], // 결제 내역을 저장할 배열
    };
  },

  created() {
    // 오늘 날짜로 payDate 설정
    this.payDate = this.getCurrentDate();

    // 결제 내역 검색 함수 호출
    this.searchPaymentById();

    //서버로부터 로그인한 사용자의 정보를 받아오기
    axios.get('/admin/user/id?userId=' + this.$store.state.id)
      .then(response => {
        this.userId = response.data.userId;
        this.searchPaymentById(this.userId);
      })
      .catch(error => {
        console.error('사용자 정보를 가져오는데 실패했습니다:', error);
      });
  },

  methods: {
    //결제날짜
    getCurrentDate() {
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    // 금액에 따른 결제
    checkModule(amount) {
      var IMP = window.IMP;
      IMP.init(""); // 대외비, 가맹점 코드 입력

      IMP.request_pay(
        {
          pg: 'kakaopay.TC0ONETIME',
          pay_method: 'card',
          merchant_uid: 'heart_' + new Date().getTime(),
          name: 'cokkirri 하트 결제',
          payDate: this.payDate,
          amount: amount,
          buyer_email: String(this.userId),
        },
        (rsp) => {
          console.log(rsp);
          let msg;

          if (rsp.success) {
            msg = '결제가 완료되었습니다.';
            this.$router.push("/Payments"); // Payments 페이지로 이동

            // 결제 정보 및 날짜 데이터 콘솔에 출력
            console.log('고유ID:', rsp.imp_uid);
            console.log('상점 거래ID:', rsp.merchant_uid);
            console.log('결제 금액:', rsp.paid_amount);
            console.log('카드 승인번호:', rsp.apply_num);

            // 서버에 결제 정보 전송
            this.sendPaymentInfo(amount);
          } else {
            msg = '결제에 실패하였습니다.';
            msg += '에러내용 : ' + rsp.error_msg;
            this.$router.push("/Payments"); // Payments 페이지로 이동
          }
          alert(msg);
        }
      );
    },

    searchPaymentById() {
      try {
        const userId = this.$store.state.id;
        
        axios.get('/admin/user/payment', {
          params: {
            userId: this.$store.state.id
          }
        })
        .then((result) => {
          console.log(userId + '의 결제내역 조회 요청');
          this.userId = this.$store.state.id; // 이거였다.. 이거였어... 이것이 문제였습니다...
          const paymentData = result.data;

          // 결제 내역 데이터를 필요한 형식으로 가공
          const formattedHistory = paymentData.map(item => ({
            date: item.payDate,
            amount: item.amount
          }));

          // 가공된 데이터를 usageHistory 배열에 할당
          this.usageHistory = formattedHistory;

          console.log(result);
        })
        .catch(function(error) {
          console.log(error);
        });
      } catch (error) {
        console.log(error);
      }
    },

    sendPaymentInfo(amount) {
      const paymentData = {
        userId: this.userId,
        payDate: this.payDate,
        amount: parseInt(amount),
      };

      console.log(paymentData);

      axios.put('/payment', paymentData)
      .then(response => {
        console.log('결제 성공!:', response.data);

        // 결제 내역 가공
        const formattedHistory = {
            date: this.payDate,
            amount: amount
          };

        // Vuex 스토어의 usageHistory 상태 값을 업데이트
        this.$store.commit('updateUsageHistory', formattedHistory);
        // 변경된 정보를 즉각 반영하기 위해 userInfoUpdate 액션을 디스패치
        this.$store.dispatch('userInfoUpdate');
      })
      .catch(error => {
        console.error('결제 실패:', error);
      });
    },
  },
};
</script>

<style scoped>
.Boxing-Payments {
  height: 100vh;
  width: 100vw;
  margin:0;

  background-color: #FFFEF9;
  display: grid;
  grid-template-rows: auto;
  justify-items: center;
  align-items: center;

  overflow: auto;
  white-space: nowrap;
}

.Banner-Payments{
  align-content : center;
}
.Shift-Back{
  float : right;
}

.Back-icon {
  position: absolute;
  left: 3.68%;
  right: 93.89%;
  top: 6%;
  bottom: 90.11%;
}

.Back_txt {
  color: #B87514;
  position: absolute;
  width: 98px;
  height: 38px;
  left: 111px;
  top: 0px;
  text-decoration: none;
  font-style: normal;
  font-weight: 600;
  font-size: 25px;
  line-height: 38px;
}

.Title {
  color: #B87514;
  position: absolute;
  width: 300px;
  height: 120px;
  left: 800px;
  top: 60px;
  font-style: normal;
  font-weight: bold;
  font-size: 70px;
  line-height: 120px;
  display: flex;
  text-align: center;
  white-space: nowrap;
}

.Parent-Payments {
  width: 1500px;
  height : 600px;
  margin-top : -400px;
  align-content : center;
}

.HeartNum {
  position: relative;
  text-align: center;
  float: left;
  width: 30px;
  top: 80px;
  box-sizing: border-box;
}

.HeartNum_content{
  position: relative;
}

.htnum {
  width: 368px;
  height: 600px;
}

.HeartNum_text {
  color: #000;
  position: absolute;
  width: 160px;
  height: 80px;
  left: 130px;
  top: 50px;
  font-style: normal;
  font-weight: 600;
  font-size: 40px;
  line-height: 60px;
  display: flex;
  align-items: center;
  text-align: center;
}

.NumOfHeart {
  z-index : 1;
  position: absolute;
  width: 100px;
  height: 100px;
  left: 130px;
  top: 250px;
  font-style: normal;
  font-weight: 600;
  font-size: 50px;
  line-height: 75px;
  display: flex;
  align-items: center;
  text-align: center;
  color: #000000;
}

.GoPay {
  position: relative;
  text-align: center;
  margin-left: 5%;
  left: 500px;
  right: 535px;
  top: 80px;
  float: left;
  width: 30px;
  box-sizing: border-box;
}

/* GoPay 컨테이너 */
.GoPay_content {
  position: relative;
}

.GoPay_heart,
.GoPay_5ht,
.GoPay_10ht,
.GoPay_15ht,
.GoPay_20ht,
.GoPay_text,
.heart-1000,
.heart-3000,
.heart-5000,
.heart-10000,
.charge1000,
.charge3000,
.charge5000,
.charge10000,
.GoPay_img {
  position: absolute;
}

.GoPay_heart {
  left: 64px;
  top: 240px;
  vertical-align: auto;
}

.GoPay_5ht {
  left: 120px;
  top: 228px;
  font-weight: bold;
  font-size: 20px;
  white-space: nowrap; /* 화면 축소 시 자동줄바꿈 방지 */
}

.GoPay_10ht {
  left: 120px;
  top: 304px;
  font-weight: bold;
  font-size: 20px;
  white-space: nowrap; /* 화면 축소 시 자동줄바꿈 방지 */
}

.GoPay_15ht {
  left: 120px;
  top: 380px;
  font-weight: bold;
  font-size: 20px;
  white-space: nowrap; /* 화면 축소 시 자동줄바꿈 방지 */
}

.GoPay_20ht {
  left: 120px;
  top: 456px;
  font-weight: bold;
  font-size: 20px;
  white-space: nowrap; /* 화면 축소 시 자동줄바꿈 방지 */
}

.GoPay_text {
  width: 160px;
  height: 80px;
  left: 128px;
  top: 80px;
  color: #000;
  font-style: normal;
  font-weight: 600;
  font-size: 40px;
  line-height: 60px;
  display: flex;
  align-items: center;
  text-align: center;
}

.GoPay_img {
  left: 0;
  top: 0;
}

.heart-1000 {
  position: absolute;
  left: 2px;
  top: 0px;
  vertical-align: auto;
}

.heart-3000 {
  position: absolute;
  left: 2px;
  top: 80px;
  vertical-align: auto;
}

.heart-5000 {
  position: absolute;
  left: 2px;
  top: 160px;
  vertical-align: auto;
}

.heart-10000 {
  position: absolute;
  left: 2px;
  top: 240px;
  vertical-align: auto;
}

.charge1000 {
  color: white;
  background: #ECBC76;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 12px 20px;
  gap: 10px;
  position: absolute;
  width: 100px;
  height: 30px;
  left: 240px;
  top: 240px;
}

.charge3000 {
  color: white;
  background: #ECBC76;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 12px 20px;
  gap: 10px;
  position: absolute;
  width: 100px;
  height: 30px;
  left: 240px;
  top: 320px;
}

.charge5000 {
  color: white;
  background: #ECBC76;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 12px 20px;
  gap: 10px;
  position: absolute;
  width: 100px;
  height: 30px;
  left: 240px;
  top: 400px;
}

.charge10000 {
  color: white;
  background: #ECBC76;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 12px 20px;
  gap: 10px;
  position: absolute;
  width: 100px;
  height: 30px;
  left: 240px;
  top: 480px;
}


.UseHistory {
  position: relative;
  text-align: center;
  top: 80px;
  width: 30px;
  box-sizing: border-box;
  left :  1200px;
}

.UseHistory_content{
  position : relative;
}

.UseHistory_text {
  color: #FFFEF9;
  position: absolute;
  width: 160px;
  height: 80px;
  left: 130px;
  top: 80px;
  font-style: normal;
  font-weight: 600;
  font-size: 40px;
  line-height: 60px;
  display: flex;
  align-items: center;
  text-align: center;
}

.UseHistory_his {
  position: absolute;
  width: 251px;
  height: 800px;
  left: 75px;
  top: 200px;
  font-style: normal;
  font-weight: bold;
  font-size: 20px;
  line-height: 30px;
  display: block;
  align-items: center;
  color: white;
  max-height: 350px;
  overflow-y: auto;
}
</style>