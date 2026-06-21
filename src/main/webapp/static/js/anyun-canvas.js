(function () {
	'use strict';

	var canvas = document.getElementById('anyun-particle-canvas');
	var banner = canvas && canvas.closest('.anyun-banner');
	if (!canvas || !banner || !canvas.getContext) return;

	var ctx = canvas.getContext('2d');
	var particleCount = 200;
	var particles = [];
	var angles = [];
	var width = 0;
	var height = 0;
	var depth = 200;
	var targetX = 0;
	var targetY = 0;
	var cameraX = 0;
	var cameraY = 0;
	var cameraZ = 150;
	var rotation = {x: 20, y: -20, z: 0};
	var velocity = 0.04;
	var animationId;
	var visible = true;
	var dpr = Math.min(window.devicePixelRatio || 1, 2);

	function radians(degrees) {
		return degrees * Math.PI / 180;
	}

	function randomWave() {
		return Math.sin(Math.floor(Math.random() * 360) * Math.PI / 180);
	}

	function addParticle() {
		particles.push({
			vertex: {x: randomWave(), y: randomWave(), z: randomWave()},
			position: {
				x: depth * Math.sin(radians(360 * Math.random())),
				y: depth * Math.sin(radians(360 * Math.random())),
				z: depth * Math.sin(radians(360 * Math.random()))
			}
		});
		angles.push({
			x: 360 * Math.random(),
			y: 360 * Math.random(),
			z: 360 * Math.random()
		});
	}

	function rotatePoint(point) {
		var rx = radians(rotation.x);
		var ry = radians(rotation.y);
		var rz = radians(rotation.z);
		var x1 = point.x;
		var y1 = point.y * Math.cos(rx) - point.z * Math.sin(rx);
		var z1 = point.y * Math.sin(rx) + point.z * Math.cos(rx);
		var x2 = x1 * Math.cos(ry) + z1 * Math.sin(ry);
		var y2 = y1;
		var z2 = -x1 * Math.sin(ry) + z1 * Math.cos(ry);
		return {
			x: x2 * Math.cos(rz) - y2 * Math.sin(rz),
			y: x2 * Math.sin(rz) + y2 * Math.cos(rz),
			z: z2
		};
	}

	function project(particle) {
		var scale = {
			x: width / 5,
			y: height / 5,
			z: width / 5
		};
		var point = {
			x: particle.vertex.x * scale.x,
			y: particle.vertex.y * scale.y,
			z: particle.vertex.z * scale.z
		};
		point = rotatePoint(point);
		point.x += particle.position.x;
		point.y += particle.position.y;
		point.z += particle.position.z;

		var destX = -cameraX;
		var destY = -cameraY;
		var destZ = 1 - cameraZ;
		var planeLength = Math.sqrt(destX * destX + destZ * destZ) || 1;
		var fullLength = Math.sqrt(destX * destX + destY * destY + destZ * destZ) || 1;
		var cplane = -destZ / planeLength;
		var splane = destX / planeLength;
		var ctheta = planeLength / fullLength;
		var stheta = -destY / fullLength;

		var px = point.x * cplane + point.z * splane;
		var pz = point.x * -splane + point.z * cplane;
		var py = point.y * ctheta - pz * stheta;
		pz = point.y * stheta + pz * ctheta;
		px -= cameraX;
		py -= cameraY;
		pz -= cameraZ;
		if (pz === 0) pz = 0.001;

		var perspective = destZ / pz;
		return {
			x: px * perspective + width / 2,
			y: -py * perspective + height / 2,
			p: perspective
		};
	}

	function resize() {
		var rect = banner.getBoundingClientRect();
		width = Math.max(1, Math.round(rect.width));
		height = Math.max(1, Math.round(rect.height));
		dpr = Math.min(window.devicePixelRatio || 1, 2);
		canvas.width = Math.round(width * dpr);
		canvas.height = Math.round(height * dpr);
		canvas.style.width = width + 'px';
		canvas.style.height = height + 'px';
		ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
	}

	function updatePointer(clientX, clientY) {
		var rect = banner.getBoundingClientRect();
		targetX = (clientX - rect.left - rect.width / 2) * -0.8;
		targetY = (clientY - rect.top - rect.height / 2) * 0.8;
	}

	function draw() {
		if (!visible) {
			animationId = requestAnimationFrame(draw);
			return;
		}

		cameraX += (targetX - cameraX) * 0.05;
		cameraY += (targetY - cameraY) * 0.05;
		rotation.x += 0.1;
		rotation.y += 0.1;
		rotation.z += 0.1;
		ctx.clearRect(0, 0, width, height);
		ctx.globalCompositeOperation = 'lighter';

		for (var i = 0; i < particles.length; i++) {
			angles[i].x = (angles[i].x + velocity) % 360;
			angles[i].y = (angles[i].y + velocity) % 360;
			angles[i].z = (angles[i].z + velocity) % 360;
			particles[i].position.x = depth * Math.cos(radians(angles[i].x));
			particles[i].position.y = depth * Math.sin(radians(angles[i].y));
			particles[i].position.z = depth * Math.sin(radians(angles[i].z));

			var point = project(particles[i]);
			if (point.p <= 0 || !isFinite(point.x) || !isFinite(point.y)) continue;
			var radius = Math.max(0.7, Math.min(5, point.p * 2));
			var gradient = ctx.createRadialGradient(point.x, point.y, radius * .5, point.x, point.y, radius * 2);
			gradient.addColorStop(0, 'rgba(255,255,255,1)');
			gradient.addColorStop(.5, 'hsla(' + ((i + 2) % 360) + ',85%,55%,1)');
			gradient.addColorStop(1, 'hsla(' + (i % 360) + ',85%,40%,.15)');
			ctx.fillStyle = gradient;
			ctx.beginPath();
			ctx.arc(point.x, point.y, radius * 2, 0, Math.PI * 2);
			ctx.fill();
		}

		animationId = requestAnimationFrame(draw);
	}

	for (var i = 0; i < particleCount; i++) addParticle();
	resize();
	draw();

	banner.addEventListener('mousemove', function (event) {
		updatePointer(event.clientX, event.clientY);
	});
	banner.addEventListener('mouseleave', function () {
		targetX = 0;
		targetY = 0;
	});
	banner.addEventListener('pointerdown', function (event) {
		if (event.pointerType === 'mouse' && event.button !== 0) return;
		for (var i = 0; i < 35 && particles.length < 320; i++) addParticle();
	});
	banner.addEventListener('touchmove', function (event) {
		if (event.touches[0]) updatePointer(event.touches[0].clientX, event.touches[0].clientY);
	}, {passive: true});
	window.addEventListener('resize', resize);

	if ('IntersectionObserver' in window) {
		new IntersectionObserver(function (entries) {
			visible = entries[0].isIntersecting;
		}, {threshold: 0}).observe(banner);
	}

	window.addEventListener('beforeunload', function () {
		cancelAnimationFrame(animationId);
	});
})();
