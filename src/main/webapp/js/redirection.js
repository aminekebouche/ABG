// Triggers a function after a button click, here we are going to redirect the user to a selected game page
document.getElementById("Chess").onclick = () => {
	console.log('Selected chess');

	window.location.assign("Chess.html");
};

document.getElementById("Checkers").onclick = () => {
	console.log('Selected checkers');

	// window.location.assign("");		TODO: Specify path to checkers html page later, when the checkers page will be available
}