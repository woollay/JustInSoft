<template>
	<div id="task-content" class="task-content">
		<div class="task-operation">
			<div class="task-option1">
				<div class="take-picture-container" v-if="showTakePicture">
					<div class="opt-take-picture">
						<img src="assets/button.png" @click="takePicture">
					</div>
				</div>
				<div class="pre-view-container" v-else>
					<div class="pre-view">
						<img :src="preViewImg">
						<div class="reset-take-picture" @click="takePicture">
							<div>
								<span>重拍</span>
							</div>
							<div style="display:flex;justify-content:center;align-items:center">
								<img src="assets/button.png">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
	import WeixinTester from "../commons/simple_biz";

	export default {
		name: "Weixin",
		data() {
			return {
				showTakePicture: true,
				preViewImg: ""
			};
		},
		mounted: function() {
			this.biz = new WeixinTester();
		},
		methods: {
			takePicture: function() {
				let self = this;
				this.biz.takePicture({
					success: function(res) {
						console.log("Take picture success.");
						self.showTakePicture = false;
						self.preViewImg = res;
					},
					error: function(res) {
						console.log("Take picture error.");
						alert("Take picture error.");
					}
				});
			}
		}
	};
</script>

<style scoped>
	.task-content {
		height: 400px;
		width: 200px;
		display: flex;
		justify-content: center;
		align-items: center;
		flex-wrap: wrap;
		left: 50%;
		margin-left: 100px;
	}

	.task-description {
		width: 125%;
		height: 100px;
		margin-top: 40px;
		margin-left: -12.5%;
	}

	.task-description span {
		font-size: 15px;
	}

	.task-target {
		margin-left: 12.5%;
		font-size: 15px;
	}
	.task-operation {
		height: 150px;
		width: 100%;
	}

	.task-option1 span {
		margin-top: 10px;
		font-weight: bold;
	}

	.take-picture-container {
		width: 200px;
	}

	.take-picture-container img {
		position: absolute;
		width: 200px;
		height: 160px;
		z-index: 999;
	}
	.take-picture-mask {
		width: 200px;
		height: 80px;
		top: 50%;
		margin-top: 60px;
		position: absolute;
		z-index: 1000;
		background: #fbf6e8;
		opacity: 0.8;
	}
	.opt-take-picture {
		width: 100px;
		position: relative;
		left: 50%;
		margin-left: -50px;
		top: 70px;
		z-index: 1000;
	}

	.opt-take-picture img {
		width: 100px;
		height: 100px;
	}

	.pre-view {
		width: 100px;
		position: relative;
		left: 50%;
		margin-left: -50px;
		margin-top: 5px;
		display: flex;
		flex-wrap: wrap;
		justify-content: center;
	}

	.pre-view img {
		width: 100px;
		height: 100px;
	}

	.pre-view span {
		font-size: 15px;
		text-align: center;
	}

	.reset-take-picture {
		margin-top: 10px;
		width: 80px;
		height: 30px;
		position: relative;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.reset-take-picture img {
		width: 30px;
		height: 30px;
	}
</style>
