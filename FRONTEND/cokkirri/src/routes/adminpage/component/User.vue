<template>
    <div class="frame-main">
        <div class="title-h1">유저 기록 관리</div>
        <div class="line-for-division"></div>
        <div class="frame-sub-head">
            <div class="btn-loadUserList" @click="loadUserList()">전체 조회</div>

            <div class="btn-searchUserId" @click="searchUserId()">아이디 검색</div>
            <input type="text" placeholder="Search Id" class="input-searchUserId" v-model="searchId" @change="searchUserId()">

            <div class="btn-searchUserName" @click="searchUserName()">이름 검색</div>
            <input type="text" placeholder="Search Name" class="input-searchUserName" v-model="searchName" @change="searchUserName()">
            <div style="clear:both;"></div>
        </div>
        <div class="frame-sub-body">
            <div class="line-for-division"></div>
            <div class="attribute-name">이름</div>
            <div class="attribute-email">이메일</div>
            <div style="clear:both;"></div>
            <div class="line-for-division"></div>

            <div v-for="(user,index) in userList" :key="index">
                <div class="content-row-name">{{user.name}}</div>
                <div class="content-row-email">{{user.id}}</div>
                <div class="content-row-btn-edit" @click="openReportWindow(user.id)">수정</div>
                <div class="content-row-btn-delete" @click="deleteUserById(user.id)">제거</div>
                <div style="clear:both;"></div>
            </div>
        </div>
        <div class="line-for-division"></div>
    </div>
</template>

<script>
    import axios from '../../../api/index.js'
    export default {
        data(){
            return{
                userList: [],

                searchId: '',
                searchName: '',
            }
        },
        methods:{
            // 유저 정소 수정 새창 열기
            openReportWindow(email){
                const route = this.$router.resolve({
                    path: '/admin/user', 
                    query: { 
                        email: email, 
                    } });
                
                const width = 600;
                const height = 750;
                const left = (window.screen.width - width) / 2;
                const top = (window.screen.height - height) / 2;

                window.open(window.location.origin + route.href, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
            },
            // 모든 유저 정보 불러오기
            async loadUserList(){
                try{
                    await axios.get('/admin/user')
                    .then(result=>{
                        this.userList = result.data
                        console.log(result)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            // 유저를 아이디로 검색
            async searchUserId(){
                try{
                    await axios.get('/admin/user/id',{
                        params: {
                            userId: this.searchId
                        }
                    })
                    .then((result) =>{
                        // id 검색 결과는 {} 반환됨
                        this.userList = [result.data]
                        console.log("Id 검색결과")
                        console.log(result)
                    })
                    .catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            // 유저를 이름으로 검색
            async searchUserName(){
                try{
                    await axios.get('/admin/user/name',{
                        params: {
                            userName: this.searchName
                        }
                    })
                    .then((result) =>{
                        // Name 검색 결과는 [{},...] 반환됨
                        this.userList = result.data
                        console.log("Name 검색결과")
                        console.log(result)
                    })
                    .catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }    
            },
            // 유저의 아이디로 해당 유저 삭제
            async deleteUserById(deleteId){
                try{
                    await axios.delete('/admin/user/'+deleteId)
                    .then((result)=>{
                        console.log("상태: "+result.status+", "+deleteId+"제거 요청 완료")
                        this.loadUserList()
                    })
                    .catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            }
        },
        mounted(){
            this.loadUserList()
        }
    }
</script>


<style lang="scss" scoped>
        .frame-main{
            width: 600px;
            height: 600px;

            //border: 1px solid #ECBC76; // frame 표시용 시용
            .title-h1{
                width: 600px;
                height: 50px;

                padding: 0px;
                margin-top: 0px;
                margin-left: 5px;

                display: flex;
                align-items: center;
                justify-content: left;
                text-align: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 30px;
                line-height: 24px;
                color: #B87514;
            }
            .frame-sub-head{
                width: 600px;
                height: 110px;
                
                // border: 1px solid #ECBC76; // frame 표시용 시용

                .btn-loadUserList{
                    width: 590px;
                    height: 45px;

                    margin-top: 10px;
                    margin-left: 5px;

                    cursor: pointer;

                    background: #B87514;
                    border-radius: 10px;    

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;

                    color: #FFFEF9;
                }
                .btn-searchUserId{
                    width: 95px;
                    height: 45px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 5px;
                    float: left;

                    cursor: pointer;

                    background: #ECBC76;
                    border-radius: 10px;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #FFFEF9;
                }
                .input-searchUserId{
                    width: 183px;
                    height: 43px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 10px;
                    margin-right: 0px;
                    float: left;

                    border: 1px solid #C4C4C4;
                    border-radius: 8px;

                    display: flex;
                    align-items: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 15px;
                    line-height: 130%;
                    color: #000000;
                }
                .btn-searchUserName{
                    width: 95px;
                    height: 45px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 10px;
                    float: left;

                    cursor: pointer;

                    background: #ECBC76;
                    border-radius: 10px;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #FFFEF9;
                }
                .input-searchUserName{
                    width: 183px;
                    height: 43px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 10px;
                    margin-right: 0px;
                    float: left;

                    border: 1px solid #C4C4C4;
                    border-radius: 8px;

                    display: flex;
                    align-items: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 15px;
                    line-height: 130%;
                    color: #000000;
                }
            }
            .frame-sub-body{
                width: 600px;
                height: 428px;

                overflow-y: auto;

                .attribute-name{
                    width: 115px;
                    height: 54px;

                    padding: 0px;
                    margin-top: 0px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #ECBC76;
                }
                .attribute-email{
                    width: 266px;
                    height: 54px;

                    padding: 0px;
                    margin-top: 0px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #ECBC76;
                }
                .content-row-name{
                    width: 115px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 16px;
                    line-height: 24px;
                    color: #B87514;
                }
                .content-row-email{
                    width: 266px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 16px;
                    line-height: 24px;
                    color: #B87514;
                }
                .content-row-btn-edit{
                    width: 90px;
                    height: 45px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 10px;
                    float: left;

                    cursor: pointer;

                    background: #ECBC76;
                    border-radius: 10px;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #FFFEF9;
                }
                .content-row-btn-delete{
                    width: 90px;
                    height: 45px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 10px;
                    float: left;

                    cursor: pointer;

                    background: #B87514;
                    border-radius: 10px;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #FFFEF9;
                }
            }
            .line-for-division{
                width: 580px;
                height: 0px;
                margin-top: 0px;
                margin-left: 9px;
                margin-bottom: 0px;

                border: 1px solid #ECBC76;
            }
        }
        
</style>