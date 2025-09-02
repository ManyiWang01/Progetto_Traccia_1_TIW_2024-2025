(function() {
	document.getElementById("loginButton").addEventListener('click', function(e) {
		document.getElementById("errorMessage").textContent = "";
		let form = e.target.closest("form");
		if (form.checkValidity()) {
			e.preventDefault();
			makeCall("POST", 'Login', form, 
				function(x) {
					if (x.readyState == XMLHttpRequest.DONE) {
						var message = x.responseText;
						if (x.status === 200) { 
							sessionStorage.setItem('userData', message);
							window.location.href = "Home.html";
						}
						else {
							document.getElementById("errorMessage").textContent = message;
						}
					}
				}
			);
		}
		else {
			form.reportValidity();
		}
	});
})();