<template>
    <div class="frame-main">
        <div class="title-h1">매칭 기록 관리</div>
        <div class="line-for-division"></div>
        <div class="frame-sub-head">
            <div class="btn-loadMatchingList" @click="loadMatchingList()">전체 조회</div>
            <!---
            <div class="btn-temp" @click="searchMatchingId()">점검중</div>
            <input type="text" placeholder="Search Id" class="input-temp" v-model="searchId" @change="searchMatchingId()">
            --->
            <div class="btn-temp" @click="searchMatchingId()">매칭ID검색</div>
            <input type="text" placeholder="Search Matching ID" class="input-searchMatchingId" v-model="MatchingIdForSearch" @change="searchMatchingId()">
            <div style="clear:both;"></div>
        </div>
        <div class="frame-sub-body">
            <div class="line-for-division"></div>
            <div class="attribute-type">타입</div>
            <div class="attribute-sub-small">매칭ID</div>
            <div class="attribute-sub-small">인원</div>
            <div class="attribute-sub-small">매칭잡힌날</div>
            <div style="clear:both;"></div>

            <div class="attribute-sub-long">클래스</div>
            <div class="attribute-sub-long">매칭구성원</div>
            <div style="clear:both;"></div>
            <div class="line-for-division"></div>
            <div style="clear:both;"></div>
            <!---
                matchingId
                headCount
                matchingTime
                emailList ["","",...]
            --->
            <div v-for="(matching,index) in publicMatchingList" :key="index">
                <div class="content-row-type">공강 매칭</div>
                <div class="content-row-sub-small">{{matching.matchingId}}</div>
                <div class="content-row-sub-small">{{matching.headCount}}</div>
                <div class="content-row-sub-small">{{matching.matchingTime}}</div>
                <dir class="content-row-btn-delete" @click="deleteMatchingByMatchingId(matching.matchingId,'free')">제거</dir>
                <div style="clear:both;"></div>

                <div class="content-row-sub-long">-</div>
                <div class="content-row-sub-long">{{matching.emailList}}</div>
                <div style="clear:both;"></div>
            </div>
            <!---
                matchingId
                headCount
                matchingTime
                courseNumber ["","",...]
                emailList ["","",...]
            --->
            <div v-for="(matching,index) in classMatchingList" :key="index">
                <div class="content-row-type">수업 매칭</div>
                <div class="content-row-sub-small">{{matching.matchingId}}</div>
                <div class="content-row-sub-small">{{matching.headCount}}</div>
                <div class="content-row-sub-small">{{matching.matchingTime}}</div>
                <dir class="content-row-btn-delete" @click="deleteMatchingByMatchingId(matching.matchingId,'class')">제거</dir>
                <div style="clear:both;"></div>

                <div class="content-row-sub-long">{{matching.courseNumber}}</div>
                <div class="content-row-sub-long">{{matching.emailList}}</div>
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
                publicMatchingList: [],
                classMatchingList: [],

                MatchingIdForSearch: '',

                classMatchingIdForDelete: '',
                publicMatchingIdForDelete: '',
            }
        },
        methods:{
            // 전체 각 타입별 매칭 리스트 불러오기
            loadMatchingList(){
                this.loadPublicMatchingList()
                this.loadClassMatchingList()
            },
            async loadPublicMatchingList(){
                try{
                    await axios.get('/admin/publicMatching')
                    .then((result)=>{
                        console.log("공강 매칭 리스트 모두 불러오기")
                        this.publicMatchingList = result.data
                        //this.classMatchingList = []
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async loadClassMatchingList(){
                try{
                    await axios.get('/admin/classMatching')
                    .then((result)=>{
                        console.log("수업 매칭 리스트 모두 불러오기")
                        //this.publicMatchingList = []
                        this.classMatchingList = result.data
                    })
                }catch(error){
                    console.log(error)
                }
            },
            // Ec2에 아래 두 개 api 업데이트가 아직 안 된 상황임
            searchMatchingId(){
                this.searchClassMatchingId()
                this.searchPublicMatchingId()
            },
            // 매칭아이디로 타입별 매칭 검색하기
            async searchClassMatchingId(){
                try{
                    await axios.get('/matching/admin/classMatchedList',{
                        params:{
                            matchingId: this.MatchingIdForSearch
                        }
                    }).then((result)=>{
                        console.log("매칭 검색- 수업")
                        console.log(result)
                        //this.publicMatchingList = []
                        this.classMatchingList = [result.data]
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async searchPublicMatchingId(){
                try{
                    await axios.get('/matching/admin/publicMatchedList',{
                        params:{
                            matchingId: this.MatchingIdForSearch
                        }
                    }).then((result)=>{
                        console.log("매칭 검색- 공강")
                        console.log(result)
                        this.publicMatchingList = [result.data]
                        //this.classMatchingList = []
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },

            // 매칭아이디로 타입별 매칭 삭제하기
            async deleteMatchingByMatchingId(matchingId,matchingType){
                if(matchingType==="class"){
                    this.deleteClassMatchingByMatchingId(matchingId)
                }
                else if(matchingType==="free"){
                    this.deletePublicMatchingByMatchingId(matchingId)
                }
                this.loadMatchingList()
            },
            async deleteClassMatchingByMatchingId(matchingId){
                try{
                    await axios.get('/matching/delete/class',{
                        params:{
                            matchingId: matchingId
                        }
                    })
                    .then((result)=>{
                        alert(result.data)
                        this.loadMatchingList()
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async deletePublicMatchingByMatchingId(matchingId){
                try{
                    await axios.get('/matching/delete/free',{
                        params:{
                            matchingId: matchingId
                        }
                    })
                    .then((result)=>{
                        alert(result.data)
                        this.loadMatchingList()
                    })
                }catch(error){
                    console.log(error)
                }
            },
        },
        mounted(){
            this.loadMatchingList()
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

                .btn-loadMatchingList{
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
                .btn-temp{
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
                .input-temp{
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
                .btn-searchMatchingId{
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
                .input-searchMatchingId{
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

                .attribute-type{
                    width: 100px;
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
                .attribute-sub-small{
                    width: 100px;
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
                .attribute-sub-long{
                    width: 300px;
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
                .content-row-type{
                    width: 100px;
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
                .content-row-sub-small{
                    width: 100px;
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
                .content-row-sub-long{
                    width: 300px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 0px;
                    float: left;
                    overflow-x: auto;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 12px;
                    line-height: 24px;
                    color: #B87514;
                }
                .content-row-btn-delete{
                    width: 100px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 50px;
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
            .line-for-division-short{
                width: 500px;
                height: 0px;
                margin-top: 0px;
                margin-left: 49px;
                margin-bottom: 0px;

                border: 1px solid #ECBC76;
            }
        }
</style>