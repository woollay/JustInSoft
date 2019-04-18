<template>
	<div class="android">
		<geo-location-component></geo-location-component>
		<camera-component></camera-component>
		<record-component></record-component>
		<timeline></timeline>
		<rocket></rocket>
	</div>
</template>

<script>
	import GeoLocationComponent from "./GeoLocation";
	import CameraComponent from "./Camera";
	import RecordComponent from "./Record";
	import Timeline from "./Timeline";
	import Rocket from "./Rocket";

	export default {
		name: "Hybird-App",
		data() {
			return {};
		},
		components: {
			GeoLocationComponent,
			CameraComponent,
			RecordComponent,
			Timeline,
			Rocket
		}
	};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
	.android {
		width: 100%;
		height: 100%;
		margin: 0px;
		padding: 0px;
		overflow: hidden;
	}
</style>
