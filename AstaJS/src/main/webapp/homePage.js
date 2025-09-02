{
	let vendoUrl = document.getElementById("Vendo");
	let acquistoUrl = document.getElementById("Acquisto");
	let logoutUrl = document.getElementById("logout");
	let vendoPage = new VendoPage(document.getElementById("page"));
	let acquistoPage = new AcquistoPage(document.getElementById("page"));
	let oAuction;
	let cAuction;
	let createAuctionForm;
	
	window.addEventListener("load", function() {
		let userData = JSON.parse(sessionStorage.getItem('userData'));
		let lastAction = userData.lastAction;
		let lastVisited = userData.lastVisited;
		vendoUrl.addEventListener('click', (e) => {
			e.preventDefault();
			document.getElementById("errorMessage").textContent = "";
			vendoPage.show();
		});
		acquistoUrl.addEventListener('click', (e) => {
			e.preventDefault();
			document.getElementById("errorMessage").textContent = "";
			acquistoPage.show(lastVisited, lastAction);
		});
		logoutUrl.addEventListener('click', (e) => {
			e.preventDefault();
			document.getElementById("errorMessage").textContent = "";
			makeCall("GET", "Logout", null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					sessionStorage.removeItem('userData');
					window.location.href = x.responseText;
				}
			});
		});
		
		if (lastAction === undefined || lastAction === "Acquisto" || lastAction === "new") {
			acquistoUrl.click();
		}
		else {
			vendoUrl.click();
		}
	  }, false);
	 
	  
  	function VendoPage(page) {
  		this.page = page;
  		
  		this.show = function() {
  			let self = this;
  			
  			this.page.innerHTML = "";
  			makeCall("GET", 'Vendo', null, 
  				function(x) {
  					if (x.readyState == XMLHttpRequest.DONE) {
  						if (x.status === 200) {
  							var datiNeccessari = JSON.parse(x.responseText);
  							var openAuctionList = datiNeccessari.openAuctionList;
  							var closedAuctionList = datiNeccessari.closedAuctionList;
  							var remainingTimeMap = datiNeccessari.remainingTimeMap;
  							var articleMap = datiNeccessari.articleMap;
  							var offerMap = datiNeccessari.offerMap;
  							var freeArticleList = datiNeccessari.freeArticleList;
  							let auctionTableRow = document.createElement("tr");
  							let formRow = document.createElement("tr");
  							let openAuctionTable = document.createElement("td");
  							openAuctionTable.id = "openAuction";
  							let closedAuctionTable = document.createElement("td");
  							closedAuctionTable.id = "closedAuction";
  							let addArticleFormCell = document.createElement("td");
  							addArticleFormCell.id = "addArticleFormCell";
  							let createAuctionFormCell = document.createElement("td");
  							createAuctionFormCell.id = "createAuctionFormCell";
  							auctionTableRow.appendChild(openAuctionTable);
  							auctionTableRow.appendChild(closedAuctionTable);
  							formRow.appendChild(addArticleFormCell);
  							formRow.appendChild(createAuctionFormCell);
  							self.page.appendChild(auctionTableRow);
  							self.page.appendChild(formRow);
  							oAuction = new AuctionList(openAuctionTable, "aperta");
  							oAuction.show(openAuctionList, remainingTimeMap, articleMap, offerMap);
							cAuction = new AuctionList(closedAuctionTable, "chiusa");
  							cAuction.show(closedAuctionList, remainingTimeMap, articleMap, offerMap);
							let addArticleForm = new AddArticleForm(addArticleFormCell);
							addArticleForm.show();
							createAuctionForm = new CreateAuctionForm(createAuctionFormCell);
							createAuctionForm.show(freeArticleList);
  						}
						else if (x.status === 401) {
							window.location.href = x.responseText;
						}
  						else {
  							document.getElementById("errorMessage").textContent = x.responseText;
  						}
  					}
  			});
  		}
  	}
	  	
  	function AuctionList(table, mode) {
  		this.table = table;
		this.mode = mode;
  		
  		this.show = function(auctionList, remainingTime, articleMap, offerMap) {
			this.table.innerHTML = "";
  			let row, id, offer, time, anchor, articleTable, articleList, articleCell, detailRow;
  			let self = this;
			
			self.createTable(self.table, self.mode, auctionList);
  			auctionList.forEach(function(auction) {
				let id_auction = auction.id_asta;
  				row = document.createElement("tr");
				id = document.createElement("td");
				offer = document.createElement("td");
				time = document.createElement("td");
				articleCell = document.createElement("td");
				anchor = document.createElement("a");
				detailRow = document.createElement("tr");
				detailRow.id = "detail_" + id_auction;
				anchor.href = "#";
				anchor.setAttribute("id_asta", id_auction);
				anchor.appendChild(document.createTextNode(id_auction));
				if (offerMap[id_auction] === null || offerMap[id_auction] === undefined) {
					offer.textContent = "Non ci sono offerte";
				}
				else {
					offer.textContent = offerMap[id_auction] + "(EUR)";
				}
				time.textContent = remainingTime[id_auction];
				anchor.addEventListener('click', (e) => {
					e.preventDefault();
					let id = e.target.closest("a").getAttribute("id_asta");
					let path = "DettaglioAsta?id=" + id;
					document.getElementById("errorMessage").textContent = "";
					makeCall("GET", path, null, function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								let dettaglio = JSON.parse(x.responseText);
								let row = document.getElementById("detail_" + id);
								let dettaglioRow = new DettaglioAsta(row);
								dettaglioRow.show(dettaglio);
							}
							else if (x.status === 401) {
								window.location.href = x.responseText;
							}
							else {
								document.getElementById("errorMessage").textContent = x.responseText;
							}
						}
					});
				});
				id.appendChild(anchor);
				row.appendChild(id);
				row.appendChild(offer);
				row.appendChild(time);
				articleTable = document.createElement("table");
				articleCell.appendChild(articleTable);
				articleList = new ArticleList(articleTable);
				articleList.show(articleMap[id_auction]);
				row.appendChild(articleCell);
				self.table.appendChild(row);
				self.table.appendChild(detailRow);
  			});
  		};
		this.createTable = function(table, mode, auctionList) {
			let header4, tHead, rowHead, idHead, offerHead, timeHead;
			if (auctionList === null || auctionList.length === 0) {
				return;
			}
			header4 = document.createElement("h4");
			if (mode === "aperta") header4.textContent = "Asta Aperte:";
			else if (mode === "chiusa") header4.textContent = "Asta Chiuse:";
			header4.style = "color : red;"
			table.appendChild(header4);
  			tHead = document.createElement("thead");
  			rowHead = document.createElement("tr");
  			idHead = document.createElement("th");
  			idHead.textContent = "ID Asta";
  			rowHead.appendChild(idHead);
			offerHead = document.createElement("th");
  			offerHead.textContent = "Offerta Massima";
  			rowHead.appendChild(offerHead);
			timeHead = document.createElement("th");
  			timeHead.textContent = "Tempo Rimanente";
  			rowHead.appendChild(timeHead);
  			tHead.appendChild(rowHead);
			table.appendChild(tHead);
		}
  	}
		
	function ArticleList(articleTable) {
		this.articleTable = articleTable;
		let self = this;
		
		this.show = function(articleList) {
			self.createTable(self.articleTable);
			articleList.forEach(function(article) {
				let row, id, name;
				row = document.createElement("tr");
				id = document.createElement("td");
				name = document.createElement("td");
				id.textContent = article.id_articolo;
				name.textContent = article.nome_articolo;
				row.appendChild(id);
				row.appendChild(name);
				self.articleTable.appendChild(row);
			});
		}
		this.createTable = function(table) {
			let tHead, rowHead, idHead, nameHead;
  			tHead = document.createElement("thead");
  			rowHead = document.createElement("tr");
  			idHead = document.createElement("th");
  			idHead.textContent = "ID Articolo";
  			rowHead.appendChild(idHead);
			nameHead = document.createElement("th");
  			nameHead.textContent = "Nome Articolo";
  			rowHead.appendChild(nameHead);
  			tHead.appendChild(rowHead);
			table.appendChild(tHead);
		}
	}
	
	function DettaglioAsta(dettaglioRow) {
		this.dettaglioRow = dettaglioRow;
		
		this.resetAll = function() {
			let detailElements = document.querySelectorAll('[id^="detail_"]');
			document.getElementById("errorMessage").textContent = "";
			detailElements.forEach(function(detail) {
				detail.innerHTML = "";
			});
		}
		this.show = function(dettaglio) {
			this.resetAll();
			this.createAstaInfoTable(dettaglio.asta);
			this.createOfferteInfoTable(dettaglio);
			let actionButton;
			if (dettaglio.asta.status === false) {
				actionButton = document.createElement("button");
				actionButton.textContent = "Chiudi";
				actionButton.addEventListener('click', function(e) {
					document.getElementById("errorMessage").textContent = "";
					e.preventDefault();
					makeCall("GET", "EndAuction?id=" + dettaglio.asta.id_asta, null , (x) => {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								self.update();
							}
							else if (x.status === 401) {
								window.location.href = x.responseText;
							}
							else {
								document.getElementById("errorMessage").textContent = x.responseText;
							}
						}
					});
				});
				this.dettaglioRow.appendChild(actionButton);
			}
			let self = this;
			let closeButton = document.createElement("button");
			closeButton.textContent = "Back";
			closeButton.addEventListener('click', function(e) {
				e.preventDefault();
				self.resetAll();
			});
			
			this.dettaglioRow.appendChild(closeButton);
		}
		this.createAstaInfoTable = function(auction) {
			let table, tHead, tRow, pInizialeHead, rialzoMinimoHead, dataScadenzaHead, header4, h4td, tabletd;
			table = document.createElement("table");
			header4 = document.createElement("h4");
			tHead = document.createElement("thead");
			tRow = document.createElement("tr");
			pInizialeHead = document.createElement("th");
			rialzoMinimoHead = document.createElement("th");
			dataScadenzaHead = document.createElement("th");
			h4td = document.createElement("td");
			tabletd = document.createElement("td");

			header4.style = "color : red";
			header4.textContent = "Dettaglio Asta:";
			pInizialeHead.textContent = "Prezzo Iniziale";
			rialzoMinimoHead.textContent = "Rialzo Minimo";
			dataScadenzaHead.textContent = "Data Scadenza";
			
			tRow.appendChild(pInizialeHead);
			tRow.appendChild(rialzoMinimoHead);
			tRow.appendChild(dataScadenzaHead);
			tHead.appendChild(tRow);
			table.appendChild(tHead);
			tabletd.appendChild(table);
			h4td.appendChild(header4);
			this.dettaglioRow.appendChild(h4td);
			this.dettaglioRow.appendChild(tabletd);
			
			let auctionInfo = new AuctionInfo(table);
			auctionInfo.show(auction);
		}
		this.createOfferteInfoTable = function(dettaglio) {
			let tabletd, table, tRow;
			let winner = dettaglio.winner;
			let offertaMassima = dettaglio.offertaMassima;
			let offerteList = dettaglio.offerte;
			let errorMessage = "Non ci sono offerte";
			
			if (dettaglio.status === "aperta") {
				if (offerteList === null || offerteList.length === 0) {
					document.getElementById("errorMessage").textContent = errorMessage;
					return;
				}
				
			}
			else if (dettaglio.status === "chiusa") {
				if (offertaMassima === undefined || offertaMassima === null) {
					document.getElementById("errorMessage").textContent = errorMessage;
					return;
				}	
			}
			
			tabletd = document.createElement("td");
			table = document.createElement("table");
			tRow = document.createElement("tr");
			table.appendChild(tRow);
			tabletd.appendChild(table);
			if (auction.status === false) {
				let usernameHead = document.createElement("th");
				let offertaHead = document.createElement("th");
				let dateHead = document.createElement("th");
				usernameHead.textContent = "Nome Utente";
				offertaHead.textContent = "Offerta";
				dateHead.textContent = "Data Offerta";
				tRow.appendChild(usernameHead);
				tRow.appendChild(offertaHead);
				tRow.appendChild(dateHead);
				let openAuctionInfo = new OpenAuctionInfo(table);
				openAuctionInfo.show(offerteList);
			}
			else if (winner !== undefined){
				let winnerHead = document.createElement("th");
				let finalPriceHead = document.createElement("th");
				let addressHead = document.createElement("th");
				winnerHead.textContent = "Winner";
				finalPriceHead.textContent = "Prezzo Finale";
				addressHead.textContent = "Indirizzo Consegna";
				tRow.appendChild(winnerHead);
				tRow.appendChild(finalPriceHead);
				tRow.appendChild(addressHead);
				let closedAuctionInfo = new ClosedAuctionInfo(table);
				closedAuctionInfo.show(winner, offertaMassima);
			}
			
			this.dettaglioRow.appendChild(tabletd);
		}

		this.update = function() {
			makeCall("GET", 'Vendo', null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status === 200) {
						var datiNeccessari = JSON.parse(x.responseText);
						var openAuctionList = datiNeccessari.openAuctionList;
						var closedAuctionList = datiNeccessari.closedAuctionList;
						var remainingTimeMap = datiNeccessari.remainingTimeMap;
						var articleMap = datiNeccessari.articleMap;
						var offerMap = datiNeccessari.offerMap;
						oAuction.show(openAuctionList, remainingTimeMap, articleMap, offerMap);
						cAuction.show(closedAuctionList, remainingTimeMap, articleMap, offerMap);
					}
					else if (x.status === 401) {
						window.location.href = x.responseText;
					}
					else {
						document.getElementById("errorMessage").textContent = x.responseText;
					}
				}
			});
		}
	}
	
	function OpenAuctionInfo(table) {
		this.table = table;
		
		this.show = function(offerteList) {
			let row, username, offer, date;
			let self = this;
			offerteList.forEach(function(offerta) {
				row = document.createElement("tr");
				username = document.createElement("td");
				offer = document.createElement("td");
				date = document.createElement("td");
				username.textContent = offerta.user;
				offer.textContent = offerta.p_offerta;
				date.textContent = offerta.data_offerta;
				
				row.appendChild(username);
				row.appendChild(offer);
				row.appendChild(date);
				self.table.appendChild(row);
			});
		}
	}
	
	function ClosedAuctionInfo(table) {
		this.table = table;
		
		this.show = function(winner, offertaMassima) {
			let row, nome, offer, address;
			let self = this;
			row = document.createElement("tr");
			nome = document.createElement("td");
			offer = document.createElement("td");
			address = document.createElement("td");
			nome.textContent = winner.cognome + " " + winner.nome;
			offer.textContent = offertaMassima.p_offerta + "(EUR)";
			address.textContent = winner.indirizzo;
			
			row.appendChild(nome);
			row.appendChild(offer);
			row.appendChild(address);
			self.table.appendChild(row);
		}
	}
	
	function AuctionInfo(table) {
		this.table = table;
		
		this.show = function(auction) {
			let pIniziale, rialzoMinimo, dataScadenza;
			let row, pInizialeCell, rialzoMinimoCell, dataScadenzaCell;
			pIniziale = auction.p_iniziale;
			rialzoMinimo = auction.min_rialzo;
			dataScadenza = auction.data_scadenza;
			
			row = document.createElement("tr");
			pInizialeCell = document.createElement("td");
			rialzoMinimoCell = document.createElement("td");
			dataScadenzaCell = document.createElement("td");
			pInizialeCell.textContent = pIniziale + "(EUR)";
			rialzoMinimoCell.textContent = rialzoMinimo + "(EUR)";
			dataScadenzaCell.textContent = dataScadenza;
			row.appendChild(pInizialeCell);
			row.appendChild(rialzoMinimoCell);
			row.appendChild(dataScadenzaCell);
			this.table.appendChild(row);
		}
	}
	
	function AddArticleForm(cell) {
		this.cell = cell;
		
		this.show = function() {
			let form;
			let h4, nomeArticolo, immagine, prezzo, descrizione, button;
			let nomeArticoloLabel, immagineLabel, descrizioneLabel, prezzoLabel, buttonLabel;
			let self = this;
			
			form = document.createElement("form");
			
			h4 = document.createElement("h4");
			nomeArticolo = document.createElement("input");
			nomeArticoloLabel = document.createElement("p");
			immagine = document.createElement("input");
			immagineLabel = document.createElement("p");
			prezzo = document.createElement("input");
			prezzoLabel = document.createElement("p");
			descrizione = document.createElement("textarea");
			descrizioneLabel = document.createElement("p");
			button = document.createElement("button");
			buttonLabel = document.createElement("p");
			
			h4.textContent = "Aggiungi Articolo:";
			h4.style = "color : red;"
			nomeArticoloLabel.textContent = "Nome Articolo:";
			immagineLabel.textContent = "Immagine(URL):";
			prezzoLabel.textContent = "Prezzo(EUR):";
			descrizioneLabel.textContent = "Descrizione:";
			button.textContent = "Aggiungi";
			
			nomeArticolo.required = 'true';
			immagine.required = 'true';
			prezzo.required = 'true';
			descrizione.required = 'true';
			
			nomeArticolo.type = "text";
			prezzo.type = "number";
			prezzo.step = "0.05";
			prezzo.min = "0.05";
			immagine.type = "text";
			descrizione.rows = 4;
			descrizione.cols = 30;
			
			nomeArticolo.name = "nome_articolo";
			immagine.name = "url_immagine";
			prezzo.name = "prezzo";
			descrizione.name = "descrizione";
			
			this.cell.appendChild(h4);
			nomeArticoloLabel.appendChild(nomeArticolo);
			form.appendChild(nomeArticoloLabel);
			immagineLabel.appendChild(immagine);
			form.appendChild(immagineLabel);
			prezzoLabel.appendChild(prezzo);
			form.appendChild(prezzoLabel);
			form.appendChild(descrizioneLabel);
			form.appendChild(descrizione);
			button.addEventListener('click', function(e) {
				document.getElementById("errorMessage").textContent = "";
				e.preventDefault();
				let form = e.target.closest("form");
				if (form.checkValidity()) {
					makeCall("POST", "AddArticle", form, (x) => {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								self.update();
							}
						}
						else if (x.status === 401) {
							window.location.href = x.responseText;
						}
						else {
							document.getElementById("errorMessage").textContent = x.responseText;
						}
					});
				}
				else {
					form.reportValidity();
				}
			});
			buttonLabel.appendChild(button);
			form.appendChild(buttonLabel);
			this.cell.appendChild(form);
		}
		this.update = function() {
			makeCall("GET", 'Vendo', null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status === 200) {
						var datiNeccessari = JSON.parse(x.responseText);
						var freeArticleList = datiNeccessari.freeArticleList;
						createAuctionForm.show(freeArticleList);
					}
					else if (x.status === 401) {
						window.location.href = x.responseText;
					}
					else {
						document.getElementById("errorMessage").textContent = x.responseText;
					}
				}
			});
		}
	}
	
	function CreateAuctionForm(cell) {
		this.cell = cell;
		
		this.show = function(freeArticle) {
			this.cell.innerHTML = "";
			let form;
			let h4, articoliTable, articoliP, p, checkBoxCell, row, nomeArticoloCell, prezzoCell;
			
			form = document.createElement("form");
			h4 = document.createElement("h4");
			articoliP = document.createElement("p");
			
			h4.textContent = "Crea Asta:";
			h4.style = "color : red;"
			this.cell.appendChild(h4);
			articoliP.textContent = "Seleziona Articoli:";
			this.cell.appendChild(articoliP);
			if (freeArticle === null || freeArticle === undefined || freeArticle.length === 0) {
				p = document.createElement("p");
				p.textContent = "Non ci sono articoli liberi";
				p.style = "color : red;";
				this.cell.appendChild(p);
			}
			else {
				articoliTable = document.createElement("table");
				freeArticle.forEach(function(article) {
					row = document.createElement("tr");
					checkBoxCell = document.createElement("td");
					nomeArticoloCell = document.createElement("td");
					prezzoCell = document.createElement("td");
					checkBox = document.createElement("input");
					checkBox.type = "checkBox";
					checkBox.name = "selectedElements[]";
					checkBox.value = article.id_articolo;
					nomeArticoloCell.textContent = article.nome_articolo;
					prezzoCell.textContent = article.prezzo + "(EUR)";
					checkBoxCell.appendChild(checkBox);
					
					row.appendChild(checkBoxCell);
					row.appendChild(nomeArticoloCell);
					row.appendChild(prezzoCell);
					articoliTable.appendChild(row);
				});
				form.appendChild(articoliTable);
			}
			let rialzoMinimoP, rialzoMinimo, scadenzaP, scadenza, buttonP, button;
			rialzoMinimoP = document.createElement("p");
			scadenzaP = document.createElement("p");
			buttonP = document.createElement("p");
			rialzoMinimo = document.createElement("input");
			scadenza = document.createElement("input");
			button = document.createElement("button");
			rialzoMinimo.type = "number";
			rialzoMinimo.step = "0.5";
			rialzoMinimo.min = "1";
			rialzoMinimo.required = 'true';
			rialzoMinimo.name = "rialzo_minimo";
			scadenza.type = "datetime-local";
			scadenza.name = "date";
			scadenza.required = 'true';
			let now = new Date(Date.now() + (2 * 60 * 60 * 1000));
			now = now.toISOString().slice(0, 16);
			scadenza.min = now;
			scadenza.value = now;
			button.textContent = "Crea";
			button.addEventListener('click', function(e) {
				e.preventDefault();
				document.getElementById("errorMessage").textContent = "";
				let currentForm = e.target.closest("form");
				if (currentForm.checkValidity()) {
					makeCall("POST", "CreateAuction", currentForm, function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								vendoUrl.click();
							}
							else if (x.status === 401) {
								window.location.href = x.responseText;
							}
							else {
								document.getElementById("errorMessage").textContent = x.responseText;
							}
						}
					});
				}
				else {
					currentForm.reportValidity();
				}
			})
			
			rialzoMinimoP.textContent = "Rialzo Minimo(EUR):";
			rialzoMinimoP.appendChild(rialzoMinimo);
			scadenzaP.textContent = "Scadenza:";
			scadenzaP.appendChild(scadenza);
			buttonP.appendChild(button);
						
			form.appendChild(rialzoMinimoP);
			form.appendChild(scadenzaP);
			form.appendChild(buttonP);
			
			this.cell.appendChild(form);
		}
	}
	
	function AcquistoPage(page) {
		this.page = page;
		
		this.show = function(lastVisited, lastAction) {
			this.page.innerHTML = "";
			let self = this;
			
			makeCall("GET", "Acquisto", null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status === 200) {
						let winnedAuction = JSON.parse(x.responseText);
						let h4, tRowSearchBar, searchBarCell;
						let form, searchBar, searchBarP, button;
						h4 = document.createElement("h4");
						tRowSearchBar = document.createElement("tr");
						searchBarCell = document.createElement("td");
						
						form = document.createElement("form");
						searchBarP = document.createElement("p");
						searchBar = document.createElement("input");
						button = document.createElement("button");
						
						h4.textContent = "Ricerca per parola chiave:";
						h4.style = "color : red;";
						searchBar.type = "text";
						searchBar.required = "true";
						searchBar.name = "searchBar";
						button.textContent = "Cerca";
						button.addEventListener('click', function(e) {
							e.preventDefault();
							document.getElementById("errorMessage").textContent = "";
							let foundedTable = document.getElementById("foundAuctionTable");
							if (foundedTable !== null) {
								foundedTable.innerHTML = "";
							}
							makeCall("POST", "FindAuctionByKey", form, function(x) {
								if (x.readyState == XMLHttpRequest.DONE) {
									if (x.status === 200) {
										let foundAuctionList = JSON.parse(x.responseText);
										let foundAuction = new FoundAuction(self.page);
										foundAuction.show(foundAuctionList.openAuctionList);
									}
									else if (x.status === 401) {
										window.location.href = x.responseText;
									}
									else {
										document.getElementById("errorMessage").textContent = x.responseText;
									}
								}
							})
						});
						
						searchBarP.appendChild(searchBar);
						searchBarP.appendChild(button);
						form.appendChild(searchBarP);
						
						searchBarCell.appendChild(h4);
						searchBarCell.appendChild(form);
						tRowSearchBar.appendChild(searchBarCell);
						self.page.appendChild(tRowSearchBar);
						
						let foundAuctionTable = document.createElement("table");
						foundAuctionTable.id = "foundAuctionTable";
						this.page.appendChild(foundAuctionTable);
						
						let winnedAuctionRow = document.createElement("table");
						winnedAuctionRow.id = "winnedAuctionTable";
						self.page.appendChild(winnedAuctionRow);
						
						let winnedAuctionPage = new WinnedAuction(winnedAuctionRow);
						winnedAuctionPage.show(winnedAuction);
						
						let lastVisitedRow = document.createElement("table");
						lastVisitedRow.id = "lastVisitedAuctionTable";
						self.page.appendChild(lastVisitedRow);

						if (lastAction === "Acquisto") {
							let visitedAuctionRow = document.createElement("table");
							visitedAuctionRow.id = "visitedAuctionTable";
							self.page.appendChild(visitedAuctionRow);

							let lastVisitedTable = new LastVisited(visitedAuctionRow);
							lastVisitedTable.show(lastVisited);
						}
					}
					else if (x.status === 401) {
						window.location.href = x.responseText;
					}
					else {
						document.getElementById("errorMessage").textContent = x.responseText;
					}
				}
			});
		}
	}
	
	function FoundAuction() {
		
		this.show = function(foundList) {
			let h4, foundIDHead, foundPInizialeHead, foundRialzoMinimoHead, foundScadenzaHead;
			let row, foundID, foundPIniziale, foundRialzoMinimo, foundScadenza, anchor; 
			let tRowOfferta, tRowArticoli, tRowOfferForm;
			let foundAuctionTable = document.getElementById("foundAuctionTable");
			let foundAuctionRow = document.createElement("tr");
			
			h4 = document.createElement("h4");
			h4.textContent = "Risultati Ricerca:";
			h4.style = "color : red;";
			foundIDHead = document.createElement("th");
			foundIDHead.textContent = "ID Asta";
			foundPInizialeHead = document.createElement("th");
			foundPInizialeHead.textContent = "Prezzo Iniziale";
			foundRialzoMinimoHead = document.createElement("th");
			foundRialzoMinimoHead.textContent = "Rialzo Minimo";
			foundScadenzaHead = document.createElement("th");
			foundScadenzaHead.textContent = "Data Scadenza";

			foundAuctionRow.appendChild(foundIDHead);
			foundAuctionRow.appendChild(foundPInizialeHead);
			foundAuctionRow.appendChild(foundRialzoMinimoHead);
			foundAuctionRow.appendChild(foundScadenzaHead);
			foundAuctionTable.appendChild(h4);
			foundAuctionTable.appendChild(foundAuctionRow);
			
			foundList.forEach(function(auction) {
				row = document.createElement("tr");
				foundID = document.createElement("td");
				foundPIniziale = document.createElement("td");
				foundRialzoMinimo = document.createElement("td");
				foundScadenza = document.createElement("td");
				foundPIniziale.textContent = auction.p_iniziale + "(EUR)";
				foundRialzoMinimo.textContent = auction.min_rialzo + "(EUR)";
				foundScadenza.textContent = auction.data_scadenza;
				anchor = document.createElement("a");
				anchor.textContent = auction.id_asta;
				anchor.href = "#";
				anchor.addEventListener('click', function(e) {
					e.preventDefault();
					document.getElementById("errorMessage").textContent = "";
					let openOfferPage = document.querySelectorAll('[id^="offerta_"]');
					openOfferPage.forEach(function(page) {
						page.innerHTML = "";
					});
					let openArticleList = document.querySelectorAll('[id^="articleList_"]');
					openArticleList.forEach(function(list) {
						list.innerHTML = "";
					});
					let openOfferFormList = document.querySelectorAll('[id^="form_"]');
					openOfferFormList.forEach(function(form) {
						form.innerHTML = "";
					});
					makeCall("GET", "Offerta?id=" + auction.id_asta, null, function(x) {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								let offerData = JSON.parse(x.responseText);
								let articleList = new AcquistoArticleList(tRowArticoli);
								let offerPage = new OfferPage(tRowOfferta);
								let offerForm = new OfferForm(tRowOfferForm, offerPage);
								articleList.show(offerData.articleList);
								offerPage.show(offerData.offerList);
								offerForm.show(offerData.maxOffer, offerData.auction);
							}
							else if (x.status === 401) {
								window.location.href = x.responseText;
							}
							else {
								document.getElementById("errorMessage").textContent = x.responseText;
							}
						}
					});
				});
				foundID.appendChild(anchor);
				row.appendChild(foundID);
				row.appendChild(foundPIniziale);
				row.appendChild(foundRialzoMinimo);
				row.appendChild(foundScadenza);
				foundAuctionTable.appendChild(row);
				
				tRowArticoli = document.createElement("tbody"); 
				tRowArticoli.id = "articleList_" + auction.id_asta;
				foundAuctionTable.appendChild(tRowArticoli);
				tRowOfferta = document.createElement("tbody"); 
				tRowOfferta.id = "offerta_" + auction.id_asta;
				foundAuctionTable.appendChild(tRowOfferta);
				tRowOfferForm = document.createElement("tbody"); 
				tRowOfferForm.id = "form_" + auction.id_asta;
				foundAuctionTable.appendChild(tRowOfferForm);
			});
		}
	}
	
	function AcquistoArticleList(tbody) {
		this.tbody = tbody;
		
		this.show = function(articleList) {
			let self = this;
			let h4, articleIDHead, articleNameHead;
			let row, articleID, articleName;
			let articleListRow = document.createElement("tr"); 
			
			h4 = document.createElement("h4");
			h4.textContent = "Lista Articoli:";
			h4.style = "color : red;";
			articleIDHead = document.createElement("th");
			articleIDHead.textContent = "ID Articolo";
			articleNameHead = document.createElement("th");
			articleNameHead.textContent = "Nome Articolo";
			
			articleListRow.appendChild(articleIDHead);
			articleListRow.appendChild(articleNameHead);
			this.tbody.appendChild(h4);
			this.tbody.appendChild(articleListRow);
			
			articleList.forEach(function(article) {
				row = document.createElement("tr"); 
				articleID = document.createElement("td");
				articleID.textContent = article.id_articolo;
				articleName = document.createElement("td");
				articleName.textContent = article.nome_articolo;
				
				row.appendChild(articleID);
				row.appendChild(articleName);
				self.tbody.appendChild(row);
			});
		}
	}
	
	function OfferPage(tbody) {
		this.tbody = tbody;

		this.show = function(offerList) {
			let h4, tRowHead, usernameHead, offerHead, dataHead;
			let row, username, price, data;
			let self = this;
			
			h4 = document.createElement("h4");
			h4.textContent = "Lista Offerte:";
			h4.style = "color : red;";
			this.tbody.appendChild(h4);
			if (offerList.length === 0) {
				let p = document.createElement("p");
				p.textContent = "Non ci sono offerte al momento";
				this.tbody.appendChild(p);
			}
			else {
				tRowHead = document.createElement("tr");
				usernameHead = document.createElement("th");
				usernameHead.textContent = "Nome Utente";
				offerHead = document.createElement("th");
				offerHead.textContent = "Offerta";
				dataHead = document.createElement("th");
				dataHead.textContent = "Data Offerta";
				
				tRowHead.appendChild(usernameHead);
				tRowHead.appendChild(offerHead);
				tRowHead.appendChild(dataHead);
				this.tbody.appendChild(tRowHead);
				
				offerList.forEach(function(offer) {
					row = document.createElement("tr"); 
					username = document.createElement("td");
					username.textContent = offer.user;
					price = document.createElement("td");
					price.textContent = offer.p_offerta + "(EUR)";
					data = document.createElement("td");
					data.textContent = offer.data_offerta;
					
					row.appendChild(username);
					row.appendChild(price);
					row.appendChild(data);
					self.tbody.appendChild(row);
				});
			}
		}
		this.reset = function() {
			let openOfferPage = document.querySelectorAll('[id^="offerta_"]');
			openOfferPage.forEach(function(page) {
				page.innerHTML = "";
			});
			let openOfferForm = document.querySelectorAll('[id^="form_"]');
			openOfferForm.forEach(function(form) {
				form.innerHTML = "";
			});
		}
		this.update = function(id, form) {
			let self = this;
			self.reset();
			document.getElementById("errorMessage").textContent = "";
			makeCall("GET", "Offerta?id=" + id, null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status === 200) {
						let offerData = JSON.parse(x.responseText);
						self.show(offerData.offerList);
						form.show(offerData.maxOffer, offerData.auction);
					}
					else if (x.status === 401) {
						window.location.href = x.responseText;
					}
					else {
						document.getElementById("errorMessage").textContent = x.responseText;
					}
				}
			});
		}
	}

	function OfferForm(tbody, offerPage) {
		this.tbody = tbody;
		this.offerPage = offerPage;
		
		this.show = function(maxOffer, auction) {
			let form = document.createElement("form");
			let row, formTD;
			let input, p, actionButton, closeButton;
			let self = this;
			
			p = document.createElement("p");
			p.textContent = "Fai la tua offerta(EUR):";
			row = document.createElement("tr"); 
			formTD = document.createElement("td");
			
			input = document.createElement("input");
			input.required = "true";
			input.type = "number";
			input.step = "1";
			if (maxOffer === undefined) {
				input.min = auction.min_rialzo;
			}
			else {
				input.min = maxOffer.p_offerta + auction.min_rialzo;
			}
			input.value = input.min;
			input.name = "prezzo";
			actionButton = document.createElement("button");
			actionButton.textContent = "Offri";
			actionButton.addEventListener('click', function(e) {
				document.getElementById("errorMessage").textContent = "";
				e.preventDefault();
				if (form.checkValidity()) {
					makeCall("POST", "MakeAnOffer", form , (x) => {
						if (x.readyState == XMLHttpRequest.DONE) {
							if (x.status === 200) {
								let id = x.responseText;
								self.offerPage.update(id, self);
							}
							else if (x.status === 401) {
								window.location.href = x.responseText;
							}
							else {
								document.getElementById("errorMessage").textContent = x.responseText;
								let openOfferFormList = document.querySelectorAll('[id^="form_"]');
								openOfferFormList.forEach(function(form) {
									form.innerHTML = "";
								});
								self.show(maxOffer, auction);
							}
						}
					});
				}
				else {
					form.reportValidity();
				}
			});
			closeButton = document.createElement("button");
			closeButton.textContent = "Back";
			closeButton.addEventListener('click', function(e) {
				e.preventDefault();
				self.reset();
			});
			
			form.appendChild(input);
			form.appendChild(actionButton);
			form.appendChild(closeButton);
			formTD.appendChild(form);
			row.appendChild(formTD);
			this.tbody.appendChild(p);
			this.tbody.appendChild(row);
		}
		this.reset = function() {
			let openOfferPage = document.querySelectorAll('[id^="offerta_"]');
			openOfferPage.forEach(function(page) {
				page.innerHTML = "";
			});
			let openArticleList = document.querySelectorAll('[id^="articleList_"]');
			openArticleList.forEach(function(list) {
				list.innerHTML = "";
			});
			let openOfferFormList = document.querySelectorAll('[id^="form_"]');
			openOfferFormList.forEach(function(form) {
				form.innerHTML = "";
			});
		}
	}
	
	function WinnedAuction(page) {
		this.page = page;
		
		this.show = function(winnedAuction) {
			let h4 = document.createElement("h4");
			h4.textContent = "Aste Vinte:";
			h4.style = "color : red;";
			this.page.appendChild(h4);
			if (winnedAuction.winnedAuction === undefined) {
				let p = document.createElement("p");
				p.textContent = "Non hai vinto alcun'asta";
				this.page.appendChild(p);
				return;
			}
			
			let tHeadRow, auctionIDHead, finalOfferHead;
			let auctionRow, auctionID, finalOffer, articleList;
			let articleListTable, articleRowHead, articleIDHead, articleNameHead;
			let articleRow, articleID, articleName;
			let winnedAuctionList = winnedAuction.winnedAuction;
			let winnedArticleMap = winnedAuction.winnedArticle;
			let maxOfferMap = winnedAuction.maxOffer;
			let self = this;
			tHeadRow = document.createElement("tr");
			
			auctionIDHead = document.createElement("th");
			auctionIDHead.textContent = "ID Asta";
			finalOfferHead = document.createElement("th");
			finalOfferHead.textContent = "Prezzo Finale";

			tHeadRow.appendChild(auctionIDHead);
			tHeadRow.appendChild(finalOfferHead);
			
			this.page.appendChild(tHeadRow);
			
			winnedAuctionList.forEach(function(auction) {
				auctionRow = document.createElement("tr");
				
				auctionID = document.createElement("td");
				auctionID.textContent = auction.id_asta;
				finalOffer = document.createElement("td");
				finalOffer.textContent = maxOfferMap[auction.id_asta] + "(EUR)";
				articleList = document.createElement("td");
				articleListTable = document.createElement("table");
				articleRowHead = document.createElement("tr");
								
				articleIDHead = document.createElement("th");
				articleIDHead.textContent = "ID Articolo";
				articleNameHead = document.createElement("th");
				articleNameHead.textContent = "Nome Articolo";
				
				articleRowHead.appendChild(articleIDHead);
				articleRowHead.appendChild(articleNameHead);
				articleListTable.appendChild(articleRowHead);
				articleList.appendChild(articleListTable);
				auctionRow.appendChild(auctionID);
				auctionRow.appendChild(finalOffer);
				
				let winnedArticle = winnedArticleMap[auction.id_asta];
				winnedArticle.forEach(function(article) {
					articleRow = document.createElement("tr");
					articleID = document.createElement("td");
					articleName = document.createElement("td");
					
					articleID.textContent = article.id_articolo;
					articleName.textContent = article.nome_articolo;
					
					articleRow.appendChild(articleID);
					articleRow.appendChild(articleName);
					
					articleListTable.appendChild(articleRow);
				});
				
				auctionRow.appendChild(articleList);
				self.page.appendChild(auctionRow);
			});
		}
	}
		
	function LastVisited(table) {
		this.table = table;

		this.show = function(lastVisited) {
			this.table.innerHTML = "";
			let row, id, time;
			let self = this;
			let header4;
			let status = false;

			header4 = document.createElement("h4");
			header4.textContent = "Asta Visitate:";
			header4.style = "color : red;";
			this.table.appendChild(header4);
			lastVisited.forEach(function(index) {
				makeCall("GET", "LastVisited?id=" + index, null, function(x) {
					document.getElementById("errorMessage").textContent = "";
					if (x.readyState == XMLHttpRequest.DONE) {
						if (x.status ===  200) {
							let json = JSON.parse(x.responseText);
							let auction = json.auction;
							let remainingTime = json.remainingTime;
							if (auction === null || auction === undefined) {
								return;
							}
							else {
								if (status === false) {
									self.createTable(self.table);
									status = true;
								}
								row = document.createElement("tr");
								id = document.createElement("td");
								time = document.createElement("td");
								id.textContent = auction.id_asta;
								time.textContent = remainingTime;
								row.appendChild(id);
								row.appendChild(time);
								self.table.appendChild(row);
							}
						}
						else if (x.status === 401) {
							window.location.href = x.responseText;
						}
						else {
							document.getElementById("errorMessage").textContent = x.responseText;
						}
					}
				});
			});
		};
		this.createTable = function(table) {
			let rowHead, idHead, timeHead;

			rowHead = document.createElement("tr");
			idHead = document.createElement("th");
			idHead.textContent = "ID Asta";
			rowHead.appendChild(idHead);
			timeHead = document.createElement("th");
			timeHead.textContent = "Tempo Rimanente:";
			rowHead.appendChild(timeHead);
			table.appendChild(rowHead);
		}

	}
}
 