<template>
    <!-- 이곳에 로그인 뼈대 생성. 추후 컴포넌트로 빼내서 활용할 예정.  -->
    <div class="background-setting">
        <div class="container" >
            <div class="form-frame">
                <div>
                    <p class="link-describe">아직 회원이 아니라면 ?</p>
                    <router-link 
                        to="/signup" 
                        class="link-to-signup">회원가입</router-link>
                </div>
                <h2 class="font-h1">로그인</h2>
                <div>
                    <p class="font-login-describe">아이디 또는 이메일 주소를 입력하세요.</p>
                    <input
                        name="user id" 
                        placeholder="email address"
                        class="login-id-input"
                        @keydown.enter="submitForm"
                        v-model="user.id">
                    <p class="font-password-describe">비밀번호를 입력하세요</p>
                    <input 
                        type="password"
                        name="password" 
                        placeholder="password"
                        class="login-password-input"
                        @keydown.enter="submitForm"
                        v-model="user.password">
                    <div 
                        class="submit-btn"
                        @click="submitForm">
                        로그인
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>

export default {
    data() {
        return {
            user: {
                id: '',
                password: '',
            },
        }
    },
    // 비밀번호 찾기 api 구현되면 구성할 부분
    methods: {
        alertMassage(){
            alert("구현 예정")
        },
        // 로그인 입력 값 검사 후 vuex의 action을 통해 api 요청
        submitForm() {
            if (this.user.id === '') {
                alert('아이디를 입력하세요.')
            }
            else if (this.user.password === '') {
                alert('비밀번호를 입력하세요.')
            }
            else{
                this.$store.dispatch('loginRequest',{inputId:this.user.id, inputPassword:this.user.password})
            }
        }
    },
    mounted(){
        if(this.$route.query.status === 'verified'){
            alert(this.$route.query.userId+"님의 이메일 인증이 완료되었습니다.")
            this.$router.replace('/');
        }
    }
}
</script>

<style lang="scss" scoped>
    @import "../scss/main";
    // 배경화면 설정
    .background-setting{
        height: 100vh;
        width: 100vw;
        margin:0;
        background: url("../assets/login/background.jpg") no-repeat center center fixed;
        -webkit-background-size: cover;
        -moz-background-size: cover;
        -o-background-size: cover;
        background-size: cover;
        display: grid;
        grid-template-rows: auto;
        justify-items: center;
        align-items: center;
    }
    // container 클래스 위치 조정
    .container{
        display: flex;
        align-items: center;
        justify-content: center;
    }
    // form 틀 만들기
    .form-frame {
        width: 539px;
        height: 741px;
        background: #FFFFFF;
        box-shadow: 0px 4px 35px rgba(0, 0, 0, 0.08);
        border-radius: 40px;

        .link-describe{
            margin-left: 346px;
            margin-top: 53px;
            margin-bottom: 0px;

            font-size: 16px;
            color: #8D8D8D;
        }
        .link-to-signup{
            margin-left: 436px;
            color: #B87514;
        }

        .font-h1{
            margin-top: 18px;
            margin-left: 44px;
            font-size: 55px;
        }

        .font-login-describe{
            margin-top: 52px;
            margin-left: 44px;
            font-size: 16px;
            margin-bottom: 0px;
        }
        .login-id-input{
            margin-top: 13px;
            width: 451px;
            height: 50px;
            margin-left: 44px;
            background: #FFFFFF;
            border: 1px solid #4285F4;
            border-radius: 9px;

            font-size: 16px;
            letter-spacing: 2px;
        }
        .font-password-describe{
            margin-top: 69px;
            margin-left: 44px;
            margin-bottom: 0px;
            
            font-size: 16px;
        }
        .login-password-input{
            width: 451px;
            height: 50px;
            margin-top: 13px;
            margin-bottom: 8px;
            margin-left: 44px;
            background: #FFFFFF;
            border: 1px solid #4285F4;
            border-radius: 9px;

            font-size: 16px;
            letter-spacing: 2px;
        }
        .link-finding-password{
            margin-left: 416px;
            
            font-size: 13px;
            color: #AD3113;
        }
        .submit-btn{
            width: 451px;
            height: 54px;
            margin-top: 100px;
            margin-left: 44px;
            margin-right: 44px;

            cursor: pointer;

            background: #E48700;
            border-radius: 10px;
            border-color: #E48700;
            box-shadow: 0px 4px 19px rgba(119, 147, 65, 0.3);

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            font-size: 16px;
            color: #FFFFFF;
        }
    }
</style>