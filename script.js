let bContactsShower = document.querySelector('.button');
let instagramLink = document.querySelector('.instagram');
let telegramLink = document.querySelector('.telegram');
let gitLink = document.querySelector('.git');
let contactsShowed = false;
bContactsShower.onclick = function () {
	contactsShowed = !contactsShowed
	if (contactsShowed) {
		bContactsShower.textContent = 'HIDE CONTACTS';
		instagramLink.textContent = 'INSTAGRAM';
		telegramLink.textContent = 'TELEGRAM';
		gitLink.textContent = 'GITHUB';
	}
	else {
		bContactsShower.textContent = 'SHOW CONTACTS';
		instagramLink.textContent = '';
		telegramLink.textContent = '';
		gitLink.textContent = '';
	}
}

