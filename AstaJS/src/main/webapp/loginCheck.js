(function() {
	document.getElementById("loginButton").addEventListener('click', function(e) {
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
						else if (x.status === 401) {
							window.location.href = x.responseText;
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