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
            <div class="title">노쇼 신고</div>
            <div class="sub-title">
                신고 매칭ID : {{matchingId}}<br>
                매칭 종류 : {{alteredmatchingType}}
            </div>
            <div class="describe-title">제목</div>
            <input type="text" placeholder="  제목을 입력해주세요." class="input-title" v-model="title">
            <div class="describe-content">내용</div>
            <textarea type="text" placeholder=" 내용을 입력해주세요." class="input-content" v-model="comment"/>
            <div class="btn-submit" @click="submitReport()">제출</div>
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
                matchingType: '',
                matchingId: '',
                email: '',
                title: '',
                comment: '',
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
            submitReport(){
                if(this.title===''){
                    alert("제목이 입력되지 않았습니다.")
                }
                else if(this.comment===''){
                    alert("내용이 입력되지 않았습니다.")
                }
                else{
                    if(this.matchingType==='class'){
                        this.reportClassNoshow()
                    }else if(this.matchingType==='free'){
                        this.reportFreeNoshow()
                    }else{
                        alert("잘못된 접근입니다.")
                    }
                }
            },
            async reportClassNoshow(){
                try{
                    await axios.post('/matching/post/class/accusation',{
                        matchingType: this.matchingType,
                        matchingId: this.matchingId,
                        email: this.email,
                        title: this.title,
                        comment: this.comment
                    }).then((result)=>{
                        console.log(result)
                        alert("수업 매칭 노쇼 신고가 완료되었습니다.")
                        window.close()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async reportFreeNoshow(){
                try{
                    await axios.post('/matching/post/free/accusation',{
                        matchingType: this.matchingType,
                        matchingId: this.matchingId,
                        email: this.email,
                        title: this.title,
                        comment: this.comment
                    }).then((result)=>{
                        console.log(result)
                        alert("공강 매칭 노쇼 신고가 완료되었습니다.")
                        window.close()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            cancleReport(){
                alert("노쇼 신고가 취소되었습니다.")
                window.close()
            },
        },
        mounted(){
            this.email = this.$route.query.email;
            this.matchingType = this.$route.query.matchingType;
            this.matchingId = this.$route.query.matchingId;
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
            .describe-title{
                width: 150px;
                height: 30px;

                margin-left: 25px;
                margin-top: 5px;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 20px;
                line-height: 30px;

                color: #000000;
            }
            .input-title{
                // 테두리 고려해서 가로 세로 2px 감소 적용됨
                width: 498px;
                height: 43px;

                box-sizing: border-box;
                margin-left: 25px;
                margin-right: 25px;
                margin-top: 0px;

                display: flex;
                align-items: center;
                justify-content: left;

                background: #FFFFFF;
                border: 1px solid #4285F4;
                border-radius: 9px;
            }
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
            .btn-submit{
                width: 150px;
                height: 63px;

                float: left;
                margin-left: 210px;
                margin-top: 18px;

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