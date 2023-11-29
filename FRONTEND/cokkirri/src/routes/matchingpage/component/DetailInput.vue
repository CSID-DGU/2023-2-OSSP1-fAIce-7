<template>
    <div class="frame-form">
        <div v-if="selectedMatchingType===''" class="font-warning">
            ※ 매칭 타입 선택 필요
        </div>
        <router-link to="/" v-else-if="!this.$store.state.isLogin" class="font-warning">
            로그인을 해야 합니다.
        </router-link>
        <div v-else-if="selectedMatchingType==='free'">
            <div class="font-name">날짜</div>
            <div class="font-input">
                <Calendar
                    v-model="date"
                    showIcon
                    :pt="{
                        input: { class: 'w-16rem' },
                        dropdownButton: {
                            root: { class: 'bg-teal-500 border-teal-500' }
                        }
                    }"
                    :minDate="new Date()"
                    dateFormat="yy/mm/dd"
                />
            </div>
            <div style="clear:both;"></div>

            <div class="font-name">시작:</div>

            <div class="font-input-small">
                <Dropdown 
                    v-model="startTime" 
                    :options="timeList" 
                    optionLabel="name" 
                    placeholder="희망 시간"
                    @change="updateStartTime()"
                />
            </div>
            <div class="font-name">종료:</div>
            <div class="font-input-small">
                <Dropdown 
                    v-model="endTime" 
                    :options="timeList" 
                    optionLabel="name" 
                    placeholder="희망 시간"
                    @change="updateEndTime()"
                />
            </div>
        </div>
        <div v-else>
                <router-link to="/my/timetable" v-if="this.$store.state.course.length===0" class="font-warning">
                    학수번호를 추가해야 합니다.
                </router-link>
                <div v-else>
                    <div class="font-name">학수번호</div>
                    <div class="font-input-overflow-x">
                    <div 
                        v-for="(item, index) 
                        in course" :key="index" 
                        :class="{'selected-btn': selectedCourses.includes(item), 'unselected-btn': !selectedCourses.includes(item)}"
                        @click="toggleSelection(item);updateCorse()">
                        {{item}}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Calendar from 'primevue/calendar';
import Dropdown from 'primevue/dropdown';
import 'primevue/resources/themes/saga-blue/theme.css';       // theme
import 'primevue/resources/primevue.min.css';                 // core css
//import 'primeicons/primeicons.css';   

import { ref } from "vue";
import moment from 'moment'

export default {
    
    components:{
        Calendar,
        Dropdown
    },
    data() {
        return{
            startTime: { name: '', code: '' },
            endTime: { name: '', code: '' },
            date: '',
            formatD: '',
            //course: this.$store.state.course,
            course: this.$store.state.course,
            selectedCourses: [],
            timeList: ref([
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00", "23:30"
            ].map((time) => ({ name: time, code: time })))
        }
    },
    computed: {
        formattedDate() {
            return moment(this.date).format('YYYY-MM-DD');
        }
    },
    props: [
        'selectedMatchingType'
    ],
    methods: {

        toggleSelection(course){
            const index = this.selectedCourses.indexOf(course)
            if (index >= 0){
                this.selectedCourses.splice(index,1)
            } else {
                this.selectedCourses.push(course)
            }
        },
        updateDate(){
            this.$emit('update:date', this.formattedDate)
        },
        updateStartTime(){
            this.$emit('update:starttime', this.startTime.code)
        },
        updateEndTime(){
            this.$emit('update:endtime', this.endTime.code)
        },
        updateCorse(){
            this.$emit('update:course', this.selectedCourses)
        },
    },
    watch: {
        date() {
            this.updateDate();
        }
    },
}
</script>

<style lang="scss" scoped>

    .frame-form{
        width: 659px;
        height: 401px;

        float: left;
        display: flex;
        align-items: center;
        justify-content: center;

        border: 5px solid #ECBC76;
        border-radius: 20px;

        .font-warning{
            height: 123px;
            
            margin-top:8px;
            float: left;
            
            
            font-style: normal;
            font-weight: 500;
            font-size: 30px;
            line-height: 45px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            color: #B87514;
        }

        .font-name{
            width: 117px;
            height: 123px;
            
            margin-top:8px;
            float: left;
            
            
            font-style: normal;
            font-weight: 500;
            font-size: 30px;
            line-height: 45px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            color: #B87514;

        }
        .font-input{
            width: 532px;
            height: 123px;

            margin-top:8px;
            float: left;

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            font-size: 25px;
            color: #000000;
        }
        .font-input-overflow-x{
            width: 532px;
            height: 123px;

            margin-top:8px;
            float: left;

            overflow-x: auto;
            white-space: nowrap;

            display: flex;
            align-items: center;
            
            font-size: 25px;
            color: #000000;
        }
        .selected-btn{
            width: 120px;
            height: 100px;
            
            margin-left: 10px;
            background: #B87514;
            border-radius: 20px;

            cursor: pointer;

            display: flex;
            align-items: center;
            justify-content: center;

            
            font-style: normal;
            font-weight: 700;
            line-height: 45px;
            font-size: 20px;
            color: #000000;
        }
        .unselected-btn{
            width: 120px;
            height: 100px;
            
            margin-left: 10px;
            background: #ECBC76;
            border-radius: 20px;

            cursor: pointer;

            display: flex;
            align-items: center;
            justify-content: center;

            
            font-style: normal;
            font-weight: 700;
            line-height: 45px;
            font-size: 20px;
            color: #000000;
        }
        .font-input-small{
            width: 205px;
            height: 124px;

            margin-top:8px;
            float: left;

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            font-size: 25px;
            color: #000000;
        }
        .p-calendar .p-inputtext {
            height: 50px;
            font-size: 25px;
            color: #B87514;
            background: transparent;
            border: none;
            line-height: 50px;
        }
    }
</style>