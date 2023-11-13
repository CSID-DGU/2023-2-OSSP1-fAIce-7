<template>
    <!---모든 주소 정보를 RouterView로 가져왔음--->
    <div>
        <RouterView />
    </div>
    <!-- <div id="app">
        <Chatting />
    </div> -->
</template>

<style lang="scss">
    @font-face {
        font-family: 'omyu_pretty';
        src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_2304-01@1.0/omyu_pretty.woff2') format('woff2');
        font-weight: normal;
        font-style: normal;
    }
    body, html {
        font-family: 'omyu_pretty', Arial, sans-serif;
        margin: 0;
        height: 100%;
    }
</style>