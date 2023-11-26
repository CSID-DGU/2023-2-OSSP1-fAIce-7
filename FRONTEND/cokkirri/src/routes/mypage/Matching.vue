<template>
    <!-- 매칭 기록  -->
    <div class="background-setting">
        <div class="container" >
            <div>
                <div class="frame-body">
                    <div>
                        <router-link to="/my" class="my-link">&lt;</router-link>
                        <div style="clear:both;"></div>

                        <div class="matching-img-box">
                            <div class="matching-img"></div>
                        </div>
                        <div class="matching-txt">매칭 결과</div>

                        <div class="matching-refresh" @click="callMatchingRecord()">새로고침</div>
                        <div class="matching-btn" @click="goToApply()">매칭추가</div>
                        <div style="clear:both;"></div>

                        <div class="line-for-division"></div>

                        <div class="frame-sub-body">
                            <div v-if="this.$store.state.classMatchingWait!==null" class="matching-frame">
                                <div class="matching-describe">
                                    # 신청 날짜 : {{this.$store.state.classMatchingWait.matchingTime}}
                                    <div class="btn-report" @click="cancelClassWait(this.$store.state.classMatchingWait.id)">매칭 신청 취소</div>
                                    <div>&nbsp;# 매칭 완료 후 하트 10개 차감 예정~!</div>
                                </div>
                                <div class="matching-box">
                                    <div class="record-img"></div>
                                    <div class="record-type">수업 매칭</div>
                                    <div class="record-head-count"></div>
                                    <div class="record-timetable"></div>
                                    <div class="record-ing-btn">#매칭 대기</div>
                                </div>
                            </div>
                            <div v-if="this.$store.state.publicMatchingWait!==null" class="matching-frame">
                                <div class="matching-describe">
                                    # 신청 날짜 : {{this.$store.state.publicMatchingWait.matchingTime}}
                                    <div class="btn-report" @click="cancelPublicWait(this.$store.state.publicMatchingWait.id)">매칭 신청 취소</div>
                                    <div>&nbsp;# 매칭 완료 후 하트 10개 차감 예정~!</div>
                                </div>
                                <div class="matching-box">
                                    <div class="record-img"></div>
                                    <div class="record-type">공강 매칭</div>
                                    <div class="record-head-count"></div>
                                    <div class="record-timetable"></div>
                                    <div class="record-ing-btn">#매칭 대기</div>
                                </div>
                            </div>
                            <div v-for="(record, index) in matchingListClass" :key="index" class="matching-frame">
                                <div class="matching-describe">
                                    # 신청 날짜 : {{record.matchingTime}} # 매칭 식별 번호 : {{record.matchingId}}
                                    <div v-if="record.matchingRes==='매칭중'" class="btn-report" @click="openReportWindow('class',record.matchingId)">노쇼 신고</div>
                                    <div v-if="!record.agreeList.includes(this.$store.state.id) && record.matchingRes==='매칭중'" class="btn-report" @click="closeClassMatching(record.matchingId)">매칭 완료</div>
                                    <div v-else-if="record.matchingRes==='매칭중'">&nbsp;# 매칭 종료 접수 됨</div>
                                </div>
                                <div  class="matching-box">
                                    <div class="record-img"></div>
                                    <div class="record-type">수업 매칭</div>
                                    <div class="record-head-count">인원 {{record.headCount}}명</div>
                                    <div class="record-timetable">
                                        <div v-for="(timeId, index) in record.courseNumber" :key="index" class="time-id">
                                            {{timeId}}
                                        </div>
                                    </div>
                                    <div v-if="record.matchingRes==='매칭중'" class="record-btn-move" @click="moveToChatroom(record.matchingId,'class')">#채팅방</div>
                                    <div v-else class="record-ing-btn">#매칭 완료</div>
                                </div>
                            </div>
                            <div v-for="(record, index) in matchingListFree" :key="index" class="matching-frame">
                                <div class="matching-describe">
                                    # 신청 날짜 : {{record.matchingTime}} # 매칭 식별 번호 : {{record.matchingId}}
                                    <div v-if="record.matchingRes==='매칭중'" class="btn-report" @click="openReportWindow('free',record.matchingId)">노쇼 신고</div>
                                    <div v-if="!record.agreeList.includes(this.$store.state.id) && record.matchingRes==='매칭중'" class="btn-report" @click="closePublicMatching(record.matchingId)">매칭 완료</div>
                                    <div v-else-if="record.matchingRes==='매칭중'">&nbsp;# 매칭 종료 접수 됨</div>
                                </div>
                                <div class="matching-box">
                                    <div class="record-img"></div>
                                    <div class="record-type">공강 매칭</div>
                                    <div class="record-head-count">인원 {{record.headCount}}명</div>
                                    <div class="record-detail">{{record.availableDay}}</div>
                                    <div class="record-detail">{{record.promiseTime[0].slice(0,5)}}~{{record.promiseTime[1].slice(0,5)}}</div>
                                    <div v-if="record.matchingRes==='매칭중'" class="record-btn-move" @click="moveToChatroom(record.matchingId,'free')">#채팅방</div>
                                    <div v-else class="record-ing-btn">#매칭 완료</div>
                                </div>
                            </div>
                            <div class="matching-frame">
                                <div class="matching-describe"># 매칭 기록이 더이상 없습니다.</div>
                                <div class="matching-box-ex">
                                    매칭을 원하실 경우, 매칭 추가 버튼을 누르십시오.
                                </div>
                            </div>
                            <div :style="{'margin-top': '30px'}"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    //import Report from './component/Report.vue'
    import axios from '../../api/index.js'
    export default {
        data(){
            return {
                matchingListClass: [],
                matchingListFree: [],
            }
        },
        methods: {
            goToApply(){
                this.checkSubmitState().then((result)=>{
                    if(result){
                        alert("이미 매칭 대기 중인 항목이 존재합니다.")
                    }
                    else{
                        this.$router.replace('/matchingStart')
                    }
                })
            },
            async checkSubmitState(){
                return await this.$store.dispatch('checkMatchingSubmitState')
            },
            async callMatchingRecord() {
                if(this.$store.state.isLogin){
                    await this.$store.dispatch('callMatchingRecord')
                    this.matchingListClass = [...this.$store.state.classMatchingRecord].reverse()
                    this.matchingListFree = [...this.$store.state.publicMatchingRecord].reverse()
                }
            },
            openReportWindow(matchingType,matchingId){
                const route = this.$router.resolve({
                    path: '/my/matching/report', 
                    query: { 
                        email: this.$store.state.id, 
                        matchingType: matchingType,
                        matchingId: matchingId
                    } });
                
                const width = 600;
                const height = 750;
                const left = (window.screen.width - width) / 2;
                const top = (window.screen.height - height) / 2;

                window.open(window.location.origin + route.href, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
            },
            async closeClassMatching(matchingId){
                try{
                    await axios.put('matching/agree/class',null,{
                        params:{
                            matchingId: matchingId,
                            userId: this.$store.state.id
                        }
                    }).then((result)=>{
                        console.log("매칭 완료")
                        alert(result.data)
                        this.callMatchingRecord()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async closePublicMatching(matchingId){
                try{
                    await axios.put('matching/agree/free',null,{
                        params:{
                            matchingId: matchingId,
                            userId: this.$store.state.id
                        }
                    }).then((result)=>{
                        console.log("매칭 완료")
                        alert(result.data)
                        this.callMatchingRecord()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async cancelClassWait(waitId){
                await axios.get('/matching/delete/class/matchingWait',{
                    params:{
                        waitId: waitId
                    }
                }).then(()=>{
                    console.log("매칭대기 취소 완료")

                }).catch(function(error){
                    console.log(error)
                })
                this.callMatchingRecord()
            },
            async cancelPublicWait(waitId){
                await axios.get('/matching/delete/free/matchingWait',{
                    params:{
                        waitId: waitId
                    }
                }).then(()=>{
                    console.log("매칭대기 취소 완료")
                }).catch(function(error){
                    console.log(error)
                })
                this.callMatchingRecord()
            },
            moveToChatroom(matchingId, matchingType){
                this.$store.state.matchingIdForChatroom = matchingId
                this.$store.state.matchingTypeForChatroom = matchingType
                this.$router.replace('/ChatRoom')
            }
        },
        mounted(){
            if(this.$store.state.isLogin){
                this.$store.dispatch('callMatchingRecord')
                this.matchingListClass =  [...this.$store.state.classMatchingRecord].reverse();
                this.matchingListFree = [...this.$store.state.publicMatchingRecord].reverse();
            }
        },
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
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .frame-body{
        width: 996px;
        height: 569px;
        background-color: #FFFFFF;
       
        border: 5px solid #ECBC76;
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
        .matching-img-box{
            width: 68px;
            height: 55px;
            
            margin-top: 0px;
            margin-left: 68px;
            float: left;

            display: flex;
            justify-content: left;
            align-items: center;
        }
        .matching-img{
            width: 40px;
            height: 40px;
            
            float: left;

            background-image: url("../../assets/mypage/matching.png");
            background-size: cover;
            background-repeat: no-repeat;
        }
        .matching-txt{
            width: 200px;
            height: 55px;

            margin-top: 0px;
            margin-left: 0px;
            float: left;

            display: flex;
            justify-content: left;
            align-items: center;

            
            font-style: normal;
            font-weight: 400;
            font-size: 30px;
            line-height: 45px;
        }
        .matching-refresh{
            width: 163px;
            height: 55px;
            background-color: #B87514;

            margin-top: 0px;
            margin-left: 261px;
            border-radius: 20px;
            float: left;

            cursor: pointer;

            color: #FFFFFF;
            display: flex;
            justify-content: center;
            align-items: center;

            
            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }
        .matching-btn{
            width: 163px;
            height: 55px;
            background-color: #B87514;

            margin-top: 0px;
            margin-left: 22px;
            border-radius: 20px;
            float: left;

            cursor: pointer;

            color: #FFFFFF;
            display: flex;
            justify-content: center;
            align-items: center;

            
            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }

        .line-for-division{
            width: 891px;
            height: 1px;
            margin-top: 30px;
            margin-left: 53px;
            margin-bottom: 0px;

            border: 1px solid #B87514
        }
    }
    .frame-sub-body{
        width: 950px;
        height: 400px;
        margin-top: 5px;
        margin-left: 0px;

        background: #FFFFFF;
        border-radius: 20px;
        overflow-y: auto;
        .matching-frame{
            width: 891px;
            height: 160px;

            margin-top: 30px;
            margin-left: 53px;
        }
        .matching-describe{
            width: 891px;
            height: 34px;

            display: flex;
            justify-content: left;
            align-items: center;

            
            font-style: normal;
            font-weight: 500;
            font-size: 25px;
            line-height: 38px;
            color: #B87514;
        }
        .btn-report{
            width: 135px;
            height: 34px;

            margin-left: 15px;

            cursor: pointer;

            display: flex;
            justify-content: center;
            align-items: center;

            background: #ECBC76;
            border-radius: 10px;
            color: #FFFFFF;
        }
        .matching-box{
            width: 891px;
            height: 110px;

            margin-top: 16px;

            border: 5px solid #ECBC76;
            border-radius: 20px;
            .record-img{
                width: 50px;
                height: 51px;
                
                margin-top: 26px;
                margin-left: 27px;
                float: left;

                background-image: url("../../assets/mypage/matching/people.png");
                background-size: cover;
                background-repeat: no-repeat;
            }
            .record-type{
                width: 100px;
                height: 74px;

                margin-top: 18px;
                margin-left: 49px;
                float: left;

                
                font-style: normal;
                font-weight: 500;
                font-size: 25px;
                line-height: 38px;

                display: flex;
                align-items: center;
                justify-content: left;
                color: #B87514;
            }
            .record-head-count{
                width: 100px;
                height: 74px;

                margin-top: 18px;
                float: left;

                
                font-style: normal;
                font-weight: 300;
                font-size: 25px;
                line-height: 45px;
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                color: #B87514;
            }
            .record-detail{
                width: 170px;
                height: 74px;

                margin-top: 18px;
                float: left;

                
                font-style: normal;
                font-weight: 300;
                font-size: 25px;
                line-height: 45px;
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                color: #B87514;
            }

            .record-timetable{
                width: 340px;
                height: 74px;

                margin-top: 18px;
                float: left;
                overflow-x: auto;

                
                font-style: normal;
                font-weight: 300;
                font-size: 25px;
                line-height: 45px;
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                color: #B87514;
                .time-id{
                    width: 120px;
                    height: 50px;

                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;
                    
                    
                    font-style: normal;
                    font-weight: 300;
                    font-size: 20px;
                    line-height: 45px;
                    color: #FFFFFF;

                    background: #B87514;
                    border-radius: 10px;
                }
            }
            .record-ing-btn{
                width: 150px;
                height: 65px;

                margin-top: 18px;
                margin-left: 35px;
                float: left;

                border: 5px solid #ECBC76;
                border-radius: 20px;

                align-items: center;
                text-align: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 300;
                font-size: 30px;
                line-height: 45px;
                display: flex;

                color: #B87514;
            }
            .record-btn-move{
                width: 150px;
                height: 65px;

                margin-top: 18px;
                margin-left: 35px;
                float: left;

                cursor: pointer;

                background: #E48700;
                //#ECBC76;
                border-radius: 20px;

                align-items: center;
                text-align: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 300;
                font-size: 30px;
                line-height: 45px;
                display: flex;

                color: #FFFFFF;
            }
        } 
        .matching-box-ex{
            width: 891px;
            height: 110px;

            margin-top: 16px;

            border: 5px solid #ECBC76;
            border-radius: 20px;

            display: flex;
            justify-content: center;
            align-items: center;

            
            font-style: normal;
            font-weight: 500;
            font-size: 25px;
            line-height: 38px;
            color: #B87514;
        }  
    }
</style>