<template>
    <!-- 회원가입 -->
    <div class="background-setting">
        <div class="container" >
            <div class="form-frame">
                <div>
                    <p class="describe-link-to-login">
                        계정이 있으신가요 ?
                    </p>
                    <router-link to="/" class="link-to-login">로그인</router-link>
                </div>
                <h2 class="font-h1">회원가입</h2>
                <p class="describe-email-input">동국대 웹메일 주소를 입력하세요</p>
                <input
                    name="emailAddress" 
                    placeholder="email address"
                    class="input-style"
                    @keydown.enter="returnEmailValidationResults();isEmailKnown()"
                    @change="returnEmailValidationResults(); isEmailKnown()"
                    v-model="member.userEmailAddress">
                <p 
                    v-if="formControl.isGoodEmail & !formControl.isAlreadyKnown"
                    class="describe-email-state-ok">
                    동국대 웹메일 주소가 확인 되었습니다.</p>
                <p 
                    v-if="formControl.isErrorEmail"
                    class="describe-email-state-warning">
                    동국대 웹메일만 가능합니다. 잘못된 입력입니다.</p>
                <p 
                    v-if="formControl.isAlreadyKnown"
                    :style="{'margin-left':'44px','color':'red'}">
                    이미 가입한 이메일입니다.</p>
                <p class="describe-password-input">비밀번호</p>
                <input 
                    type="password"
                    name="password" 
                    placeholder="password" 
                    class="input-style"
                    @keydown.enter="returnPasswordAgainValidationResult"
                    @change="returnPasswordAgainValidationResult"
                    v-model="member.userPassword" >
                <p class="describe-sub-input">재확인</p>
                <input
                    type="password"
                    placeholder="password again"
                    class="input-style"
                    @keydown.enter="returnPasswordAgainValidationResult"
                    @change="returnPasswordAgainValidationResult(), turnOnPasswordAgainValidation()"
                    v-model="member.userPasswordAgain">
                <div v-if="formControl.onPasswordAgainValidation">
                    <p 
                    v-if="formControl.isGoodPasswordAgain"
                    :style="{'margin-left':'44px','color':'royalblue'}">
                    확인되었습니다.</p>
                    <p 
                    v-if="formControl.isErrorPasswordAgain"
                    :style="{'margin-left':'44px','color':'red'}">
                    비밀번호가 일치하지 않습니다.</p>
                </div>
                <p class="describe-sub-input" :style="[{'margin-top':'35px'}]">이름</p>
                <input
                    placeholder="name"
                    class="input-style"
                    v-model="member.userName">
                <p class="describe-sub-input">성별</p>
                <input
                    type="radio"
                    id="male"
                    :style="{'margin-top': '13px','margin-left': '44px'}"
                    value="male" v-model="member.userSex">
                    <label for="male">남자</label>
                <input
                    type="radio"
                    id="female"
                    :style="{'margin-left': '10px'}" 
                    value="female" v-model="member.userSex">
                    <label for="female">여자</label>
                <p class="describe-sub-input">학과</p>
                <input
                    placeholder="major"
                    class="input-style"
                    v-model="member.userMajor">
                <p class="describe-sub-input">휴대폰 번호</p>
                <input
                    placeholder="phone number"
                    class="input-style"
                    v-model="member.userPhoneNumber">
                <p class="describe-sub-input">학번</p>
                <input
                    placeholder="student ID"
                    class="input-style"
                    v-model="member.userStudentId">
                <div 
                    @click= "submitForm"
                    class="btn-style">
                    확인
                </div>
            </div>
        </div>
    </div>
</template>

<script>
// axios 객체 불러오기. 해당 객체에 내포된 baseURL을 사용하기 위해서다.
import axios from '../api/index.js';

