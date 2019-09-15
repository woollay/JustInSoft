import Vue from 'vue';
import App from '../components/App';

Vue.prototype.url = urlConfig;

new Vue({
  el: '#proxy',
  components: {
    App
  },
  template: '<App/>'
});