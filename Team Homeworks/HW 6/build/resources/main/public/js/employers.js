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
    if (sector.length < 2 || sector.length > 100 ){
        //checking for alphabetical characters only not working
        //!(sector.matches("[a-zA-Z]+"))
        alert("Employer sector cannot be less than 2 characters, more than 100 characters, or include any digits or $, @, ^, %, ~!");
        return false;
    }
    if (name.length < 2 || name.length > 150) {
        //checking for alphabetical characters only not working
        //!(name.matches("[a-zA-Z]+"))
        alert("Employer name cannot be less than 2 characters, more than 150 characters, or include $, @, ^, %, ~!");
        return false;
    }

    if (name != null && sector != null) {
        fetch('http://localhost:7000/employers?name='+name+ '&sector=' + sector+ '&summary=' + summary,{
                method: 'Post',
            }
        ).then(res => window.location.reload = window.location.reload(true));
        return true;
    }
    else {
        alert("something else went wrong :/");
        return false;
    }
}








