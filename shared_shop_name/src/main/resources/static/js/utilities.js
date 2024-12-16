const rangeInput = document.querySelectorAll(".range-input input"),
	priceInput = document.querySelectorAll(".price-input input"),
	range = document.querySelector(".slider .progress");
let priceGap = 1000;

priceInput.forEach((input) => {
	input.addEventListener("input", (e) => {
		let minPrice = parseInt(priceInput[0].value),
			maxPrice = parseInt(priceInput[1].value);

		if (maxPrice - minPrice >= priceGap && maxPrice <= rangeInput[1].max) {
			if (e.target.classList.contains("input-min")) {
				rangeInput[0].value = minPrice;
				range.style.left = (minPrice / rangeInput[0].max) * 100 + "%";
			} else {
				rangeInput[1].value = maxPrice;
				range.style.right = 100 - (maxPrice / rangeInput[1].max) * 100 + "%";
			}
		}
	});
});

rangeInput.forEach((input) => {
	input.addEventListener("input", (e) => {
		let minVal = parseInt(rangeInput[0].value),
			maxVal = parseInt(rangeInput[1].value);

		if (maxVal - minVal < priceGap) {
			if (e.target.classList.contains("range-min")) {
				rangeInput[0].value = maxVal - priceGap;
			} else {
				rangeInput[1].value = minVal + priceGap;
			}
		} else {
			priceInput[0].value = minVal;
			priceInput[1].value = maxVal;
			range.style.left = (minVal / rangeInput[0].max) * 100 + "%";
			range.style.right = 100 - (maxVal / rangeInput[1].max) * 100 + "%";
		}
	});
});


const trackingArea = document.getElementById("tracking-area");

if (trackingArea) {
	let firstChild = trackingArea.firstElementChild;
	let firstChildClass = firstChild.className;

	const dots = document.getElementsByClassName("dot");
	const progressBars = document.getElementsByClassName("progress-bar");
	console.log(progressBars);

	if (firstChildClass == "shipped") {
		dots[1].style.background = "#0C84D9";
		dots[1].style.color = "white";
		progressBars[0].style.background = "#0C84D9";
	} else if (firstChildClass == "delivery") {
		dots[1].style.background = "#0C84D9";
		dots[1].style.color = "white";
		progressBars[0].style.background = "#0C84D9";
		dots[2].style.background = "#0C84D9";
		dots[2].style.color = "white";
		progressBars[1].style.background = "#0C84D9";
	} else if (firstChildClass == "delivered") {
		dots[1].style.background = "#0C84D9";
		dots[1].style.color = "white";
		progressBars[0].style.background = "#0C84D9";
		dots[2].style.background = "#0C84D9";
		dots[2].style.color = "white";
		progressBars[1].style.background = "#0C84D9";
		dots[3].style.background = "#0C84D9";
		dots[3].style.color = "white";
		progressBars[2].style.background = "#0C84D9";
	}

}





