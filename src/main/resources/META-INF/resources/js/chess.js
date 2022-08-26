document.getElementById('btn-dark-mode').onclick = function () {
    document.body.classList.toggle('dark-mode');
}

let GM = new GameManager();
let his = document.getElementById('his');
GM.initialiseChessBoard();

document.getElementById('btn-new-game').onclick = function() {
    GM.newGame();
}
document.getElementById('btn-load-game').onclick = function () {
    GM.loadGame();
}
document.getElementById('btn-historique').onclick = function () {
	let data = GM.getAndPlayMoves();
	let list = document.getElementById('demo');
	list.remove();
	let nouvelleList = document.createElement('demo');
	his.appendChild(nouvelleList);
	data.forEach(obj =>{
		let entry = document.createElement('li');
		entry.appendChild(document.createTextNode(obj.promotion + "-" + obj.from + "-" + obj.to));
		nouvelleList.appendChild(entry);	
	})
		
}
