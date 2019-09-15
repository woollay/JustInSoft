<template>
  <div class="proxy">
    <my-component></my-component>
  </div>
</template>

<script>
import MyComponent from "./MyComponent";

export default {
  name: "App",
  data () {
    return {};
  },
  components: {
    MyComponent
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.proxy {
  width: 400px;
  height: 500px;
  margin: 0px;
  padding: 0px;
  display: flex;
  justify-content: center;
  align-items: flex-end;
}
</style>
