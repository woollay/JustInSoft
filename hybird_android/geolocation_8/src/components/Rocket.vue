<template>
	<div id="rocket">
		<div class="rocket" ref="rocket" v-if="shotRocket">
			<img src="assets/rocket.png">
		</div>
		<div ref="rocket-circle" class="rocket-circle" :data-pct="percent">
			<svg
				width="100"
				height="100"
				viewPort="0 0 50 50"
				version="1.1"
				xmlns="http://www.w3.org/2000/svg"
			>
				<circle class="circle-inner" :r="radius" cx="50" cy="50" fill="transparent"></circle>
				<circle ref="circle-bar" class="circle-bar" :r="radius" cx="50" cy="50" fill="transparent"></circle>
			</svg>
		</div>
	</div>
</template>
<script>
	import Touch from "../commons/touch";
	export default {
		name: "Rocket",
		data() {
			return {
				percent: 0,
				radius: 45,
				showRocket: false
			};
		},
		mounted: function() {
			let self = this;
			let node = self.$refs["rocket-circle"];
			let touch = new Touch(node, 5);
			touch.registerTouchStart({
				success: percent => {
					console.log("current percent:" + percent);
					self.percentChange(percent);
				}
			});
			touch.registerTouchEnd({
				success: result => {
					console.log("current result:" + result);
					if (result) {
						self.shotRocket();
					}
				}
			});
		},
		methods: {
			shotRocket: function() {
				let self = this;
				let rocket = self.$refs["rocket"];
				let maxHight = window.screen.availHeight;
				let timer = setInterval(() => {
					let bottom = parseInt(rocket.style.bottom);
					if (!bottom) {
						bottom = 15;
					}
					bottom += 18;
					console.log("start move to:" + bottom);
					rocket.style.bottom = bottom + "px";
					if (bottom >= maxHight) {
						clearInterval(timer);
						console.log("finish move:" + bottom);
						self.showRocket = false;
					}
				}, 100);
			},
			percentChange: function(percent) {
				let self = this;
				let input = this.$refs["percent"];
				let cirle = self.$refs["circle-bar"];
				let rocket = self.$refs["rocket"];
				if (isNaN(percent)) {
					percent = 0;
				} else {
					let r = self.radius;
					let c = Math.PI * (r * 2);

					if (percent < 0) {
						percent = 0;
					}
					if (percent > 1) {
						percent = 1;
					}
					if (percent > 0 && !self.showRocket) {
						self.showRocket = true;
						rocket.style.bottom = "15px";
					}
					let pct = percent * c;
					cirle.style.strokeDashoffset = c - pct;
					cirle.style.strokeDashArray = pct + " " + (c - pct);
					cirle.style.stroke = "#ff9f1e";
					self.percent = percent * 100;
				}
			}
		}
	};
</script>

<style  scoped>
	#rocket {
		font-family: "Avenir", Helvetica, Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		color: #2c3e50;
	}

	.rocket {
		height: 100px;
		width: 100px;
		position: absolute;
		z-index: 1000;
		bottom: 15px;
		left: 50%;
		margin-left: -50px;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.rocket img {
		height: 60px;
		width: 60px;
	}

	svg {
		transform: rotate(-90deg);
	}
	svg circle {
		stroke-dashoffset: 0px;
		stroke: #666;
		stroke-width: 10px;
	}

	svg .circle-inner {
		stroke-dasharray: 282.74px;
		stroke: #666;
	}

	svg .circle-bar {
		stroke-dasharray: 282.74px;
		transition: stroke-dasharray 0.5s linear;
	}

	.rocket-circle {
		height: 100px;
		width: 100px;
		box-shadow: 0 0 5px black;
		border-radius: 100%;
		position: absolute;
		z-index: 1000;
		bottom: 15px;
		left: 50%;
		margin-left: -50px;
	}

	.rocket-circle:after {
		height: 80px;
		width: 80px;
		box-shadow: inset 0 0 5px black;
		content: attr(data-pct) "%";
		border-radius: 100%;
		line-height: 80px;
		font-size: 10px;
		text-shadow: 0 0 5px black;
		margin-top: -40px;
		margin-left: -40px;
		position: absolute;
		z-index: 1000;
		bottom: 15px;
		left: 50%;
		top: 50%;
		display: flex;
		justify-content: center;
	}

	.circle-input {
		width: 100px;
		position: absolute;
		z-index: 1000;
		bottom: 155px;
		left: 50%;
		margin-left: -50px;
	}
</style>


