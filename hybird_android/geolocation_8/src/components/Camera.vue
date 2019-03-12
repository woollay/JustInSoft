<template>
	<div class="camera">
		<div class="camera-title">Click following button to take a picture:</div>
		<img src="assets/button.png" @click="takePictures">
		<div class="preview-img">
			<div class="camera-title">Current photos is:</div>
			<img :src="previewImg" v-if="previewImg!==''">
		</div>
	</div>
</template>

<script>
	import CameraSdk from "../commons/camera-sdk";
	export default {
		name: "Camera",
		data() {
			return {
				previewImg: ""
			};
		},
		methods: {
			takePictures: function() {
				console.log("start to task a picture now.");
				let self = this;
				CameraSdk.takePictures({
					success: res => {
						console.log("current picture is:" + res);
						self.previewImg = res;
					},
					error: res => {
						console.log("failed to get picture:" + res);
						self.previewImg = "";
					}
				});
			}
		}
	};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	.camera {
		width: 100%;
		height: 200px;
		display: flex;
		justify-content: center;
		align-items: flex-start;
		flex-wrap: wrap;
	}

	.camera-title {
		width: 100%;
		height: 20px;
	}

	.camera img {
		width: 100px;
		height: 30px;
	}

	.preview-img {
		width: 100%;
		height: 100px;
	}

	.preview-img img {
		width: 100px;
		height: 100px;
	}
</style>
