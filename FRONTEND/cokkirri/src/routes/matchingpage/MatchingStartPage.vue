<template>
    <div class="background-setting">
        <div class="container">
            <div class="frame-first-step-body">
                <div class="upper-area">
                    <div class="interest-title">나의 관심 분야</div>
                    <div class="interest-list">
                        <ul>
                            <!-- 관심 분야 목록만 출력 -->
                            <li v-for="(interest, index) in filteredInterests" :key="index">
                                {{ interest }}
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="matching-submit-btn" @click="submitMatching">매칭 신청</div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '../../api/index.js'

export default {
    computed: {
        filteredInterests() {
            // 이메일 형식을 제외한 관심분야 목록만 필터링
            return this.$store.state.userInterests.filter(interest => {
                return typeof interest === 'string' && !interest.includes('@'); // 이메일 형식 제외
            });
        }
    },
    methods: {
        async submitMatching() {
            try {
                const payload = {
                    email: this.$store.state.id,
                    interests: this.filteredInterests,
                    matchingType: 'hobby'
                };
                await axios.post('/api/matching', payload);
                this.$store.dispatch('callMatchingRecord');
                this.$router.replace('/my/matching');
            } catch (error) {
                console.error('매칭 신청 오류:', error);
                alert('매칭 신청에 실패했습니다.');
            }
        }
    }
}
</script>

<style lang="scss" scoped>
.background-setting {
    height: 100vh;
    width: 100vw;
    margin: 0;
    background-image: url("../../assets/mypage/background.png");
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center center;
    display: grid;
    grid-template-rows: auto;
    justify-items: center;
    align-items: center;
}

.container {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #FFFFFF;
    border-radius: 20px;
}

.frame-first-step-body {
    width: 996px;
    height: 625px;
    border: 5px solid #ECBC76;
    border-radius: 20px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.upper-area {
    display: flex;
    height: 80%;
}

.interest-title {
    width: 30%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2vw;
    color: #B87514;
    position: relative; /* 가상 요소의 위치 기준점 */
    border: 1px solid #333; /* 테두리 색상 지정 */
    border-radius: 10px; /* 둥근 꼭지점 */
    padding: 10px; /* 내부 여백 */
    box-sizing: border-box; /* 박스 크기 계산 방식 */
}



.interest-list {
    width: 70%;
    display: flex;
    align-items: center;
    justify-content: center;
    ul {
        list-style-type: none;
        padding: 0;
    }
    li {
        position: relative; /* 가상 요소의 위치 기준점 */
        margin-bottom: 20px; /* 목록 간 간격 */
        font-size: 1.8vw;
        color: #333;
        border: 1px solid #333; /* 테두리 색상 지정 */
        border-radius: 10px; /* 둥근 꼭지점 */
        padding: 10px; /* 내부 여백 */
        box-sizing: border-box; /* 박스 크기 계산 방식 */
        background-color: white; /* 박스 내부 색상 */
    }
}



.matching-submit-btn {
    width: 400px;
    height: 60px;
    margin-top: 20px;
    margin-left: 293px;
    margin-bottom: 50px;
    background: #B87514;
    border-radius: 50px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    font-style: normal;
    font-weight: 700;
    font-size: 32px;
    color: #FFFFFF;
    line-height: 24px;
    letter-spacing: 0.5px;
}

</style>
