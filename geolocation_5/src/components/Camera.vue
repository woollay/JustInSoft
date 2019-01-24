<template>
	<div class="camera">
		<h1>Click following button to task a picture:</h1>
		<input id="camera" type="file" accept="image/*" capture="camera">
		<div class="preview-img" v-if="previewImg!==''">
			<h2>Current photos is:</h2>
			<img :src="previewImg">
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
		mounted: function() {
			console.log("start to init camera now.");
			let self = this;
			CameraSdk.takePictures("camera", {
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
	};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	.camera {
		width: 100%;
		height: 100%;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-wrap: wrap;
	}

	.camera h1,
	.camera h2 {
		width: 100%;
	}

	.preview-img {
		width: 100%;
		height: 300px;
	}

	.preview-img img {
		width: 300px;
		height: 300px;
	}
</style>
