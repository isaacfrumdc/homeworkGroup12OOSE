function deleteEmployer(employerName) {
    fetch('http://localhost:7000/employers?name=' + employerName, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

function addEmployer(employerName, employerSector, employerSummary) {
    fetch('http://localhost:7000/employers?name=' + employerName + employerSector + employerSummary, {
            method: 'Post',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

const name = document.getElementById("eName").value;
const sector = document.getElementById("eSector").value;
const summary = document.getElementById("eSummary").value;

document.getElementById("addbtn").addEventListener("click", addEmployer.bind(this, {name, sector, summary}));

let delButtons = document.querySelectorAll("li > button")
Array.prototype.forEach.call(delButtons, function(button) {
    button.addEventListener('click', deleteEmployer.bind(null, button.id));
});

