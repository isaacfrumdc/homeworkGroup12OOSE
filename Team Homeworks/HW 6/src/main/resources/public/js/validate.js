function validateUsername() {
    const name = document.getElementById("username");
    if (name.value.length < 1) {
        alert("Username cannot be empty!");
        return false;
    } else {
        return true;
    }
}

function validateEmployer() {
    const name = document.getElementById("name");
    const sector = document.getElementById("sector");

    /* Check employer name */
    if (name.value.length < 2 || name.value.length > 150 || name.value.contains("$")
        || name.value.contains("@") || name.value.contains("^") || name.value.contains("%")
        || name.value.contains("~")) {
        alert("Employer name cannot be less than 2 characters, more than 150 characters, or include $, @, ^, %, ~!");
        return false;
    }

    /* Check employer sector */
    if (sector.value.length < 2 || sector.value.length > 100 || sector.value.contains("$")
        || sector.value.contains("@") || sector.value.contains("^") || sector.value.contains("%")
        || sector.value.contains("~")) {
        alert("Employer sector cannot be less than 2 characters, more than 100 characters, or include any digits or $, @, ^, %, ~!");
        return false;
    }

    return true;
}