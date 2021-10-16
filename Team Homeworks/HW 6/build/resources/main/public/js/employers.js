function deleteEmployer(employerName) {
    fetch('http://localhost:7000/employers?name=' + employerName, {
            method: 'Delete',
        }
    ).then(res => window.location.reload = window.location.reload(true));
}

let delButtons = document.querySelectorAll("li > button")
Array.prototype.forEach.call(delButtons, function(button) {
    button.addEventListener('click', deleteEmployer.bind(null, button.id));
});


function addEmployer() {
    const name = document.getElementById("name").value;
    const sector = document.getElementById("sector").value;
    const summary = document.getElementById("summary").value;
    if (name != null && sector != null) {
        fetch('http://localhost:7000/employers?name='+name+ '&sector=' + sector+ '&summary=' + summary,{
                method: 'Post',
            }
        ).then(res => window.location.reload = window.location.reload(true));
        return true;
    }
    else {
        return false;
    }
}








