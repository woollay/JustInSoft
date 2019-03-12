// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import HybirdApp from '../components/HybirdApp'

/* eslint-disable no-new */
new Vue({
    el: '#android',
    components: {
        HybirdApp
    },
    template: '<HybirdApp/>'
})