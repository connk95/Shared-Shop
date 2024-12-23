
// rangeinput要素を探す
const rangeInput = document.querySelectorAll(".range-input input"),
	priceInput = document.querySelectorAll(".price-input input"),
	range = document.querySelector(".slider .progress");

// 検索値段の格差
let priceGap = 1000;

// 画面表示時のスタイル調整
window.onload = function() {
	let minPrice = parseInt(priceInput[0].value),
		maxPrice = parseInt(priceInput[1].value);
	if(maxPrice > 10000){
		 maxPrice = 10000;
	}
	rangeInput[0].value = minPrice;
	range.style.left = (minPrice / rangeInput[0].max) * 100 + "%";
	rangeInput[1].value = maxPrice;
	range.style.right = 100 - (maxPrice / rangeInput[1].max) * 100 + "%";
}

// 検索値段を設定
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

// 2つ目のスライダーを変更に合わせて調整
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


// 配達情報要素を探す
const trackingArea = document.getElementById("tracking-area");

// 配達情報要素が存在する場合は、配達情報を表示する
if (trackingArea) {
	let firstChild = trackingArea.firstElementChild;
	let firstChildClass = firstChild.className;

	const dots = document.getElementsByClassName("dot");
	const progressBars = document.getElementsByClassName("progress-bar");

	// 発送済み場合は、2つ目のノードを表示
	if (firstChildClass == "shipped") {
		dots[1].style.background = "#0C84D9";
		dots[1].style.color = "white";
		progressBars[0].style.background = "#0C84D9";

		// 発送済み場合は、2, 3つ目のノードを表示
	} else if (firstChildClass == "delivery") {
		dots[1].style.background = "#0C84D9";
		dots[1].style.color = "white";
		progressBars[0].style.background = "#0C84D9";
		dots[2].style.background = "#0C84D9";
		dots[2].style.color = "white";
		progressBars[1].style.background = "#0C84D9";

		// 発送済み場合は、2, 3, 4つ目のノードを表示
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





