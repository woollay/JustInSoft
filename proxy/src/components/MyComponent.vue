<template>
  <div class="component-style">
    <div>{{content}}</div>
    <img class="btn-style" src="assets/button.png" @click="sendRequest" />
  </div>
</template>
<script>
import axios from "axios";
export default {
  name: "MyComponent",
  data () {
    return {
      content: ""
    };
  },
  mounted () {
  },
  methods: {
    sendRequest () {
      //网上随便找了一个公开的天气API(来自https://www.sojson.com/api/weather.html)
      //标准地址：http://t.weather.sojson.com/api/weather/city/101030100
      // 方法A：此处写成"/ppp/api/weather/city/101030100";
      // 方法B：此处写成"/api/weather/city/101030100";
      let url = this.url.apiUrl + "/api/weather/city/101030100";
      axios.get(url).then((resp) => {
        console.log("result:" + resp.data);
        this.content = resp.data;
        alert("request success!");
      }).catch((e) => {
        console.log("exception:" + e);
        alert("request failed!");
      });
    }
  }
};
</script>
<style scoped>
.component-style {
  width: 300px;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.btn-style {
  width: 150px;
  height: 60px;
}

.component-style div {
  width: 100%;
  height: 340px;
  overflow: scroll;
}
</style>
