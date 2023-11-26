<template>
    <!--  마이페이지의 시간표 및 관리  -->
    <div class="background-setting">
        <div class="container" >
            <div>
                <div class="frame-body">
                    <div>
                        <router-link to="/my" class="my-link">&lt;</router-link>
                        <div style="clear:both;"></div>

                        <div class="timetable-img-box">
                            <div class="timetable-img"></div>
                        </div>
                        

                        <div class="timetable-txt">내 시간표</div>

                        <div class="timetable-btn-edit" @click="userInfoUpdateClientToServer()">저장</div>
                        <div style="clear:both;"></div>

                        <div class="line-for-division"></div>

                        <div class="frame-sub-body">
                            <div class="search-box">
                                <div class="search-box-sub">
                                    <input type="text" placeholder="학수번호 조회" class="input-search" @change="showtimetable()" @click="inputSelected()" v-model="inputData.id">
                                    <div class="box-for-line"><div class="line-for-division-sub"></div></div>
                                    <div class="user-recent-box">
                                        <div 
                                            v-for="(course, index) in reversedCourseList" :key="index" 
                                            class="user-recent-course" @click="recordSelected(course)">{{course}}</div>
                                    </div>
                                </div>
                                <div v-if="stateTF.isRecordSelected" class="search-resister-btn-delete" @click="deleteCoursFromList()">삭제</div>
                                <div v-else class="search-resister-btn-add" @click="addCoursToList()">추가</div>
                            </div>
                            <div v-if="stateTF.isknown" class="detail-box">
                                <div class="font-row-name">넘버</div>
                                <div class="font-row-content">{{lecture.id}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-row-name">과목</div>
                                <div class="font-row-content">{{lecture.subjectName}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-row-name">성함</div>
                                <div class="font-row-content">{{lecture.teacherName}}</div>
                                <div style="clear:both;"></div>

                                <div class="font-row-name">날짜</div>
                                <div class="font-row-content">{{lecture.lectureDate}}</div>
                                <div style="clear:both;"></div>
                            </div>
                            <div v-else class="detail-box-alert">
                                왼편 입력창을 통해 학수번호를 조회할 수 있습니다.
                            </div>
                            <div style="clear:both;"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '../../api/index.js'
export default {
    data() {
        return {
            inputData: {
                id: '',
                courseList: [...this.$store.state.course],
                deleteId: ''
            },
            lecture: {
                id: '',
                lectureDate: '',
                subjectName: '',
                teacherName: ''
            },
            stateTF: {
                iseditState: false,
                isknown: false,
                isRecordSelected: false
            },
            temp: {
                start: false,
                end: false
            }
        }
    },
    methods: {
        recordSelected(course){
            this.inputData.deleteId = course
            this.stateTF.isRecordSelected = true
        },
        inputSelected(){
            return this.stateTF.isRecordSelected = false
        },
        async showtimetable(){
            try{
                this.stateTF.isknown = false
                await axios.get('/timeTable', {
                params: {
                    id : this.inputData.id
                }
                }).then((result)=>{
                    this.lecture.id = result.data.id
                    this.lecture.lectureDate = result.data.lectureDate
                    this.lecture.subjectName = result.data.subjectName
                    this.lecture.teacherName = result.data.teacherName
                    this.stateTF.isknown = true
                }).catch(function(){
                    console.log("해당 학수번호는 존재하지 않습니다.")
                })
            }catch(error){
                console.log("해당 학수번호는 존재하지 않습니다.")
            }
        },
        async isthereid(){
            try{
                this.temp.start = false
                await axios.get('/timeTable', {
                params: {
                    id : this.inputData.id
                }
                }).then(()=>{
                    this.temp.start = true
                }).catch(function(){
                    console.log("해당 학수번호는 존재하지 않습니다.")
                })
            }catch(error){
                console.log("해당 학수번호는 존재하지 않습니다.")
            }
            if(this.temp.start){
                this.temp.end = true
            }
            else{
                this.temp.end = false
            }
        },
        async addCoursToList() {
            if(this.inputData.id !== ''){
                if(this.inputData.courseList.indexOf(this.inputData.id)===-1){
                    await this.isthereid()
                    if(this.temp.end){
                        this.inputData.courseList.push(this.inputData.id)
                        this.inputData.id = ''
                    }
                    else{
                        alert("해당 학수번호는 존재하지 않습니다.")
                    }
                }
                else{
                    alert("이미 추가한 학수번호입니다.")
                }
            }
            else{
                alert("입력 학수번호가 없습니다.")
            }
        },
        deleteCoursFromList() {
            const index = this.inputData.courseList.indexOf(this.inputData.deleteId);
            if (index !== -1) {
                this.inputData.courseList.splice(index, 1);
            }
            this.stateTF.isRecordSelected = false
        },
        userInfoUpdateClientToServer(){
            this.$store.dispatch('userInfoEdit', {
                inputId: this.$store.state.id,
                inputMajor: this.$store.state.major, 
                inputNumber: this.$store.state.number, 
                inputCourse: this.inputData.courseList,
                inputPassword: this.$store.state.password
            })
            this.$router.replace('/my');
        },
    },
    computed:{
        reversedCourseList() {
            return [...this.inputData.courseList].reverse();
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
        .timetable-img-box{
            width: 69px;
            height: 55px;
            
            margin-top: 0px;
            margin-left: 68px;
            float: left;

            display: flex;
            justify-content: left;
            align-items: center;
        }
        .timetable-img{
            width: 40px;
            height: 40px;

            float: left;

            background-image: url("../../assets/mypage/timetable.png");
            background-size: cover;
            background-repeat: no-repeat;
        }
        .timetable-txt{
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
        .timetable-btn-edit{
            width: 163px;
            height: 55px;
            background-color: #B87514;

            cursor: pointer;

            margin-top: 0px;
            margin-left: 444px;
            border-radius: 20px;
            float: left;

            color: #FFFFFF;
            display: flex;
            justify-content: center;
            align-items: center;

            
            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }
        .timetable-btn-complete{
            width: 163px;
            height: 55px;
            background-color: #FFFEF9;

            cursor: pointer;

            margin-top: 0px;
            margin-left: 444px;
            border-radius: 20px;
            float: left;

            color: #000000;
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
        .frame-sub-body{
            width: 996px;
            height: 432px;
            margin-top: 5px;
            margin-left: 0px;

            background: #FFFEF9;
            border-radius: 20px;
            overflow-y: scroll;

            .search-box{
                width: 273px;
                height: 310px;

                margin-top: 63px;
                margin-left: 57px;
                float: left;

                .search-box-sub{
                    width: 273px;
                    height: 242px;

                    border: 5px solid #ECBC76;
                    border-radius: 20px;

                    .input-search{
                        width: 263px;
                        height: 73px;

                        border: none; 
                        background: transparent;
                        outline: none;

                        
                        font-style: normal;
                        font-weight: 500;
                        font-size: 25px;
                        line-height: 38px;
                        display: flex;
                        align-items: center;
                        text-align: center;
                        color: #000000;
                    }
                    .box-for-line{
                        width: 263px;
                        height: 7px;

                        display: flex;
                        align-items: center;
                        text-align: center;
                        justify-content: center;

                        .line-for-division-sub{
                            width: 223px;
                            height: 1px;

                            border: 1px solid #B87514;
                        }
                    }
                    .user-recent-box{
                        width: 263px;
                        height: 153px;

                        overflow-y: scroll;
                    }
                    .user-recent-course{
                        width: 263px;
                        height: 73px;

                        
                        font-style: normal;
                        font-weight: 500;
                        font-size: 25px;
                        line-height: 38px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        text-align: center;
                        color: #B87514;
                    }
                }
                .search-resister-btn-add{
                    width: 163px;
                    height: 55px;
                    
                    margin-top: 13px;
                    margin-left: 55px;

                    cursor: pointer;

                    background: #B87514;
                    border-radius: 20px;

                    display: flex;
                    align-items: center;
                    text-align: center;
                    justify-content: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 23px;
                    line-height: 28px;
                    color: #FFFFFF;
                }
                .search-resister-btn-delete{
                    width: 163px;
                    height: 55px;
                    
                    margin-top: 13px;
                    margin-left: 55px;

                    cursor: pointer;

                    background: gray;
                    border-radius: 20px;

                    display: flex;
                    align-items: center;
                    text-align: center;
                    justify-content: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 23px;
                    line-height: 28px;
                    color: #FFFFFF;
                }
            }
            .detail-box{
                width: 583px;
                height: 311px;

                margin-top: 63px;
                margin-left: 18px;
                float: left;
                overflow-y: scroll;

                border: 5px solid #ECBC76;
                border-radius: 20px;
                .font-row-name{
                    width: 95px;
                    height: 75px;

                    float: left;

                    display: flex;
                    justify-content: center;
                    align-items: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 500;
                    font-size: 30px;
                    line-height: 45px;
                    color: #B87514;
                }
                .font-row-content{
                    width: 478px;
                    height: 75px;

                    float: left;

                    display: flex;
                    justify-content: center;
                    align-items: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 500;
                    font-size: 25px;
                    line-height: 38px;
                    color: #000000;
                }
            }
            .detail-box-alert{
                width: 583px;
                height: 311px;

                margin-top: 63px;
                margin-left: 18px;
                float: left;

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
    }

</style>