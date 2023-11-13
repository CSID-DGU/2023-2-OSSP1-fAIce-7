<template>
    <div class="container">
        <!---
            matchingType 신고된 매칭의 타입
            matchingId 신고된 매칭의 id
            email 신고자
            title 신고 제목
            comment 신고 내용
        --->
        <div class="frame-main">
            <div class="title">유저 정보 수정</div>
            <div class="sub-title">
                수정 ID : {{email}}<br>
                수정 모드 : 관리자
            </div>
            <div class="describe-content">아이디</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="id">
            <div style="clear:both;"></div>
            <div class="describe-content">비밀번호</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="password">
            <div style="clear:both;"></div>
            <div class="describe-content">이름</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="name">
            <div style="clear:both;"></div>
            <div class="describe-content">성별</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="sex">
            <div style="clear:both;"></div>
            <div class="describe-content">전공</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="major">
            <div style="clear:both;"></div>
            <div class="describe-content">H.P</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="number">
            <div style="clear:both;"></div>
            <div class="describe-content">학번</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="studentNum">
            <div style="clear:both;"></div>
            <div class="describe-content">강좌</div>
            <div class="input-content">{{course}}</div>
            <div style="clear:both;"></div>
            <div class="describe-content">규제 기한</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="restructionDate">
            <div style="clear:both;"></div>
            <div class="describe-content">하트 개수</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="heart">
            <div style="clear:both;"></div>
            <div class="describe-content">관리자 권한</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="admin">
            <div style="clear:both;"></div>
            <div class="describe-content">인증 여부</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="auth">
            <div style="clear:both;"></div>
            <div class="describe-content">수업 매칭 현황</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="isClassMatching">
            <div style="clear:both;"></div>
            <div class="describe-content">공강 매칭 현황</div>
            <input type="text" placeholder="NULL" class="input-content" v-model="isPublicMatching">
            <div style="clear:both;"></div>
            
            <div class="btn-submit" @click="submitUserInfo()">제출</div>
            <div class="btn-cancel" @click="cancleReport()">취소</div>
            <div style="clear:both;"></div>
        </div>
    </div>
</template>

<script>
    import axios from '../../../api/index.js'
    export default {
        
        data(){
            return{
                email: '',

                id: '',
                password: '',
                name: '',
                sex: '',
                major: '',
                number: '',
                studentNum: '',
                course: [],
                restructionDate: '',
                heart: '',
                admin: '',
                auth: '',
                isClassMatching: '',
                isPublicMatching: '',

                title: '',
            }
        },
        computed:{
            alteredmatchingType(){
                if(this.matchingType==='class'){
                    return '수업 매칭'
                }
                else if(this.matchingType==='free'){
                    return '공강 매칭'
                }
                else{
                    return '지정된 매칭이 없음'
                }
            }
        },
        methods:{
            // 수정된 유저 정보 제출
            async submitUserInfo(){
                let restructionDate = null
                if(this.restructionDate !== ''){
                    restructionDate = this.restructionDate
                }
                await axios.put('/admin/user/'+this.email,null,{
                    params:{
                        id:this.id,
                        password:this.password,
                        name:this.name,
                        sex:this.sex,
                        major:this.major,
                        number:this.number,
                        studentNum:this.studentNum,
                        course:this.course,
                        restructionDate:restructionDate,
                        heart:this.heart,
                        admin:this.admin,
                        auth:this.auth,
                        isClassMatching:this.isClassMatching,
                        isPublicMatching:this.isPublicMatching,
                    }
                }).then(()=>{
                    alert("유저 정보 수정이 완료되었습니다.")
                    window.close()
                }).catch(function(error){
                    console.log(error)
                })
            },
            // 유저 기존 정보 불러오기
            async loadUserInfo(email){
                await axios.get('/admin/user/id',{
                    params: {
                        userId: email
                    }
                })
                .then((result) =>{
                    this.id = result.data.id
                    this.password = result.data.password
                    this.name = result.data.name
                    this.sex = result.data.sex
                    this.major = result.data.major
                    this.number = result.data.number
                    this.studentNum = result.data.studentNum
                    this.course = result.data.course
                    this.restructionDate = result.data.restructionDate
                    this.heart = result.data.heart
                    this.admin = result.data.admin
                    this.auth = result.data.auth
                    this.isClassMatching = result.data.classMatching
                    this.isPublicMatching = result.data.publicMatching
                    console.log(result.data)
                })
                .catch(function(error){
                    console.log(error)
                })
            },
            // 수정 취소 버튼
            cancleReport(){
                alert("유저 정보 수정이 취소되었습니다.")
                window.close()
            },
        },
        mounted(){
            this.email = this.$route.query.email;
            this.loadUserInfo(this.email)
        },
    }
</script>

<style lang="scss" scoped>
    .container{
        width: 100vw;
        height: 100vh;

        display: flex;
        align-items: center;
        justify-content: center;
        text-align: center;

        background-color: #ECBC76; 
        
        .frame-main{
            width: 550px;
            height: 700px;

            overflow: auto;

            background: #FFFEF9;
            box-shadow: 0px 4px 35px rgba(0, 0, 0, 0.08);
            border-radius: 40px;
            
            .title{
                width: 150;
                height: 35;

                margin-left: 25px;
                margin-top: 34px;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 24px;
                line-height: 36px;
                color: #B87514;
            }
            .sub-title{
                width: 300px;
                height: 60px;

                margin-left: 25px;
                margin-top: 0px;

                display: flex;
                align-items: center;
                justify-content: left;
                text-align: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 16px;
                line-height: 24px;
                color: #8D8D8D;
            }
            .describe-content{
                width: 150px;
                height: 45px;

                margin-left: 25px;
                margin-top: 10px;

                float: left;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 20px;
                line-height: 30px;

                color: #000000;
            }
            .input-content{
                // 테두리 고려해서 가로 세로 2px 감소 적용됨
                width: 338px;
                height: 43px;

                box-sizing: border-box;
                margin-left: 10px;
                margin-right: 25px;
                margin-top: 10px;

                float: left;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                background: #FFFFFF;
                border: 1px solid #4285F4;
                border-radius: 9px;
            }
            /*
            .describe-content{
                width: 150px;
                height: 30px;

                margin-left: 25px;
                margin-top: 10px;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 20px;
                line-height: 30px;

                color: #000000;
            }
            .input-content{
                // 테두리 고려해서 가로 세로 2px 감소 적용됨
                width: 498px;
                height: 340px;

                box-sizing: border-box;
                margin-left: 25px;
                margin-right: 25px;
                margin-top: 0px;

                text-align: left;
                vertical-align: top;

                background: #FFFFFF;
                border: 1px solid #4285F4;
                border-radius: 9px;
            }
            */
            .btn-submit{
                width: 150px;
                height: 63px;

                float: left;
                margin-left: 210px;
                margin-top: 18px;
                margin-bottom: 50px;

                cursor: pointer;

                background: #E48700;
                box-shadow: 0px 4px 19px rgba(119, 147, 65, 0.3);
                border-radius: 10px;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 500;
                font-size: 24px;
                line-height: 36px;

                color: #FFFFFF;
            }
            .btn-cancel{
                width: 150px;
                height: 63px;

                float: left;
                margin-left: 15px;
                margin-top: 18px;
                margin-bottom: 50px;

                cursor: pointer;

                background: #E48700;
                box-shadow: 0px 4px 19px rgba(119, 147, 65, 0.3);
                border-radius: 10px;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 500;
                font-size: 24px;
                line-height: 36px;

                color: #FFFFFF;
            }
        }
    }
</style>