function deleteEmployer(employerName) {
    fetch('/employers?name=' + employerName, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

let delButtons = document.querySelectorAll("li > button")
Array.prototype.forEach.call(delButtons, function(button) {
    button.addEventListener('click', deleteEmployer.bind(null, button.id));
});



function addEmployer() {
    fetch('/employers?name=' + document.getElementById("eName").value+ document.getElementById("eSector").value + document.getElementById("eSummary").value, {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}
// name = document.getElementById("eName").value;
// sector = document.getElementById("eSector").value;
// summary = document.getElementById("eSummary").value;
//
// let addBtn = document.getElementById("addE");
// addBtn.addEventListener('click', addEmployer.bind(null, {name, sector, summary}))

document.getElementById("addE").addEventListener("click", addEmployer.bind(this, {name, sector, summary}));