export default {
    // back-end의 API와 연결 필요함
    data() {
        return {
            // 회원가입 유저의 입력 폼
            member: {
                // id
                userEmailAddress: '',
                // password
                userPassword: '',
                userPasswordAgain: '',
                // name
                userName: '',
                 // sex(성별)
                userSex: '',
                 // major 변수 이름 수정 필요
                userMajor: '',
                // number 핸드폰 번호
                userPhoneNumber: '',
                // studentNum
                userStudentId: '',
                // course 추후에 user Course 추가 가능성 염두
                userCourse: []
            },
            // 유효성 상태 표시
            formControl:    {
                // email 유효성
                isGoodEmail: false,
                isErrorEmail: false,
                isAlreadyKnown: false,
                // passwordAgain
                isGoodPasswordAgain: false,
                isErrorPasswordAgain: false,
                onPasswordAgainValidation: false
            },
    }},
    methods: {
        // 이메일 유효성 검사 추가(기존 데이터베이스에서 중복 가입한 이메일이 있는지 확인하는 용도)
        async isEmailKnown(){
            try{
                await axios.get('/emailCheck', {
                    params: {
                        id: this.member.userEmailAddress
                    }
                })
                .then((result)=>{
                    this.formControl.isAlreadyKnown = result.data
                })
                .catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
        },
        // 이메일 유효성 판단
        returnEmailValidationResults(){
            if(this.member.userEmailAddress.endsWith('@dongguk.edu') | this.member.userEmailAddress.endsWith('@dgu.ac.kr') | this.member.userEmailAddress.endsWith('@gmail.com')){
                this.formControl.isGoodEmail = true
                this.formControl.isErrorEmail = false
            }
            else{
                this.formControl.isGoodEmail = false
                this.formControl.isErrorEmail = true
            }
        },
        // password again 유효성 판단
        returnPasswordAgainValidationResult(){
            if(this.member.userPassword === this.member.userPasswordAgain){
                this.formControl.isGoodPasswordAgain = true
                this.formControl.isErrorPasswordAgain = false
            }
            else{
                this.formControl.isGoodPasswordAgain = false
                this.formControl.isErrorPasswordAgain = true
            }
        },
        turnOnPasswordAgainValidation(){
            this.formControl.onPasswordAgainValidation = true
        },
        // 회원 가입 api로 post 요청
        async signUp() {
            try{
                await axios.post('/signup', null, {
                    params: {
                        id: this.member.userEmailAddress,
                        password: this.member.userPassword,
                        name: this.member.userName,
                        sex: this.member.userSex,
                        major: this.member.userMajor,
                        number:this.member.userPhoneNumber,
                        studentNum: this.member.userStudentId,
                        
                    }
                })
                .then((result) => {
                    console.log(result.status);
                    if(result.status === 200){
                        alert(this.member.userEmailAddress+"(으)로 인증 메일을 보냈습니다.")
                    }
                })
                .catch(function(error){
                    console.log(error);
                })
            } catch(error){
                console.log(error)
            }
        },
        // 계정, 비밀번호에 대한 유효성 검사 고려. 이외에는 빈칸만 제한 적용.
        submitForm(){
            if(this.formControl.isGoodEmail & !(this.member.userPassword==='') & this.formControl.isGoodPasswordAgain & !(this.member.userName=='') & !(this.member.userSex=='') & !(this.member.userMajor=='') & !(this.member.userPhoneNumber=='') & !(this.member.userStudentId=='')){
                this.$router.replace('/');
                this.signUp()
            }
            else{
                alert("잘못된 입력입니다.")
            }
        },
    },
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
        background: #FFFEF9;
        box-shadow: 0px 4px 35px rgba(0, 0, 0, 0.08);
        border-radius: 40px;
        overflow-y: scroll;

        .describe-link-to-login{
            margin-left: 350px;
            margin-top: 53px;
            margin-bottom: 0px;

            font-size: 16px;
            color: #8D8D8D;
        }
        .link-to-login{
            margin-left: 436px;

            color: #B87514;
        }
        .font-h1{
            padding-top: 10px;
            margin-left: 44px;
        }
        .describe-email-input{
            margin-top: 52px;
            margin-left: 44px;
            margin-bottom: 0px;
        }
        .describe-email-state-ok{
            margin-left: 44px;
            color: royalblue;
        }
        .describe-email-state-warning{
            margin-left: 44px;
            color: red;
        }
        .describe-password-input{
            margin-top: 35px;
            margin-left: 44px;
            margin-bottom: 0px;
        }
        .describe-sub-input{
            margin-top: 13px;
            margin-left: 44px;
            margin-bottom: 0px;
        }
        .input-style{
            width: 451px;
            height: 50px;
            margin-top: 13px;
            margin-left: 44px;

            background: #FFFFFF;
            border: 1px solid #4285F4;
            border-radius: 9px;
        }
        .btn-style{
            width: 451px;
            height: 54px;
            margin-top: 49px;
            margin-left: 44px;
            margin-right: 44px;
            margin-bottom: 78px;

            cursor: pointer;

            background: #E48700;
            border-radius: 10px;
            border-color: #E48700;
            box-shadow: 0px 4px 19px rgba(119, 147, 65, 0.3);

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            color: #FFFFFF;
        }
    }
</style>