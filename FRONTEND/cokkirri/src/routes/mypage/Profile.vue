<template>
    <!-- 마이페이지의 프로필 세부사항 -->
    <div class="background-setting">
        <div class="container">
            <div class="sub-frame">
                <router-link to="/my" class="my-link">&lt;</router-link>
            </div>
            <div>
                <div class="frame-head">
                    <div class="user-img"></div>
                    <router-link to="/" v-if="!this.$store.state.isLogin" class="user-name">로그인</router-link>
                    <div v-else class="user-name">{{this.$store.state.name}}</div>
                    <div v-if="stateTF.isEditState" class="user-btn-complete" @click="editComplete()">완료</div>
                    <div v-else>
                        <div class="user-btn-edit" @click="userInfoUpdateServerToClient()">새로고침</div>
                        <div class="user-btn-edit" :style="{'margin-left': '22px'}" @click="editStateOn()">수정</div>
                        <div class="user-btn-edit" :style="{'margin-left': '22px'}" @click="passwordEditStateOn()">비밀번호 변경</div>
                    </div>
                    <div style="clear:both;"></div>
                </div>
                <div class="frame-body">
                    <div>
                        <div style="clear:both;"></div>
                        <div v-if="!stateTF.isPasswordEditState">
                            <div v-if="stateTF.isEditState" class="frame-sub-body">
                                <div class="font-head" :style="{'margin-top': '5px'}">아이디</div>
                                <div class="font-body" :style="{'margin-top': '-75px'}">{{userInfo.id}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">전공</div>
                                <div class="font-input"> <input type="text" v-model="userInfo.major"> </div>
                                <div style="clear:both;"></div>

                                <div class="font-head">이름</div>
                                <div class="font-body">{{userInfo.name}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">H.P</div>
                                <div class="font-input"> <input type="text" v-model="userInfo.number"> </div>
                                <div style="clear:both;"></div>

                                <div class="font-head">성별</div>
                                <div class="font-body">{{userInfoEToK}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">학번</div>
                                <div class="font-body">{{userInfo.studentNum}}</div>
                                <div style="clear:both;"></div>

                            </div>
                            <div v-else class="frame-sub-body">
                                <div class="font-head" :style="{'margin-top': '5px'}">아이디 </div>
                                <div class="font-body" :style="{'margin-top': '-75px'}">{{userInfo.id}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">전공</div>
                                <div class="font-body">{{userInfo.major}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">이름</div>
                                <div class="font-body">{{userInfo.name}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">H.P</div>
                                <div class="font-body">{{userInfo.number}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">성별</div>
                                <div class="font-body">{{userInfoEToK}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-head">학번</div>
                                <div class="font-body">{{userInfo.studentNum}}</div>
                                <div style="clear:both;"></div>
                            </div>
                        </div>
                        <div v-else class="frame-sub-form-body">
                            <div>
                                <div class="font-head">Before</div>
                                <input type="password" class="font-input" placeholder="기존 비밀번호" v-model="passwordForAuth">
                                <div style="clear:both;"></div>
                            </div>
                            <div>
                                <div class="font-head">PWD</div>
                                <input type="password" class="font-input" placeholder="비밀번호 재설정" v-model="userInfo.password">
                                <div style="clear:both;"></div>
                            </div>
                            <div>
                                <div class="font-head">REPWD</div>
                                <input type="password" class="font-input" placeholder="비밀번호 재설정 확인" v-model="userInfo.repassword">
                                <div style="clear:both;"></div>
                            </div>
                        </div>
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
            userInfo: {
                id : this.$store.state.id,
                major: this.$store.state.major,
                name: this.$store.state.name,
                number: this.$store.state.number,
                sex: this.$store.state.sex,
                studentNum: this.$store.state.studentNum,
                course: this.$store.state.course,
                password: '',
                repassword: '',
                submitPassword: this.$store.state.password
            },
            passwordForAuth: '',
            stateTF: {
                isEditState: false,
                isPasswordEditState: false
            },
        }
    },
    computed: {
        userInfoEToK: function(){
            if(this.userInfo.sex ==='male'){
                return '남성'
            }
            else if(this.userInfo.sex === 'female'){
                return '여성'
            }
            else{
                return ''
            }
        }
    },
    methods: {
        editStateOn() {
            return this.stateTF.isEditState = true;
        },
        passwordEditStateOn() {
            this.stateTF.isPasswordEditState = true;
            this.stateTF.isEditState = true;
        },
        editComplete(){
            if(this.stateTF.isPasswordEditState){  
                if(this.userInfo.password===this.userInfo.repassword & this.userInfo.password!==''){
                    if(this.passwordForAuth===this.userInfo.submitPassword){
                        this.userInfo.submitPassword = this.userInfo.password
                        this.userInfo.password = ''
                        this.userInfo.repassword = ''
                        this.passwordForAuth = ''
                        this.userInfoUpdateClientToServer()
                        this.stateTF.isEditState = false;
                        this.stateTF.isPasswordEditState = false;
                    }else{
                        alert("입력하신 기존 비밀번호가 잘못되었습니다.")
                    }
                }
                else{
                    alert("비밀번호를 입력하지 않았거나 PWD와 REPWD가 동일하지 않습니다.");
                }
            }
            else{
                if(this.userInfo.major!='' & this.userInfo.number!=''){
                    this.stateTF.isEditState = false;
                    this.userInfoUpdateClientToServer()
                }
                else{
                    alert("빈 항목이 존재합니다.");
                }
            }
        },
        userInfoUpdateClientToServer(){
            this.$store.dispatch('userInfoEdit', {
                inputId: this.userInfo.id,
                inputMajor: this.userInfo.major, 
                inputNumber: this.userInfo.number, 
                inputCourse: this.userInfo.course, 
                inputPassword: this.userInfo.submitPassword
            })
        },
        async userInfoUpdateServerToClient(){
            await this.$store.dispatch('userInfoUpdate')
            this.userInfo.id = this.$store.state.id,
            this.userInfo.major = this.$store.state.major,
            this.userInfo.name = this.$store.state.name,
            this.userInfo.number = this.$store.state.number,
            this.userInfo.sex = this.$store.state.sex,
            this.userInfo.studentNum = this.$store.state.studentNum,
            this.userInfo.course = this.$store.state.course,
            this.userInfo.password = '',
            this.userInfo.repassword = ''
            this.userInfo.submitPassword = this.$store.state.password
            alert("새로고침 완료")
        }
    }
}

</script>

<style lang="scss" scoped>
    @import "../../scss/main";
    
    // 배경화면 설정
    .background-setting{
        height: 100vh;
        width: 100vw;
        margin:0;

        background-image: url("../../assets/mypage/background.png"); // 배경 이미지 적용
        background-size: cover; // 이미지가 배경 전체를 커버하도록 설정
        background-repeat: no-repeat; // 이미지가 반복되지 않도록 설정
        background-position: center center; // 이미지가 배경 중앙에 위치하도록 설정
        display: grid;
        grid-template-rows: auto;
        justify-items: center;
        align-items: center;
    }
    // container 클래스 위치 조정
    .container{
        width: 996px;   
        height: 680px;
        background-color: #FFFFFF; 
        display: flex;
        border: 7px solid #ECBC76;
        border-radius: 20px;
        flex-direction: column; //행 방향 정렬
        align-items: center;
        justify-content: center;
    }
    .sub-frame{
        width: 950px;
        height: 70px;
        margin-top:-35px;
        background-color: #FFFFFF;
    }
    .my-link{
        width: 51px;
        height: 46px;

        float:left;

        cursor: pointer;
        text-decoration: none;

        font-style: normal;
        font-weight: 400;
        font-size: 40px;
        line-height: 75px;
        color: #B87514;
        display: flex;
    }
    .frame-head{
        width: 900px;
        height: 125px;

        background-color: #FFFFFF;
        border-radius: 20px;
        border: 7px solid #ECBC76;
        display: flex;
        align-items: center;
        .user-img{
            width: 92px;
            height: 95px;
            margin-left: 30px;
            background-image: url("../../assets/mypage/user.png");
            background-size: cover;
            background-repeat: no-repeat;
            float: left;
        }
        .user-name{
            width: 200px;
            height: 57px;

            font-size: 35px; 
            display: flex; 
            justify-content: center;
            align-items: center;
            float: left;
        }
        .user-btn-edit{
            width: 163px;
            height: 55px;
            float: left;

            cursor: pointer;

            border-radius: 20px;
            background-color: #ECBC76;

            display: flex;
            border-radius: 20px;
            justify-content: center;
            align-items: center;

            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }
        .user-btn-edit:hover {
                background-color: darken($color: #B87514, $amount: 10%);
        }
        .user-btn-complete{
            width: 163px;
            height: 55px;
            
            margin-left: 370px;
            float: left;

            cursor: pointer;

            border-radius: 20px;
            background-color: #ECBC76;

            display: flex;
            justify-content: center;
            align-items: center;

            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }
        .user-btn-complete:hover {
                background-color: darken($color: #B87514, $amount: 10%);
        }
    }
    .frame-body{
        width: 900px;
        height: 397px;
        margin-top: 40px;
        background-color: #FFFFFF;
        border: 7px solid #ECBC76;
        border-radius: 20px;
        .frame-sub-body{
            width: 850px;
            height: 337px;
            margin-top: 20px;
            margin-left: 20px;
            background-image: url("../../assets/mypage/profile/sub-frame.png");
            float: left;
            overflow-y: scroll;
        }
        .frame-sub-form-body{
            width: 850px;
            height: 337px;
            margin-top: 20px;
            margin-left: 20px;

            display: flex;
            flex-direction: column; 
            justify-content: center;
           
            background-image: url("../../assets/mypage/profile/sub-frame.png");
            background-size: cover;
          
            float: left;
            overflow-y: scroll;
        }
        .line-for-division{
            width: 891px;
            height: 1px;
            margin-top: 39px;
            margin-left: 53px;
            margin-bottom: 0px;

            border: 1px solid #B87514
        }
        .font-head{
            width: 144px;
            height: 75px;
            margin-top: 10px;

            float: left;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
            
            font-style: normal;
            font-weight: 500;
            font-size: 30px;
            color: #B87514;
        }
        .font-body{
            width: 754px;
            height: 75px;
            margin-top: -73px;
            margin-left: 12px;

            float: left;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center; 
            
            font-style: normal;
            font-weight: 500;
            font-size: 25px;
            color: #000000;
        }
        .font-input{
            width: 300px;
            height: 49px;
            margin-top: 23px;
            margin-left: 100px;
            margin-bottom: 13px;

            float: left;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;

            font-style: normal;
            font-weight: 500;
            font-size: 25px;
            color: #000000;
        }
        .font-input input{
            text-align:center;
        }
    }
        //스크롤바 스타일
        .frame-sub-body::-webkit-scrollbar {  //스크롤바의 너비
            width: 8px;
        }
        .frame-sub-body::-webkit-scrollbar-track {  //트랙(바탕 부분)의 색
            width: 30px;
            background: #FFDBAA;
            border-radius: 10px;
        }
        .frame-sub-body::-webkit-scrollbar-thumb {  //스크롤바의 이동 부분
            height: 10%;
            background-color: #B87514;
            border-radius: 10px;
        }
        .frame-sub-form-body::-webkit-scrollbar {  //스크롤바의 너비
            width: 8px;
        }
        .frame-sub-form-body::-webkit-scrollbar-track {  //트랙(바탕 부분)의 색
            width: 30px;
            background: #ECBC76;
            border-radius: 20px;
        }
        .frame-sub-form-body::-webkit-scrollbar-thumb {  //스크롤바의 이동 부분
            height: 10%;
            background-color: #B87514;
            border-radius: 10px;
        }
</style>