<!--매칭페이지로 넘어가기 전 스타팅페이지-->

<template>
  <div class="background-setting">
      <div class = "Gotopage">
        <div class="link-page1">유저 : {{this.$store.state.name}}</div>
        <!-- 요소 두 개 추가 -->
        <router-link class="link-page2" to = "/my">마이페이지</router-link>
        <div class="link-page3" @click="logout()">로그아웃</div>
      </div>
      <div class = "whole">
          <div class = "question">
            <div class="img-box">
              <div class="text-1">
                <!-- <p>심각한<br><br><br>'Co-끼리' 중독입니다.</p>  -->
              </div>
            </div>
            <div style="clear:both;"></div>
            <div class="img-box-2">
              <div class="text-2">
                <!-- <p>흥, 중독인지 아닌지<br><br><br>매칭된 사람한테 물어봐요.</p>  -->
              </div>
            </div>
            <router-link class="mybutton" to="/my/matching">매칭 페이지</router-link>
          </div>
      </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      imgePath_q: require('@/assets/startmainpage/user_question.png'),
      imagePath_a: require('@/assets/startmainpage/user_answer.png'),
      imagePath_back: require('@/assets/startmainpage/MainBack.png')
    };
  },
  methods:{
    logout(){
      try{
        this.$store.commit('logout')
        alert("로그인 해제가 완료되었습니다.")
      }catch(error){
        console.log(error)
      }
    }
  }
};
</script>


<style lang="scss" scoped>

.background-setting{
  height: 100vh;
  width: 100vw;

  background-image: url("@/assets/startmainpage/MainBack.png");
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;

  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.whole{
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.mybutton{
  width: 230px;
  height: 67px;

  background: #B87514;
  border-radius: 50px;

  cursor: pointer;
  text-decoration : none;

  display: flex;
  justify-content: center;
  align-items: center;

  font-style: normal;
  font-weight: 700;
  font-size: 28px;
  line-height: 24px;
  text-align: center;
  letter-spacing: 0.5px;

  color: #FFFFFF;
}

.Gotopage{
 /* 세 개 묶음 박스 width여기서 수정 */
  width: 500px;
  height: 30px;

  margin-left : 700px;
  margin-bottom: 0px;

  .link-page1{
    width: 120px;
    height: 30px;

    float: left;

    text-decoration : none;
    font-style: normal;
    font-weight: 600;
    font-size: 20px;

    line-height: 30px;
    color: #B87514;

    padding-right : 20px;
  }

  .link-page2{
      width: 120px;
      height: 30px;

      float: left;

      text-decoration : none;
      font-style: normal;
      font-weight: 600;
      font-size: 20px;
      line-height: 30px;
      color: #B87514;

      padding-right : 20px;
  }

  .link-page3{
      width: 120px;
      height: 30px;

      float: left;

      cursor: pointer;

      text-decoration : none;
      font-style: normal;
      font-weight: 600;
      font-size: 20px;
      line-height: 30px;
      color: #B87514;

      padding-right : 20px;
  }
}



/* 각 요소 스타일 수정 */


.question{ /* 중독입니다.*/
  width: 500px;
  height: 800px;

  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  
  .img-box{
    //background-image: url('../assets/startmainpage/user_question.png');
    background-size: cover;
    background-repeat: no-repeat;

    width: 484px;
    height: 230px;
  }
  .text-1{
    margin-left: 30px;

    font-style: normal;
    font-weight: bold;
    font-size: 40px;
    line-height: 24px;

    letter-spacing: 2.5px;

    color: #FFFFFF;
  }
  .img-box-2{
    //background-image: url('../assets/startmainpage/user_answer.png');
    background-size: cover;
    background-repeat: no-repeat;

    width: 484px;
    height: 230px;

    margin-top: 80px;
  }
  .text-2{
    margin-left: 30px; 

    font-style: normal;
    font-weight: bold;
    font-size: 40px;
    line-height: 24px;

    letter-spacing: 2.5px;

    color: #000000;
  }
}
</style>
