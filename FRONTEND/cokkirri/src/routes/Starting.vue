<!--매칭페이지로 넘어가기 전 스타팅페이지-->
<template>
  <div class="background-setting">
    <div class="Gotopage">
      <div class="link-page1">유저 : {{ this.$store.state.name }}</div>
      <router-link class="link-page2" to="/my">마이페이지</router-link>
      <div class="link-page3" @click="logout()">로그아웃</div>
    </div>
    <div class="button-frame">
      <div class="title">Co-kirri</div>
      <div class="subtitle">: 동국대생들을 위한 취미 매칭 서비스</div>
      <router-link class="mybutton" to="/my/matching">매칭 페이지</router-link>
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
}

.Gotopage{
 /* 세 개 묶음 박스 width여기서 수정 */
  width: 500px;
  height: 30px;

  margin-left : 1000px;
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
.button-frame{
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.title{
  width: 620px;
  height: 100px;
  
  margin-top: 50px;

  font-weight: 600;
  font-size: 180px;
  line-height: 50px;
  color: #FF7518;
}
.subtitle{
  width: 400px;
  height: 50px;

  margin-bottom:5px;

  font-weight: 600;
  font-size: 30px;
  line-height: 50px;
  color: #FF7518;
}
.mybutton{
  width: 230px;
  height: 67px;

  margin-top: 10px;
  margin-right: 60px;

  background: #EC9619;
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
.mybutton:hover {
                background-color: darken($color: #B87514, $amount: 10%);
}

</style>
